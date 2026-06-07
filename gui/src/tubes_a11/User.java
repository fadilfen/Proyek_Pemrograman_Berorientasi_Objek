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
    private int    umur; // umur anak

    // ── Daftar aktivitas di-load dari tabel 'aktivitas_digital' ──────────
    private ArrayList<AktivitasDigital> aktivitasList = new ArrayList<>();
    private boolean isAktivitasLoaded = false; // flag lazy load agar tidak hit DB berulang
    
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
     * @param umur     umur pengguna (0 jika parent)
     */
    public User(long id, String namaUser, int token, String role, Long parentId, int umur) {
        this.id       = id;
        this.namaUser = namaUser;
        this.token    = token;
        this.role     = role;
        this.parentId = parentId;
        this.umur     = umur;
        // Data di-load nanti saat dibutuhkan (lazy loading)
    }
    
    /**
     * Konstruktor sederhana untuk child account (tanpa load data)
     */
    public User(long id, String namaUser, String username, String role, int umur) {
        this.id = id;
        this.namaUser = namaUser;
        this.username = username;
        this.role = role;
        this.umur = umur;
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
     * Catatan: pengurangan token dilakukan secara terpisah oleh pemanggil (showActivityPage).
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
     * Batas penggunaan layar harian berdasarkan umur:
     * - Umur < 5 tahun: max 60 menit
     * - Umur 5 - 10 tahun: max 120 menit
     * - Umur 11 - 18 tahun: max 180 menit
     * - Umur > 18 tahun: max 240 menit
     */
    public int hitungScoreKesehatan() {
        int score = 100;
        int totalScreenTime = hitungTotalScreenTime();
        
        int batasHarian = 240; // Default untuk > 18 tahun
        if (umur < 5) batasHarian = 60;
        else if (umur <= 10) batasHarian = 120;
        else if (umur <= 18) batasHarian = 180;
        
        // Pengurangan karena melebihi batas harian (20 poin per setiap jam lebih)
        if (totalScreenTime > batasHarian) {
            int kelebihanJam = (totalScreenTime - batasHarian) / 60;
            if ((totalScreenTime - batasHarian) % 60 > 0) kelebihanJam++; // Bulatkan ke atas untuk setiap pecahan jam
            score -= (kelebihanJam * 20);
        }

        // Pengurangan spesifik aplikasi
        for (AktivitasDigital a : aktivitasList) {
            if (a.melebihiBatas()) score -= 10;
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
            String sql = "SELECT id, nama_user, username, role, umur FROM users WHERE parent_id = ?";
            PreparedStatement ps = DatabaseHelper.getConnection().prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User child = new User(
                    rs.getLong("id"),
                    rs.getString("nama_user"),
                    rs.getString("username"),
                    rs.getString("role"),
                    rs.getInt("umur")
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
                java.sql.Date sqlDate = rs.getDate("tanggal");
                java.time.LocalDate tgl = (sqlDate != null) ? sqlDate.toLocalDate() : java.time.LocalDate.now();
                int durationMins = rs.getInt("duration_minutes");
                long remSecs = rs.getObject("remaining_seconds") != null ? 
                               rs.getLong("remaining_seconds") : 
                               (durationMins * 60L);
                               
                AppTimer timer = new AppTimer(
                    rs.getLong("id"),
                    rs.getLong("child_id"),
                    rs.getString("app_name"),
                    durationMins,
                    tgl,
                    rs.getTime("start_time").toLocalTime(),
                    rs.getTime("end_time").toLocalTime(),
                    rs.getBoolean("is_tracking"),
                    remSecs
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
    public int    getUmur()                     { return umur; }
    public boolean isParent()                   { return "parent".equals(role); }
    public boolean isChild()                    { return "child".equals(role); }
    
    public ArrayList<AktivitasDigital> getAktivitasList() { 
        if (!isAktivitasLoaded) {
            memuatAktivitasDariDB();
            isAktivitasLoaded = true;
        }
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
    
    public void refreshAktivitas() {
        isAktivitasLoaded = false;
        aktivitasList.clear();
        memuatAktivitasDariDB();
        isAktivitasLoaded = true;
    }
    
    public void refreshAppTimers() { 
        loadAppTimers(); 
    }
}