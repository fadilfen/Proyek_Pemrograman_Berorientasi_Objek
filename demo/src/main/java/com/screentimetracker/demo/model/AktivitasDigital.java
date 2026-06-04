package com.screentimetracker.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity AktivitasDigital merepresentasikan tabel 'aktivitas_digital' di database.
 * Menyimpan data log penggunaan aplikasi digital oleh pengguna setiap harinya.
 */
@Entity
@Table(name = "aktivitas_digital")
public class AktivitasDigital {

    // Primary key, auto increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nama aplikasi yang digunakan (contoh: TikTok, YouTube)
    @Column(name = "nama_aplikasi", nullable = false)
    private String namaAplikasi;

    // Durasi penggunaan dalam menit
    @Column(name = "durasi_menit")
    private int durasiMenit;

    // Batas durasi harian yang ditetapkan pengguna
    @Column(name = "batas_durasi")
    private int batasDurasi;

    // Jam mulai penggunaan aktivitas
    @Column(name = "jam_mulai")
    private java.time.LocalTime jamMulai;

    // Tanggal aktivitas dilakukan
    private LocalDate tanggal;

    // Waktu pencatatan ke sistem
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Relasi many-to-one ke tabel users (setiap aktivitas milik satu user)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // ── Konstruktor ──────────────────────────────────────────────────────

    // Konstruktor kosong wajib untuk JPA
    public AktivitasDigital() {}

    // Konstruktor untuk membuat aktivitas baru tanpa jam mulai (compatibility)
    public AktivitasDigital(String namaAplikasi, int durasiMenit,
                             int batasDurasi, LocalDate tanggal, User user) {
        this.namaAplikasi = namaAplikasi;
        this.durasiMenit  = durasiMenit;
        this.batasDurasi  = batasDurasi;
        this.tanggal      = tanggal;
        this.user         = user;
        this.createdAt    = LocalDateTime.now();
    }

    // Konstruktor lengkap dengan jam mulai
    public AktivitasDigital(String namaAplikasi, int durasiMenit,
                             int batasDurasi, java.time.LocalTime jamMulai, LocalDate tanggal, User user) {
        this.namaAplikasi = namaAplikasi;
        this.durasiMenit  = durasiMenit;
        this.batasDurasi  = batasDurasi;
        this.jamMulai     = jamMulai;
        this.tanggal      = tanggal;
        this.user         = user;
        this.createdAt    = LocalDateTime.now();
    }

    // ── Business Logic ───────────────────────────────────────────────────

    /**
     * Mengecek apakah waktu sekarang sudah melebihi batas waktu penggunaan
     * (Jam Mulai + Durasi) untuk aktivitas hari ini.
     */
    public boolean melebihiBatas() {
        if (jamMulai != null && tanggal != null && tanggal.equals(LocalDate.now())) {
            return java.time.LocalTime.now().isAfter(getJamSelesai());
        }
        return false;
    }

    // ── Getter dan Setter ─────────────────────────────────────────────────

    public Long getId()                              { return id; }
    public String getNamaAplikasi()                  { return namaAplikasi; }
    public void setNamaAplikasi(String namaAplikasi) { this.namaAplikasi = namaAplikasi; }
    public int getDurasiMenit()                      { return durasiMenit; }
    public void setDurasiMenit(int durasiMenit)      { this.durasiMenit = durasiMenit; }
    public int getBatasDurasi()                      { return batasDurasi; }
    public void setBatasDurasi(int batasDurasi)      { this.batasDurasi = batasDurasi; }
    
    public java.time.LocalTime getJamMulai()         { return jamMulai; }
    public void setJamMulai(java.time.LocalTime jamMulai) { this.jamMulai = jamMulai; }
    
    public java.time.LocalTime getJamSelesai() {
        if (jamMulai == null) return null;
        return jamMulai.plusMinutes(durasiMenit);
    }
    
    public LocalDate getTanggal()                    { return tanggal; }
    public void setTanggal(LocalDate tanggal)        { this.tanggal = tanggal; }
    public LocalDateTime getCreatedAt()              { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt){ this.createdAt = createdAt; }
    public User getUser()                            { return user; }
    public void setUser(User user)                   { this.user = user; }
}
