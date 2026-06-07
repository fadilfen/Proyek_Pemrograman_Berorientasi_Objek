package testing;

import org.junit.jupiter.api.*;
import tubes_a11.TopUp;
import tubes_a11.User;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test untuk class TopUp
 * Total: 15 skenario testing
 */
public class TopUpTest {

    @Test
    @DisplayName("Test 1: Constructor dengan parameter valid")
    public void testConstructorValid() {
        // Membuat top up 10 token dengan metode QRIS
        TopUp topup = new TopUp(10, "QRIS");
        assertNotNull(topup);
        assertEquals(10, topup.getJumlahToken());
        assertEquals("QRIS", topup.getMetode());
    }

    @Test
    @DisplayName("Test 2: hitungTotalBiaya() untuk 5 token")
    public void testHitungTotalBiaya5Token() {
        // 5 token x Rp 1.000 = Rp 5.000
        TopUp topup = new TopUp(5, "Transfer");
        assertEquals(5000, topup.hitungTotalBiaya());
    }

    @Test
    @DisplayName("Test 3: hitungTotalBiaya() untuk 10 token")
    public void testHitungTotalBiaya10Token() {
        // 10 token x Rp 1.000 = Rp 10.000
        TopUp topup = new TopUp(10, "E-Wallet");
        assertEquals(10000, topup.hitungTotalBiaya());
    }

    @Test
    @DisplayName("Test 4: hitungTotalBiaya() untuk 1 token")
    public void testHitungTotalBiaya1Token() {
        // 1 token x Rp 1.000 = Rp 1.000
        TopUp topup = new TopUp(1, "QRIS");
        assertEquals(1000, topup.hitungTotalBiaya());
    }

    @Test
    @DisplayName("Test 5: hitungTotalBiaya() untuk 100 token")
    public void testHitungTotalBiaya100Token() {
        // 100 token x Rp 1.000 = Rp 100.000
        TopUp topup = new TopUp(100, "Transfer");
        assertEquals(100000, topup.hitungTotalBiaya());
    }

    @Test
    @DisplayName("Test 6: HARGA_PER_TOKEN adalah konstanta 1000")
    public void testHargaPerToken() {
        // Verifikasi harga per token
        assertEquals(1000, TopUp.HARGA_PER_TOKEN);
    }

    @Test
    @DisplayName("Test 7: getJumlahToken() mengembalikan nilai yang benar")
    public void testGetJumlahToken() {
        // Memasukkan 25 token
        TopUp topup = new TopUp(25, "QRIS");
        assertEquals(25, topup.getJumlahToken());
    }

    @Test
    @DisplayName("Test 8: getMetode() mengembalikan metode yang benar")
    public void testGetMetode() {
        // Memilih metode E-Wallet
        TopUp topup = new TopUp(15, "E-Wallet");
        assertEquals("E-Wallet", topup.getMetode());
    }

    @Test
    @DisplayName("Test 9: TopUp dengan metode QRIS")
    public void testMetodeQRIS() {
        // Membayar dengan QRIS untuk 20 token
        TopUp topup = new TopUp(20, "QRIS");
        assertEquals("QRIS", topup.getMetode());
        assertEquals(20000, topup.hitungTotalBiaya());
    }

    @Test
    @DisplayName("Test 10: TopUp dengan metode Transfer")
    public void testMetodeTransfer() {
        // Membayar dengan Transfer untuk 30 token
        TopUp topup = new TopUp(30, "Transfer");
        assertEquals("Transfer", topup.getMetode());
        assertEquals(30000, topup.hitungTotalBiaya());
    }

    @Test
    @DisplayName("Test 11: TopUp dengan metode E-Wallet")
    public void testMetodeEWallet() {
        // Membayar dengan E-Wallet untuk 40 token
        TopUp topup = new TopUp(40, "E-Wallet");
        assertEquals("E-Wallet", topup.getMetode());
        assertEquals(40000, topup.hitungTotalBiaya());
    }

    @Test
    @DisplayName("Test 12: TopUp dengan 0 token")
    public void testTopUp0Token() {
        // Test edge case dengan 0 token
        TopUp topup = new TopUp(0, "QRIS");
        assertEquals(0, topup.hitungTotalBiaya());
    }

    @Test
    @DisplayName("Test 13: Multiple TopUp instances dengan nilai berbeda")
    public void testMultipleInstances() {
        // Membuat 2 top up berbeda
        TopUp topup1 = new TopUp(5, "QRIS");
        TopUp topup2 = new TopUp(10, "Transfer");
        
        assertEquals(5000, topup1.hitungTotalBiaya());
        assertEquals(10000, topup2.hitungTotalBiaya());
        assertNotEquals(topup1.getJumlahToken(), topup2.getJumlahToken());
    }

    @Test
    @DisplayName("Test 14: TopUp dengan jumlah token sangat besar")
    public void testTopUpTokenBesar() {
        // Test dengan 1000 token
        TopUp topup = new TopUp(1000, "Transfer");
        assertEquals(1000000, topup.hitungTotalBiaya());
    }

    @Test
    @DisplayName("Test 15: Verifikasi formula perhitungan biaya")
    public void testFormulaCalculation() {
        // Verifikasi formula: token x harga
        TopUp topup = new TopUp(7, "E-Wallet");
        int expected = 7 * TopUp.HARGA_PER_TOKEN;
        assertEquals(expected, topup.hitungTotalBiaya());
    }
}
