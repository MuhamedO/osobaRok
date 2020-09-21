package ba.unsa.etf.rpr;

import ba.etf.unsa.rpr.*;
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

// Testovi vezani za editovanje postojećeg grada
@ExtendWith(ApplicationExtension.class)
public class IspitGradControllerSet {
    Stage theStage;
    GradController ctrl;
    ArrayList<Grad> gradovi;

    @Start
    public void start(Stage stage) throws Exception {
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();

        GeografijaDAO dao = GeografijaDAO.getInstance();
        Drzava francuska = dao.nadjiDrzavu("Francuska");

        ArrayList<Osoba> osobe = dao.osobe();
        Osoba o = new Osoba(456, "John", "Smith");
        osobe.add(o);

        Grad rejkjavik = new Grad(12345, "Rejkjavik", 350000, francuska);
        rejkjavik.setGradonacelnik(o);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/grad.fxml"));
        ctrl = new GradController(rejkjavik, dao.drzave(), osobe);
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
    public void testIspravneVrijednosti(FxRobot robot) {
        TextField tf = robot.lookup("#fieldGradonacelnik").queryAs(TextField.class);
        assertNotNull(tf);
        assertEquals("John Smith", tf.getText());
    }

    @Test
    public void testBrisanjeGradonacelnika(FxRobot robot) {
        KeyCode ctl = KeyCode.CONTROL;
        if (System.getProperty("os.name").equals("Mac OS X"))
            ctl = KeyCode.COMMAND;
        robot.clickOn("#fieldGradonacelnik");
        robot.press(ctl).press(KeyCode.A).release(KeyCode.A).release(ctl);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);

        // Klik na Ok
        robot.clickOn("#btnOk");

        Grad rejkjavik = ctrl.getGrad();
        assertNull(rejkjavik.getGradonacelnik());
    }

    @Test
    public void testValidacija(FxRobot robot) {
        robot.clickOn("#fieldGradonacelnik");
        robot.press(KeyCode.END).release(KeyCode.END);
        robot.write(" drugoprezime");
        robot.clickOn("#btnOk");

        // Nije validno, ima dva razmaka
        TextField tf = robot.lookup("#fieldGradonacelnik").queryAs(TextField.class);
        assertFalse(poljeValidno(tf));
        assertTrue(poljeNevalidno(tf));

        KeyCode ctl = KeyCode.CONTROL;
        if (System.getProperty("os.name").equals("Mac OS X"))
            ctl = KeyCode.COMMAND;
        robot.clickOn("#fieldGradonacelnik");
        robot.press(ctl).press(KeyCode.A).release(KeyCode.A).release(ctl);
        robot.write("ime");
        robot.clickOn("#btnOk");

        // Nije validno, nema razmaka
        assertFalse(poljeValidno(tf));
        assertTrue(poljeNevalidno(tf));

        robot.clickOn("#fieldGradonacelnik");
        robot.press(KeyCode.END).release(KeyCode.END);
        robot.write(" prezime");

        // Brišemo ime grada da se forma ne bi zatvorila
        robot.clickOn("#fieldNaziv");
        robot.press(ctl).press(KeyCode.A).release(KeyCode.A).release(ctl);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);

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
    public void testIzmjenaGradonacelnika(FxRobot robot) {
        KeyCode ctl = KeyCode.CONTROL;
        if (System.getProperty("os.name").equals("Mac OS X"))
            ctl = KeyCode.COMMAND;
        robot.clickOn("#fieldGradonacelnik");
        robot.press(ctl).press(KeyCode.A).release(KeyCode.A).release(ctl);
        robot.write("Novi Gradonacelnik");

        // Klik na Ok
        robot.clickOn("#btnOk");

        Grad rejkjavik = ctrl.getGrad();
        assertEquals("Novi", rejkjavik.getGradonacelnik().getIme());
        assertEquals("Gradonacelnik", rejkjavik.getGradonacelnik().getPrezime());
    }
}
