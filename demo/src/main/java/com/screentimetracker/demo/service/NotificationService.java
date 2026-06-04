package com.screentimetracker.demo.service;

import com.screentimetracker.demo.model.Notification;
import com.screentimetracker.demo.model.User;
import com.screentimetracker.demo.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * NotificationService mengelola semua operasi notifikasi.
 * Dipanggil secara otomatis oleh MindFullService saat terjadi event:
 * - Registrasi user baru → notifikasi selamat datang
 * - Aktivitas melebihi batas → notifikasi peringatan
 * - Token hampir habis → notifikasi pengingat top up
 * - Top up berhasil → notifikasi konfirmasi
 *
 * Service ini juga menyediakan endpoint untuk:
 * - Menampilkan notifikasi di dropdown navbar
 * - Menandai satu/semua notifikasi sebagai sudah dibaca
 * - Menghapus notifikasi
 */
@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notifRepo;

    public NotificationService(NotificationRepository notifRepo) {
        this.notifRepo = notifRepo;
    }

    // ── MEMBUAT NOTIFIKASI ────────────────────────────────────────────────

    /**
     * Membuat notifikasi baru untuk user tertentu.
     * Dipanggil internal oleh service lain saat terjadi event.
     *
     * @param user      Pengguna penerima notifikasi
     * @param judul     Judul singkat notifikasi
     * @param pesan     Pesan lengkap notifikasi
     * @param tipe      Tipe: INFO / SUCCESS / WARNING / DANGER
     * @param urlTarget URL tujuan saat notifikasi diklik (bisa null)
     */
    public Notification buatNotifikasi(User user, String judul, String pesan,
                                        String tipe, String urlTarget) {
        Notification notif = new Notification(judul, pesan, tipe, urlTarget, user);
        return notifRepo.save(notif);
    }

    // ── MEMBACA NOTIFIKASI ────────────────────────────────────────────────

    /**
     * Mengambil 10 notifikasi terbaru untuk ditampilkan di dropdown navbar.
     * @param userId ID pengguna
     */
    public List<Notification> getNotifikasiTerbaru(Long userId) {
        return notifRepo.findTop10ByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Mengambil semua notifikasi milik user.
     * @param userId ID pengguna
     */
    public List<Notification> getAllNotifikasi(Long userId) {
        return notifRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Menghitung jumlah notifikasi yang belum dibaca.
     * Digunakan untuk menampilkan badge angka di ikon lonceng navbar.
     * @param userId ID pengguna
     */
    public long countUnread(Long userId) {
        return notifRepo.countByUserIdAndIsReadFalse(userId);
    }

    // ── MENGUBAH STATUS NOTIFIKASI ────────────────────────────────────────

    /**
     * Menandai satu notifikasi sebagai sudah dibaca.
     * Dipanggil saat user mengklik notifikasi tertentu.
     * @param notifId ID notifikasi yang akan ditandai
     * @param userId  ID user (untuk validasi kepemilikan)
     */
    public boolean tandaiBaca(Long notifId, Long userId) {
        return notifRepo.findById(notifId)
                .filter(n -> n.getUser().getId().equals(userId))
                .map(n -> {
                    n.setRead(true);
                    notifRepo.save(n);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Menandai semua notifikasi user sebagai sudah dibaca.
     * Dipanggil saat user klik "Tandai Semua Sudah Dibaca".
     * @param userId ID pengguna
     */
    public void tandaiSemuaBaca(Long userId) {
        notifRepo.markAllAsReadByUserId(userId);
    }

    /**
     * Menghapus satu notifikasi.
     * Hanya bisa menghapus notifikasi milik sendiri.
     * @param notifId ID notifikasi yang akan dihapus
     * @param userId  ID user (untuk validasi kepemilikan)
     */
    public boolean hapusNotifikasi(Long notifId, Long userId) {
        return notifRepo.findById(notifId)
                .filter(n -> n.getUser().getId().equals(userId))
                .map(n -> {
                    notifRepo.delete(n);
                    return true;
                })
                .orElse(false);
    }
}
