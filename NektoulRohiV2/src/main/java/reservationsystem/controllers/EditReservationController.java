package reservationsystem.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import reservationsystem.database.ReservationDB;
import reservationsystem.model.Reservation;
import reservationsystem.model.SalleReunion;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EditReservationController {
    @FXML private Label titleLabel;
    @FXML private TextField codeField;
    @FXML private TextField employeeField;
    @FXML private TextField roomField;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private TextField durationField;
    @FXML private Button saveButton;

    private Reservation reservation;
    private ReservationDB reservationDB;
    private Runnable onReservationUpdated;

    @FXML
    public void initialize() {
        FormatValidation.setupRoomValidation(roomField);
        FormatValidation.setupDurationValidation(durationField);
        FormatValidation.setupTimeValidation(timeField);
        InputValidation.setupValidation(roomField, durationField, timeField);
    }
    //
    public void setReservation(Reservation reservation, ReservationDB reservationDB) {
        if (reservation == null || reservationDB == null) {
            throw new IllegalArgumentException("Reservation and DB cannot be null");
        }

        this.reservation = reservation;
        this.reservationDB = reservationDB;
        populateFields();
    }
    private void populateFields() {
        //remplir les champs avec les valeurs de la reservation existante
        codeField.setText(reservation.getCodeReservation());
        employeeField.setText(reservation.getNomEmployee());

        if (reservation.getSalle() != null) {
            roomField.setText(reservation.getSalle().toString());
        }

        LocalDateTime dateTime = reservation.getDateHeureRes();
        datePicker.setValue(dateTime.toLocalDate());
        timeField.setText(dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));

        durationField.setText(String.valueOf(reservation.getDuree()));

        // rend le champ du id de la reservation non modifiable
        codeField.setEditable(false);
        codeField.setStyle("-fx-opacity: 1; -fx-background-color: #f4f4f4;");
    }

    public void setOnReservationUpdated(Runnable callback) {
        this.onReservationUpdated = callback;
    }
    //methode pour edit la reservation a mettre on action dans le fxml
    @FXML
    private void handleEdit(ActionEvent event) {

        LocalDate date = datePicker.getValue();
        LocalTime time = LocalTime.parse(timeField.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));
        int duration = Integer.parseInt(durationField.getText().trim());
        LocalDateTime newStart = LocalDateTime.of(date, time);
        LocalDateTime newEnd = newStart.plusMinutes(duration);

        try { // veridie si les inputs sont valide si non on annule l'edit
            if (!validateInputs()) {
                return;
            }

            String roomCode = roomField.getText().trim().toUpperCase();

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


            // verifie si la salle existe,sinon l'ajoute
            if (!reservationDB.roomExists(roomCode)) {
                boolean added = addNewRoom(roomCode);
                if (!added) {
                    showAlert("Error", "Failed to create new room");
                    return;
                }
            }

            // mettre a jour les informations de la reservation
            updateReservationFromFields();

            if (reservationDB.updateReservation(reservation)) {
                if (onReservationUpdated != null) {
                    onReservationUpdated.run();
                }
                closeWindow();
            } else {
                showAlert("Error", "Failed to update reservation");
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to save changes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean addNewRoom(String roomCode) {
        try {
            // ajouter dans la bdd
            return reservationDB.addRoom(roomCode);
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to create room: " + e.getMessage());
            return false;
        }
    }
    // methode pour sauvegarder les modifications dans la bdd
    private void updateReservationFromFields() throws Exception {
        reservation.setNomEmployee(employeeField.getText().trim());
        // extraction du code de la salle
        String roomCode = roomField.getText().trim();
        char building = roomCode.charAt(0);
        int roomNumber = Integer.parseInt(roomCode.substring(1));
        reservation.setSalle(new SalleReunion(building, roomNumber));
        // mise a jour de la date et de l'heure
        LocalDate date = datePicker.getValue();
        LocalTime time = LocalTime.parse(timeField.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));
        reservation.setDateHeureRes(LocalDateTime.of(date, time));
        // mise a  jour de la duree
        reservation.setDuree(Integer.parseInt(durationField.getText().trim()));
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
        saveButton.getScene().getWindow().hide();
    }
}