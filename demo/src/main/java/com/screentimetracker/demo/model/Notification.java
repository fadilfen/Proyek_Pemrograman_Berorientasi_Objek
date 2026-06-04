package com.screentimetracker.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity Notification merepresentasikan tabel 'notifications' di database.
 * Mendukung sistem notifikasi lengkap dengan:
 * - Status baca/belum baca
 * - Tipe notifikasi (INFO, WARNING, SUCCESS, DANGER)
 * - URL tujuan saat notifikasi diklik
 * - Tampilan dropdown dari ikon lonceng di navbar
 */
@Entity
@Table(name = "notifications")
public class Notification {

    // Primary key, auto increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Judul singkat notifikasi (ditampilkan di dropdown)
    @Column(nullable = false)
    private String judul;

    // Isi pesan notifikasi lengkap
    @Column(nullable = false, columnDefinition = "TEXT")
    private String pesan;

    /**
     * Tipe notifikasi untuk menentukan warna ikon:
     * - INFO    : biru — informasi umum
     * - SUCCESS : hijau — aksi berhasil
     * - WARNING : kuning — peringatan screen time
     * - DANGER  : merah — over limit
     */
    @Column(nullable = false)
    private String tipe = "INFO";

    /**
     * Status baca notifikasi.
     * false = belum dibaca (ditampilkan sebagai badge merah)
     * true  = sudah dibaca
     */
    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    // URL tujuan navigasi saat notifikasi diklik (bisa null)
    @Column(name = "url_target")
    private String urlTarget;

    // Waktu notifikasi dibuat
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Relasi many-to-one ke tabel users (setiap notifikasi milik satu user)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // ── Konstruktor ──────────────────────────────────────────────────────

    // Konstruktor kosong wajib untuk JPA
    public Notification() {}

    // Konstruktor untuk membuat notifikasi baru
    public Notification(String judul, String pesan, String tipe, User user) {
        this.judul     = judul;
        this.pesan     = pesan;
        this.tipe      = tipe;
        this.user      = user;
        this.isRead    = false;
        this.createdAt = LocalDateTime.now();
    }

    // Konstruktor dengan URL target (untuk notifikasi yang bisa diklik)
    public Notification(String judul, String pesan, String tipe,
                        String urlTarget, User user) {
        this.judul     = judul;
        this.pesan     = pesan;
        this.tipe      = tipe;
        this.urlTarget = urlTarget;
        this.user      = user;
        this.isRead    = false;
        this.createdAt = LocalDateTime.now();
    }

    // ── Getter dan Setter ─────────────────────────────────────────────────

    public Long getId()                              { return id; }
    public String getJudul()                         { return judul; }
    public void setJudul(String judul)               { this.judul = judul; }
    public String getPesan()                         { return pesan; }
    public void setPesan(String pesan)               { this.pesan = pesan; }
    public String getTipe()                          { return tipe; }
    public void setTipe(String tipe)                 { this.tipe = tipe; }
    public boolean isRead()                          { return isRead; }
    public void setRead(boolean read)                { isRead = read; }
    public String getUrlTarget()                     { return urlTarget; }
    public void setUrlTarget(String urlTarget)       { this.urlTarget = urlTarget; }
    public LocalDateTime getCreatedAt()              { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt){ this.createdAt = createdAt; }
    public User getUser()                            { return user; }
    public void setUser(User user)                   { this.user = user; }
}
