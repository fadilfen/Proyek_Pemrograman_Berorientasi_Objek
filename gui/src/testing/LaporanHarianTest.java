package testing;

import org.junit.jupiter.api.*;
import tubes_a11.LaporanHarian;
import tubes_a11.AktivitasDigital;
import java.time.LocalDate;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test untuk class LaporanHarian
 * Total: 15 skenario testing
 */
public class LaporanHarianTest {

    @Test
    @DisplayName("Test 1: Constructor dengan parameter valid")
    public void testConstructorValid() {
        // Membuat laporan dengan durasi 120, skor 85
        ArrayList<AktivitasDigital> aktivitasList = new ArrayList<>();
        LaporanHarian laporan = new LaporanHarian(120, 85, aktivitasList, "John Doe");
        assertNotNull(laporan);
    }

    @Test
    @DisplayName("Test 2: generateLaporan() dengan list aktivitas kosong")
    public void testGenerateLaporanListKosong() {
        // Generate laporan tanpa aktivitas
        ArrayList<AktivitasDigital> aktivitasList = new ArrayList<>();
        LaporanHarian laporan = new LaporanHarian(0, 100, aktivitasList, "Jane Doe");
        String hasil = laporan.generateLaporan();
        
        assertTrue(hasil.contains("Jane Doe"));
        assertTrue(hasil.contains("Total Screen Time : 0 menit"));
        assertTrue(hasil.contains("Skor Harian       : 100"));
    }

    @Test
    @DisplayName("Test 3: generateLaporan() dengan 1 aktivitas")
    public void testGenerateLaporan1Aktivitas() {
        // Menambahkan 1 aktivitas Instagram
        ArrayList<AktivitasDigital> aktivitasList = new ArrayList<>();
        aktivitasList.add(new AktivitasDigital("Instagram", 60, 120, LocalDate.now()));
        
        LaporanHarian laporan = new LaporanHarian(60, 90, aktivitasList, "Alice");
        String hasil = laporan.generateLaporan();
        
        assertTrue(hasil.contains("Instagram"));
        assertTrue(hasil.contains("60 menit"));
    }

    @Test
    @DisplayName("Test 4: generateLaporan() dengan multiple aktivitas")
    public void testGenerateLaporanMultipleAktivitas() {
        // Menambahkan 3 aktivitas berbeda
        ArrayList<AktivitasDigital> aktivitasList = new ArrayList<>();
        aktivitasList.add(new AktivitasDigital("TikTok", 30, 60, LocalDate.now()));
        aktivitasList.add(new AktivitasDigital("YouTube", 45, 90, LocalDate.now()));
        aktivitasList.add(new AktivitasDigital("WhatsApp", 15, 30, LocalDate.now()));
        
        LaporanHarian laporan = new LaporanHarian(90, 75, aktivitasList, "Bob");
        String hasil = laporan.generateLaporan();
        
        assertTrue(hasil.contains("TikTok"));
        assertTrue(hasil.contains("YouTube"));
        assertTrue(hasil.contains("WhatsApp"));
    }

    @Test
    @DisplayName("Test 5: Status 'Sehat' ketika skor >= 70")
    public void testStatusSehat() {
        // Skor 70 menunjukkan status sehat
        ArrayList<AktivitasDigital> aktivitasList = new ArrayList<>();
        LaporanHarian laporan = new LaporanHarian(60, 70, aktivitasList, "Charlie");
        String hasil = laporan.generateLaporan();
        
        assertTrue(hasil.contains("Status            : Sehat"));
    }

    @Test
    @DisplayName("Test 6: Status 'Kurangi Screen Time' ketika skor < 70")
    public void testStatusKurangiScreenTime() {
        // Skor 50 menunjukkan harus kurangi screen time
        ArrayList<AktivitasDigital> aktivitasList = new ArrayList<>();
        LaporanHarian laporan = new LaporanHarian(180, 50, aktivitasList, "David");
        String hasil = laporan.generateLaporan();
        
        assertTrue(hasil.contains("Status            : Kurangi Screen Time"));
    }

    @Test
    @DisplayName("Test 7: Laporan menampilkan nama user")
    public void testLaporanMenampilkanNamaUser() {
        // Memasukkan nama user Emma
        ArrayList<AktivitasDigital> aktivitasList = new ArrayList<>();
        LaporanHarian laporan = new LaporanHarian(100, 80, aktivitasList, "Emma");
        String hasil = laporan.generateLaporan();
        
        assertTrue(hasil.contains("Nama User         : Emma"));
    }

    @Test
    @DisplayName("Test 8: Laporan menampilkan total durasi")
    public void testLaporanMenampilkanTotalDurasi() {
        // Verifikasi total durasi ditampilkan
        ArrayList<AktivitasDigital> aktivitasList = new ArrayList<>();
        LaporanHarian laporan = new LaporanHarian(250, 60, aktivitasList, "Frank");
        String hasil = laporan.generateLaporan();
        
        assertTrue(hasil.contains("Total Screen Time : 250 menit"));
    }

    @Test
    @DisplayName("Test 9: Laporan menampilkan skor harian")
    public void testLaporanMenampilkanSkorHarian() {
        // Verifikasi skor harian ditampilkan
        ArrayList<AktivitasDigital> aktivitasList = new ArrayList<>();
        LaporanHarian laporan = new LaporanHarian(120, 85, aktivitasList, "Grace");
        String hasil = laporan.generateLaporan();
        
        assertTrue(hasil.contains("Skor Harian       : 85"));
    }

    @Test
    @DisplayName("Test 10: Laporan menampilkan tanggal dari aktivitas pertama")
    public void testLaporanMenampilkanTanggal() {
        // Menampilkan tanggal hari ini
        ArrayList<AktivitasDigital> aktivitasList = new ArrayList<>();
        LocalDate today = LocalDate.now();
        aktivitasList.add(new AktivitasDigital("Facebook", 40, 60, today));
        
        LaporanHarian laporan = new LaporanHarian(40, 90, aktivitasList, "Henry");
        String hasil = laporan.generateLaporan();
        
        assertTrue(hasil.contains("Tanggal           : " + today));
    }

    @Test
    @DisplayName("Test 11: Laporan dengan skor 0")
    public void testLaporanSkor0() {
        // Test skor 0 menunjukkan tidak sehat
        ArrayList<AktivitasDigital> aktivitasList = new ArrayList<>();
        LaporanHarian laporan = new LaporanHarian(500, 0, aktivitasList, "Ivy");
        String hasil = laporan.generateLaporan();
        
        assertTrue(hasil.contains("Skor Harian       : 0"));
        assertTrue(hasil.contains("Kurangi Screen Time"));
    }

    @Test
    @DisplayName("Test 12: Laporan dengan skor 100")
    public void testLaporanSkor100() {
        // Test skor sempurna 100
        ArrayList<AktivitasDigital> aktivitasList = new ArrayList<>();
        LaporanHarian laporan = new LaporanHarian(30, 100, aktivitasList, "Jack");
        String hasil = laporan.generateLaporan();
        
        assertTrue(hasil.contains("Skor Harian       : 100"));
        assertTrue(hasil.contains("Sehat"));
    }

    @Test
    @DisplayName("Test 13: Laporan boundary skor 69 (tidak sehat)")
    public void testLaporanBoundarySkor69() {
        // Test boundary skor 69 (di bawah 70)
        ArrayList<AktivitasDigital> aktivitasList = new ArrayList<>();
        LaporanHarian laporan = new LaporanHarian(150, 69, aktivitasList, "Kate");
        String hasil = laporan.generateLaporan();
        
        assertTrue(hasil.contains("Kurangi Screen Time"));
    }

    @Test
    @DisplayName("Test 14: Laporan menampilkan header")
    public void testLaporanMenampilkanHeader() {
        // Verifikasi header laporan ditampilkan
        ArrayList<AktivitasDigital> aktivitasList = new ArrayList<>();
        LaporanHarian laporan = new LaporanHarian(100, 80, aktivitasList, "Leo");
        String hasil = laporan.generateLaporan();
        
        assertTrue(hasil.contains("=== LAPORAN HARIAN ==="));
    }

    @Test
    @DisplayName("Test 15: Laporan menampilkan separator")
    public void testLaporanMenampilkanSeparator() {
        // Verifikasi separator laporan ditampilkan
        ArrayList<AktivitasDigital> aktivitasList = new ArrayList<>();
        LaporanHarian laporan = new LaporanHarian(100, 80, aktivitasList, "Mia");
        String hasil = laporan.generateLaporan();
        
        assertTrue(hasil.contains("------------------------------"));
    }
}
