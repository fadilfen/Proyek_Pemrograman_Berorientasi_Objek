package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DatabaseHelper — Kelas utilitas untuk koneksi ke database MySQL (Laragon).
 *
 * Database ini dipakai bersama antara proyek GUI (Swing) dan
 * proyek Spring Boot (demo/). Keduanya mengakses database 'mindfull_db'
 * yang sama di MySQL server Laragon.
 *
 * Pastikan Laragon sudah aktif sebelum menjalankan aplikasi GUI.
 */
public class DatabaseHelper {

    // ── Konfigurasi koneksi MySQL ─────────────────────────────────────────
    private static final String JDBC_URL  =
            "jdbc:mysql://localhost:3306/mindfull_db" +
            "?useSSL=false" +
            "&serverTimezone=Asia/Jakarta" +
            "&allowPublicKeyRetrieval=true" +
            "&autoReconnect=true" +
            "&useUnicode=true" +
            "&characterEncoding=UTF-8" +
            "&connectTimeout=3000" +
            "&socketTimeout=10000";
    private static final String DB_USER     = "root";
    private static final String DB_PASSWORD = ""; // kosong = default Laragon

    // Satu instance koneksi yang digunakan sepanjang sesi (singleton)
    private static Connection connection = null;

    /**
     * Mendapatkan koneksi ke database MySQL.
     * Jika koneksi belum ada atau sudah tertutup, buat koneksi baru.
     *
     * @return objek Connection aktif
     * @throws SQLException jika koneksi gagal (Laragon belum aktif, dll)
     */
    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                // Muat driver MySQL secara eksplisit
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
                // Set connection properties untuk performa
                connection.setAutoCommit(true);
                System.out.println("[DB] Koneksi ke MySQL berhasil");
                alterTablesIfNeeded(); // Ensure the table has the required column
            }
            return connection;
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "Driver MySQL tidak ditemukan. " +
                "Pastikan mysql-connector-j.jar ada di classpath.", e);
        }
    }

    /**
     * Pastikan tabel memiliki kolom yang dibutuhkan (tanggal dan umur)
     */
    private static void alterTablesIfNeeded() {
        try {
            // Cek app_timers: tambah kolom tanggal
            String checkSql = "SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = 'mindfull_db' AND TABLE_NAME = 'app_timers' AND COLUMN_NAME = 'tanggal'";
            ResultSet rs = connection.createStatement().executeQuery(checkSql);
            if (!rs.next()) {
                connection.createStatement().execute("ALTER TABLE app_timers ADD COLUMN tanggal DATE");
                System.out.println("[DB] Kolom tanggal berhasil ditambahkan ke app_timers");
            }
            
            // Cek app_timers: tambah kolom remaining_seconds
            String checkSqlRem = "SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = 'mindfull_db' AND TABLE_NAME = 'app_timers' AND COLUMN_NAME = 'remaining_seconds'";
            ResultSet rsRem = connection.createStatement().executeQuery(checkSqlRem);
            if (!rsRem.next()) {
                connection.createStatement().execute("ALTER TABLE app_timers ADD COLUMN remaining_seconds BIGINT DEFAULT NULL");
                System.out.println("[DB] Kolom remaining_seconds berhasil ditambahkan ke app_timers");
            }
            
            // Cek users: tambah kolom umur
            String checkSql2 = "SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = 'mindfull_db' AND TABLE_NAME = 'users' AND COLUMN_NAME = 'umur'";
            ResultSet rs2 = connection.createStatement().executeQuery(checkSql2);
            if (!rs2.next()) {
                connection.createStatement().execute("ALTER TABLE users ADD COLUMN umur INT DEFAULT 0");
                System.out.println("[DB] Kolom umur berhasil ditambahkan ke users");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error saat mengecek atau mengubah tabel: " + e.getMessage());
        }
    }

    /**
     * Menutup koneksi database saat aplikasi ditutup.
     */
    public static void tutupKoneksi() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[DB] Koneksi database ditutup.");
            } catch (SQLException e) {
                System.err.println("[DB] Gagal menutup koneksi: " + e.getMessage());
            }
        }
    }

    // ── Utilitas kueri ────────────────────────────────────────────────────

    /**
     * Mencari user berdasarkan username dan password untuk login.
     */
    public static ResultSet cariUserLogin(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        PreparedStatement ps = getConnection().prepareStatement(sql);
        ps.setString(1, username);
        ps.setString(2, password);
        return ps.executeQuery();
    }
    
    /**
     * Menambahkan app timer untuk child
     */
    public static void tambahAppTimer(long childId, String appName, int durationMinutes, 
                                      java.time.LocalDate tanggal, java.time.LocalTime startTime, java.time.LocalTime endTime) 
            throws SQLException {
        String sql = "INSERT INTO app_timers (child_id, app_name, duration_minutes, tanggal, start_time, end_time, is_tracking, remaining_seconds) "
                   + "VALUES (?, ?, ?, ?, ?, ?, 1, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, childId);
            ps.setString(2, appName);
            ps.setInt(3, durationMinutes);
            ps.setDate(4, java.sql.Date.valueOf(tanggal));
            ps.setTime(5, java.sql.Time.valueOf(startTime));
            ps.setTime(6, java.sql.Time.valueOf(endTime));
            ps.setLong(7, durationMinutes * 60L); // Set sisa waktu default sesuai durasi
            ps.executeUpdate();
        }
    }
    
    /**
     * Update status tracking app timer
     */
    public static void updateAppTimerTracking(long timerId, boolean isTracking) throws SQLException {
        String sql = "UPDATE app_timers SET is_tracking = ? WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setBoolean(1, isTracking);
            ps.setLong(2, timerId);
            ps.executeUpdate();
        }
    }
    
    /**
     * Update sisa waktu detik aplikasi
     */
    public static void updateAppTimerRemainingSeconds(long timerId, long remainingSeconds) throws SQLException {
        String sql = "UPDATE app_timers SET remaining_seconds = ? WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, remainingSeconds);
            ps.setLong(2, timerId);
            ps.executeUpdate();
        }
    }
    
    /**
     * Update semua app timers untuk child menjadi tracking
     */
    public static void startTrackingForChild(long childId) throws SQLException {
        String sql = "UPDATE app_timers SET is_tracking = 1 WHERE child_id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, childId);
            ps.executeUpdate();
        }
    }
    
    /**
     * Hapus app timer
     */
    public static void hapusAppTimer(long timerId) throws SQLException {
        String sql = "DELETE FROM app_timers WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, timerId);
            ps.executeUpdate();
        }
    }

    /**
     * Memeriksa apakah username sudah terdaftar.
     */
    public static boolean usernameAda(String username) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    /**
     * Mendaftarkan user baru ke database dengan role parent (default)
     *
     * @return true jika berhasil, false jika username sudah ada
     */
    public static boolean daftarUser(String namaUser, String username, String password)
            throws SQLException {
        if (usernameAda(username)) return false;
        String sql = "INSERT INTO users (nama_user, username, password, token, role, parent_id) VALUES (?,?,?,50,'parent',NULL)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, namaUser);
            ps.setString(2, username);
            ps.setString(3, password);
            ps.executeUpdate();
            return true;
        }
    }
    
    /**
     * Mendaftarkan child account oleh parent
     * @param parentId id parent yang membuat child
     */
    public static boolean daftarChildUser(long parentId, String namaUser, String username, String password, int umur)
            throws SQLException {
        if (usernameAda(username)) return false;
        String sql = "INSERT INTO users (nama_user, username, password, token, role, parent_id, umur) VALUES (?,?,?,0,'child',?,?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, namaUser);
            ps.setString(2, username);
            ps.setString(3, password);
            ps.setLong(4, parentId);
            ps.setInt(5, umur);
            ps.executeUpdate();
            return true;
        }
    }

    /**
     * Mengupdate nilai token user di database.
     */
    public static void updateToken(long userId, int token) throws SQLException {
        String sql = "UPDATE users SET token = ? WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, token);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }

    /**
     * Mengupdate username dan password user.
     *
     * @return true jika berhasil, false jika username baru sudah dipakai user lain
     */
    public static boolean updateKredensial(long userId, String usernameBar, String passwordBar)
            throws SQLException {
        // Cek apakah username baru sudah dipakai user lain
        String cek = "SELECT id FROM users WHERE username = ? AND id != ?";
        try (PreparedStatement ps = getConnection().prepareStatement(cek)) {
            ps.setString(1, usernameBar);
            ps.setLong(2, userId);
            if (ps.executeQuery().next()) return false;
        }
        String sql = "UPDATE users SET username = ?, password = ? WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, usernameBar);
            ps.setString(2, passwordBar);
            ps.setLong(3, userId);
            ps.executeUpdate();
            return true;
        }
    }
}
