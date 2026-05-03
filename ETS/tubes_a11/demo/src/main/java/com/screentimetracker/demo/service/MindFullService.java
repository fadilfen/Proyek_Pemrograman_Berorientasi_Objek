package com.screentimetracker.demo.service;

import com.midtrans.httpclient.error.MidtransError;
import com.screentimetracker.demo.model.User;
import com.screentimetracker.demo.model.AktivitasDigital;
import com.screentimetracker.demo.model.TopUp;
import com.screentimetracker.demo.repository.UserRepository;
import com.screentimetracker.demo.repository.AktivitasDigitalRepository;
import com.screentimetracker.demo.repository.TopUpRepository;
import com.screentimetracker.demo.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * MindFullService adalah lapisan service yang menangani semua logika bisnis aplikasi.
 * Menggabungkan fungsi-fungsi dari UserManager, User, AktivitasDigital,
 * TopUp, dan LaporanHarian yang ada di proyek GUI ke dalam satu service terpusat.
 */
@Service
@Transactional
public class MindFullService {

    private final UserRepository            userRepo;
    private final AktivitasDigitalRepository aktivitasRepo;
    private final TopUpRepository           topUpRepo;
    private final PaymentService            paymentService;

    // Injeksi dependency melalui konstruktor (best practice Spring)
    public MindFullService(UserRepository userRepo,
                           AktivitasDigitalRepository aktivitasRepo,
                           TopUpRepository topUpRepo,
                           PaymentService paymentService) {
        this.userRepo      = userRepo;
        this.aktivitasRepo = aktivitasRepo;
        this.topUpRepo     = topUpRepo;
        this.paymentService = paymentService;
    }

    // ── AUTENTIKASI ───────────────────────────────────────────────────────

    /**
     * Memvalidasi username dan password saat login.
     * Menggantikan fungsi login() di UserManager.java proyek GUI.
     * Mengembalikan objek User jika berhasil, null jika gagal.
     */
    public User login(String username, String password) {
        return userRepo.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .orElse(null);
    }

    /**
     * Mendaftarkan pengguna baru ke database.
     * Menggantikan fungsi register() di UserManager.java proyek GUI.
     * Mengembalikan false jika username sudah digunakan.
     */
    public boolean register(String username, String password, String namaLengkap) {
        if (userRepo.existsByUsername(username)) return false;
        userRepo.save(new User(namaLengkap, username, password));
        return true;
    }

    /**
     * Memperbarui username dan password pengguna.
     * Menggantikan fungsi updateCredentials() di UserManager.java proyek GUI.
     * Mengembalikan false jika username baru sudah dipakai user lain.
     */
    public boolean updateCredentials(Long userId, String newUsername, String newPassword) {
        Optional<User> opt = userRepo.findById(userId);
        if (opt.isEmpty()) return false;

        User user = opt.get();

        // Cek apakah username baru sudah dipakai user lain
        if (!user.getUsername().equals(newUsername) && userRepo.existsByUsername(newUsername)) {
            return false;
        }

        user.setUsername(newUsername);
        user.setPassword(newPassword);
        userRepo.save(user);
        return true;
    }

    /**
     * Mengambil data user berdasarkan ID.
     */
    public User getUser(Long id) {
        return userRepo.findById(id).orElse(null);
    }

    // ── AKTIVITAS DIGITAL ─────────────────────────────────────────────────

    /**
     * Menambahkan aktivitas digital baru dan memotong 5 token dari user.
     * Menggantikan fungsi tambahAktivitas() di User.java proyek GUI.
     * Mengembalikan false jika token tidak cukup (minimal 5 token).
     */
    public boolean tambahAktivitas(Long userId, String namaAplikasi,
                                   int durasi, int batas, LocalDate tanggal) {
        User user = userRepo.findById(userId).orElse(null);

        // Validasi: user harus ada dan memiliki minimal 5 token
        if (user == null || user.getToken() < 5) return false;

        AktivitasDigital aktivitas = new AktivitasDigital(namaAplikasi, durasi, batas, tanggal, user);
        aktivitasRepo.save(aktivitas);

        // Kurangi 5 token setelah berhasil log aktivitas
        user.setToken(user.getToken() - 5);
        userRepo.save(user);
        return true;
    }

    /**
     * Mengambil semua aktivitas digital milik user tertentu.
     */
    public List<AktivitasDigital> getAktivitasByUser(Long userId) {
        return aktivitasRepo.findByUserId(userId);
    }

    // ── TOP UP TOKEN ──────────────────────────────────────────────────────

    /**
     * Hasil proses top up.
     */
    public static class TopUpResult {
        private boolean success;
        private String message;
        private String snapToken; // untuk QRIS

        public TopUpResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public TopUpResult(boolean success, String message, String snapToken) {
            this.success = success;
            this.message = message;
            this.snapToken = snapToken;
        }

        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getSnapToken() { return snapToken; }
    }

    /**
     * Memproses transaksi top up token untuk user.
     * Jika metode QRIS, tampilkan QRIS warung statis dan simpan transaksi sebagai belum dibayar.
     * Jika metode lain, langsung proses top up.
     * Menggantikan fungsi prosesTopUp() di TopUp.java proyek GUI.
     */
    public TopUpResult prosesTopUp(Long userId, int jumlah, String metode) {
        User user = userRepo.findById(userId).orElse(null);

        // Validasi: user harus ada dan jumlah harus lebih dari 0
        if (user == null || jumlah <= 0) {
            return new TopUpResult(false, "User tidak ditemukan atau jumlah tidak valid.");
        }

        if ("QRIS (Instant)".equals(metode)) {
            // Untuk QRIS sederhana, tampilkan QR code warung statis dan simpan transaksi sebagai belum dibayar
            TopUp topUp = new TopUp(jumlah, metode, user);
            topUpRepo.save(topUp);

            return new TopUpResult(true, "QRIS siap. Silakan scan QR code warung.", "qris-warung.png");
        } else {
            // Untuk metode lain, langsung proses
            TopUp topUp = new TopUp(jumlah, metode, user);
            topUpRepo.save(topUp);

            user.setToken(user.getToken() + jumlah);
            userRepo.save(user);
            return new TopUpResult(true, "Top up berhasil! Token telah ditambahkan.");
        }
    }

    /**
     * Konfirmasi top up QRIS manual: kredit token untuk top up QRIS terakhir yang belum dibayar.
     */
    public boolean confirmQrisTopUp(Long userId) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) return false;

        Optional<TopUp> latestOpt = topUpRepo.findTopByUserOrderByWaktuTopUpDesc(user);
        if (latestOpt.isEmpty()) return false;

        TopUp latest = latestOpt.get();
        if (!"QRIS (Instant)".equals(latest.getMetodePembayaran()) || latest.isPaid()) {
            return false;
        }

        user.setToken(user.getToken() + latest.getJumlahKoin());
        latest.setPaid(true);
        userRepo.save(user);
        topUpRepo.save(latest);
        return true;
    }

    // ── LAPORAN HARIAN ────────────────────────────────────────────────────

    /**
     * Handle pembayaran berhasil dari Midtrans.
     * Update token user berdasarkan order_id.
     */
    public boolean handlePaymentSuccess(String orderId) {
        // Parse orderId: "TOPUP-{userId}-{random}"
        if (!orderId.startsWith("TOPUP-")) return false;

        String[] parts = orderId.split("-");
        if (parts.length < 3) return false;

        try {
            Long userId = Long.parseLong(parts[1]);
            User user = userRepo.findById(userId).orElse(null);
            if (user == null) return false;

            // Cari topup terbaru untuk user ini dengan metode QRIS yang belum paid
            TopUp latestTopUp = topUpRepo.findTopByUserOrderByWaktuTopUpDesc(user)
                    .filter(t -> "QRIS (Instant)".equals(t.getMetodePembayaran()) && !t.isPaid())
                    .orElse(null);

            if (latestTopUp != null) {
                user.setToken(user.getToken() + latestTopUp.getJumlahKoin());
                userRepo.save(user);
                latestTopUp.setPaid(true);
                topUpRepo.save(latestTopUp);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * Menghasilkan laporan harian dalam bentuk teks.
     * Menggantikan fungsi generateLaporan() di LaporanHarian.java proyek GUI.
     */
    public String generateLaporan(Long userId) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) return "";

        List<AktivitasDigital> list = aktivitasRepo.findByUserId(userId);

        // Hitung total durasi screen time
        int totalDurasi = list.stream().mapToInt(AktivitasDigital::getDurasiMenit).sum();

        // Hitung skor kesehatan (sama dengan logika di proyek GUI)
        int score = 100;
        for (AktivitasDigital a : list) {
            score -= a.getDurasiMenit() / 10;
            if (a.melebihiBatas()) score -= 20;
        }
        score = Math.max(0, score);

        // Susun teks laporan
        StringBuilder sb = new StringBuilder();
        sb.append("=== LAPORAN HARIAN ===\n");
        sb.append("Nama User         : ").append(user.getNamaUser()).append("\n");
        if (!list.isEmpty()) {
            sb.append("Tanggal           : ").append(list.get(0).getTanggal()).append("\n");
        }
        sb.append("------------------------------\n");
        sb.append("Detail Aplikasi:\n");
        for (AktivitasDigital a : list) {
            sb.append("- ").append(a.getNamaAplikasi())
              .append(" : ").append(a.getDurasiMenit()).append(" menit\n");
        }
        sb.append("------------------------------\n");
        sb.append("Total Screen Time : ").append(totalDurasi).append(" menit\n");
        sb.append("Skor Harian       : ").append(score).append("\n");
        sb.append("Status            : ").append(score >= 70 ? "Sehat" : "Kurangi Screen Time");

        return sb.toString();
    }
}
