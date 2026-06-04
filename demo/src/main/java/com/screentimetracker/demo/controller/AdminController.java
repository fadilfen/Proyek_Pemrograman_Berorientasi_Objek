package com.screentimetracker.demo.controller;

import com.screentimetracker.demo.model.Payment;
import com.screentimetracker.demo.model.User;
import com.screentimetracker.demo.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * AdminController mengelola halaman-halaman dan aksi administrator (RBAC).
 * Hanya dapat diakses oleh user dengan role = "ADMIN".
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    private boolean isAdmin(HttpSession session) {
        return session.getAttribute("userId") != null && "ADMIN".equals(session.getAttribute("role"));
    }

    private Long getAdminId(HttpSession session) {
        return (Long) session.getAttribute("userId");
    }

    // ── DASHBOARD ADMIN ───────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        AdminService.AdminStats stats = adminService.getStats();
        model.addAttribute("stats", stats);
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("namaUser", session.getAttribute("namaUser"));
        return "admin/dashboard";
    }

    // ── USER MANAGEMENT ───────────────────────────────────────────────────

    @GetMapping("/users")
    public String listUsers(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        List<User> users = adminService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("namaUser", session.getAttribute("namaUser"));
        return "admin/users";
    }

    @PostMapping("/users/update")
    public String updateUser(HttpSession session,
                             @RequestParam Long userId,
                             @RequestParam String namaUser,
                             @RequestParam String username,
                             @RequestParam String role,
                             @RequestParam(defaultValue = "false") boolean isActive,
                             RedirectAttributes ra) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        boolean success = adminService.updateUser(userId, namaUser, username, role, isActive);
        if (success) {
            ra.addFlashAttribute("success", "Data pengguna berhasil diperbarui.");
        } else {
            ra.addFlashAttribute("error", "Gagal memperbarui data pengguna. Kemungkinan username sudah terdaftar.");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/toggle")
    public String toggleUserStatus(HttpSession session,
                                   @RequestParam Long userId,
                                   @RequestParam boolean isActive,
                                   RedirectAttributes ra) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        boolean success = adminService.toggleUserStatus(userId, isActive);
        if (success) {
            ra.addFlashAttribute("success", "Status pengguna berhasil diubah.");
        } else {
            ra.addFlashAttribute("error", "Gagal mengubah status pengguna (minimal harus ada 1 admin aktif).");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete")
    public String deleteUser(HttpSession session,
                             @RequestParam Long userId,
                             RedirectAttributes ra) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        if (getAdminId(session).equals(userId)) {
            ra.addFlashAttribute("error", "Anda tidak bisa menghapus akun Anda sendiri!");
            return "redirect:/admin/users";
        }

        boolean success = adminService.hapusUser(userId);
        if (success) {
            ra.addFlashAttribute("success", "Pengguna berhasil dihapus secara permanen.");
        } else {
            ra.addFlashAttribute("error", "Gagal menghapus pengguna.");
        }
        return "redirect:/admin/users";
    }

    // ── PAYMENT MANAGEMENT ────────────────────────────────────────────────

    @GetMapping("/payments")
    public String listPayments(HttpSession session,
                               @RequestParam(required = false) String status,
                               Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        List<Payment> payments;
        if (status != null && !status.isEmpty()) {
            payments = adminService.getPaymentsByStatus(status);
        } else {
            payments = adminService.getAllPayments();
        }

        model.addAttribute("payments", payments);
        model.addAttribute("statusFilter", status);
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("namaUser", session.getAttribute("namaUser"));
        return "admin/payments";
    }

    @PostMapping("/payments/verify")
    public String verifyPayment(HttpSession session,
                                @RequestParam Long paymentId,
                                RedirectAttributes ra) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        boolean success = adminService.verifyPayment(paymentId, getAdminId(session));
        if (success) {
            ra.addFlashAttribute("success", "Pembayaran berhasil diverifikasi. Token dikreditkan ke user.");
        } else {
            ra.addFlashAttribute("error", "Gagal memverifikasi pembayaran.");
        }
        return "redirect:/admin/payments";
    }

    @PostMapping("/payments/reject")
    public String rejectPayment(HttpSession session,
                                @RequestParam Long paymentId,
                                RedirectAttributes ra) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        boolean success = adminService.rejectPayment(paymentId, getAdminId(session));
        if (success) {
            ra.addFlashAttribute("success", "Pembayaran ditolak.");
        } else {
            ra.addFlashAttribute("error", "Gagal memproses penolakan pembayaran.");
        }
        return "redirect:/admin/payments";
    }

    @PostMapping("/payments/delete")
    public String deletePayment(HttpSession session,
                                @RequestParam Long paymentId,
                                RedirectAttributes ra) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        boolean success = adminService.hapusPayment(paymentId);
        if (success) {
            ra.addFlashAttribute("success", "Data transaksi berhasil dihapus.");
        } else {
            ra.addFlashAttribute("error", "Gagal menghapus data transaksi.");
        }
        return "redirect:/admin/payments";
    }
}
