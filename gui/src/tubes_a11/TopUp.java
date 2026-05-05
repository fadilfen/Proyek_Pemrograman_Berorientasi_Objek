package tubes_a11;

/**
 * TopUp — Merepresentasikan transaksi pengisian token.
 *
 * Mekanisme baru:
 *   - User memasukkan JUMLAH TOKEN yang diinginkan (bukan rupiah)
 *   - Sistem menghitung biaya: jumlahToken × Rp 1.000
 *   - Contoh: 5 token → Rp 5.000
 *
 * Setelah konfirmasi, data tersimpan ke tabel 'topup' di database
 * melalui metode simpanTopUp() milik User.
 */
public class TopUp {

    // Harga per token dalam Rupiah
    public static final int HARGA_PER_TOKEN = 1000;

    // Jumlah token yang ingin ditambahkan (bukan rupiah)
    private int    jumlahToken;
    // Metode pembayaran yang dipilih (QRIS, Transfer, E-Wallet)
    private String metode;

    /**
     * @param jumlahToken jumlah token yang ingin dibeli (mis. 5 = 5 token)
     * @param metode      metode pembayaran yang dipilih
     */
    public TopUp(int jumlahToken, String metode) {
        this.jumlahToken = jumlahToken;
        this.metode      = metode;
    }

    /**
     * Menghitung total biaya yang harus dibayar.
     * Contoh: 5 token × Rp 1.000 = Rp 5.000
     *
     * @return total biaya dalam Rupiah
     */
    public int hitungTotalBiaya() {
        return jumlahToken * HARGA_PER_TOKEN;
    }

    /**
     * Memproses top up:
     * - Tambah token ke akun user (otomatis update ke DB)
     * - Simpan riwayat transaksi ke tabel topup di DB
     *   (jumlah yang disimpan adalah rupiah = jumlahToken × 1000)
     *
     * @param user user yang melakukan top up
     */
    public void prosesTopUp(User user) {
        // Tambah token ke akun user dan update database
        user.tambahToken(jumlahToken);
        // Simpan riwayat dengan jumlah rupiah ke tabel topup
        user.simpanTopUp(hitungTotalBiaya(), metode);
    }

    public int    getJumlahToken() { return jumlahToken; }
    public String getMetode()      { return metode; }
}
