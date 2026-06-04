package com.screentimetracker.demo;

import com.screentimetracker.demo.model.User;
import com.screentimetracker.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * DataSeeder dijalankan otomatis saat aplikasi pertama kali start.
 * Bertugas mengisi data awal ke database jika belum ada,
 * sesuai dengan data default yang ada di proyek Java GUI (UserManager).
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final com.screentimetracker.demo.repository.AdminTokenRepository adminTokenRepo;

    public DataSeeder(UserRepository userRepo,
                      com.screentimetracker.demo.repository.AdminTokenRepository adminTokenRepo) {
        this.userRepo = userRepo;
        this.adminTokenRepo = adminTokenRepo;
    }

    @Override
    public void run(String... args) {
        // Buat akun admin default jika belum ada di database
        if (!userRepo.existsByUsername("admin")) {
            userRepo.save(new User("Admin Operator", "admin", "12345", "ADMIN"));
        }

        // Buat token admin default untuk registrasi admin baru
        if (adminTokenRepo.count() == 0) {
            adminTokenRepo.save(new com.screentimetracker.demo.model.AdminToken("MINDFULL-ADMIN-2026"));
        }
    }
}
