package com.screentimetracker.demo.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity User merepresentasikan tabel 'users' di database.
 * Berisi data akun pengguna beserta logika perhitungan
 * screen time dan skor kesehatan, sesuai dengan kelas User.java di proyek GUI.
 */
@Entity
@Table(name = "users")
public class User {

    // Primary key, auto increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nama lengkap pengguna
    @Column(nullable = false)
    private String namaUser;

    // Username unik untuk login
    @Column(unique = true, nullable = false)
    private String username;

    // Password pengguna (plain text, sesuai proyek GUI)
    @Column(nullable = false)
    private String password;

    // Token awal pengguna saat pertama kali daftar
    private int token = 50;

    // Peran pengguna: ORANG_TUA atau ANAK
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ANAK;

    // Batas harian aktivitas (dalam menit), default 120 menit
    private int batasHarian = 120;

    // Relasi parent-child (Orang Tua - Anak)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private User parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> anakList = new ArrayList<>();

    // Relasi one-to-many ke tabel aktivitas_digital
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AktivitasDigital> aktivitasList = new ArrayList<>();

    // Konstruktor kosong wajib untuk JPA
    public User() {}

    // Konstruktor untuk membuat user baru (default role)
    public User(String namaUser, String username, String password) {
        this.namaUser  = namaUser;
        this.username  = username;
        this.password  = password;
        this.role      = Role.ANAK; // Default jika tidak diset
    }

    // Konstruktor untuk membuat user baru dengan role spesifik
    public User(String namaUser, String username, String password, Role role) {
        this.namaUser  = namaUser;
        this.username  = username;
        this.password  = password;
        this.role      = role;
    }

    /**
     * Menghitung total screen time dari semua aktivitas pengguna.
     * Logika sama dengan hitungTotalScreenTime() di proyek GUI.
     */
    public int hitungTotalScreenTime() {
        return aktivitasList.stream()
                .mapToInt(AktivitasDigital::getDurasiMenit)
                .sum();
    }

    /**
     * Menghitung skor kesehatan berdasarkan durasi dan batas aktivitas.
     * Logika sama dengan hitungScoreKesehatan() di proyek GUI.
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

    public Long getId()                                          { return id; }
    public String getNamaUser()                                  { return namaUser; }
    public void setNamaUser(String namaUser)                     { this.namaUser = namaUser; }
    public String getUsername()                                  { return username; }
    public void setUsername(String username)                     { this.username = username; }
    public String getPassword()                                  { return password; }
    public void setPassword(String password)                     { this.password = password; }
    public int getToken()                                        { return token; }
    public void setToken(int token)                              { this.token = token; }
    public Role getRole()                                        { return role; }
    public void setRole(Role role)                               { this.role = role; }
    public int getBatasHarian()                                  { return batasHarian; }
    public void setBatasHarian(int batasHarian)                  { this.batasHarian = batasHarian; }
    public User getParent()                                      { return parent; }
    public void setParent(User parent)                           { this.parent = parent; }
    public List<User> getAnakList()                              { return anakList; }
    public void setAnakList(List<User> anakList)                 { this.anakList = anakList; }
    public List<AktivitasDigital> getAktivitasList()             { return aktivitasList; }
    public void setAktivitasList(List<AktivitasDigital> list)    { this.aktivitasList = list; }
}
