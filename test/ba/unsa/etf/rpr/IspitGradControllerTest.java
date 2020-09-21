package ba.unsa.etf.rpr;


import ba.etf.unsa.rpr.GeografijaDAO;
import ba.etf.unsa.rpr.Grad;
import ba.etf.unsa.rpr.GradController;
import ba.etf.unsa.rpr.Osoba;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.File;
import java.util.ArrayList;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class IspitGradControllerTest {
    Stage theStage;
    GradController ctrl;

    @Start
    public void start(Stage stage) throws Exception {
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();

        GeografijaDAO dao = GeografijaDAO.getInstance();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/grad.fxml"));

        // Ručno dodajemo osobu u spisak osoba
        ArrayList<Osoba> osobe = dao.osobe();
        osobe.add(new Osoba(123, "Abdulah", "Skaka"));

        // Pored konstruktora sa dva parametra dodajemo i konstruktor sa tri parametra
        // kako bismo mogli proslijediti spisak osoba
        ctrl = new GradController(null, dao.drzave(), osobe);
        loader.setController(ctrl);
        Parent root = loader.load();
        stage.setTitle("Grad");
        stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        stage.setResizable(false);
        stage.show();
        stage.toFront();
        theStage = stage;
    }

    @Test
    public void testPoljaPostoje(FxRobot robot) {
        TextField tf = robot.lookup("#fieldGradonacelnik").queryAs(TextField.class);
        assertNotNull(tf);
    }

    @Test
    public void testValidacija(FxRobot robot) {
        robot.clickOn("#fieldGradonacelnik");
        robot.write("abcabc");

        // Klikamo Ok
        robot.clickOn("#btnOk");

        // Nije validno, nema razmaka
        TextField tf = robot.lookup("#fieldGradonacelnik").queryAs(TextField.class);
        assertFalse(poljeValidno(tf));
        assertTrue(poljeNevalidno(tf));

        robot.clickOn("#fieldGradonacelnik");
        robot.press(KeyCode.END).release(KeyCode.END);
        robot.write(" def ghe");

        // Klikamo Ok
        robot.clickOn("#btnOk");

        // Opet nije validno, ima dva razmaka
        assertFalse(poljeValidno(tf));
        assertTrue(poljeNevalidno(tf));

        KeyCode ctrl = KeyCode.CONTROL;
        if (System.getProperty("os.name").equals("Mac OS X"))
            ctrl = KeyCode.COMMAND;
        robot.clickOn("#fieldGradonacelnik");
        robot.press(ctrl).press(KeyCode.A).release(KeyCode.A).release(ctrl);
        robot.write(" def ghe");

        // Klikamo Ok
        robot.clickOn("#btnOk");

        // Opet nije validno, opet ima dva razmaka
        assertFalse(poljeValidno(tf));
        assertTrue(poljeNevalidno(tf));

        robot.clickOn("#fieldGradonacelnik");
        robot.press(ctrl).press(KeyCode.A).release(KeyCode.A).release(ctrl);
        robot.write("ime prezime");

        // Klikamo Ok
        robot.clickOn("#btnOk");

        // Sada je validno
        assertTrue(poljeValidno(tf));
        assertFalse(poljeNevalidno(tf));
    }

    private boolean poljeValidno(Control tf) {
        Background bg = tf.getBackground();
        boolean colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("adff2f"))
                colorFound = true;
        return colorFound;
    }

    private boolean poljeNevalidno(Control tf) {
        Background bg = tf.getBackground();
        boolean colorFound = false;
        for (BackgroundFill bf : bg.getFills())
            if (bf.getFill().toString().contains("ffb6c1"))
                colorFound = true;
        return colorFound;
    }

    @Test
    public void testDodajGradonacelnika(FxRobot robot) {
        // Upisujemo grad
        robot.clickOn("#fieldNaziv");
        robot.write("Sarajevo");
        robot.clickOn("#fieldBrojStanovnika");
        robot.write("350000");
        robot.clickOn("#choiceDrzava");
        robot.clickOn("Francuska");
        robot.clickOn("#fieldGradonacelnik");
        robot.write("Abdulah Skaka");

        robot.clickOn("#btnOk");

        Grad sarajevo = ctrl.getGrad();
        assertEquals("Sarajevo", sarajevo.getNaziv());
        assertEquals(350000, sarajevo.getBrojStanovnika());
        assertEquals("Francuska", sarajevo.getDrzava().getNaziv());

        // Pošto smo unijeli ime postojećeg gradonačelnika, on treba biti pronađen
        // u nizu osoba sa IDom 123
        assertEquals("Abdulah", sarajevo.getGradonacelnik().getIme());
        assertEquals("Skaka", sarajevo.getGradonacelnik().getPrezime());
        assertEquals(123, sarajevo.getGradonacelnik().getId());
    }


    @Test
    public void testDodajNovogGradonacelnika(FxRobot robot) {
        // Upisujemo grad
        robot.clickOn("#fieldNaziv");
        robot.write("Rejkjavik");
        robot.clickOn("#fieldBrojStanovnika");
        robot.write("360000");
        robot.clickOn("#choiceDrzava");
        robot.clickOn("Austrija");
        robot.clickOn("#fieldGradonacelnik");
        robot.write("Ime Prezime");

        robot.clickOn("#btnOk");

        Grad sarajevo = ctrl.getGrad();
        assertEquals("Rejkjavik", sarajevo.getNaziv());
        assertEquals(360000, sarajevo.getBrojStanovnika());
        assertEquals("Austrija", sarajevo.getDrzava().getNaziv());

        assertEquals("Ime", sarajevo.getGradonacelnik().getIme());
        assertEquals("Prezime", sarajevo.getGradonacelnik().getPrezime());
    }
}