package ba.unsa.etf.rpr;

import ba.etf.unsa.rpr.Drzava;
import ba.etf.unsa.rpr.GeografijaDAO;
import ba.etf.unsa.rpr.Grad;
import ba.etf.unsa.rpr.Osoba;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class IspitDAOTest {

    @BeforeEach
    void regenerisiBazu() {
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();
    }

    @Test
    void testDodajOsobu() {
        GeografijaDAO dao = GeografijaDAO.getInstance();
        Osoba meho = new Osoba(0, "Meho", "Mehic");
        dao.dodajOsobu(meho);

        boolean dodato = false;
        for(Osoba o : dao.osobe())
            if (o.getIme().equals("Meho") && o.getPrezime().equals("Mehic"))
                dodato = true;
        assertTrue(dodato);
    }

    @Test
    void testGradonacelnik() {
        GeografijaDAO dao = GeografijaDAO.getInstance();
        Grad london = dao.nadjiGrad("London");
        // Po defaultu, nijedan grad nema gradonacelnika
        assertNull(london.getGradonacelnik());

        Osoba sadik = new Osoba(0, "Sadiq", "Khan");
        london.setGradonacelnik(sadik);
        dao.izmijeniGrad(london);

        Grad l2 = dao.nadjiGrad("London");
        assertNotNull(l2.getGradonacelnik());
        assertEquals("Sadiq", l2.getGradonacelnik().getIme());
        assertEquals("Khan", l2.getGradonacelnik().getPrezime());

        // Osoba Sadiq Khan je dodata u bazu
        boolean dodato = false;
        for(Osoba o : dao.osobe())
            if (o.getIme().equals("Sadiq") && o.getPrezime().equals("Khan"))
                dodato = true;
        assertTrue(dodato);
    }

    @Test
    void testGradonacelnikBrisanje() {
        GeografijaDAO dao = GeografijaDAO.getInstance();

        // Dodajemo gradonacelnika Londonu. Sigurno radi (testGradonacelnik)
        Grad london = dao.nadjiGrad("London");
        assertNull(london.getGradonacelnik());
        Osoba sadik = new Osoba(0, "Sadiq", "Khan");
        london.setGradonacelnik(sadik);
        dao.izmijeniGrad(london);

        // Brisemo gradonacelnika
        Grad l2 = dao.nadjiGrad("London");
        assertNotNull(l2.getGradonacelnik());
        l2.setGradonacelnik(null);
        dao.izmijeniGrad(l2);

        Grad l3 = dao.nadjiGrad("London");
        assertNull(l3.getGradonacelnik());
    }

    @Test
    void testGradonacelnikNoviGrad() {
        GeografijaDAO dao = GeografijaDAO.getInstance();
        // Najprije kreiramo grad Sarajevo
        Drzava vb = dao.nadjiDrzavu("Velika Britanija");
        Grad sarajevo = new Grad(0, "Sarajevo", 350000, vb);
        Osoba skaka = new Osoba(0, "Abdulah", "Skaka");
        sarajevo.setGradonacelnik(skaka);
        dao.dodajGrad(sarajevo);

        // Da li je grad dodat u bazu?
        Grad s2 = dao.nadjiGrad("Sarajevo");
        assertNotNull(s2);

        // Da li je drzava dodata u bazu?
        assertEquals("Skaka", s2.getGradonacelnik().getPrezime());

        boolean dodato = false;
        for(Osoba o : dao.osobe())
            if (o.getIme().equals("Abdulah") && o.getPrezime().equals("Skaka"))
                dodato = true;
        assertTrue(dodato);
    }

    @Test
    void testBazaDirekt() {
        // Test koji direktno pristupa bazi zaobilazeći DAO klasu

        // Regenerišemo bazu ako je promijenjena prethodnim testovima
        GeografijaDAO dao = GeografijaDAO.getInstance();

        // Sad ćemo se opet diskonektovati jer radimo sa bazom direktno
        GeografijaDAO.removeInstance();

        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:baza.db");
            try {
                PreparedStatement osobaUpit = conn.prepareStatement("SELECT id, ime, prezime FROM osoba");
                osobaUpit.execute();
                conn.close();
            } catch (SQLException e) {
                fail("Tabela osoba ne postoji ili ne sadrži kolone id, ime i prezime");
            }
        } catch (SQLException e) {
            fail("Datoteka sa bazom ne postoji ili je nedostupna");
        }
    }

}
