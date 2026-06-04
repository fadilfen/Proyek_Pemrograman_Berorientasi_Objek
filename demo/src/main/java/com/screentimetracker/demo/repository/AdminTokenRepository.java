package com.screentimetracker.demo.repository;

import com.screentimetracker.demo.model.AdminToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository untuk operasi database tabel 'admin_tokens'.
 * Digunakan untuk memvalidasi token saat registrasi admin.
 */
public interface AdminTokenRepository extends JpaRepository<AdminToken, Long> {

    /**
     * Mencari token yang aktif berdasarkan string token.
     * Mengembalikan Optional empty jika token tidak ditemukan atau tidak aktif.
     * @param token  String token yang dimasukkan pengguna
     */
    Optional<AdminToken> findByTokenAndIsActiveTrue(String token);

    /**
     * Mengecek apakah token tertentu valid dan masih aktif.
     */
    boolean existsByTokenAndIsActiveTrue(String token);
}
