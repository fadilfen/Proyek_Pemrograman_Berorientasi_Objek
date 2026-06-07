package testing;

import tubes_a11.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Simple Test Runner - Tanpa JUnit Framework
 * Untuk quick testing tanpa dependency eksternal
 */
public class SimpleTestRunner {

    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Simple Test Runner - Mental Wellbeing");
        System.out.println("========================================\n");

        // Test AktivitasDigital
        testAktivitasDigital();
        
        // Test AppTimer
        testAppTimer();
        
        // Test TopUp
        testTopUp();
        
        // Test Notifikasi
        testNotifikasi();
        
        // Test LaporanHarian
        testLaporanHarian();

        // Summary
        System.out.println("\n========================================");
        System.out.println("  Test Summary");
        System.out.println("========================================");
        System.out.println("Total Tests  : " + totalTests);
        System.out.println("Passed       : " + passedTests + " ✓");
        System.out.println("Failed       : " + failedTests + " ✗");
        System.out.println("Success Rate : " + (passedTests * 100 / totalTests) + "%");
        System.out.println("========================================");
    }

    private static void testAktivitasDigital() {
        System.out.println("Testing AktivitasDigital...");
        
        // Test 1: Constructor
        test("AktivitasDigital - Constructor", () -> {
            AktivitasDigital akt = new AktivitasDigital("Instagram", 60, 120, LocalDate.now());
            return akt.getNamaAplikasi().equals("Instagram");
        });
        
        // Test 2: melebihiBatas true
        test("AktivitasDigital - melebihiBatas true", () -> {
            AktivitasDigital akt = new AktivitasDigital("TikTok", 150, 120, LocalDate.now());
            return akt.melebihiBatas();
        });
        
        // Test 3: melebihiBatas false
        test("AktivitasDigital - melebihiBatas false", () -> {
            AktivitasDigital akt = new AktivitasDigital("YouTube", 90, 120, LocalDate.now());
            return !akt.melebihiBatas();
        });
        
        System.out.println();
    }

    private static void testAppTimer() {
        System.out.println("Testing AppTimer...");
        
        // Test 1: Constructor
        test("AppTimer - Constructor", () -> {
            AppTimer timer = new AppTimer(1, "Instagram", 60, LocalDate.now(), LocalTime.of(10, 0));
            return timer.getAppName().equals("Instagram");
        });
        
        // Test 2: Remaining seconds
        test("AppTimer - Remaining seconds", () -> {
            AppTimer timer = new AppTimer(1, "TikTok", 30, LocalDate.now(), LocalTime.of(10, 0));
            return timer.getRemainingSeconds() == 1800;
        });
        
        // Test 3: Decrement
        test("AppTimer - Decrement", () -> {
            AppTimer timer = new AppTimer(1, "YouTube", 10, LocalDate.now(), LocalTime.of(10, 0));
            long initial = timer.getRemainingSeconds();
            timer.decrementRemainingSeconds();
            return timer.getRemainingSeconds() == (initial - 1);
        });
        
        System.out.println();
    }

    private static void testTopUp() {
        System.out.println("Testing TopUp...");
        
        // Test 1: Constructor
        test("TopUp - Constructor", () -> {
            TopUp topup = new TopUp(10, "QRIS");
            return topup.getJumlahToken() == 10;
        });
        
        // Test 2: Calculate 5 tokens
        test("TopUp - Calculate 5 tokens", () -> {
            TopUp topup = new TopUp(5, "Transfer");
            return topup.hitungTotalBiaya() == 5000;
        });
        
        // Test 3: Calculate 10 tokens
        test("TopUp - Calculate 10 tokens", () -> {
            TopUp topup = new TopUp(10, "E-Wallet");
            return topup.hitungTotalBiaya() == 10000;
        });
        
        System.out.println();
    }

    private static void testNotifikasi() {
        System.out.println("Testing Notifikasi...");
        
        // Test 1: Di bawah limit
        test("Notifikasi - Di bawah limit", () -> {
            Notifikasi notif = new Notifikasi();
            notif.kirimPeringatan(100, 150);
            return notif.getPesan().contains("dalam batas");
        });
        
        // Test 2: Sama dengan limit
        test("Notifikasi - Sama dengan limit", () -> {
            Notifikasi notif = new Notifikasi();
            notif.kirimPeringatan(120, 120);
            return notif.getPesan().contains("mencapai batas");
        });
        
        // Test 3: Melebihi limit
        test("Notifikasi - Melebihi limit", () -> {
            Notifikasi notif = new Notifikasi();
            notif.kirimPeringatan(200, 150);
            return notif.getPesan().contains("melebihi");
        });
        
        System.out.println();
    }

    private static void testLaporanHarian() {
        System.out.println("Testing LaporanHarian...");
        
        // Test 1: Generate laporan
        test("LaporanHarian - Generate laporan", () -> {
            LaporanHarian laporan = new LaporanHarian(100, 80, new java.util.ArrayList<>(), "Test User");
            String hasil = laporan.generateLaporan();
            return hasil.contains("Test User");
        });
        
        // Test 2: Status sehat
        test("LaporanHarian - Status sehat", () -> {
            LaporanHarian laporan = new LaporanHarian(60, 70, new java.util.ArrayList<>(), "User");
            String hasil = laporan.generateLaporan();
            return hasil.contains("Sehat");
        });
        
        // Test 3: Status kurangi screen time
        test("LaporanHarian - Status kurangi", () -> {
            LaporanHarian laporan = new LaporanHarian(180, 50, new java.util.ArrayList<>(), "User");
            String hasil = laporan.generateLaporan();
            return hasil.contains("Kurangi Screen Time");
        });
        
        System.out.println();
    }

    private static void test(String testName, TestCase testCase) {
        totalTests++;
        try {
            boolean result = testCase.run();
            if (result) {
                passedTests++;
                System.out.println("  ✓ " + testName);
            } else {
                failedTests++;
                System.out.println("  ✗ " + testName + " - Assertion failed");
            }
        } catch (Exception e) {
            failedTests++;
            System.out.println("  ✗ " + testName + " - Exception: " + e.getMessage());
        }
    }

    @FunctionalInterface
    interface TestCase {
        boolean run() throws Exception;
    }
}
