package testing;

import org.junit.jupiter.api.*;
import tubes_a11.AktivitasDigital;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test untuk class AktivitasDigital
 * Total: 15 skenario testing
 */
public class AktivitasDigitalTest {

    @Test
    @DisplayName("Test 1: Constructor dengan parameter valid")
    public void testConstructorValid() {
        // Membuat aktivitas dengan nama Instagram, durasi 60, batas 120
        AktivitasDigital aktivitas = new AktivitasDigital("Instagram", 60, 120, LocalDate.now());
        assertNotNull(aktivitas);
        assertEquals("Instagram", aktivitas.getNamaAplikasi());
        assertEquals(60, aktivitas.getDurasiMenit());
        assertEquals(120, aktivitas.getBatasDurasi());
    }

    @Test
    @DisplayName("Test 2: melebihiBatas() ketika durasi melebihi batas")
    public void testMelebihiBatasTrue() {
        // Durasi 150 menit melebihi batas 120 menit
        AktivitasDigital aktivitas = new AktivitasDigital("TikTok", 150, 120, LocalDate.now());
        assertTrue(aktivitas.melebihiBatas());
    }

    @Test
    @DisplayName("Test 3: melebihiBatas() ketika durasi tidak melebihi batas")
    public void testMelebihiBatasFalse() {
        // Durasi 90 menit tidak melebihi batas 120 menit
        AktivitasDigital aktivitas = new AktivitasDigital("YouTube", 90, 120, LocalDate.now());
        assertFalse(aktivitas.melebihiBatas());
    }

    @Test
    @DisplayName("Test 4: melebihiBatas() ketika durasi sama dengan batas")
    public void testMelebihiBatasExact() {
        // Durasi 120 sama dengan batas 120
        AktivitasDigital aktivitas = new AktivitasDigital("Facebook", 120, 120, LocalDate.now());
        assertFalse(aktivitas.melebihiBatas());
    }

    @Test
    @DisplayName("Test 5: getNamaAplikasi() mengembalikan nama yang benar")
    public void testGetNamaAplikasi() {
        // Memasukkan nama aplikasi WhatsApp
        AktivitasDigital aktivitas = new AktivitasDigital("WhatsApp", 30, 60, LocalDate.now());
        assertEquals("WhatsApp", aktivitas.getNamaAplikasi());
    }

    @Test
    @DisplayName("Test 6: getDurasiMenit() mengembalikan durasi yang benar")
    public void testGetDurasiMenit() {
        // Memasukkan durasi 45 menit
        AktivitasDigital aktivitas = new AktivitasDigital("Twitter", 45, 60, LocalDate.now());
        assertEquals(45, aktivitas.getDurasiMenit());
    }

    @Test
    @DisplayName("Test 7: getBatasDurasi() mengembalikan batas yang benar")
    public void testGetBatasDurasi() {
        // Memasukkan batas durasi 150 menit
        AktivitasDigital aktivitas = new AktivitasDigital("Spotify", 100, 150, LocalDate.now());
        assertEquals(150, aktivitas.getBatasDurasi());
    }

    @Test
    @DisplayName("Test 8: getTanggal() mengembalikan tanggal yang benar")
    public void testGetTanggal() {
        // Memasukkan tanggal hari ini
        LocalDate today = LocalDate.now();
        AktivitasDigital aktivitas = new AktivitasDigital("Netflix", 120, 180, today);
        assertEquals(today, aktivitas.getTanggal());
    }

    @Test
    @DisplayName("Test 9: Aktivitas dengan durasi 0 menit")
    public void testDurasiNol() {
        // Test edge case dengan durasi 0
        AktivitasDigital aktivitas = new AktivitasDigital("Telegram", 0, 60, LocalDate.now());
        assertEquals(0, aktivitas.getDurasiMenit());
        assertFalse(aktivitas.melebihiBatas());
    }

    @Test
    @DisplayName("Test 10: Aktivitas dengan batas 0 dan durasi 0")
    public void testBatasDanDurasiNol() {
        // Test edge case batas dan durasi keduanya 0
        AktivitasDigital aktivitas = new AktivitasDigital("TestApp", 0, 0, LocalDate.now());
        assertFalse(aktivitas.melebihiBatas());
    }

    @Test
    @DisplayName("Test 11: Aktivitas dengan durasi sangat besar")
    public void testDurasiSangatBesar() {
        // Test dengan durasi ekstrem 1000 menit
        AktivitasDigital aktivitas = new AktivitasDigital("Gaming", 1000, 120, LocalDate.now());
        assertTrue(aktivitas.melebihiBatas());
        assertEquals(1000, aktivitas.getDurasiMenit());
    }

    @Test
    @DisplayName("Test 12: Aktivitas dengan nama aplikasi kosong")
    public void testNamaAplikasiKosong() {
        // Test edge case dengan nama aplikasi kosong
        AktivitasDigital aktivitas = new AktivitasDigital("", 60, 120, LocalDate.now());
        assertEquals("", aktivitas.getNamaAplikasi());
    }

    @Test
    @DisplayName("Test 13: Aktivitas dengan tanggal kemarin")
    public void testTanggalKemarin() {
        // Memasukkan tanggal kemarin
        LocalDate yesterday = LocalDate.now().minusDays(1);
        AktivitasDigital aktivitas = new AktivitasDigital("Reddit", 90, 120, yesterday);
        assertEquals(yesterday, aktivitas.getTanggal());
    }

    @Test
    @DisplayName("Test 14: Aktivitas dengan durasi 1 menit melebihi batas 0")
    public void testDurasiMinimalMelebihiBatas() {
        // Test boundary: 1 menit melebihi batas 0
        AktivitasDigital aktivitas = new AktivitasDigital("AppTest", 1, 0, LocalDate.now());
        assertTrue(aktivitas.melebihiBatas());
    }

    @Test
    @DisplayName("Test 15: Multiple instances dengan data berbeda")
    public void testMultipleInstances() {
        // Membuat 2 aktivitas berbeda
        AktivitasDigital akt1 = new AktivitasDigital("App1", 50, 100, LocalDate.now());
        AktivitasDigital akt2 = new AktivitasDigital("App2", 150, 100, LocalDate.now());
        
        assertFalse(akt1.melebihiBatas());
        assertTrue(akt2.melebihiBatas());
        assertNotEquals(akt1.getNamaAplikasi(), akt2.getNamaAplikasi());
    }
}
