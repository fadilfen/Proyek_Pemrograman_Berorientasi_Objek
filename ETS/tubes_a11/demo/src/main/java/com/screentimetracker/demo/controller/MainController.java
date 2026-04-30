package com.screentimetracker.demo.controller;

import com.screentimetracker.demo.model.AktivitasDigital;
import com.screentimetracker.demo.model.User;
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
 * Dashboard, Activity Tracker, Top Up, Health Report, dan Profile.
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
     * token, wellness score, dan total screen time.
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

        model.addAttribute("user",            user);
        model.addAttribute("totalScreenTime", totalScreenTime);
        model.addAttribute("score",           score);
        model.addAttribute("today",           LocalDate.now());
        return "dashboard";
    }

    // ── ACTIVITY TRACKER ──────────────────────────────────────────────────

    /**
     * Menampilkan halaman activity tracker beserta daftar aktivitas user.
     * Setara dengan showActivityPage() di proyek GUI.
     */
    @GetMapping("/activity")
    public String activityPage(HttpSession session, Model model) {
        if (!isLoggedIn(session)) return "redirect:/login";

        Long userId = getUserId(session);
        List<AktivitasDigital> list = service.getAktivitasByUser(userId);

        model.addAttribute("user",          service.getUser(userId));
        model.addAttribute("aktivitasList", list);
        model.addAttribute("today",         LocalDate.now());
        return "activity";
    }

    /**
     * Memproses form penambahan aktivitas digital baru.
     * Memotong 5 token dari saldo user setiap kali log aktivitas.
     */
    @PostMapping("/activity/add")
    public String addActivity(HttpSession session,
                              @RequestParam String namaAplikasi,
                              @RequestParam int durasiMenit,
                              @RequestParam int batasDurasi,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal,
                              RedirectAttributes ra) {
        if (!isLoggedIn(session)) return "redirect:/login";

        boolean berhasil = service.tambahAktivitas(
                getUserId(session), namaAplikasi, durasiMenit, batasDurasi, tanggal);

        if (berhasil) {
            ra.addFlashAttribute("success", "Aktivitas berhasil ditambahkan!");
        } else {
            ra.addFlashAttribute("error", "Token tidak cukup! Minimal 5 token untuk log aktivitas.");
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

    /**
     * Memproses transaksi top up token yang dikirim dari form.
     */
    @PostMapping("/topup/proses")
    public String prosesTopUp(HttpSession session,
                              @RequestParam int jumlahKoin,
                              @RequestParam String metodePembayaran,
                              RedirectAttributes ra) {
        if (!isLoggedIn(session)) return "redirect:/login";

        boolean berhasil = service.prosesTopUp(getUserId(session), jumlahKoin, metodePembayaran);

        if (berhasil) {
            ra.addFlashAttribute("success", "Top up berhasil! Token telah ditambahkan.");
        } else {
            ra.addFlashAttribute("error", "Jumlah tidak valid! Masukkan angka lebih dari 0.");
        }
        return "redirect:/topup";
    }

    // ── HEALTH REPORT ─────────────────────────────────────────────────────

    /**
     * Menampilkan halaman health report.
     * Setara dengan showReportPage() di proyek GUI.
     */
    @GetMapping("/report")
    public String reportPage(HttpSession session, Model model) {
        if (!isLoggedIn(session)) return "redirect:/login";

        model.addAttribute("user", service.getUser(getUserId(session)));
        return "report";
    }

    /**
     * Menghasilkan dan menampilkan laporan harian pengguna.
     * Setara dengan tombol "Generate Summary Report" di proyek GUI.
     */
    @PostMapping("/report/generate")
    public String generateReport(HttpSession session, Model model) {
        if (!isLoggedIn(session)) return "redirect:/login";

        Long userId = getUserId(session);
        model.addAttribute("user",    service.getUser(userId));
        model.addAttribute("laporan", service.generateLaporan(userId));
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

    /**
     * Memproses pembaruan username dan password pengguna.
     * Setara dengan tombol "Update Credentials" di proyek GUI.
     */
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
}
