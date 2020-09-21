package ba.etf.unsa.rpr;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;

public class GradController {
    public TextField fieldNaziv;
    public TextField fieldBrojStanovnika;
    public ChoiceBox<Drzava> choiceDrzava;
    public ObservableList<Drzava> listDrzave;
    public TextField fieldGradonacelnik;
    public ArrayList<Osoba> osobe;
    private Grad grad;

    public GradController(Grad grad, ArrayList<Drzava> drzave) {
        this.grad = grad;
        listDrzave = FXCollections.observableArrayList(drzave);
        this.osobe = new ArrayList<>();
    }

    public GradController(Grad grad, ArrayList<Drzava> drzave, ArrayList<Osoba> osobe) {
        this.grad = grad;
        listDrzave = FXCollections.observableArrayList(drzave);
        this.osobe = osobe;
    }

    @FXML
    public void initialize() {
        choiceDrzava.setItems(listDrzave);
        if (grad != null) {
            fieldNaziv.setText(grad.getNaziv());
            fieldBrojStanovnika.setText(Integer.toString(grad.getBrojStanovnika()));
            // choiceDrzava.getSelectionModel().select(grad.getDrzava());
            // ovo ne radi jer grad.getDrzava() nije identički jednak objekat kao član listDrzave
            for (Drzava drzava : listDrzave)
                if (drzava.getId() == grad.getDrzava().getId())
                    choiceDrzava.getSelectionModel().select(drzava);
            if (grad.getGradonacelnik() != null)
                fieldGradonacelnik.setText(grad.getGradonacelnik().getIme() + " " + grad.getGradonacelnik().getPrezime());
        } else {
            choiceDrzava.getSelectionModel().selectFirst();
        }
    }

    public Grad getGrad() {
        return grad;
    }

    public void clickCancel(ActionEvent actionEvent) {
        grad = null;
        Stage stage = (Stage) fieldNaziv.getScene().getWindow();
        stage.close();
    }

    public void clickOk(ActionEvent actionEvent) {
        boolean sveOk = true;

        if (fieldNaziv.getText().trim().isEmpty()) {
            fieldNaziv.getStyleClass().removeAll("poljeIspravno");
            fieldNaziv.getStyleClass().add("poljeNijeIspravno");
            sveOk = false;
        } else {
            fieldNaziv.getStyleClass().removeAll("poljeNijeIspravno");
            fieldNaziv.getStyleClass().add("poljeIspravno");
        }

        if (!fieldGradonacelnik.getText().trim().isEmpty()) {
            int brojRazmaka = 0;
            for(int i=0; i < fieldGradonacelnik.getText().length(); i++) {
                if (fieldGradonacelnik.getText().charAt(i) == ' ')
                    brojRazmaka++;
            }
            if (brojRazmaka != 1) {
                fieldGradonacelnik.getStyleClass().removeAll("poljeIspravno");
                fieldGradonacelnik.getStyleClass().add("poljeNijeIspravno");
                System.out.println("Broj razmaka: " + brojRazmaka);
                sveOk = false;
            } else {
                fieldGradonacelnik.getStyleClass().removeAll("poljeNijeIspravno");
                fieldGradonacelnik.getStyleClass().add("poljeIspravno");
            }
        } else {
            fieldGradonacelnik.getStyleClass().removeAll("poljeNijeIspravno");
            fieldGradonacelnik.getStyleClass().add("poljeIspravno");
        }

        int brojStanovnika = 0;
        try {
            brojStanovnika = Integer.parseInt(fieldBrojStanovnika.getText());
        } catch (NumberFormatException e) {
            // ...
        }
        if (brojStanovnika <= 0) {
            fieldBrojStanovnika.getStyleClass().removeAll("poljeIspravno");
            fieldBrojStanovnika.getStyleClass().add("poljeNijeIspravno");
            sveOk = false;
        } else {
            fieldBrojStanovnika.getStyleClass().removeAll("poljeNijeIspravno");
            fieldBrojStanovnika.getStyleClass().add("poljeIspravno");
        }

        if (!sveOk) return;

        if (grad == null) grad = new Grad();
        grad.setNaziv(fieldNaziv.getText());
        grad.setBrojStanovnika(Integer.parseInt(fieldBrojStanovnika.getText()));
        grad.setDrzava(choiceDrzava.getValue());

        Osoba pronadjena = null;
        if (fieldGradonacelnik.getText().trim().length() > 0) {
            for (Osoba o : osobe)
                if (fieldGradonacelnik.getText().equals(o.getIme() + " " + o.getPrezime()))
                    pronadjena = o;

            if (pronadjena == null) {
                String[] dijelovi = fieldGradonacelnik.getText().split(" ");
                pronadjena = new Osoba(0, dijelovi[0], dijelovi[1]);
            }
        }
        grad.setGradonacelnik(pronadjena);

        Stage stage = (Stage) fieldNaziv.getScene().getWindow();
        stage.close();
    }
}
