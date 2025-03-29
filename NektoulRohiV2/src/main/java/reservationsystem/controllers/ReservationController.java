package reservationsystem.controllers;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import reservationsystem.controllers.AddReservationController;
import reservationsystem.controllers.EditReservationController;
import reservationsystem.database.ReservationDB;
import reservationsystem.model.Reservation;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ReservationController {
    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, String> codeColumn;
    @FXML private TableColumn<Reservation, String> employeeColumn;
    @FXML private TableColumn<Reservation, String> roomColumn;
    @FXML private TableColumn<Reservation, String> dateColumn;
    @FXML private TableColumn<Reservation, String> durationColumn;
    @FXML private TableColumn<Reservation, Void> actionColumn;

    @FXML private JFXButton meetingReservationsBtn;
    @FXML private JFXButton roomsButton;
    @FXML private Button addButton;
    @FXML private TextField searchField;
    @FXML private Label roomCountLabel;
    @FXML private TextField roomCountField;

    private final ReservationDB reservationDB = new ReservationDB();
    private ObservableList<Reservation> allReservations = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns(); // initialisation des colonnes de la table
        loadReservations(); // chargement des réservations de la bdd
        setupSearchFilter(); // mise en place du filtre de recherche
    }

    private void setupTableColumns() {
        codeColumn.setCellValueFactory(cellData -> cellData.getValue().codeReservationProperty());
        employeeColumn.setCellValueFactory(cellData -> cellData.getValue().nomEmployeeProperty());
        roomColumn.setCellValueFactory(cellData -> cellData.getValue().getSalle().salleProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().getFormattedDateTime());
        durationColumn.setCellValueFactory(cellData -> cellData.getValue().dureeProperty());
        // configuration de la colonne des actions
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttons = new HBox(5, editButton, deleteButton);

            {
                editButton.getStyleClass().add("edit-button");
                deleteButton.getStyleClass().add("delete-button");
                buttons.getStyleClass().add("hbox-button");
                // action pour editer une reservation
                editButton.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    handleEditReservation(reservation);
                });
               //action pour supprimer une reservation
                deleteButton.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    handleDeleteReservation(reservation);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });
    }
    //methode pour charger les reservations
    private void loadReservations() {
        try {
            List<Reservation> reservations = reservationDB.getAllReservations();
            allReservations.setAll(reservations);
            reservationTable.setItems(allReservations);
            roomCountField.setText(String.valueOf(reservations.size()));
        } catch (SQLException e) {
            showAlert("Error", "Failed to load reservations: " + e.getMessage());
        }
    }
     //configuration du filtre de recherche
    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                loadReservations();
            } else {
                searchReservations(newValue);
            }
        });
    }
   //methode pour chercher une reservation depuis le nom d'employer
    private void searchReservations(String searchText) {
        try {
            List<Reservation> results = reservationDB.searchReservationsByEmployee(searchText);
            allReservations.setAll(results);
            reservationTable.setItems(allReservations);
            roomCountField.setText(String.valueOf(results.size()));
        } catch (SQLException e) {
            showAlert("Error", "Failed to search reservations: " + e.getMessage());
        }
    }
    // gestion de l'ajout d'une nouvelle reservation
    @FXML
    private void handleAddReservation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddReservation.fxml"));
            Parent root = loader.load();
            AddReservationController controller = loader.getController();
            controller.setReservationDB(this.reservationDB);
            controller.setOnReservationAdded(() -> {
                loadReservations();
            });

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add New Reservation");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            showAlert("Error", "Could not load add reservation form: " + e.getMessage());
        }
    }
    // gestion de l'edit d'une réservation
    private void handleEditReservation(Reservation reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditReservation.fxml"));
            Parent root = loader.load();
            EditReservationController controller = loader.getController();

            // verifie que le controller est bien charge
            if (controller == null) {
                showAlert("Error", "Failed to initialize the edit form.");
                return;
            }

            // configuration du controller avec la reservation et maj de la liste apres modification
            controller.setReservation(reservation, reservationDB);
            controller.setOnReservationUpdated(this::loadReservations);

            // creation et affichage de la fenetre de modification
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Reservation - " + reservation.getCodeReservation());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            showAlert("Error", "Failed to load edit form: " + e.getMessage());
        }
    }

//gestion de la suppression d'une reservation
    private void handleDeleteReservation(Reservation reservation) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setContentText("Are you sure you want to delete reservation " +
                reservation.getCodeReservation() + "?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (reservationDB.deleteReservation(reservation.getCodeReservation())) {
                    loadReservations();
                    showAlert("Success", "Reservation deleted successfully");
                }
            } catch (SQLException e) {
                showAlert("Error", "Failed to delete reservation: " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}