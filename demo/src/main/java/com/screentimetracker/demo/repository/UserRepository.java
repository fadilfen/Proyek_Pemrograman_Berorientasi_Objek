package com.screentimetracker.demo.repository;

import com.screentimetracker.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Repository untuk operasi database tabel 'users'.
 * Menyediakan query untuk autentikasi, pengecekan duplikat,
 * dan manajemen user oleh admin.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Mencari user berdasarkan username (untuk login).
     */
    Optional<User> findByUsername(String username);

    /**
     * Mengecek apakah username sudah terdaftar.
     */
    boolean existsByUsername(String username);

    /**
     * Mengambil semua user dengan role tertentu.
     * Digunakan admin untuk melihat daftar user biasa.
     */
    List<User> findByRole(String role);

    /**
     * Mengambil semua user aktif untuk manajemen admin.
     */
    List<User> findByIsActiveOrderByCreatedAtDesc(boolean isActive);

    /**
     * Mengambil semua user diurutkan berdasarkan tanggal daftar terbaru.
     */
    List<User> findAllByOrderByCreatedAtDesc();
}
