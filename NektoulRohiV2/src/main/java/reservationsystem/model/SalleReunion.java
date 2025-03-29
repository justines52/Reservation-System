package reservationsystem.model;

import com.mysql.cj.conf.StringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

public class SalleReunion {
    private char batiment;
    private int nbSalle;

    public SalleReunion(char batiment, int nbSalle) {
        this.batiment = batiment;
        this.nbSalle = nbSalle;
    }

    public char getBatiment() {
        return batiment;
    }

    public int getNbSalle() {
        return nbSalle;
    }

    @Override
    public String toString() {
        return batiment + String.format("%02d", nbSalle);
    }

    public ObservableValue<String> toStringProperty() {
        return new SimpleStringProperty(toString());
    }
    public ObservableValue<String> salleProperty() {
        return new SimpleStringProperty(toString());
    }
}
