package com.screentimetracker.demo.repository;

import com.screentimetracker.demo.model.LaporanHarian;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository untuk operasi database tabel 'laporan_harian'.
 * Spring Data JPA otomatis mengimplementasikan method CRUD standar.
 */
public interface LaporanHarianRepository extends JpaRepository<LaporanHarian, Long> {

    // Mengambil semua laporan milik user tertentu, diurutkan terbaru dulu
    List<LaporanHarian> findByUserIdOrderByTanggalDesc(Long userId);

    // Mencari laporan berdasarkan user dan tanggal (cek apakah sudah ada laporan hari ini)
    Optional<LaporanHarian> findByUserIdAndTanggal(Long userId, LocalDate tanggal);
}
