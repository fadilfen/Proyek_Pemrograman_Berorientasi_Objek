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
            "?useSSL=false&serverTimezone=Asia/Jakarta&allowPublicKeyRetrieval=true";
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
        if (connection == null || connection.isClosed()) {
            try {
                // Muat driver MySQL secara eksplisit
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
                System.out.println("[DB] Koneksi ke MySQL berhasil: " + JDBC_URL);
            } catch (ClassNotFoundException e) {
                throw new SQLException(
                    "Driver MySQL tidak ditemukan. " +
                    "Pastikan mysql-connector-j.jar ada di classpath.", e);
            }
        }
        return connection;
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
     * Mendaftarkan user baru ke database dengan token awal 50.
     *
     * @return true jika berhasil, false jika username sudah ada
     */
    public static boolean daftarUser(String namaUser, String username, String password)
            throws SQLException {
        if (usernameAda(username)) return false;
        String sql = "INSERT INTO users (nama_user, username, password, token) VALUES (?,?,?,50)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, namaUser);
            ps.setString(2, username);
            ps.setString(3, password);
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
