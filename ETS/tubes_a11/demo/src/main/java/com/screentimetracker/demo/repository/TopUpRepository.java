package com.screentimetracker.demo.repository;

import com.screentimetracker.demo.model.TopUp;
import com.screentimetracker.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Repository untuk operasi database tabel 'topup'.
 * Spring Data JPA otomatis mengimplementasikan method CRUD standar.
 */
public interface TopUpRepository extends JpaRepository<TopUp, Long> {

    // Mengambil semua riwayat top up milik user tertentu berdasarkan ID user
    List<TopUp> findByUserId(Long userId);

    // Mengambil top up terbaru untuk user tertentu
    Optional<TopUp> findTopByUserOrderByWaktuTopUpDesc(User user);
}
