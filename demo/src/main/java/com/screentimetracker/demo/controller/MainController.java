package com.screentimetracker.demo.controller;

import com.screentimetracker.demo.model.AktivitasDigital;
import com.screentimetracker.demo.model.User;
import com.screentimetracker.demo.service.MindFullService;
import com.screentimetracker.demo.service.PdfService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * MainController menangani semua halaman utama setelah user login:
 * Dashboard, Activity Tracker, Top Up, Health Report, dan Profile.
 * Menggantikan semua method showXxxPage() di MentalWellbeingApp.java proyek GUI.
 */
@Controller
public class MainController {

    private final MindFullService service;
    private final PdfService pdfService;

    public MainController(MindFullService service, PdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
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
    public String activityPage(HttpSession session,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                Model model) {
        if (!isLoggedIn(session)) return "redirect:/login";

        Long userId = getUserId(session);
        List<AktivitasDigital> list;

        if (startDate != null && endDate != null) {
            list = service.getAktivitasByUserAndDateRange(userId, startDate, endDate);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
        } else if (tanggal != null) {
            list = service.getAktivitasByUserAndTanggal(userId, tanggal);
            model.addAttribute("tanggalFilter", tanggal);
        } else {
            list = service.getAktivitasByUser(userId);
        }

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
                              @RequestParam String jamMulai,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal,
                              RedirectAttributes ra) {
        if (!isLoggedIn(session)) return "redirect:/login";

        java.time.LocalTime parsedJamMulai = null;
        try {
            if (jamMulai != null && !jamMulai.isEmpty()) {
                parsedJamMulai = java.time.LocalTime.parse(jamMulai);
            } else {
                parsedJamMulai = java.time.LocalTime.now();
            }
        } catch (Exception e) {
            parsedJamMulai = java.time.LocalTime.now();
        }

        boolean berhasil = service.tambahAktivitas(
                getUserId(session), namaAplikasi, durasiMenit, parsedJamMulai, tanggal);

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
                model.addAttribute("snapToken", result.getSnapToken());
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

    // ── HEALTH REPORT ─────────────────────────────────────────────────────

    /**
     * Menampilkan halaman health report.
     * Setara dengan showReportPage() di proyek GUI.
     */
    @GetMapping("/report")
    public String reportPage(HttpSession session,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal,
                             Model model) {
        if (!isLoggedIn(session)) return "redirect:/login";

        model.addAttribute("user", service.getUser(getUserId(session)));
        model.addAttribute("tanggalFilter", tanggal);
        return "report";
    }

    /**
     * Menghasilkan dan menampilkan laporan harian pengguna.
     * Setara dengan tombol "Generate Summary Report" di proyek GUI.
     */
    @PostMapping("/report/generate")
    public String generateReport(HttpSession session,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal,
                                 Model model) {
        if (!isLoggedIn(session)) return "redirect:/login";

        Long userId = getUserId(session);
        MindFullService.LaporanData laporanData = service.generateLaporanData(userId, tanggal);
        
        model.addAttribute("user",    service.getUser(userId));
        model.addAttribute("laporan", service.generateLaporan(userId));
        model.addAttribute("laporanData", laporanData);
        model.addAttribute("tanggalFilter", tanggal);
        return "report";
    }

    /**
     * Mengunduh laporan PDF.
     */
    @GetMapping("/report/download")
    public ResponseEntity<byte[]> downloadReport(HttpSession session,
                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal) {
        if (!isLoggedIn(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = getUserId(session);
        MindFullService.LaporanData laporanData = service.generateLaporanData(userId, tanggal);
        byte[] pdfBytes = pdfService.generatePdfReport(laporanData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "Laporan_Digital_Wellness_" + (tanggal != null ? tanggal.toString() : "Total") + ".pdf";
        headers.setContentDispositionFormData("attachment", filename);

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
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

    @PostMapping("/topup/confirm")
    public String confirmTopUp(HttpSession session, RedirectAttributes ra) {
        if (!isLoggedIn(session)) return "redirect:/login";

        boolean success = service.confirmQrisTopUp(getUserId(session));
        if (success) {
            ra.addFlashAttribute("success", "Konfirmasi pembayaran QRIS terkirim! Status saat ini PENDING menunggu persetujuan admin.");
        } else {
            ra.addFlashAttribute("error", "Konfirmasi pembayaran gagal. Pastikan transaksi Anda valid.");
        }
        return "redirect:/topup";
    }

    /**
     * Handle notification callback dari Midtrans untuk update status pembayaran.
     */
    @PostMapping("/payment/notification")
    public String handlePaymentNotification(@RequestBody String notificationBody) {
        // Parse notification JSON sederhana
        // Dalam implementasi nyata, gunakan library JSON dan verifikasi signature

        String orderId = null;
        String transactionStatus = null;

        // Ekstrak order_id dan transaction_status dari body
        if (notificationBody.contains("order_id")) {
            int start = notificationBody.indexOf("\"order_id\":\"") + 12;
            int end = notificationBody.indexOf("\"", start);
            if (start > 11 && end > start) {
                orderId = notificationBody.substring(start, end);
            }
        }

        if (notificationBody.contains("transaction_status")) {
            int start = notificationBody.indexOf("\"transaction_status\":\"") + 22;
            int end = notificationBody.indexOf("\"", start);
            if (start > 21 && end > start) {
                transactionStatus = notificationBody.substring(start, end);
            }
        }

        if (orderId != null && "settlement".equals(transactionStatus)) {
            boolean success = service.handlePaymentSuccess(orderId);
            if (success) {
                // Log success
            }
        }

        return "OK";
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

    // ── SIMULASI MY APPS (DUMMY SMARTPHONE) ───────────────────────────────

    @GetMapping("/my-apps")
    public String myAppsPage(HttpSession session, Model model) {
        if (!isLoggedIn(session)) return "redirect:/login";

        model.addAttribute("user", service.getUser(getUserId(session)));
        return "my_apps";
    }

    @GetMapping("/api/my-apps/status")
    @org.springframework.web.bind.annotation.ResponseBody
    public java.util.Map<String, Object> getMyAppsStatus(HttpSession session, @RequestParam String appName) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        if (!isLoggedIn(session)) {
            response.put("error", "Unauthorized");
            return response;
        }

        Long userId = getUserId(session);
        // Menggunakan LocalDate.now() untuk mengambil log aktivitas hari ini
        List<AktivitasDigital> list = service.getAktivitasByUserAndTanggal(userId, LocalDate.now());
        
        Optional<AktivitasDigital> optAct = list.stream()
                .filter(a -> a.getNamaAplikasi().equalsIgnoreCase(appName))
                .findFirst();
                
        long sisaDetik = 0;
        String status = "UNTRACKED";
        String message = "";

        if (optAct.isPresent()) {
            AktivitasDigital act = optAct.get();
            java.time.LocalTime now = java.time.LocalTime.now();
            java.time.LocalTime mulai = act.getJamMulai();
            java.time.LocalTime selesai = act.getJamSelesai();

            if (mulai != null && selesai != null) {
                if (now.isAfter(mulai) && now.isBefore(selesai)) {
                    sisaDetik = java.time.Duration.between(now, selesai).getSeconds();
                    status = "ACTIVE";
                    message = "Sesi aktif terdeteksi.";
                } else if (now.isBefore(mulai)) {
                    sisaDetik = act.getDurasiMenit() * 60L;
                    status = "WAITING";
                    message = "Sesi dijadwalkan mulai pukul " + mulai;
                } else {
                    sisaDetik = 0;
                    status = "EXPIRED";
                    message = "Batas waktu penggunaan untuk sesi ini telah habis.";
                }
            } else {
                sisaDetik = act.getDurasiMenit() * 60L;
                status = "ACTIVE";
                message = "Sesi aktif tanpa jadwal jam mulai.";
            }
        } else {
            // Default demo mode: 30 menit
            sisaDetik = 1800;
            status = "UNTRACKED";
            message = "Menggunakan waktu demo 30 menit. Silakan jadwalkan aktivitas untuk presisi lebih baik.";
        }

        response.put("appName", appName);
        response.put("sisaDetik", sisaDetik);
        response.put("status", status);
        response.put("message", message);
        return response;
    }

    @PostMapping("/api/my-apps/over-limit")
    @org.springframework.web.bind.annotation.ResponseBody
    public java.util.Map<String, Object> triggerOverLimitNotification(HttpSession session, @RequestParam String appName) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        if (!isLoggedIn(session)) {
            response.put("success", false);
            return response;
        }

        Long userId = getUserId(session);
        service.kirimNotifikasiOverLimit(userId, appName);
        
        response.put("success", true);
        return response;
    }
}
