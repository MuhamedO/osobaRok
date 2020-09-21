package ba.unsa.etf.rpr;

import ba.etf.unsa.rpr.GeografijaDAO;
import ba.etf.unsa.rpr.GlavnaController;
import ba.etf.unsa.rpr.Grad;
import ba.etf.unsa.rpr.Osoba;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class IspitGlavnaTest {
    Stage theStage;
    GlavnaController ctrl;

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/glavna.fxml"));
        ctrl = new GlavnaController();
        loader.setController(ctrl);
        Parent root = loader.load();
        stage.setTitle("Gradovi svijeta");
        stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        stage.setResizable(false);
        stage.show();

        stage.toFront();

        theStage = stage;
    }

    @Test
    public void testDodajGrad(FxRobot robot) {
        ctrl.resetujBazu();

        // Otvaranje forme za dodavanje
        robot.clickOn("#btnDodajGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Ovo moramo jer robot klika po glavnoj formi umjesto po ovoj
        Platform.runLater(() -> theStage.hide());

        // Postoji li fieldNaziv
        robot.clickOn("#fieldNaziv");
        robot.write("Sarajevo");

        robot.clickOn("#fieldBrojStanovnika");
        robot.write("350000");

        robot.clickOn("#choiceDrzava");
        robot.clickOn("Francuska");

        robot.clickOn("#fieldGradonacelnik");
        robot.write("Abdulah Skaka");

        // Klik na dugme Ok
        robot.clickOn("#btnOk");

        // Da li je Sarajevo dodano u bazu?
        GeografijaDAO dao = GeografijaDAO.getInstance();
        assertEquals(6, dao.gradovi().size());

        Grad sarajevo = null;
        for (Grad grad : dao.gradovi())
            if (grad.getNaziv().equals("Sarajevo"))
                sarajevo = grad;
        assertNotNull(sarajevo);

        assertEquals(350000, sarajevo.getBrojStanovnika());
        assertEquals("Francuska", sarajevo.getDrzava().getNaziv());
        assertNotNull(sarajevo.getGradonacelnik());
        assertEquals("Abdulah", sarajevo.getGradonacelnik().getIme());
        assertEquals("Skaka", sarajevo.getGradonacelnik().getPrezime());

        // Da li je Skaka dodan u listu osoba
        boolean imaSkaka = false;
        for (Osoba o : dao.osobe())
            if (o.getPrezime().equals("Skaka"))
                imaSkaka = true;
        assertTrue(imaSkaka);

        // Ponovo prikazujemo glavnu formu
        Platform.runLater(() -> theStage.show());
    }


    @Test
    public void testIzmijeniGrad(FxRobot robot) {
        ctrl.resetujBazu();

        robot.clickOn("Beč");
        // Otvaranje forme za dodavanje
        robot.clickOn("#btnIzmijeniGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        robot.clickOn("#fieldGradonacelnik");
        robot.write("Michael Ludwig");

        // Klik na dugme Ok
        robot.clickOn("#btnOk");

        // Tražimo Beč u bazi
        GeografijaDAO dao = GeografijaDAO.getInstance();
        Grad bech = dao.nadjiGrad("Beč");

        assertEquals("Michael", bech.getGradonacelnik().getIme());
        assertEquals("Ludwig", bech.getGradonacelnik().getPrezime());
        boolean pronadjen = false;
        for (Osoba o : dao.osobe()) {
            if (o.getPrezime().equals("Ludwig")) pronadjen = true;
        }
        assertTrue(pronadjen);
    }


    @Test
    public void testIstiGradonacelnik(FxRobot robot) {
        // Provjeravamo da li se isti gradonacelnik dodaje X puta u bazu
        ctrl.resetujBazu();

        GeografijaDAO dao = GeografijaDAO.getInstance();
        int brojOsoba = dao.osobe().size();

        robot.clickOn("Pariz");
        // Otvaranje forme za dodavanje
        robot.clickOn("#btnIzmijeniGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        robot.clickOn("#fieldGradonacelnik");
        robot.write("John Smith");
        robot.clickOn("#btnOk");

        robot.clickOn("#btnDodajGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Postoji li fieldNaziv
        robot.clickOn("#fieldNaziv");
        robot.write("Rejkjavik");

        robot.clickOn("#fieldBrojStanovnika");
        robot.write("360000");

        robot.clickOn("#choiceDrzava");
        robot.clickOn("Austrija");

        robot.clickOn("#fieldGradonacelnik");
        robot.write("John Smith");
        robot.clickOn("#btnOk");

        robot.clickOn("Mančester");
        // Otvaranje forme za dodavanje
        robot.clickOn("#btnIzmijeniGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        robot.clickOn("#fieldGradonacelnik");
        robot.write("John Smith");
        robot.clickOn("#btnOk");

        int dodano = dao.osobe().size() - brojOsoba;
        assertEquals(1, dodano);
    }
}