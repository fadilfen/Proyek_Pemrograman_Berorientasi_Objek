package testing;

import org.junit.jupiter.api.*;
import tubes_a11.AppTimer;
import java.time.LocalDate;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test untuk class AppTimer
 * Total: 15 skenario testing
 */
public class AppTimerTest {

    @Test
    @DisplayName("Test 1: Constructor dengan parameter valid")
    public void testConstructorValid() {
        // Membuat timer dengan nama Instagram, durasi 60 menit
        AppTimer timer = new AppTimer(1, "Instagram", 60, LocalDate.now(), LocalTime.of(10, 0));
        assertNotNull(timer);
        assertEquals("Instagram", timer.getAppName());
        assertEquals(60, timer.getDurationMinutes());
        System.out.println("Skenario 1: pembuatan timer aplikasi berhasil");
    }

    @Test
    @DisplayName("Test 2: getRemainingSeconds() pada timer baru")
    public void testGetRemainingSecondsInitial() {
        // Timer 30 menit = 1800 detik
        AppTimer timer = new AppTimer(1, "TikTok", 30, LocalDate.now(), LocalTime.of(10, 0));
        assertEquals(1800, timer.getRemainingSeconds());
        System.out.println("Skenario 2: mendapatkan sisa detik awal timer berhasil");
    }

    @Test
    @DisplayName("Test 3: decrementRemainingSeconds() mengurangi 1 detik")
    public void testDecrementRemainingSeconds() {
        // Mengurangi 1 detik dari waktu tersisa
        AppTimer timer = new AppTimer(1, "YouTube", 10, LocalDate.now(), LocalTime.of(10, 0));
        long initial = timer.getRemainingSeconds();
        timer.decrementRemainingSeconds();
        assertEquals(initial - 1, timer.getRemainingSeconds());
        System.out.println("Skenario 3: pengurangan sisa waktu timer berhasil");
    }

    @Test
    @DisplayName("Test 4: decrementRemainingSeconds() tidak negatif")
    public void testDecrementNotNegative() {
        // Pastikan waktu tidak jadi negatif
        AppTimer timer = new AppTimer(1, "Facebook", 0, LocalDate.now(), LocalTime.of(10, 0));
        timer.decrementRemainingSeconds();
        assertEquals(0, timer.getRemainingSeconds());
        System.out.println("Skenario 4: pencegahan sisa waktu bernilai negatif berhasil");
    }

    @Test
    @DisplayName("Test 5: getSimulatedRemainingTimeString() dengan waktu tersisa")
    public void testSimulatedRemainingTimeString() {
        // Menampilkan waktu tersisa dalam format string
        AppTimer timer = new AppTimer(1, "WhatsApp", 2, LocalDate.now(), LocalTime.of(10, 0));
        String timeStr = timer.getSimulatedRemainingTimeString();
        assertTrue(timeStr.contains("tersisa"));
        System.out.println("Skenario 5: mendapatkan format teks sisa waktu berhasil");
    }

    @Test
    @DisplayName("Test 6: getSimulatedRemainingTimeString() ketika waktu habis")
    public void testSimulatedRemainingTimeStringExpired() {
        // Menampilkan pesan waktu habis
        AppTimer timer = new AppTimer(1, "Twitter", 0, LocalDate.now(), LocalTime.of(10, 0));
        assertEquals("Waktu habis", timer.getSimulatedRemainingTimeString());
        System.out.println("Skenario 6: mendapatkan teks pemberitahuan waktu habis berhasil");
    }

    @Test
    @DisplayName("Test 7: isSimulatedSafe() ketika waktu aman (>20%)")
    public void testIsSimulatedSafeTrue() {
        // Waktu tersisa masih banyak (aman)
        AppTimer timer = new AppTimer(1, "Spotify", 60, LocalDate.now(), LocalTime.of(10, 0));
        assertTrue(timer.isSimulatedSafe());
        System.out.println("Skenario 7: deteksi status waktu aman (>20%) berhasil");
    }

    @Test
    @DisplayName("Test 8: isSimulatedSafe() ketika waktu berbahaya (<20%)")
    public void testIsSimulatedSafeFalse() {
        // Kurangi waktu hingga di bawah 20%
        AppTimer timer = new AppTimer(1, "Netflix", 60, LocalDate.now(), LocalTime.of(10, 0));
        for (int i = 0; i < 3000; i++) {
            timer.decrementRemainingSeconds();
        }
        assertFalse(timer.isSimulatedSafe());
        System.out.println("Skenario 8: deteksi status waktu kritis (<20%) berhasil");
    }

    @Test
    @DisplayName("Test 9: getStartTime() mengembalikan waktu mulai yang benar")
    public void testGetStartTime() {
        // Memasukkan start time 14:30
        LocalTime startTime = LocalTime.of(14, 30);
        AppTimer timer = new AppTimer(1, "Gaming", 90, LocalDate.now(), startTime);
        assertEquals(startTime, timer.getStartTime());
        System.out.println("Skenario 9: mendapatkan jam mulai akses berhasil");
    }

    @Test
    @DisplayName("Test 10: getEndTime() dihitung dari startTime + duration")
    public void testGetEndTime() {
        // Start 10:00 + 60 menit = end 11:00
        LocalTime startTime = LocalTime.of(10, 0);
        AppTimer timer = new AppTimer(1, "Discord", 60, LocalDate.now(), startTime);
        LocalTime expectedEnd = LocalTime.of(11, 0);
        assertEquals(expectedEnd, timer.getEndTime());
        System.out.println("Skenario 10: perhitungan jam selesai akses berhasil");
    }

    @Test
    @DisplayName("Test 11: isTracking() default false pada constructor baru")
    public void testIsTrackingDefault() {
        // Status tracking default adalah false
        AppTimer timer = new AppTimer(1, "Telegram", 30, LocalDate.now(), LocalTime.of(10, 0));
        assertFalse(timer.isTracking());
        System.out.println("Skenario 11: mendapatkan status default pelacakan berhasil");
    }

    @Test
    @DisplayName("Test 12: setTracking() mengubah status tracking")
    public void testSetTracking() {
        // Mengubah status tracking menjadi true
        AppTimer timer = new AppTimer(1, "Reddit", 45, LocalDate.now(), LocalTime.of(10, 0));
        timer.setTracking(true);
        assertTrue(timer.isTracking());
        System.out.println("Skenario 12: pengaktifan status pelacakan berhasil");
    }

    @Test
    @DisplayName("Test 13: getStartTimeString() format HH:mm")
    public void testGetStartTimeString() {
        // Format waktu 09:05 menjadi string
        AppTimer timer = new AppTimer(1, "Pinterest", 30, LocalDate.now(), LocalTime.of(9, 5));
        assertEquals("09:05", timer.getStartTimeString());
        System.out.println("Skenario 13: mendapatkan format teks jam mulai berhasil");
    }

    @Test
    @DisplayName("Test 14: getEndTimeString() format HH:mm")
    public void testGetEndTimeString() {
        // Start 09:30 + 30 menit = end 10:00
        AppTimer timer = new AppTimer(1, "Snapchat", 30, LocalDate.now(), LocalTime.of(9, 30));
        assertEquals("10:00", timer.getEndTimeString());
        System.out.println("Skenario 14: mendapatkan format teks jam selesai berhasil");
    }

    @Test
    @DisplayName("Test 15: Constructor lengkap dengan semua parameter")
    public void testConstructorFull() {
        // Membuat timer dengan constructor lengkap
        LocalDate date = LocalDate.now();
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(9, 30);
        
        AppTimer timer = new AppTimer(100, 5, "Chrome", 90, date, start, end, true, 5400);
        
        assertEquals(100, timer.getId());
        assertEquals(5, timer.getChildId());
        assertEquals("Chrome", timer.getAppName());
        assertEquals(90, timer.getDurationMinutes());
        assertTrue(timer.isTracking());
        assertEquals(5400, timer.getRemainingSeconds());
        System.out.println("Skenario 15: inisialisasi parameter lengkap objek timer berhasil");
    }
}
