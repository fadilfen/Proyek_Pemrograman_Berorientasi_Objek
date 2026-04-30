package com.screentimetracker.demo.repository;

import com.screentimetracker.demo.model.TopUp;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository untuk operasi database tabel 'topup'.
 * Spring Data JPA otomatis mengimplementasikan method CRUD standar.
 */
public interface TopUpRepository extends JpaRepository<TopUp, Long> {

    // Mengambil semua riwayat top up milik user tertentu berdasarkan ID user
    List<TopUp> findByUserId(Long userId);
}
