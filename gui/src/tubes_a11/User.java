package tubes_a11;

import database.DatabaseHelper;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * User — Merepresentasikan pengguna yang sedang login.
 * Data di-load dari database H2 saat login dan disinkronkan
 * setiap kali ada perubahan (tambah aktivitas, top up, dll).
 */
public class User {

    // ── Data dasar user (disinkronkan dengan tabel 'users') ──────────────
    private long   id;
    private String namaUser;
    private String username;
    private int    token;

    // ── Daftar aktivitas di-load dari tabel 'aktivitas_digital' ──────────
    private ArrayList<AktivitasDigital> aktivitasList = new ArrayList<>();

    // ── Konstruktor ───────────────────────────────────────────────────────

    /**
     * Konstruktor utama saat login berhasil.
     * Data aktivitas langsung di-load dari database.
     *
     * @param id       id user dari tabel users
     * @param namaUser nama lengkap
     * @param token    token saat ini
     */
    public User(long id, String namaUser, int token) {
        this.id       = id;
        this.namaUser = namaUser;
        this.token    = token;
        memuatAktivitasDariDB();
    }

    // ── Operasi Database ──────────────────────────────────────────────────

    /**
     * Memuat semua aktivitas digital user dari database.
     * Dipanggil saat login dan saat halaman Activity di-refresh.
     */
    public void memuatAktivitasDariDB() {
        aktivitasList.clear();
        try {
            String sql = "SELECT * FROM aktivitas_digital WHERE user_id = ? ORDER BY tanggal DESC";
            PreparedStatement ps = DatabaseHelper.getConnection().prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Buat objek AktivitasDigital dari baris di database
                Date tgl = rs.getDate("tanggal");
                LocalDate tanggal = (tgl != null) ? tgl.toLocalDate() : LocalDate.now();

                AktivitasDigital akt = new AktivitasDigital(
                        rs.getString("nama_aplikasi"),
                        rs.getInt("durasi_menit"),
                        rs.getInt("batas_durasi"),
                        tanggal
                );
                aktivitasList.add(akt);
            }
        } catch (SQLException e) {
            System.err.println("[User] Gagal memuat aktivitas: " + e.getMessage());
        }
    }

    /**
     * Menambahkan aktivitas baru ke database dan list lokal.
     * Token dikurangi 5 setiap kali log aktivitas.
     *
     * @param akt objek AktivitasDigital yang akan disimpan
     */
    public void tambahAktivitas(AktivitasDigital akt) {
        try {
            String sql = """
                INSERT INTO aktivitas_digital
                    (user_id, nama_aplikasi, durasi_menit, batas_durasi, tanggal)
                VALUES (?, ?, ?, ?, ?)
            """;
            PreparedStatement ps = DatabaseHelper.getConnection().prepareStatement(sql);
            ps.setLong(1, id);
            ps.setString(2, akt.getNamaAplikasi());
            ps.setInt(3, akt.getDurasiMenit());
            ps.setInt(4, akt.getBatasDurasi());
            ps.setDate(5, Date.valueOf(akt.getTanggal()));
            ps.executeUpdate();

            // Tambahkan juga ke list lokal
            aktivitasList.add(0, akt);
        } catch (SQLException e) {
            System.err.println("[User] Gagal simpan aktivitas: " + e.getMessage());
        }
    }

    /**
     * Menambah token (saat top up) dan update ke database.
     *
     * @param jumlah jumlah token yang ditambahkan
     */
    public void tambahToken(int jumlah) {
        token += jumlah;
        simpanTokenKDB();
    }

    /**
     * Mengurangi token (saat log aktivitas) dan update ke database.
     *
     * @param jumlah jumlah token yang dikurangi
     */
    public void kurangiToken(int jumlah) {
        token -= jumlah;
        simpanTokenKDB();
    }

    /**
     * Menyimpan nilai token terkini ke database.
     */
    private void simpanTokenKDB() {
        try {
            DatabaseHelper.updateToken(id, token);
        } catch (SQLException e) {
            System.err.println("[User] Gagal update token: " + e.getMessage());
        }
    }

    /**
     * Menyimpan top up ke tabel topup di database.
     *
     * @param jumlahKoin       jumlah koin/token yang dibeli
     * @param metodePembayaran nama metode pembayaran yang dipilih
     */
    public void simpanTopUp(int jumlahKoin, String metodePembayaran) {
        try {
            // Simpan riwayat top up ke tabel topup
            // Kolom: user_id, jumlah_koin, metode_pembayaran, waktu_top_up (sesuai database.sql)
            String sql = "INSERT INTO topup (user_id, jumlah_koin, metode_pembayaran, waktu_top_up) "
                       + "VALUES (?, ?, ?, NOW())";
            PreparedStatement ps = DatabaseHelper.getConnection().prepareStatement(sql);
            ps.setLong(1, id);
            ps.setInt(2, jumlahKoin);
            ps.setString(3, metodePembayaran);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[User] Gagal simpan top up: " + e.getMessage());
        }
    }


    // ── Perhitungan Kesehatan ─────────────────────────────────────────────

    /**
     * Menjumlahkan total screen time dari semua aktivitas.
     */
    public int hitungTotalScreenTime() {
        int total = 0;
        for (AktivitasDigital a : aktivitasList) total += a.getDurasiMenit();
        return total;
    }

    /**
     * Menghitung skor kesehatan (0–100) berdasarkan aktivitas.
     * Logika sama dengan proyek Spring Boot.
     */
    public int hitungScoreKesehatan() {
        int score = 100;
        for (AktivitasDigital a : aktivitasList) {
            score -= a.getDurasiMenit() / 10;
            if (a.melebihiBatas()) score -= 20;
        }
        return Math.max(0, score);
    }

    /**
     * Menghasilkan objek LaporanHarian berdasarkan data saat ini.
     */
    public LaporanHarian lihatLaporan() {
        return new LaporanHarian(
                hitungTotalScreenTime(),
                hitungScoreKesehatan(),
                aktivitasList,
                namaUser
        );
    }

    // ── Getter & Setter ───────────────────────────────────────────────────
    public long   getId()                       { return id; }
    public String getNamaUser()                 { return namaUser; }
    public String getUsername()                 { return username; }
    public void   setUsername(String username)  { this.username = username; }
    public int    getToken()                    { return token; }
    public ArrayList<AktivitasDigital> getAktivitasList() { return aktivitasList; }
}