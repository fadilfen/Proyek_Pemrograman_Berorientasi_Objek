package com.screentimetracker.demo.repository;

import com.screentimetracker.demo.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository untuk operasi database tabel 'payments'.
 * Menggantikan TopUpRepository lama dengan fitur tambahan
 * untuk manajemen pembayaran oleh admin.
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Mengambil semua pembayaran milik user tertentu, terbaru dulu.
     */
    List<Payment> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Mengambil pembayaran terakhir oleh user (untuk konfirmasi QRIS).
     */
    Optional<Payment> findTopByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Mengambil semua pembayaran berdasarkan status (untuk admin).
     * Contoh: status = "PENDING" untuk melihat yang menunggu verifikasi.
     */
    List<Payment> findByStatusOrderByCreatedAtDesc(String status);

    /**
     * Mengambil semua pembayaran diurutkan terbaru (untuk admin melihat semua).
     */
    List<Payment> findAllByOrderByCreatedAtDesc();

    /**
     * Menghitung jumlah pembayaran yang masih PENDING.
     * Digunakan untuk badge notifikasi admin.
     */
    long countByStatus(String status);
}
