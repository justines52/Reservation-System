package reservationsystem.controllers;

import javafx.scene.control.TextField;

public class FormatValidation {
    // format des codes salles (1 lettre + 2chiffres representant la salle)
    public static void setupRoomValidation(TextField roomField) {
        roomField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[A-Za-z]?\\d{0,2}")) {
                roomField.setText(oldVal);
            }
            roomField.setStyle(newVal.matches("[A-Z]\\d{2}") ? "" : "-fx-border-color: red;");
        });
    }
    //validation de la duree(doit etre un int positif)
    public static void setupDurationValidation(TextField durationField) {
        durationField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                durationField.setText(oldVal);
            }
        });
    }
    // check si la l'heure est dans le format h:min
    public static void setupTimeValidation(TextField timeField) {
        timeField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d{0,2}:?\\d{0,2}")) {
                timeField.setText(oldVal);
            }
            timeField.setStyle(newVal.matches("([01]\\d|2[0-3]):[0-5]\\d") ? "" : "-fx-border-color: red;");
        });
    }
}
