package com.screentimetracker.demo.service;

import com.screentimetracker.demo.model.AktivitasDigital;
import com.screentimetracker.demo.model.LaporanHarian;
import com.screentimetracker.demo.model.Notifikasi;
import com.screentimetracker.demo.model.TopUp;
import com.screentimetracker.demo.model.User;
import com.screentimetracker.demo.model.Role;
import com.screentimetracker.demo.repository.AktivitasDigitalRepository;
import com.screentimetracker.demo.repository.LaporanHarianRepository;
import com.screentimetracker.demo.repository.NotifikasiRepository;
import com.screentimetracker.demo.repository.TopUpRepository;
import com.screentimetracker.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * MindFullService adalah lapisan service yang menangani semua logika bisnis aplikasi.
 * Menggabungkan fungsi-fungsi dari UserManager, User, AktivitasDigital,
 * TopUp, LaporanHarian, dan Notifikasi yang ada di proyek GUI ke dalam satu service terpusat.
 */
@Service
@Transactional
public class MindFullService {

    private final UserRepository             userRepo;
    private final AktivitasDigitalRepository aktivitasRepo;
    private final TopUpRepository            topUpRepo;
    private final NotifikasiRepository       notifikasiRepo;
    private final LaporanHarianRepository    laporanRepo;

    // Injeksi dependency melalui konstruktor (best practice Spring)
    public MindFullService(UserRepository userRepo,
                           AktivitasDigitalRepository aktivitasRepo,
                           TopUpRepository topUpRepo,
                           NotifikasiRepository notifikasiRepo,
                           LaporanHarianRepository laporanRepo) {
        this.userRepo       = userRepo;
        this.aktivitasRepo  = aktivitasRepo;
        this.topUpRepo      = topUpRepo;
        this.notifikasiRepo = notifikasiRepo;
        this.laporanRepo    = laporanRepo;
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
     * Mengembalikan pesan error jika gagal, atau null jika berhasil.
     */
    public String register(String username, String password, String namaLengkap, Role role, String parentUsername) {
        if (userRepo.existsByUsername(username)) return "Username sudah terdaftar!";

        User user = new User(namaLengkap, username, password, role);

        if (role == Role.ANAK) {
            if (parentUsername == null || parentUsername.trim().isEmpty()) {
                return "Username orang tua wajib diisi untuk mendaftar sebagai anak!";
            }
            User parent = userRepo.findByUsername(parentUsername).orElse(null);
            if (parent == null) {
                return "Username orang tua tidak ditemukan!";
            }
            if (parent.getRole() != Role.ORANG_TUA) {
                return "Akun tersebut bukan akun Orang Tua!";
            }
            user.setParent(parent);
        }

        userRepo.save(user);
        return null;
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
     * Setelah berhasil, otomatis kirim notifikasi jika screen time melebihi batas.
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

        // Hitung total screen time setelah aktivitas ditambahkan
        List<AktivitasDigital> semuaAktivitas = aktivitasRepo.findByUserId(userId);
        int totalMenit = semuaAktivitas.stream().mapToInt(AktivitasDigital::getDurasiMenit).sum();

        // Kirim notifikasi jika screen time >= batas harian (default 120, atau sesuai setelan orang tua)
        if (totalMenit >= user.getBatasHarian()) {
            String pesan = Notifikasi.kirimPeringatan(totalMenit);
            notifikasiRepo.save(new Notifikasi(pesan, user));
            
            // Juga kirim ke orang tua jika ada
            if (user.getParent() != null) {
                String pesanParent = "⚠️ Anak Anda (" + user.getNamaUser() + ") telah melewati batas harian screen time! (" + totalMenit + " menit)";
                notifikasiRepo.save(new Notifikasi(pesanParent, user.getParent()));
            }
        }

        return true;
    }

    /**
     * Mengubah batas harian anak (Hanya bisa dipanggil oleh orang tua dari anak tersebut)
     */
    public boolean updateBatasHarianAnak(Long parentId, Long anakId, int batasBaru) {
        User parent = userRepo.findById(parentId).orElse(null);
        User anak = userRepo.findById(anakId).orElse(null);

        if (parent == null || anak == null || anak.getParent() == null) return false;
        if (!anak.getParent().getId().equals(parent.getId())) return false;

        anak.setBatasHarian(batasBaru);
        userRepo.save(anak);

        // Beri tahu anak bahwa batas hariannya diubah
        String pesan = "ℹ️ Batas harian Anda telah diubah oleh orang tua menjadi " + batasBaru + " menit.";
        notifikasiRepo.save(new Notifikasi(pesan, anak));

        return true;
    }

    /**
     * Mengambil daftar anak dari seorang orang tua.
     */
    public List<User> getAnakByParent(Long parentId) {
        User parent = userRepo.findById(parentId).orElse(null);
        if (parent == null) return List.of();
        
        // Panggil getter anakList dan inisialisasi agar terhindar dari LazyInitializationException
        List<User> list = parent.getAnakList();
        list.size();
        return list;
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
            this.success   = success;
            this.message   = message;
            this.snapToken = snapToken;
        }

        // Getters
        public boolean isSuccess()      { return success; }
        public String getMessage()      { return message; }
        public String getSnapToken()    { return snapToken; }
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

            // Kirim notifikasi konfirmasi top up
            String pesan = "✅ Top up berhasil! " + jumlah + " token ditambahkan melalui " + metode + ".";
            notifikasiRepo.save(new Notifikasi(pesan, user));

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

        // Kirim notifikasi konfirmasi pembayaran QRIS
        String pesan = "✅ Pembayaran QRIS dikonfirmasi! " + latest.getJumlahKoin() + " token berhasil ditambahkan.";
        notifikasiRepo.save(new Notifikasi(pesan, user));

        return true;
    }

    // ── LAPORAN HARIAN ────────────────────────────────────────────────────

    /**
     * Handle pembayaran berhasil dari notifikasi manual.
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
     * Menghasilkan laporan harian dalam bentuk teks dan menyimpannya ke database.
     * Menggantikan fungsi generateLaporan() di LaporanHarian.java proyek GUI.
     * Juga menyimpan laporan ke tabel laporan_harian untuk riwayat.
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

        // Simpan atau update laporan harian ke database
        LocalDate today = LocalDate.now();
        Optional<LaporanHarian> existingLaporan = laporanRepo.findByUserIdAndTanggal(userId, today);
        LaporanHarian laporan;
        if (existingLaporan.isPresent()) {
            // Update laporan yang sudah ada
            laporan = existingLaporan.get();
            laporan.setTotalDurasi(totalDurasi);
            laporan.setSkorHarian(score);
        } else {
            // Buat laporan baru untuk hari ini
            laporan = new LaporanHarian(totalDurasi, score, today, user);
        }
        laporanRepo.save(laporan);

        // Gunakan metode generateLaporan() dari entity LaporanHarian (sesuai implementasi GUI)
        return laporan.generateLaporan(list);
    }

    // ── NOTIFIKASI ────────────────────────────────────────────────────────

    /**
     * Mengambil semua notifikasi milik user tertentu, terbaru dulu.
     */
    public List<Notifikasi> getNotifikasiByUser(Long userId) {
        return notifikasiRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Mengambil semua laporan harian milik user, terbaru dulu.
     */
    public List<LaporanHarian> getLaporanByUser(Long userId) {
        return laporanRepo.findByUserIdOrderByTanggalDesc(userId);
    }
}
