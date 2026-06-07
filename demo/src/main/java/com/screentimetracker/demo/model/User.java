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

    // Relasi one-to-many ke tabel aktivitas_digital
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AktivitasDigital> aktivitasList = new ArrayList<>();

    // Konstruktor kosong wajib untuk JPA
    public User() {}

    // Konstruktor untuk membuat user baru
    public User(String namaUser, String username, String password) {
        this.namaUser  = namaUser;
        this.username  = username;
        this.password  = password;
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
    public List<AktivitasDigital> getAktivitasList()             { return aktivitasList; }
    public void setAktivitasList(List<AktivitasDigital> list)    { this.aktivitasList = list; }
}
