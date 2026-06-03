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
    private String role; // 'parent' atau 'child'
    private Long   parentId; // null jika parent, id parent jika child

    // ── Daftar aktivitas di-load dari tabel 'aktivitas_digital' ──────────
    private ArrayList<AktivitasDigital> aktivitasList = new ArrayList<>();
    
    // ── Daftar child accounts (hanya untuk parent) ────────────────────────
    private ArrayList<User> childAccounts = new ArrayList<>();
    
    // ── Daftar app timers (untuk child account) ───────────────────────────
    private ArrayList<AppTimer> appTimers = new ArrayList<>();

    // ── Konstruktor ───────────────────────────────────────────────────────

    /**
     * Konstruktor utama saat login berhasil.
     * Data TIDAK langsung di-load - menggunakan lazy loading untuk performa.
     *
     * @param id       id user dari tabel users
     * @param namaUser nama lengkap
     * @param token    token saat ini
     * @param role     role user (parent/child)
     * @param parentId id parent (null jika parent)
     */
    public User(long id, String namaUser, int token, String role, Long parentId) {
        this.id       = id;
        this.namaUser = namaUser;
        this.token    = token;
        this.role     = role;
        this.parentId = parentId;
        // Data di-load nanti saat dibutuhkan (lazy loading)
    }
    
    /**
     * Konstruktor sederhana untuk child account (tanpa load data)
     */
    public User(long id, String namaUser, String username, String role) {
        this.id = id;
        this.namaUser = namaUser;
        this.username = username;
        this.role = role;
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

    /**
     * Load semua child accounts untuk parent (lazy loading)
     */
    private void loadChildAccounts() {
        if (!childAccounts.isEmpty()) return; // sudah di-load
        
        childAccounts.clear();
        try {
            String sql = "SELECT id, nama_user, username, role FROM users WHERE parent_id = ?";
            PreparedStatement ps = DatabaseHelper.getConnection().prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User child = new User(
                    rs.getLong("id"),
                    rs.getString("nama_user"),
                    rs.getString("username"),
                    rs.getString("role")
                );
                childAccounts.add(child);
            }
        } catch (SQLException e) {
            System.err.println("[User] Gagal load child accounts: " + e.getMessage());
        }
    }
    
    /**
     * Load app timers untuk child account (lazy loading)
     */
    private void loadAppTimers() {
        appTimers.clear();
        try {
            String sql = "SELECT * FROM app_timers WHERE child_id = ?";
            PreparedStatement ps = DatabaseHelper.getConnection().prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                AppTimer timer = new AppTimer(
                    rs.getLong("id"),
                    rs.getLong("child_id"),
                    rs.getString("app_name"),
                    rs.getInt("duration_minutes"),
                    rs.getTime("start_time").toLocalTime(),
                    rs.getTime("end_time").toLocalTime(),
                    rs.getBoolean("is_tracking")
                );
                appTimers.add(timer);
            }
        } catch (SQLException e) {
            System.err.println("[User] Gagal load app timers: " + e.getMessage());
        }
    }

    // ── Getter & Setter ───────────────────────────────────────────────────
    public long   getId()                       { return id; }
    public String getNamaUser()                 { return namaUser; }
    public String getUsername()                 { return username; }
    public void   setUsername(String username)  { this.username = username; }
    public int    getToken()                    { return token; }
    public String getRole()                     { return role; }
    public Long   getParentId()                 { return parentId; }
    public boolean isParent()                   { return "parent".equals(role); }
    public boolean isChild()                    { return "child".equals(role); }
    
    public ArrayList<AktivitasDigital> getAktivitasList() { 
        if (aktivitasList.isEmpty()) memuatAktivitasDariDB();
        return aktivitasList; 
    }
    
    public ArrayList<User> getChildAccounts() { 
        if (isParent() && childAccounts.isEmpty()) loadChildAccounts();
        return childAccounts; 
    }
    
    public ArrayList<AppTimer> getAppTimers() { 
        if (isChild()) loadAppTimers(); // selalu fresh untuk timer
        return appTimers; 
    }
    
    public void refreshChildAccounts() { 
        childAccounts.clear();
        loadChildAccounts(); 
    }
    
    public void refreshAppTimers() { 
        loadAppTimers(); 
    }
}