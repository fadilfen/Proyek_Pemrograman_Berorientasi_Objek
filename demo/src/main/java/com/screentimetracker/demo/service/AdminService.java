package com.screentimetracker.demo.service;

import com.screentimetracker.demo.model.Payment;
import com.screentimetracker.demo.model.User;
import com.screentimetracker.demo.repository.PaymentRepository;
import com.screentimetracker.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * AdminService menangani semua operasi yang hanya bisa dilakukan oleh ADMIN.
 * Meliputi:
 * - User Management: lihat, edit, nonaktifkan, hapus user
 * - Payment Management: lihat, verifikasi, ubah status, hapus pembayaran
 *
 * Semua method di sini HANYA dipanggil dari AdminController
 * yang sudah diproteksi dengan cek role ADMIN.
 */
@Service
@Transactional
public class AdminService {

    private final UserRepository    userRepo;
    private final PaymentRepository paymentRepo;
    private final NotificationService notifService;

    public AdminService(UserRepository userRepo,
                        PaymentRepository paymentRepo,
                        NotificationService notifService) {
        this.userRepo     = userRepo;
        this.paymentRepo  = paymentRepo;
        this.notifService = notifService;
    }

    // ── USER MANAGEMENT ───────────────────────────────────────────────────

    /**
     * Mengambil semua user untuk ditampilkan di halaman admin.
     * Diurutkan berdasarkan tanggal daftar terbaru.
     */
    public List<User> getAllUsers() {
        return userRepo.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Mengambil user berdasarkan ID.
     */
    public Optional<User> getUserById(Long id) {
        return userRepo.findById(id);
    }

    /**
     * Memperbarui data user (nama, username, role).
     * Admin bisa mengubah role user menjadi ADMIN atau USER.
     * @return true jika berhasil, false jika username sudah dipakai user lain
     */
    public boolean updateUser(Long userId, String namaUser,
                               String username, String role, boolean isActive) {
        Optional<User> opt = userRepo.findById(userId);
        if (opt.isEmpty()) return false;

        User user = opt.get();

        // Cek duplikat username
        if (!user.getUsername().equals(username) && userRepo.existsByUsername(username)) {
            return false;
        }

        user.setNamaUser(namaUser);
        user.setUsername(username);
        user.setRole(role);
        user.setActive(isActive);
        userRepo.save(user);
        return true;
    }

    /**
     * Menonaktifkan user (tidak bisa login, akun tetap ada).
     * Admin bisa mengaktifkan kembali dengan isActive = true.
     * @param userId   ID user yang akan dinonaktifkan
     * @param isActive true = aktifkan, false = nonaktifkan
     */
    public boolean toggleUserStatus(Long userId, boolean isActive) {
        Optional<User> opt = userRepo.findById(userId);
        if (opt.isEmpty()) return false;

        User user = opt.get();
        // Tidak bisa menonaktifkan diri sendiri
        if ("ADMIN".equals(user.getRole()) && !isActive) {
            // Cek apakah ada admin lain
            long adminCount = userRepo.findByRole("ADMIN").size();
            if (adminCount <= 1) return false; // Harus ada minimal 1 admin
        }

        user.setActive(isActive);
        userRepo.save(user);
        return true;
    }

    /**
     * Menghapus user secara permanen dari database.
     * Semua data terkait (aktivitas, notifikasi, payment) ikut terhapus (CASCADE).
     */
    public boolean hapusUser(Long userId) {
        if (!userRepo.existsById(userId)) return false;
        userRepo.deleteById(userId);
        return true;
    }

    /**
     * Menghitung statistik singkat untuk dashboard admin.
     */
    public AdminStats getStats() {
        long totalUsers    = userRepo.count();
        long totalAdmins   = userRepo.findByRole("ADMIN").size();
        long pendingPayments = paymentRepo.countByStatus("PENDING");
        long totalPayments = paymentRepo.count();
        return new AdminStats(totalUsers, totalAdmins, pendingPayments, totalPayments);
    }

    // ── PAYMENT MANAGEMENT ────────────────────────────────────────────────

    /**
     * Mengambil semua data pembayaran untuk manajemen admin.
     * Diurutkan berdasarkan waktu terbaru.
     */
    public List<Payment> getAllPayments() {
        return paymentRepo.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Mengambil pembayaran berdasarkan status tertentu.
     * @param status PENDING / VERIFIED / REJECTED
     */
    public List<Payment> getPaymentsByStatus(String status) {
        return paymentRepo.findByStatusOrderByCreatedAtDesc(status);
    }

    /**
     * Memverifikasi pembayaran: ubah status ke VERIFIED dan tambahkan token ke user.
     * @param paymentId ID payment yang diverifikasi
     * @param adminId   ID admin yang melakukan verifikasi
     */
    public boolean verifyPayment(Long paymentId, Long adminId) {
        Optional<Payment> opt = paymentRepo.findById(paymentId);
        if (opt.isEmpty()) return false;

        Payment payment = opt.get();
        if (!payment.isPending()) return false; // Hanya PENDING yang bisa diverifikasi

        User user = payment.getUser();

        // Tambahkan token ke user
        user.setToken(user.getToken() + payment.getJumlahToken());
        userRepo.save(user);

        // Update status payment
        payment.setStatus("VERIFIED");
        payment.setVerifiedBy(adminId);
        payment.setVerifiedAt(LocalDateTime.now());
        paymentRepo.save(payment);

        // Kirim notifikasi ke user
        notifService.buatNotifikasi(
            user,
            "✅ Pembayaran Diverifikasi!",
            "Top up " + payment.getJumlahToken() + " token via " + payment.getMetode() +
            " telah diverifikasi. Token sudah ditambahkan ke akun kamu.",
            "SUCCESS",
            "/topup"
        );
        return true;
    }

    /**
     * Menolak pembayaran: ubah status ke REJECTED.
     * @param paymentId ID payment yang ditolak
     * @param adminId   ID admin yang menolak
     */
    public boolean rejectPayment(Long paymentId, Long adminId) {
        Optional<Payment> opt = paymentRepo.findById(paymentId);
        if (opt.isEmpty()) return false;

        Payment payment = opt.get();
        payment.setStatus("REJECTED");
        payment.setVerifiedBy(adminId);
        payment.setVerifiedAt(LocalDateTime.now());
        paymentRepo.save(payment);

        // Kirim notifikasi penolakan ke user
        notifService.buatNotifikasi(
            payment.getUser(),
            "❌ Pembayaran Ditolak",
            "Top up " + payment.getJumlahToken() + " token via " + payment.getMetode() +
            " tidak dapat diverifikasi. Silakan hubungi admin untuk informasi lebih lanjut.",
            "DANGER",
            "/topup"
        );
        return true;
    }

    /**
     * Mengubah status pembayaran secara manual.
     * @param paymentId ID payment
     * @param status    Status baru: PENDING / VERIFIED / REJECTED
     */
    public boolean updatePaymentStatus(Long paymentId, String status, Long adminId) {
        Optional<Payment> opt = paymentRepo.findById(paymentId);
        if (opt.isEmpty()) return false;

        Payment payment = opt.get();
        String oldStatus = payment.getStatus();

        // Jika diubah dari non-VERIFIED ke VERIFIED, tambahkan token
        if ("VERIFIED".equals(status) && !"VERIFIED".equals(oldStatus)) {
            User user = payment.getUser();
            user.setToken(user.getToken() + payment.getJumlahToken());
            userRepo.save(user);
        }

        // Jika diubah dari VERIFIED ke status lain, kurangi token
        if (!"VERIFIED".equals(status) && "VERIFIED".equals(oldStatus)) {
            User user = payment.getUser();
            user.setToken(Math.max(0, user.getToken() - payment.getJumlahToken()));
            userRepo.save(user);
        }

        payment.setStatus(status);
        payment.setVerifiedBy(adminId);
        payment.setVerifiedAt(LocalDateTime.now());
        paymentRepo.save(payment);
        return true;
    }

    /**
     * Menghapus data pembayaran secara permanen.
     */
    public boolean hapusPayment(Long paymentId) {
        if (!paymentRepo.existsById(paymentId)) return false;
        paymentRepo.deleteById(paymentId);
        return true;
    }

    /**
     * Kelas DTO untuk statistik dashboard admin.
     */
    public static class AdminStats {
        public final long totalUsers;
        public final long totalAdmins;
        public final long pendingPayments;
        public final long totalPayments;

        public AdminStats(long totalUsers, long totalAdmins,
                          long pendingPayments, long totalPayments) {
            this.totalUsers      = totalUsers;
            this.totalAdmins     = totalAdmins;
            this.pendingPayments = pendingPayments;
            this.totalPayments   = totalPayments;
        }
    }
}
