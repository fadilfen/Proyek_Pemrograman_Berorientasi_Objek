package com.screentimetracker.demo.repository;

import com.screentimetracker.demo.model.Notifikasi;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository untuk operasi database tabel 'notifikasi'.
 * Spring Data JPA otomatis mengimplementasikan method CRUD standar.
 */
public interface NotifikasiRepository extends JpaRepository<Notifikasi, Long> {

    // Mengambil semua notifikasi milik user tertentu, diurutkan terbaru dulu
    List<Notifikasi> findByUserIdOrderByCreatedAtDesc(Long userId);
}
