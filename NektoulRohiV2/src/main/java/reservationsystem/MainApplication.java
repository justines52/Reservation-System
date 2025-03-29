package reservationsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApplication.class.getResource("/fxml/MeetingReservation.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 700, 600);
            scene.getStylesheets().add(getClass().getResource("/Styles/reservation.css").toExternalForm());

            primaryStage.setTitle("Reservation System");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}