package reservationsystem.controllers;

import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.util.Optional;

public class InputValidation {
    public static void setupValidation(TextField roomField, TextField durationField, TextField timeField) {
        // Room format validation (1 letter + 2 digits)
        roomField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[A-Za-z]?\\d{0,2}")) {
                roomField.setText(oldVal);
            }
            roomField.setStyle(newVal.matches("[A-Z]\\d{2}") ? "" : "-fx-border-color: red;");
        });

        // Duration must be a positive number
        durationField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                durationField.setText(oldVal);
            }
        });

        // Time format validation (HH:MM)
        timeField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d{0,2}:?\\d{0,2}")) {
                timeField.setText(oldVal);
            }
            timeField.setStyle(newVal.matches("([01]\\d|2[0-3]):[0-5]\\d") ? "" : "-fx-border-color: red;");
        });
    }

    /**
     * Validates inputs for adding or editing a reservation.
     * Returns an error message if invalid, or `Optional.empty()` if valid.
     */
    public static String validateInputs(TextField employeeField, TextField roomField,
                                        TextField timeField, TextField durationField, DatePicker datePicker) {
        StringBuilder errors = new StringBuilder();

        if (employeeField.getText().trim().isEmpty()) {
            errors.append("• Employee name is required\n");
        }
        if (!roomField.getText().trim().matches("[A-Z]\\d{2}")) {
            errors.append("• Room must be in format A12 (1 letter + 2 digits)\n");
        }
        if (!timeField.getText().trim().matches("([01]\\d|2[0-3]):[0-5]\\d")) {
            errors.append("• Time must be in HH:MM format (24-hour)\n");
        }
        if (durationField.getText().trim().isEmpty()) {
            errors.append("• Duration is required\n");
        } else {
            try {
                int duration = Integer.parseInt(durationField.getText().trim());
                if (duration <= 0) {
                    errors.append("• Duration must be positive\n");
                }
            } catch (NumberFormatException e) {
                errors.append("• Duration must be a number\n");
            }
        }

        return errors.length() > 0 ? errors.toString() : null;  // Return errors or null
    }


    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
