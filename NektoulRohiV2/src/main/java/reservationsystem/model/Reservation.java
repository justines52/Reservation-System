package reservationsystem.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Reservation {
    private String codeReservation;
    private String nomEmployee;
    private LocalDateTime dateHeureRes;
    private int duree;
    private SalleReunion salle;

    public Reservation(String codeReservation, String nomEmployee, SalleReunion salle,
                       LocalDateTime dateHeureRes, int duree) {
        this.codeReservation = codeReservation;
        this.nomEmployee = nomEmployee;
        this.salle = salle;
        this.dateHeureRes = dateHeureRes;
        this.duree = duree;
    }

    // Getters and Setters
    public String getCodeReservation() { return codeReservation; }
    public String getNomEmployee() { return nomEmployee; }
    public void setNomEmployee(String nomEmployee) { this.nomEmployee = nomEmployee; }
    public SalleReunion getSalle() { return salle; }
    public void setSalle(SalleReunion salle) { this.salle = salle; }
    public LocalDateTime getDateHeureRes() { return dateHeureRes; }
    public void setDateHeureRes(LocalDateTime dateHeureRes) { this.dateHeureRes = dateHeureRes; }
    public int getDuree() { return duree; }
    public void setDuree(int duree) { this.duree = duree; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format("%s - %s - %s - %d mins",
                codeReservation,
                nomEmployee,
                dateHeureRes.format(formatter),
                duree);
    }

    public ObservableValue<String> getFormattedDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return new SimpleStringProperty(dateHeureRes.format(formatter));
    }

    // Additional property getters for TableView
    public ObservableValue<String> codeReservationProperty() {
        return new SimpleStringProperty(codeReservation);
    }

    public ObservableValue<String> nomEmployeeProperty() {
        return new SimpleStringProperty(nomEmployee);
    }

    public ObservableValue<String> dureeProperty() {
        return new SimpleStringProperty(String.valueOf(duree) + " mins");
    }
}