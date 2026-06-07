package tubes_a11;

import database.DatabaseHelper;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * UserManager — Mengelola autentikasi dan manajemen akun pengguna.
 * Semua operasi menggunakan DatabaseHelper untuk terhubung ke H2 database
 * yang sama dengan proyek Spring Boot.
 */
public class UserManager {

    /**
     * Melakukan login: mencari user berdasarkan username dan password.
     *
     * @param username username yang diinput
     * @param password password yang diinput
     * @return array [namaUser, idUser, token, role, parentId] jika berhasil, null jika gagal
     */
    public static Object[] login(String username, String password) {
        try {
            ResultSet rs = DatabaseHelper.cariUserLogin(username, password);
            if (rs.next()) {
                Long parentId = rs.getObject("parent_id", Long.class);
                return new Object[]{ 
                    rs.getString("nama_user"), 
                    rs.getLong("id"), 
                    rs.getInt("token"),
                    rs.getString("role"),
                    parentId,
                    rs.getInt("umur")
                };
            }
        } catch (SQLException e) {
            System.err.println("[UserManager] Error login: " + e.getMessage());
        }
        return null; // Login gagal
    }

    /**
     * Mendaftarkan user baru ke database (sebagai parent).
     *
     * @param username   username yang diinginkan
     * @param password   password
     * @param namaLengkap nama lengkap pengguna
     * @return true jika registrasi berhasil, false jika username sudah ada
     */
    public static boolean register(String username, String password, String namaLengkap) {
        try {
            return DatabaseHelper.daftarUser(namaLengkap, username, password);
        } catch (SQLException e) {
            System.err.println("[UserManager] Error register: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Mendaftarkan child account oleh parent
     */
    public static boolean registerChild(long parentId, String username, String password, String namaLengkap, int umur) {
        try {
            return DatabaseHelper.daftarChildUser(parentId, namaLengkap, username, password, umur);
        } catch (SQLException e) {
            System.err.println("[UserManager] Error register child: " + e.getMessage());
            return false;
        }
    }

    /**
     * Mengupdate username dan password pengguna yang sedang login.
     *
     * @param userId     id user yang akan diupdate
     * @param usernameBar username baru
     * @param passwordBar password baru
     * @return true jika berhasil
     */
    public static boolean updateCredentials(long userId, String usernameBar, String passwordBar) {
        try {
            return DatabaseHelper.updateKredensial(userId, usernameBar, passwordBar);
        } catch (SQLException e) {
            System.err.println("[UserManager] Error update credentials: " + e.getMessage());
            return false;
        }
    }
}
