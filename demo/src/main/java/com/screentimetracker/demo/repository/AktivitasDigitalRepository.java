package com.screentimetracker.demo.repository;

import com.screentimetracker.demo.model.AktivitasDigital;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository untuk operasi database tabel 'aktivitas_digital'.
 * Spring Data JPA otomatis mengimplementasikan method CRUD standar.
 */
public interface AktivitasDigitalRepository extends JpaRepository<AktivitasDigital, Long> {

    // Mengambil semua aktivitas milik user tertentu berdasarkan ID user
    List<AktivitasDigital> findByUserId(Long userId);
}
