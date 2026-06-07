package testing;

import org.junit.jupiter.api.*;
import tubes_a11.Notifikasi;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test untuk class Notifikasi
 * Total: 15 skenario testing
 */
public class NotifikasiTest {

    private Notifikasi notifikasi;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        notifikasi = new Notifikasi();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Test 1: Constructor membuat instance Notifikasi")
    public void testConstructor() {
        // Membuat instance notifikasi
        assertNotNull(notifikasi);
        System.out.println("Skenario 1: pembuatan objek notifikasi berhasil");
    }

    @Test
    @DisplayName("Test 2: kirimPeringatan() ketika screen time di bawah limit")
    public void testKirimPeringatanDiBawahLimit() {
        // Screen time 100, limit 150 (masih aman)
        notifikasi.kirimPeringatan(100, 150);
        assertEquals("Screen time masih dalam batas yang ditentukan.", notifikasi.getPesan());
        System.out.println("Skenario 2: pengiriman pesan status aman berhasil");
    }

    @Test
    @DisplayName("Test 3: kirimPeringatan() ketika screen time sama dengan limit")
    public void testKirimPeringatanSamaDenganLimit() {
        // Screen time 120 = limit 120
        notifikasi.kirimPeringatan(120, 120);
        assertEquals("Anda sudah mencapai batas screen time hari ini.", notifikasi.getPesan());
        System.out.println("Skenario 3: pengiriman pesan status tepat batas berhasil");
    }

    @Test
    @DisplayName("Test 4: kirimPeringatan() ketika screen time melebihi limit")
    public void testKirimPeringatanMelebihiLimit() {
        // Screen time 200 melebihi limit 150
        notifikasi.kirimPeringatan(200, 150);
        assertEquals("Anda sudah melebihi batas screen time!", notifikasi.getPesan());
        System.out.println("Skenario 4: pengiriman pesan status melebihi batas berhasil");
    }

    @Test
    @DisplayName("Test 5: getPesan() sebelum kirimPeringatan() dipanggil")
    public void testGetPesanSebelumKirim() {
        // Pesan masih null sebelum kirimPeringatan
        assertNull(notifikasi.getPesan());
        System.out.println("Skenario 5: pengecekan pesan kosong sebelum kirim berhasil");
    }

    @Test
    @DisplayName("Test 6: setPesan() mengubah pesan notifikasi")
    public void testSetPesan() {
        // Mengubah pesan secara manual
        notifikasi.setPesan("Pesan custom");
        assertEquals("Pesan custom", notifikasi.getPesan());
        System.out.println("Skenario 6: pengubahan isi pesan kustom berhasil");
    }

    @Test
    @DisplayName("Test 7: kirimPeringatan() mencetak ke console")
    public void testKirimPeringatanPrintToConsole() {
        // Verifikasi output ke console
        notifikasi.kirimPeringatan(100, 120);
        String output = outputStream.toString();
        assertTrue(output.contains("Notifikasi:"));
        System.out.println("Skenario 7: pencetakan log notifikasi ke konsol berhasil");
    }

    @Test
    @DisplayName("Test 8: Screen time 0 dengan limit 100")
    public void testScreenTimeNol() {
        // Test edge case screen time 0
        notifikasi.kirimPeringatan(0, 100);
        assertEquals("Screen time masih dalam batas yang ditentukan.", notifikasi.getPesan());
        System.out.println("Skenario 8: pengecekan status notifikasi screen time 0 menit berhasil");
    }

    @Test
    @DisplayName("Test 9: Screen time sangat besar melebihi limit")
    public void testScreenTimeSangatBesar() {
        // Test dengan nilai ekstrem
        notifikasi.kirimPeringatan(1000, 100);
        assertEquals("Anda sudah melebihi batas screen time!", notifikasi.getPesan());
        System.out.println("Skenario 9: pengecekan status notifikasi durasi ekstrim berhasil");
    }

    @Test
    @DisplayName("Test 10: Limit 0 dengan screen time 0")
    public void testLimitDanScreenTimeNol() {
        // Test boundary kedua nilai 0
        notifikasi.kirimPeringatan(0, 0);
        assertEquals("Anda sudah mencapai batas screen time hari ini.", notifikasi.getPesan());
        System.out.println("Skenario 10: pengecekan status batas dan penggunaan 0 menit berhasil");
    }

    @Test
    @DisplayName("Test 11: Screen time 1 menit melebihi limit 0")
    public void testScreenTime1MenitLimit0() {
        // Test boundary 1 vs 0
        notifikasi.kirimPeringatan(1, 0);
        assertEquals("Anda sudah melebihi batas screen time!", notifikasi.getPesan());
        System.out.println("Skenario 11: pengecekan status penggunaan 1 menit batas 0 menit berhasil");
    }

    @Test
    @DisplayName("Test 12: Multiple calls kirimPeringatan() update pesan")
    public void testMultipleCallsUpdatePesan() {
        // Pesan berubah setiap kali dipanggil
        notifikasi.kirimPeringatan(50, 100);
        String pesan1 = notifikasi.getPesan();
        
        notifikasi.kirimPeringatan(150, 100);
        String pesan2 = notifikasi.getPesan();
        
        assertNotEquals(pesan1, pesan2);
        System.out.println("Skenario 12: pembaruan isi pesan pada beberapa notifikasi berhasil");
    }

    @Test
    @DisplayName("Test 13: Screen time tepat 1 di bawah limit")
    public void testScreenTimeTepat1DiBawahLimit() {
        // Test boundary limit-1
        notifikasi.kirimPeringatan(119, 120);
        assertEquals("Screen time masih dalam batas yang ditentukan.", notifikasi.getPesan());
        System.out.println("Skenario 13: pengecekan batas aman (1 menit di bawah limit) berhasil");
    }

    @Test
    @DisplayName("Test 14: Screen time tepat 1 di atas limit")
    public void testScreenTimeTepat1DiAtasLimit() {
        // Test boundary limit+1
        notifikasi.kirimPeringatan(121, 120);
        assertEquals("Anda sudah melebihi batas screen time!", notifikasi.getPesan());
        System.out.println("Skenario 14: pengecekan batas bahaya (1 menit di atas limit) berhasil");
    }

    @Test
    @DisplayName("Test 15: setPesan() dengan string kosong")
    public void testSetPesanKosong() {
        // Test dengan string kosong
        notifikasi.setPesan("");
        assertEquals("", notifikasi.getPesan());
        System.out.println("Skenario 15: pengosongan pesan notifikasi berhasil");
    }
}
