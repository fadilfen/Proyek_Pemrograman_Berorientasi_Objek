package com.screentimetracker.demo.repository;

import com.screentimetracker.demo.model.AktivitasDigital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository untuk operasi database tabel 'aktivitas_digital'.
 * Menyediakan query custom untuk filter berdasarkan tanggal
 * yang diperlukan oleh fitur filter laporan harian.
 */
public interface AktivitasDigitalRepository extends JpaRepository<AktivitasDigital, Long> {

    /**
     * Mengambil semua aktivitas milik user tertentu, diurutkan terbaru dulu.
     * Digunakan di halaman Activity Tracker tanpa filter.
     */
    List<AktivitasDigital> findByUserIdOrderByTanggalDesc(Long userId);

    /**
     * Mengambil semua aktivitas milik user (untuk kompatibilitas lama).
     */
    List<AktivitasDigital> findByUserId(Long userId);

    /**
     * Filter aktivitas berdasarkan tanggal tertentu (untuk filter harian).
     * @param userId   ID pengguna
     * @param tanggal  Tanggal yang dicari
     */
    List<AktivitasDigital> findByUserIdAndTanggalOrderByTanggalDesc(
            Long userId, LocalDate tanggal);

    /**
     * Filter aktivitas dalam rentang tanggal (start date - end date).
     * Digunakan untuk filter laporan berdasarkan periode.
     * @param userId    ID pengguna
     * @param startDate Tanggal awal rentang
     * @param endDate   Tanggal akhir rentang
     */
    @Query("SELECT a FROM AktivitasDigital a WHERE a.user.id = :userId " +
           "AND a.tanggal BETWEEN :startDate AND :endDate " +
           "ORDER BY a.tanggal DESC")
    List<AktivitasDigital> findByUserIdAndTanggalBetween(
            @Param("userId")    Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate")   LocalDate endDate);

    /**
     * Menghitung jumlah aktivitas yang melebihi batas pada tanggal tertentu.
     */
    @Query("SELECT COUNT(a) FROM AktivitasDigital a " +
           "WHERE a.user.id = :userId AND a.tanggal = :tanggal " +
           "AND a.durasiMenit > a.batasDurasi")
    long countOverLimitByUserAndDate(
            @Param("userId")  Long userId,
            @Param("tanggal") LocalDate tanggal);
}
