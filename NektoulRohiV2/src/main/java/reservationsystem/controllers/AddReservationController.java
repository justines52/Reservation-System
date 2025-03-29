package reservationsystem.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import reservationsystem.database.ReservationDB;
import reservationsystem.model.Reservation;
import reservationsystem.model.SalleReunion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.sql.SQLException;
import java.util.List;

public class AddReservationController {

    @FXML
    private TextField employeeField;
    @FXML
    private TextField roomField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField timeField;
    @FXML
    private TextField durationField;

    private ReservationDB reservationDB;
    private Runnable onReservationAdded;


//methode pour initialiser les valeurs par defaut et le format
    @FXML
    public void initialize() {
        datePicker.setValue(LocalDate.now());
        FormatValidation.setupRoomValidation(roomField);
        FormatValidation.setupDurationValidation(durationField);
        FormatValidation.setupTimeValidation(timeField);
        InputValidation.setupValidation(roomField, durationField, timeField);
    }
//methode qui va etre prise dans le fxml, la logique de l'ajout d'une nouvelle reservation
    @FXML
    private void handleAddReservation() {
        try {
            if (!validateInputs()) { //verifie si tout les champs sont valide si non on annule l'ajout
                return;
            }
             //recuperer les valeurs des inputs
            String roomCode = roomField.getText().trim().toUpperCase();
            LocalDate date = datePicker.getValue();
            LocalTime time = parseTime(timeField.getText().trim());
            int duration = Integer.parseInt(durationField.getText().trim());
            LocalDateTime newStart = LocalDateTime.of(date, time);
            LocalDateTime newEnd = newStart.plusMinutes(duration);

            // verifie si la salle est deja reserve
            List<Reservation> existingReservations = reservationDB.getReservationsForRoom(roomCode);
            for (Reservation existing : existingReservations) {
                LocalDateTime existingStart = existing.getDateHeureRes();
                LocalDateTime existingEnd = existingStart.plusMinutes(existing.getDuree());

                if (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    String conflictTime = existingStart.format(formatter) + " - " + existingEnd.format(formatter);
                    showAlert("reservation already booked",
                            "This room is already booked during the requested time:\n" +
                                    "Conflict: " + conflictTime + "\n" +
                                    "Booked by: " + existing.getNomEmployee());
                    return;
                }
            }

            // verifie si la salle existe dans la bdd, sinon l'ajoute
            if (!reservationDB.roomExists(roomCode)) {
                if (!reservationDB.addRoom(roomCode)) {
                    showAlert("Error", "Could not add new room " + roomCode);
                    return;
                }
            }
            //// Creation et enregistrement de la reservation
            Reservation reservation = createReservation(
                    employeeField.getText().trim(),
                    roomCode,
                    date,
                    time,
                    duration
            );

            if (reservationDB.addReservation(reservation)) {
                showAlert("Success", "Reservation added successfully!");
                if (onReservationAdded != null) {
                    onReservationAdded.run();
                }
                closeWindow();
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Error checking availability: " + e.getMessage());
        } catch (DateTimeParseException e) {
            showAlert("Invalid Time", "Please enter time in HH:MM format");
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }
    //methode pour creer et enregistrer la reservation
    private Reservation createReservation(String employeeName, String roomCode,
                                          LocalDate date, LocalTime time, int duration) {
        return new Reservation(
                "RES-" + System.currentTimeMillis(),
                employeeName,
                new SalleReunion(roomCode.charAt(0), Integer.parseInt(roomCode.substring(1))),
                LocalDateTime.of(date, time),
                duration
        );
    }

    private LocalTime parseTime(String timeString) throws DateTimeParseException {
        return LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"));
    }

    //methode qui retourne les erreurs
    public static String validateErrors(TextField employeeField, TextField roomField,
                                        TextField timeField, TextField durationField, DatePicker datePicker) {
        StringBuilder errors = new StringBuilder();

        if (employeeField.getText().trim().isEmpty()) {
            errors.append("Employee name is required\n");
        }
        if (!roomField.getText().trim().matches("[A-Z]\\d{2}")) {
            errors.append("Room must be in format A12 (1 letter + 2 digits)\n");
        }
        if (!timeField.getText().trim().matches("([01]\\d|2[0-3]):[0-5]\\d")) {
            errors.append("Time must be in HH:MM format (24-hour)\n");
        }
        if (durationField.getText().trim().isEmpty()) {
            errors.append("Duration is required\n");
        } else {
            try {
                int duration = Integer.parseInt(durationField.getText().trim());
                if (duration <= 0) {
                    errors.append("Duration must be positive\n");
                }
            } catch (NumberFormatException e) {
                errors.append("Duration must be a number\n");
            }
        }

        return errors.length() > 0 ? errors.toString() : null;  // Return errors or null
    }

    //methode qui appelle la classe qui valide les inputs
    private boolean validateInputs() {
        String validationErrors = InputValidation.validateInputs(employeeField, roomField, timeField, durationField, datePicker);
        if (validationErrors != null) {
            InputValidation.showAlert("Validation Errors", validationErrors);
            return false;
        }
        return true;
    }





    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void closeWindow() {
        roomField.getScene().getWindow().hide();
    }
//setters pour relier avec la classe controller main et la classe contenant les requetes
    public void setReservationDB(ReservationDB reservationDB) {
        this.reservationDB = reservationDB;
    }
    public void setOnReservationAdded(Runnable callback) {
        this.onReservationAdded = callback;
    }
}
