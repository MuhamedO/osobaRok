package ba.unsa.etf.rpr;

import ba.etf.unsa.rpr.Grad;
import ba.etf.unsa.rpr.Osoba;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IspitOsobaTest {
    @Test
    void testOsoba() {
        Osoba o = new Osoba(123, "Meho", "Mehic");
        assertEquals(123, o.getId());
        assertEquals("Meho", o.getIme());
        assertEquals("Mehic", o.getPrezime());
        o.setIme("Mehaga");
        assertEquals("Mehaga", o.getIme());
    }

    @Test
    void testGradonacelnik() {
        Grad sarajevo = new Grad(1, "Sarajevo", 500000, null);
        assertNull(sarajevo.getGradonacelnik());
        Osoba skaka = new Osoba(1, "Abdulah", "Skaka");
        sarajevo.setGradonacelnik(skaka);
        assertEquals("Skaka", sarajevo.getGradonacelnik().getPrezime());
    }
}