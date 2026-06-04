package com.screentimetracker.demo.service;

import com.screentimetracker.demo.model.*;
import com.screentimetracker.demo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * MindFullService adalah lapisan service utama aplikasi MindFull.
 * Menangani logika bisnis inti: autentikasi, aktivitas digital,
 * top up token, laporan harian, dan registrasi admin.
 *
 * Versi 2.0: ditambahkan fitur filter tanggal, registrasi admin,
 * dan integrasi dengan NotificationService.
 */
@Service
@Transactional
public class MindFullService {

    private final UserRepository              userRepo;
    private final AktivitasDigitalRepository  aktivitasRepo;
    private final PaymentRepository           paymentRepo;
    private final AdminTokenRepository        adminTokenRepo;
    private final NotificationService         notifService;

    // Injeksi dependency melalui konstruktor (best practice Spring)
    public MindFullService(UserRepository userRepo,
                           AktivitasDigitalRepository aktivitasRepo,
                           PaymentRepository paymentRepo,
                           AdminTokenRepository adminTokenRepo,
                           NotificationService notifService) {
        this.userRepo       = userRepo;
        this.aktivitasRepo  = aktivitasRepo;
        this.paymentRepo    = paymentRepo;
        this.adminTokenRepo = adminTokenRepo;
        this.notifService   = notifService;
    }

    // ── AUTENTIKASI ───────────────────────────────────────────────────────

    /**
     * Memvalidasi username dan password saat login.
     * Hanya user yang aktif (isActive = true) yang bisa login.
     * Mengembalikan objek User jika berhasil, null jika gagal.
     */
    public User login(String username, String password) {
        return userRepo.findByUsername(username)
                .filter(u -> u.getPassword().equals(password) && u.isActive())
                .orElse(null);
    }

    /**
     * Mendaftarkan pengguna baru dengan role USER.
     * Role USER ditetapkan otomatis — tidak perlu dipilih pengguna.
     * Mengembalikan false jika username sudah digunakan.
     */
    public boolean register(String username, String password, String namaLengkap) {
        if (userRepo.existsByUsername(username)) return false;
        User user = new User(namaLengkap, username, password);
        user.setRole("USER");
        userRepo.save(user);

        // Buat notifikasi selamat datang
        notifService.buatNotifikasi(
            user,
            "Selamat Datang di MindFull! 🎉",
            "Halo " + namaLengkap + "! Mulai catat aktivitas digitalmu hari ini.",
            "SUCCESS",
            "/activity"
        );
        return true;
    }

    /**
     * Mendaftarkan akun admin baru dengan validasi token rahasia.
     * Token harus ada di tabel admin_tokens dan masih aktif.
     *
     * @param username    Username admin baru
     * @param password    Password admin baru
     * @param namaLengkap Nama lengkap admin
     * @param adminToken  Token rahasia admin
     * @return true jika berhasil, false jika token salah atau username sudah ada
     */
    public boolean registerAdmin(String username, String password,
                                 String namaLengkap, String adminToken) {
        // Validasi token admin
        if (!adminTokenRepo.existsByTokenAndIsActiveTrue(adminToken)) {
            return false;
        }
        // Cek duplikat username
        if (userRepo.existsByUsername(username)) return false;

        // Buat akun dengan role ADMIN
        User admin = new User(namaLengkap, username, password, "ADMIN");
        userRepo.save(admin);
        return true;
    }

    /**
     * Memperbarui username dan password pengguna.
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
     * Jika durasi melebihi batas, kirim notifikasi peringatan.
     * Mengembalikan null jika berhasil, atau String pesan error jika gagal.
     *
     * Validasi:
     * - User harus ada dan memiliki minimal 5 token.
     * - Tidak boleh ada entri duplikat untuk aplikasi yang sama di tanggal yang sama.
     */
    public String tambahAktivitas(Long userId, String namaAplikasi,
                                   int durasi, java.time.LocalTime jamMulai, LocalDate tanggal) {
        User user = userRepo.findById(userId).orElse(null);

        // Validasi: user harus ada dan memiliki minimal 5 token
        if (user == null || user.getToken() < 5) {
            return "Token tidak cukup! Minimal 5 token untuk log aktivitas.";
        }

        // Validasi: cek duplikat — tidak boleh set screen time untuk aplikasi yang sama di hari yang sama
        if (aktivitasRepo.existsByUserIdAndNamaAplikasiIgnoreCaseAndTanggal(userId, namaAplikasi, tanggal)) {
            return "Anda sudah mencatat screen time untuk " + namaAplikasi + " pada tanggal ini. "
                 + "Setiap aplikasi hanya boleh dicatat satu kali per hari.";
        }

        // Set batas durasi sama dengan durasi menit untuk menjaga kompatibilitas DB
        AktivitasDigital aktivitas = new AktivitasDigital(namaAplikasi, durasi, durasi, jamMulai, tanggal, user);
        aktivitasRepo.save(aktivitas);

        // Kurangi 5 token setelah berhasil log aktivitas
        user.setToken(user.getToken() - 5);
        userRepo.save(user);

        // Kirim notifikasi token hampir habis
        if (user.getToken() <= 10 && user.getToken() > 0) {
            notifService.buatNotifikasi(
                user,
                "🪙 Token Hampir Habis",
                "Saldo token kamu tersisa " + user.getToken() + " token. Segera top up agar bisa terus mencatat aktivitas.",
                "WARNING",
                "/topup"
            );
        }

        return null; // null berarti sukses
    }

    /**
     * Mengambil semua aktivitas user, diurutkan terbaru dulu.
     */
    public List<AktivitasDigital> getAktivitasByUser(Long userId) {
        return aktivitasRepo.findByUserIdOrderByTanggalDesc(userId);
    }

    /**
     * Mengambil aktivitas user berdasarkan tanggal tertentu (filter harian).
     * @param userId  ID pengguna
     * @param tanggal Tanggal yang dicari
     */
    public List<AktivitasDigital> getAktivitasByUserAndTanggal(Long userId, LocalDate tanggal) {
        return aktivitasRepo.findByUserIdAndTanggalOrderByTanggalDesc(userId, tanggal);
    }

    /**
     * Mengambil aktivitas user dalam rentang tanggal (filter periode).
     * @param userId    ID pengguna
     * @param startDate Tanggal awal rentang
     * @param endDate   Tanggal akhir rentang
     */
    public List<AktivitasDigital> getAktivitasByUserAndDateRange(
            Long userId, LocalDate startDate, LocalDate endDate) {
        return aktivitasRepo.findByUserIdAndTanggalBetween(userId, startDate, endDate);
    }

    // ── TOP UP TOKEN ──────────────────────────────────────────────────────

    /**
     * Kelas internal untuk hasil proses top up.
     */
    public static class TopUpResult {
        private final boolean success;
        private final String  message;
        private final String  snapToken;

        public TopUpResult(boolean success, String message) {
            this.success   = success;
            this.message   = message;
            this.snapToken = null;
        }

        public TopUpResult(boolean success, String message, String snapToken) {
            this.success   = success;
            this.message   = message;
            this.snapToken = snapToken;
        }

        public boolean isSuccess()    { return success; }
        public String getMessage()    { return message; }
        public String getSnapToken()  { return snapToken; }
    }

    /**
     * Memproses transaksi top up token untuk user.
     * Semua transaksi dimulai dengan status PENDING (menunggu verifikasi admin).
     * Untuk QRIS, tampilkan QR code warung statis.
     */
    public TopUpResult prosesTopUp(Long userId, int jumlah, String metode) {
        User user = userRepo.findById(userId).orElse(null);

        // Validasi input
        if (user == null || jumlah <= 0) {
            return new TopUpResult(false, "User tidak ditemukan atau jumlah tidak valid.");
        }

        // Hitung total harga (1 token = Rp 1.000)
        long totalHarga = (long) jumlah * 1000;

        // Simpan transaksi dengan status PENDING
        Payment payment = new Payment(jumlah, metode, totalHarga, user);
        payment.setStatus("PENDING");
        paymentRepo.save(payment);

        // Kirim notifikasi pembayaran terkirim
        notifService.buatNotifikasi(
            user,
            "💳 Top Up Terkirim",
            "Permintaan top up " + jumlah + " token (Rp " + String.format("%,d", totalHarga) +
            ") via " + metode + " sedang menunggu verifikasi admin.",
            "INFO",
            "/topup"
        );

        if ("QRIS (Instant)".equals(metode)) {
            return new TopUpResult(true, "QRIS siap. Silakan scan QR code.", "qris-warung.png");
        }

        return new TopUpResult(true, "Permintaan top up berhasil dikirim! Menunggu verifikasi admin.");
    }

    /**
     * Konfirmasi QRIS manual untuk user (sementara token langsung dikreditkan).
     * Metode ini dipertahankan untuk backward compatibility.
     */
    public boolean confirmQrisTopUp(Long userId) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) return false;

        Optional<Payment> latestOpt = paymentRepo.findTopByUserIdOrderByCreatedAtDesc(userId);
        if (latestOpt.isEmpty()) return false;

        Payment latest = latestOpt.get();
        if (!"QRIS (Instant)".equals(latest.getMetode()) || !"PENDING".equals(latest.getStatus())) {
            return false;
        }

        // Status dipertahankan PENDING untuk ditinjau admin
        latest.setStatus("PENDING");
        paymentRepo.save(latest);

        notifService.buatNotifikasi(
            user,
            "⏳ Pembayaran QRIS Sedang Ditinjau",
            "Konfirmasi pembayaran Anda untuk " + latest.getJumlahToken() + " token sedang ditinjau oleh admin.",
            "INFO",
            "/topup"
        );
        return true;
    }

    /**
     * Handle pembayaran berhasil dari Midtrans (webhook).
     */
    public boolean handlePaymentSuccess(String orderId) {
        if (!orderId.startsWith("TOPUP-")) return false;
        String[] parts = orderId.split("-");
        if (parts.length < 3) return false;

        try {
            Long userId = Long.parseLong(parts[1]);
            User user = userRepo.findById(userId).orElse(null);
            if (user == null) return false;

            Optional<Payment> latestOpt = paymentRepo.findTopByUserIdOrderByCreatedAtDesc(userId);
            if (latestOpt.isEmpty()) return false;

            Payment latest = latestOpt.get();
            if (latest.isVerified()) return false;

            user.setToken(user.getToken() + latest.getJumlahToken());
            latest.setStatus("VERIFIED");
            latest.setVerifiedAt(java.time.LocalDateTime.now());
            userRepo.save(user);
            paymentRepo.save(latest);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ── LAPORAN HARIAN ────────────────────────────────────────────────────

    /**
     * Menghasilkan data laporan kesehatan untuk user.
     * Mengembalikan objek LaporanData yang digunakan untuk generate PDF.
     * @param userId   ID pengguna
     * @param tanggal  Tanggal laporan (null = ambil semua)
     */
    public LaporanData generateLaporanData(Long userId, LocalDate tanggal) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) return null;

        List<AktivitasDigital> list;
        if (tanggal != null) {
            list = aktivitasRepo.findByUserIdAndTanggalOrderByTanggalDesc(userId, tanggal);
        } else {
            list = aktivitasRepo.findByUserIdOrderByTanggalDesc(userId);
        }

        // Hitung total durasi
        int totalDurasi = list.stream().mapToInt(AktivitasDigital::getDurasiMenit).sum();

        // Hitung skor kesehatan
        int score = 100;
        for (AktivitasDigital a : list) {
            score -= a.getDurasiMenit() / 10;
            if (a.melebihiBatas()) score -= 20;
        }
        score = Math.max(0, score);

        // Tentukan kategori
        String kategori;
        if (score >= 80)      kategori = "Sangat Sehat";
        else if (score >= 60) kategori = "Sehat";
        else if (score >= 40) kategori = "Perlu Perhatian";
        else                  kategori = "Berbahaya";

        // Susun ringkasan
        String ringkasan = buildRingkasan(user, list, totalDurasi, score, kategori);

        // Susun rekomendasi
        String rekomendasi = buildRekomendasi(score, list);

        return new LaporanData(user, list, totalDurasi, score, kategori,
                               ringkasan, rekomendasi,
                               tanggal != null ? tanggal : LocalDate.now());
    }

    /** Membangun teks ringkasan laporan. */
    private String buildRingkasan(User user, List<AktivitasDigital> list,
                                   int totalDurasi, int score, String kategori) {
        StringBuilder sb = new StringBuilder();
        sb.append("Laporan Digital Wellness – ").append(user.getNamaUser()).append("\n");
        sb.append("Total Screen Time: ").append(totalDurasi).append(" menit (")
          .append(totalDurasi / 60).append(" jam ").append(totalDurasi % 60).append(" menit)\n");
        sb.append("Skor Kesehatan: ").append(score).append("/100 — ").append(kategori).append("\n\n");
        sb.append("Detail Penggunaan:\n");
        for (AktivitasDigital a : list) {
            sb.append("• ").append(a.getNamaAplikasi())
              .append(" — ").append(a.getDurasiMenit()).append(" menit");
            if (a.melebihiBatas()) sb.append(" ⚠️ Over Limit");
            sb.append("\n");
        }
        return sb.toString();
    }

    /** Membangun rekomendasi berdasarkan skor kesehatan. */
    private String buildRekomendasi(int score, List<AktivitasDigital> list) {
        if (score >= 80) {
            return "Hebat! Penggunaan layar kamu sangat sehat. Pertahankan kebiasaan baik ini. " +
                   "Terus pantau aktivitas digital kamu setiap hari untuk menjaga keseimbangan.";
        } else if (score >= 60) {
            return "Penggunaan layar kamu masih dalam batas wajar. Coba kurangi sedikit waktu " +
                   "screen time dan tambah aktivitas fisik atau istirahat yang cukup.";
        } else if (score >= 40) {
            return "Perhatian! Screen time kamu cukup tinggi. Pertimbangkan untuk:\n" +
                   "• Menetapkan batas waktu untuk setiap aplikasi\n" +
                   "• Melakukan digital detox minimal 1 jam per hari\n" +
                   "• Meningkatkan aktivitas non-digital";
        } else {
            return "PERINGATAN: Screen time kamu sangat tinggi dan berisiko bagi kesehatan mental. " +
                   "Sangat disarankan untuk segera mengurangi penggunaan layar dan konsultasi " +
                   "dengan profesional kesehatan jika diperlukan.";
        }
    }

    /**
     * Kelas data untuk hasil laporan (DTO).
     */
    public static class LaporanData {
        public final User                    user;
        public final List<AktivitasDigital>  aktivitasList;
        public final int                     totalDurasi;
        public final int                     skor;
        public final String                  kategori;
        public final String                  ringkasan;
        public final String                  rekomendasi;
        public final LocalDate               tanggal;

        public LaporanData(User user, List<AktivitasDigital> aktivitasList,
                           int totalDurasi, int skor, String kategori,
                           String ringkasan, String rekomendasi, LocalDate tanggal) {
            this.user          = user;
            this.aktivitasList = aktivitasList;
            this.totalDurasi   = totalDurasi;
            this.skor          = skor;
            this.kategori      = kategori;
            this.ringkasan     = ringkasan;
            this.rekomendasi   = rekomendasi;
            this.tanggal       = tanggal;
        }
    }

    /**
     * Menghasilkan laporan dalam bentuk teks (untuk backward compatibility).
     */
    public String generateLaporan(Long userId) {
        LaporanData data = generateLaporanData(userId, null);
        if (data == null) return "";
        return data.ringkasan + "\n\nRekomendasi:\n" + data.rekomendasi;
    }

    /**
     * Mengirimkan notifikasi ke sistem saat aplikasi mencapai batas waktu screen time (over limit).
     */
    public void kirimNotifikasiOverLimit(Long userId, String appName) {
        User user = userRepo.findById(userId).orElse(null);
        if (user != null) {
            notifService.buatNotifikasi(
                user,
                "⚠️ Batas Waktu Habis: " + appName,
                "Halo " + user.getNamaUser() + ", batas waktu untuk aplikasi " + appName + " sudah habis.",
                "DANGER",
                "/my-apps"
            );
        }
    }
}
