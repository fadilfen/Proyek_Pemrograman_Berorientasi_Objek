package com.screentimetracker.demo.controller;

import com.screentimetracker.demo.model.AktivitasDigital;
import com.screentimetracker.demo.model.LaporanHarian;
import com.screentimetracker.demo.model.Notifikasi;
import com.screentimetracker.demo.model.User;
import com.screentimetracker.demo.model.Role;
import com.screentimetracker.demo.service.MindFullService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

/**
 * MainController menangani semua halaman utama setelah user login:
 * Dashboard, Activity Tracker, Top Up, Health Report, Profile, dan Notifikasi.
 * Menggantikan semua method showXxxPage() di MentalWellbeingApp.java proyek GUI.
 */
@Controller
public class MainController {

    private final MindFullService service;

    public MainController(MindFullService service) {
        this.service = service;
    }

    // Mengambil ID user dari session yang sedang aktif
    private Long getUserId(HttpSession session) {
        return (Long) session.getAttribute("userId");
    }

    // Mengecek apakah user sudah login (session masih aktif)
    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("userId") != null;
    }

    // ── DASHBOARD ─────────────────────────────────────────────────────────

    /**
     * Menampilkan halaman dashboard dengan statistik:
     * token, wellness score, total screen time, dan notifikasi terbaru.
     * Setara dengan showHomePage() di proyek GUI.
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isLoggedIn(session)) return "redirect:/login";

        Long userId = getUserId(session);
        User user   = service.getUser(userId);
        List<AktivitasDigital> list = service.getAktivitasByUser(userId);

        // Hitung total screen time dari semua aktivitas
        int totalScreenTime = list.stream()
                .mapToInt(AktivitasDigital::getDurasiMenit)
                .sum();

        // Hitung wellness score (logika sama dengan proyek GUI)
        int score = 100;
        for (AktivitasDigital a : list) {
            score -= a.getDurasiMenit() / 10;
            if (a.melebihiBatas()) score -= 20;
        }
        score = Math.max(0, score);

        // Ambil notifikasi terbaru untuk ditampilkan di dashboard
        List<Notifikasi> notifikasiList = service.getNotifikasiByUser(userId);

        // Jika role ORANG_TUA, tambahkan daftar anak ke model
        if (user.getRole() == Role.ORANG_TUA) {
            List<User> anakList = service.getAnakByParent(userId);
            model.addAttribute("anakList", anakList);
        }

        model.addAttribute("user",            user);
        model.addAttribute("totalScreenTime", totalScreenTime);
        model.addAttribute("score",           score);
        model.addAttribute("today",           LocalDate.now());
        model.addAttribute("notifikasiList",  notifikasiList);
        return "dashboard";
    }

    @PostMapping("/parent/update-batas")
    public String updateBatasHarian(HttpSession session,
                                    @RequestParam Long anakId,
                                    @RequestParam int batasBaru,
                                    RedirectAttributes ra) {
        if (!isLoggedIn(session)) return "redirect:/login";
        
        boolean berhasil = service.updateBatasHarianAnak(getUserId(session), anakId, batasBaru);
        if (berhasil) {
            ra.addFlashAttribute("success", "Batas harian anak berhasil diubah!");
        } else {
            ra.addFlashAttribute("error", "Gagal mengubah batas harian anak.");
        }
        return "redirect:/dashboard";
    }

    // ── ACTIVITY TRACKER ──────────────────────────────────────────────────

    /**
     * Menampilkan halaman activity tracker beserta daftar aktivitas user.
     * Setara dengan showActivityPage() di proyek GUI.
     */
    @GetMapping("/activity")
    public String activityPage(HttpSession session, Model model, @RequestParam(required = false) Long anakId) {
        if (!isLoggedIn(session)) return "redirect:/login";

        Long userId = getUserId(session);
        User user = service.getUser(userId);

        Long targetUserId = userId;
        if (anakId != null && user.getRole() == Role.ORANG_TUA) {
            // Verifikasi apakah anakId benar-benar anaknya
            List<User> anakList = service.getAnakByParent(userId);
            boolean isMyChild = anakList.stream().anyMatch(a -> a.getId().equals(anakId));
            if (isMyChild) {
                targetUserId = anakId;
                model.addAttribute("selectedAnakId", anakId);
            }
        }

        List<AktivitasDigital> list = service.getAktivitasByUser(targetUserId);

        model.addAttribute("user", user);
        model.addAttribute("targetUser", service.getUser(targetUserId));
        model.addAttribute("aktivitasList", list);
        model.addAttribute("today", LocalDate.now());

        if (user.getRole() == Role.ORANG_TUA) {
            model.addAttribute("anakList", service.getAnakByParent(userId));
        }

        return "activity";
    }

    @PostMapping("/activity/add")
    public String addActivity(HttpSession session,
                              @RequestParam String namaAplikasi,
                              @RequestParam int durasiMenit,
                              @RequestParam int batasDurasi,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal,
                              @RequestParam(required = false) Long targetUserId,
                              RedirectAttributes ra) {
        if (!isLoggedIn(session)) return "redirect:/login";

        Long userId = getUserId(session);
        User user = service.getUser(userId);

        Long actualTargetId = userId;
        if (targetUserId != null && user.getRole() == Role.ORANG_TUA) {
            List<User> anakList = service.getAnakByParent(userId);
            boolean isMyChild = anakList.stream().anyMatch(a -> a.getId().equals(targetUserId));
            if (isMyChild) {
                actualTargetId = targetUserId;
            }
        }

        boolean berhasil = service.tambahAktivitas(
                actualTargetId, namaAplikasi, durasiMenit, batasDurasi, tanggal);

        if (berhasil) {
            ra.addFlashAttribute("success", "Aktivitas berhasil ditambahkan!");
        } else {
            ra.addFlashAttribute("error", "Token tidak cukup! Minimal 5 token untuk log aktivitas.");
        }
        
        if (!actualTargetId.equals(userId)) {
            return "redirect:/activity?anakId=" + actualTargetId;
        }
        return "redirect:/activity";
    }

    @PostMapping("/activity/tambah-anak")
    public String tambahAnak(HttpSession session,
                             @RequestParam String namaAnak,
                             @RequestParam String usernameAnak,
                             @RequestParam String passwordAnak,
                             RedirectAttributes ra) {
        if (!isLoggedIn(session)) return "redirect:/login";
        
        User parent = service.getUser(getUserId(session));
        if (parent.getRole() != Role.ORANG_TUA) {
            ra.addFlashAttribute("error", "Hanya Orang Tua yang bisa menambahkan anak.");
            return "redirect:/activity";
        }

        String errorMsg = service.register(usernameAnak, passwordAnak, namaAnak, Role.ANAK, parent.getUsername());
        if (errorMsg == null) {
            ra.addFlashAttribute("success", "Akun anak berhasil ditambahkan!");
        } else {
            ra.addFlashAttribute("error", errorMsg);
        }
        return "redirect:/activity";
    }

    // ── TOP UP BALANCE ────────────────────────────────────────────────────

    /**
     * Menampilkan halaman top up token.
     * Setara dengan showTopUpPage() di proyek GUI.
     */
    @GetMapping("/topup")
    public String topupPage(HttpSession session, Model model) {
        if (!isLoggedIn(session)) return "redirect:/login";

        model.addAttribute("user", service.getUser(getUserId(session)));
        return "topup";
    }

    @GetMapping("/topup/proses")
    public String topupProsesGet(HttpSession session) {
        if (!isLoggedIn(session)) return "redirect:/login";
        return "redirect:/topup";
    }

    @GetMapping("/topup/confirm")
    public String topupConfirmGet(HttpSession session, RedirectAttributes ra) {
        if (!isLoggedIn(session)) return "redirect:/login";
        ra.addFlashAttribute("error", "Gunakan tombol konfirmasi pembayaran, tidak akses langsung.");
        return "redirect:/topup";
    }

    /**
     * Memproses transaksi top up token yang dikirim dari form.
     */
    @PostMapping("/topup/proses")
    public String prosesTopUp(HttpSession session,
                              @RequestParam int jumlahKoin,
                              @RequestParam String metodePembayaran,
                              RedirectAttributes ra,
                              Model model) {
        if (!isLoggedIn(session)) return "redirect:/login";

        MindFullService.TopUpResult result = service.prosesTopUp(getUserId(session), jumlahKoin, metodePembayaran);

        if (result.isSuccess()) {
            if (result.getSnapToken() != null) {
                // Untuk QRIS, tampilkan halaman QR code
                model.addAttribute("user", service.getUser(getUserId(session)));
                model.addAttribute("snapToken",  result.getSnapToken());
                model.addAttribute("jumlahKoin", jumlahKoin);
                model.addAttribute("totalHarga", jumlahKoin * 1000); // 1000 per token
                return "qris_payment"; // halaman baru untuk QRIS
            } else {
                ra.addFlashAttribute("success", result.getMessage());
            }
        } else {
            ra.addFlashAttribute("error", result.getMessage());
        }
        return "redirect:/topup";
    }

    @PostMapping("/topup/confirm")
    public String confirmTopUp(HttpSession session, RedirectAttributes ra) {
        if (!isLoggedIn(session)) return "redirect:/login";

        boolean success = service.confirmQrisTopUp(getUserId(session));
        if (success) {
            ra.addFlashAttribute("success", "Pembayaran QRIS dikonfirmasi. Token telah ditambahkan.");
        } else {
            ra.addFlashAttribute("error", "Konfirmasi pembayaran QRIS gagal. Pastikan Anda belum mengonfirmasi top up sebelumnya.");
        }
        return "redirect:/dashboard";
    }

    // ── HEALTH REPORT ─────────────────────────────────────────────────────

    /**
     * Menampilkan halaman health report (realtime).
     * Laporan harian di-generate otomatis setiap halaman dimuat.
     */
    @GetMapping("/report")
    public String reportPage(HttpSession session, Model model, @RequestParam(required = false) Long anakId) {
        if (!isLoggedIn(session)) return "redirect:/login";

        Long userId = getUserId(session);
        User user = service.getUser(userId);

        Long targetUserId = userId;
        if (anakId != null && user.getRole() == Role.ORANG_TUA) {
            // Verifikasi apakah anakId benar-benar anaknya
            List<User> anakList = service.getAnakByParent(userId);
            boolean isMyChild = anakList.stream().anyMatch(a -> a.getId().equals(anakId));
            if (isMyChild) {
                targetUserId = anakId;
                model.addAttribute("selectedAnakId", anakId);
            }
        }

        // Jika role ORANG_TUA, tambahkan daftar anak ke model
        if (user.getRole() == Role.ORANG_TUA) {
            model.addAttribute("anakList", service.getAnakByParent(userId));
        }

        model.addAttribute("user",        user);
        model.addAttribute("targetUser",  service.getUser(targetUserId));
        model.addAttribute("laporan",     service.generateLaporan(targetUserId));
        model.addAttribute("laporanList", service.getLaporanByUser(targetUserId));
        return "report";
    }

    // ── PROFILE ───────────────────────────────────────────────────────────

    /**
     * Menampilkan halaman profil pengguna.
     * Setara dengan showProfilePage() di proyek GUI.
     */
    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        if (!isLoggedIn(session)) return "redirect:/login";

        model.addAttribute("user", service.getUser(getUserId(session)));
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(HttpSession session,
                                @RequestParam String newUsername,
                                @RequestParam String newPassword,
                                RedirectAttributes ra) {
        if (!isLoggedIn(session)) return "redirect:/login";

        boolean berhasil = service.updateCredentials(getUserId(session), newUsername, newPassword);

        if (berhasil) {
            // Perbarui username di session agar sidebar langsung terupdate
            session.setAttribute("username", newUsername);
            ra.addFlashAttribute("success", "Credentials berhasil diupdate!");
        } else {
            ra.addFlashAttribute("error", "Username sudah digunakan oleh akun lain!");
        }
        return "redirect:/profile";
    }

    // ── NOTIFIKASI ────────────────────────────────────────────────────────

    /**
     * Menampilkan halaman notifikasi pengguna.
     */
    @GetMapping("/notifikasi")
    public String notifikasiPage(HttpSession session, Model model) {
        if (!isLoggedIn(session)) return "redirect:/login";

        Long userId = getUserId(session);
        model.addAttribute("user",           service.getUser(userId));
        model.addAttribute("notifikasiList", service.getNotifikasiByUser(userId));
        return "notifikasi";
    }
}
