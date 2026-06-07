package com.screentimetracker.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entity AktivitasDigital merepresentasikan tabel 'aktivitas_digital' di database.
 * Menyimpan data aktivitas penggunaan aplikasi digital oleh pengguna,
 * sesuai dengan kelas AktivitasDigital.java di proyek GUI.
 */
@Entity
@Table(name = "aktivitas_digital")
public class AktivitasDigital {

    // Primary key, auto increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nama aplikasi yang digunakan (contoh: TikTok, YouTube)
    @Column(nullable = false)
    private String namaAplikasi;

    // Durasi penggunaan dalam menit
    private int durasiMenit;

    // Batas durasi harian yang ditetapkan pengguna
    private int batasDurasi;

    // Tanggal aktivitas dilakukan
    private LocalDate tanggal;

    // Relasi many-to-one ke tabel users (setiap aktivitas milik satu user)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Konstruktor kosong wajib untuk JPA
    public AktivitasDigital() {}

    // Konstruktor untuk membuat aktivitas baru
    public AktivitasDigital(String namaAplikasi, int durasiMenit, int batasDurasi, LocalDate tanggal, User user) {
        this.namaAplikasi = namaAplikasi;
        this.durasiMenit  = durasiMenit;
        this.batasDurasi  = batasDurasi;
        this.tanggal      = tanggal;
        this.user         = user;
    }

    /**
     * Mengecek apakah durasi penggunaan melebihi batas yang ditentukan.
     * Logika sama dengan melebihiBatas() di proyek GUI.
     */
    public boolean melebihiBatas() {
        return durasiMenit > batasDurasi;
    }

    // ── Getter dan Setter ─────────────────────────────────────────────────

    public Long getId()                              { return id; }
    public String getNamaAplikasi()                  { return namaAplikasi; }
    public void setNamaAplikasi(String namaAplikasi) { this.namaAplikasi = namaAplikasi; }
    public int getDurasiMenit()                      { return durasiMenit; }
    public void setDurasiMenit(int durasiMenit)      { this.durasiMenit = durasiMenit; }
    public int getBatasDurasi()                      { return batasDurasi; }
    public void setBatasDurasi(int batasDurasi)      { this.batasDurasi = batasDurasi; }
    public LocalDate getTanggal()                    { return tanggal; }
    public void setTanggal(LocalDate tanggal)        { this.tanggal = tanggal; }
    public User getUser()                            { return user; }
    public void setUser(User user)                   { this.user = user; }
}
