package com.screentimetracker.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity Notifikasi merepresentasikan tabel 'notifikasi' di database.
 * Menyimpan pesan peringatan kepada pengguna ketika screen time melebihi batas,
 * sesuai dengan kelas Notifikasi.java di proyek GUI.
 */
@Entity
@Table(name = "notifikasi")
public class Notifikasi {

    // Primary key, auto increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Isi pesan notifikasi (peringatan screen time, dll)
    @Column(columnDefinition = "TEXT", nullable = false)
    private String pesan;

    // Waktu notifikasi dibuat
    private LocalDateTime createdAt;

    // Relasi many-to-one ke tabel users (setiap notifikasi milik satu user)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Konstruktor kosong wajib untuk JPA
    public Notifikasi() {}

    // Konstruktor untuk membuat notifikasi baru
    public Notifikasi(String pesan, User user) {
        this.pesan     = pesan;
        this.user      = user;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Menghasilkan pesan peringatan berdasarkan total screen time.
     * Logika sama dengan kirimPeringatan() di proyek GUI.
     *
     * @param totalMenit Total screen time dalam menit
     * @return Pesan peringatan yang sesuai
     */
    public static String kirimPeringatan(int totalMenit) {
        if (totalMenit >= 480) {
            return "⚠️ PERINGATAN KRITIS: Screen time kamu sudah mencapai " + totalMenit
                    + " menit (8 jam+). Segera istirahat dari layar!";
        } else if (totalMenit >= 240) {
            return "⚠️ PERINGATAN: Screen time kamu sudah " + totalMenit
                    + " menit (4 jam+). Pertimbangkan untuk beristirahat.";
        } else if (totalMenit >= 120) {
            return "ℹ️ INFO: Screen time kamu sudah " + totalMenit
                    + " menit (2 jam+). Jaga kesehatan matamu!";
        } else {
            return "✅ Screen time kamu " + totalMenit + " menit. Tetap jaga kesehatan digitalmu!";
        }
    }

    // ── Getter dan Setter ─────────────────────────────────────────────────

    public Long getId()                        { return id; }
    public String getPesan()                   { return pesan; }
    public void setPesan(String pesan)         { this.pesan = pesan; }
    public LocalDateTime getCreatedAt()        { return createdAt; }
    public void setCreatedAt(LocalDateTime dt) { this.createdAt = dt; }
    public User getUser()                      { return user; }
    public void setUser(User user)             { this.user = user; }
}
