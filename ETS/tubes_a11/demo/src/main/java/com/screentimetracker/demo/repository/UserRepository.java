package com.screentimetracker.demo.repository;

import com.screentimetracker.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository untuk operasi database tabel 'users'.
 * Spring Data JPA otomatis mengimplementasikan method CRUD standar.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    // Mencari user berdasarkan username (digunakan saat login)
    Optional<User> findByUsername(String username);

    // Mengecek apakah username sudah terdaftar (digunakan saat register)
    boolean existsByUsername(String username);
}
