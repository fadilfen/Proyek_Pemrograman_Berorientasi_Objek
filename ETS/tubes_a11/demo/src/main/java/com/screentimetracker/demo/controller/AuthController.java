package com.screentimetracker.demo.controller;

import com.screentimetracker.demo.model.User;
import com.screentimetracker.demo.service.MindFullService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * AuthController menangani semua proses autentikasi:
 * login, register, dan logout.
 * Menggantikan logika di UserManager.java dan loginScreen() di proyek GUI.
 */
@Controller
public class AuthController {

    private final MindFullService service;

    public AuthController(MindFullService service) {
        this.service = service;
    }

    // Redirect ke dashboard jika sudah login, ke login jika belum
    @GetMapping("/")
    public String index(HttpSession session) {
        if (session.getAttribute("userId") != null) return "redirect:/dashboard";
        return "redirect:/login";
    }

    // Menampilkan halaman login
    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        // Jika sudah login, langsung ke dashboard
        if (session.getAttribute("userId") != null) return "redirect:/dashboard";
        return "login";
    }

    // Memproses form login yang dikirim
    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          RedirectAttributes ra) {
        User user = service.login(username, password);

        if (user != null) {
            // Simpan data user ke session setelah login berhasil
            session.setAttribute("userId",   user.getId());
            session.setAttribute("namaUser", user.getNamaUser());
            session.setAttribute("username", user.getUsername());
            return "redirect:/dashboard";
        }

        // Login gagal, kirim pesan error ke halaman login
        ra.addFlashAttribute("error", "Username atau password salah!");
        return "redirect:/login";
    }

    // Menampilkan halaman register
    @GetMapping("/register")
    public String registerPage(HttpSession session) {
        // Jika sudah login, langsung ke dashboard
        if (session.getAttribute("userId") != null) return "redirect:/dashboard";
        return "register";
    }

    // Memproses form register yang dikirim
    @PostMapping("/register")
    public String doRegister(@RequestParam String namaLengkap,
                             @RequestParam String username,
                             @RequestParam String password,
                             RedirectAttributes ra) {
        if (service.register(username, password, namaLengkap)) {
            // Register berhasil, arahkan ke login
            ra.addFlashAttribute("success", "Registrasi berhasil! Silakan login.");
            return "redirect:/login";
        }

        // Register gagal karena username sudah ada
        ra.addFlashAttribute("error", "Username sudah terdaftar!");
        return "redirect:/register";
    }

    // Menghapus session dan mengarahkan ke halaman login
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
