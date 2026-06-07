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

    public DataSeeder(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public void run(String... args) {
        // Buat akun admin default jika belum ada di database
        // Sama seperti data hardcoded di UserManager.java pada proyek GUI
        if (!userRepo.existsByUsername("admin")) {
            userRepo.save(new User("Admin Operator", "admin", "12345"));
        }
    }
}
