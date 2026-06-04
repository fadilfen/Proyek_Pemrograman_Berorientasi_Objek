package com.screentimetracker.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity User merepresentasikan tabel 'users' di database mindfull_2.
 * Versi 2.0: ditambahkan field role, isActive, dan createdAt
 * untuk mendukung sistem RBAC (Role-Based Access Control).
 */
@Entity
@Table(name = "users")
public class User {

    // Primary key, auto increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nama lengkap pengguna
    @Column(name = "nama_user", nullable = false)
    private String namaUser;

    // Username unik untuk login
    @Column(unique = true, nullable = false)
    private String username;

    // Password pengguna (plain text sesuai scope proyek)
    @Column(nullable = false)
    private String password;

    // Saldo token pengguna — digunakan untuk log aktivitas
    @Column(nullable = false)
    private int token = 50;

    /**
     * Role pengguna untuk sistem RBAC.
     * Nilai: "USER" (pengguna biasa) atau "ADMIN" (administrator).
     * Role USER ditetapkan otomatis saat registrasi biasa.
     * Role ADMIN hanya bisa didapat melalui halaman admin-register + token rahasia.
     */
    @Column(nullable = false)
    private String role = "USER";

    /**
     * Status aktif akun. Admin dapat menonaktifkan user (isActive = false).
     * User yang nonaktif tidak bisa login.
     */
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    // Waktu pembuatan akun
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Relasi one-to-many ke tabel aktivitas_digital
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AktivitasDigital> aktivitasList = new ArrayList<>();

    // Relasi one-to-many ke tabel notifications
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications = new ArrayList<>();

    // Relasi one-to-many ke tabel payments
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();

    // ── Konstruktor ──────────────────────────────────────────────────────

    // Konstruktor kosong wajib untuk JPA
    public User() {}

    // Konstruktor untuk registrasi user biasa (role = USER)
    public User(String namaUser, String username, String password) {
        this.namaUser  = namaUser;
        this.username  = username;
        this.password  = password;
        this.role      = "USER";
        this.isActive  = true;
        this.createdAt = LocalDateTime.now();
    }

    // Konstruktor untuk registrasi admin (role = ADMIN)
    public User(String namaUser, String username, String password, String role) {
        this.namaUser  = namaUser;
        this.username  = username;
        this.password  = password;
        this.role      = role;
        this.isActive  = true;
        this.createdAt = LocalDateTime.now();
    }

    // ── Business Logic ───────────────────────────────────────────────────

    /**
     * Mengecek apakah user memiliki role ADMIN.
     */
    public boolean isAdmin() {
        return "ADMIN".equals(this.role);
    }

    /**
     * Menghitung total screen time dari semua aktivitas pengguna.
     */
    public int hitungTotalScreenTime() {
        return aktivitasList.stream()
                .mapToInt(AktivitasDigital::getDurasiMenit)
                .sum();
    }

    /**
     * Menghitung skor kesehatan berdasarkan durasi dan batas aktivitas.
     * Skor dimulai dari 100, dikurangi sesuai screen time dan pelanggaran batas.
     */
    public int hitungScoreKesehatan() {
        int score = 100;
        for (AktivitasDigital a : aktivitasList) {
            score -= a.getDurasiMenit() / 10;
            if (a.melebihiBatas()) score -= 20;
        }
        return Math.max(0, score);
    }

    // ── Getter dan Setter ─────────────────────────────────────────────────

    public Long getId()                                           { return id; }
    public String getNamaUser()                                   { return namaUser; }
    public void setNamaUser(String namaUser)                      { this.namaUser = namaUser; }
    public String getUsername()                                   { return username; }
    public void setUsername(String username)                      { this.username = username; }
    public String getPassword()                                   { return password; }
    public void setPassword(String password)                      { this.password = password; }
    public int getToken()                                         { return token; }
    public void setToken(int token)                               { this.token = token; }
    public String getRole()                                       { return role; }
    public void setRole(String role)                              { this.role = role; }
    public boolean isActive()                                     { return isActive; }
    public void setActive(boolean active)                         { isActive = active; }
    public LocalDateTime getCreatedAt()                           { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)             { this.createdAt = createdAt; }
    public List<AktivitasDigital> getAktivitasList()              { return aktivitasList; }
    public void setAktivitasList(List<AktivitasDigital> list)     { this.aktivitasList = list; }
    public List<Notification> getNotifications()                  { return notifications; }
    public void setNotifications(List<Notification> notifications){ this.notifications = notifications; }
    public List<Payment> getPayments()                            { return payments; }
    public void setPayments(List<Payment> payments)               { this.payments = payments; }
}
