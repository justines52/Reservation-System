package reservationsystem.database;

import reservationsystem.model.Reservation;
import reservationsystem.model.SalleReunion;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static reservationsystem.database.DbConnection.getConnection;

public class ReservationDB {
    private Connection connection;

    public ReservationDB() {
        this.connection = getConnection();
    }
//methode qui traite la requete d ajout d'une nouvelle reservation a la bdd
    public boolean addReservation(Reservation reservation) throws SQLException {
        String sql = "INSERT INTO Reservation (ID_Reservation, date_Heure_Reservation, Nom_Employe, Duree_Reservation, Code_Salle) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, reservation.getCodeReservation());
            stmt.setTimestamp(2, Timestamp.valueOf(reservation.getDateHeureRes()));
            stmt.setString(3, reservation.getNomEmployee());
            stmt.setInt(4, reservation.getDuree());
            stmt.setString(5, reservation.getSalle().toString());

            return stmt.executeUpdate() > 0;
        }
    }
//methode qui ajoute une salle a la bdd
    public boolean addRoom(String roomCode) throws SQLException {
        if (!isValidRoomCode(roomCode)) {
            return false;
        }

        String sql = "INSERT INTO Salle (Code_Salle) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, roomCode);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // si la salle existe deja
            return e.getErrorCode() == 1062; // MySQL duplicate entry error code
        }
    }
//methode qui verifie si la salle existe dans la bdd
    public boolean roomExists(String roomCode) throws SQLException {
        if (roomCode == null || roomCode.isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM Salle WHERE Code_Salle = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roomCode);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }
    private boolean isValidRoomCode(String roomCode) {
        return roomCode != null && roomCode.length() == 3 &&
                roomCode.matches("[A-Z]\\d{2}");
    }
    //methode qui met a jour la reservation dans la bdd
    public boolean updateReservation(Reservation reservation) throws SQLException {
        String sql = "UPDATE Reservation SET date_Heure_Reservation = ?, Nom_Employe = ?, Duree_Reservation = ?, Code_Salle = ? WHERE ID_Reservation = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(reservation.getDateHeureRes()));
            stmt.setString(2, reservation.getNomEmployee());
            stmt.setInt(3, reservation.getDuree());
            stmt.setString(4, reservation.getSalle().toString());
            stmt.setString(5, reservation.getCodeReservation());

            return stmt.executeUpdate() > 0;
        }
    }
//methode qui supprime une reservation de la bdd
    public boolean deleteReservation(String codeReservation) throws SQLException {
        String sql = "DELETE FROM Reservation WHERE ID_Reservation = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, codeReservation);
            return stmt.executeUpdate() > 0;
        }
    }
//methode qui liste toutes les reservations de la bdd
    public List<Reservation> getAllReservations() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM Reservation";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String code = rs.getString("ID_Reservation");
                String employee = rs.getString("Nom_Employe");
                LocalDateTime dateTime = rs.getTimestamp("date_Heure_Reservation").toLocalDateTime();
                int duration = rs.getInt("Duree_Reservation");
                String roomCode = rs.getString("Code_Salle");
                char building = roomCode.charAt(0);
                int roomNumber = Integer.parseInt(roomCode.substring(1));

                Reservation reservation = new Reservation(
                        code,
                        employee,
                        new SalleReunion(building, roomNumber),
                        dateTime,
                        duration
                );
                reservations.add(reservation);
            }
        }
        return reservations;
    }
    //methode qui cherche une reservation en prenant le nom d employe en entree
    public List<Reservation> searchReservationsByEmployee(String employeeName) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM Reservation WHERE LOWER(Nom_Employe) LIKE LOWER(?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + employeeName + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String code = rs.getString("ID_Reservation");
                String employee = rs.getString("Nom_Employe");
                LocalDateTime dateTime = rs.getTimestamp("date_Heure_Reservation").toLocalDateTime();
                int duration = rs.getInt("Duree_Reservation");
                String roomCode = rs.getString("Code_Salle");
                char building = roomCode.charAt(0);
                int roomNumber = Integer.parseInt(roomCode.substring(1));

                reservations.add(new Reservation(
                        code,
                        employee,
                        new SalleReunion(building, roomNumber),
                        dateTime,
                        duration
                ));
            }
        }
        return reservations;
    }
    // liste toutes les salles de la bdd
    public List<SalleReunion> getAllRooms() throws SQLException {
        List<SalleReunion> rooms = new ArrayList<>();
        String sql = "SELECT Code_Salle FROM Salle";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String roomCode = rs.getString("Code_Salle");
                char building = roomCode.charAt(0);
                int roomNumber = Integer.parseInt(roomCode.substring(1));
                rooms.add(new SalleReunion(building, roomNumber));
            }
        }
        return rooms;
    }
//retourne le nombre de salle existant dans la bdd
    public int getRoomCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Salle";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
    //supprime une salle de la bdd
    public boolean deleteRoom(String roomCode) throws SQLException {
        String sql = "DELETE FROM Salle WHERE Code_Salle = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, roomCode);
            return stmt.executeUpdate() > 0;
        }
    }
    public List<Reservation> getReservationsForRoom(String roomCode) throws SQLException {
        String sql = "SELECT * FROM Reservation WHERE Code_Salle = ?";
        List<Reservation> reservations = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, roomCode);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String code = rs.getString("ID_Reservation");
                String employee = rs.getString("Nom_Employe");
                LocalDateTime dateTime = rs.getTimestamp("date_Heure_Reservation").toLocalDateTime();
                int duration = rs.getInt("Duree_Reservation");

                Reservation reservation = new Reservation(
                        code,
                        employee,
                        new SalleReunion(roomCode.charAt(0), Integer.parseInt(roomCode.substring(1))),
                        dateTime,
                        duration
                );
                reservations.add(reservation);
            }
        }
        return reservations;
    }
}
