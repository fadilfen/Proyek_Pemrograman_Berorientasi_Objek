package com.screentimetracker.demo.repository;

import com.screentimetracker.demo.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository untuk operasi database tabel 'notifications'.
 * Mendukung sistem notifikasi dropdown di navbar dengan fitur:
 * - Ambil notifikasi per user
 * - Hitung notifikasi belum dibaca (untuk badge angka)
 * - Tandai baca satu / tandai semua baca
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Mengambil semua notifikasi milik user, diurutkan terbaru dulu.
     * Digunakan untuk menampilkan daftar di dropdown navbar.
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Mengambil 10 notifikasi terbaru milik user untuk dropdown navbar.
     */
    List<Notification> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Menghitung jumlah notifikasi yang belum dibaca.
     * Digunakan untuk menampilkan badge angka merah di ikon lonceng.
     */
    long countByUserIdAndIsReadFalse(Long userId);

    /**
     * Mengambil semua notifikasi yang belum dibaca oleh user tertentu.
     */
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    /**
     * Menandai semua notifikasi user sebagai sudah dibaca.
     * Digunakan saat user klik "Tandai Semua Sudah Dibaca".
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId")
    void markAllAsReadByUserId(@Param("userId") Long userId);
}
