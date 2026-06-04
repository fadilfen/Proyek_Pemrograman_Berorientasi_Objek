package com.screentimetracker.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity Payment merepresentasikan tabel 'payments' di database.
 * Menggantikan model TopUp lama dengan tambahan field:
 * status verifikasi, admin yang memverifikasi, dan total harga.
 * Admin dapat melihat, memverifikasi, mengubah status, dan menghapus data payment.
 */
@Entity
@Table(name = "payments")
public class Payment {

    // Primary key, auto increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Jumlah token yang dibeli
    @Column(name = "jumlah_token", nullable = false)
    private int jumlahToken;

    // Metode pembayaran: QRIS (Instant), Bank Transfer, E-Wallet
    @Column(nullable = false)
    private String metode;

    /**
     * Status pembayaran:
     * - PENDING  : menunggu verifikasi admin
     * - VERIFIED : sudah diverifikasi dan token sudah ditambahkan
     * - REJECTED : ditolak oleh admin
     */
    @Column(nullable = false)
    private String status = "PENDING";

    // Total harga dalam rupiah (jumlah_token × harga_per_token)
    @Column(name = "total_harga")
    private long totalHarga;

    // ID admin yang memverifikasi (null jika belum diverifikasi)
    @Column(name = "verified_by")
    private Long verifiedBy;

    // Waktu verifikasi oleh admin
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    // Waktu transaksi dibuat
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Relasi many-to-one ke tabel users (EAGER fetch untuk keperluan view admin)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    // ── Konstruktor ──────────────────────────────────────────────────────

    // Konstruktor kosong wajib untuk JPA
    public Payment() {}

    // Konstruktor untuk transaksi baru (status default = PENDING)
    public Payment(int jumlahToken, String metode, long totalHarga, User user) {
        this.jumlahToken = jumlahToken;
        this.metode      = metode;
        this.totalHarga  = totalHarga;
        this.user        = user;
        this.status      = "PENDING";
        this.createdAt   = LocalDateTime.now();
    }

    // ── Business Logic ───────────────────────────────────────────────────

    /**
     * Mengecek apakah pembayaran sudah diverifikasi oleh admin.
     */
    public boolean isVerified() {
        return "VERIFIED".equals(this.status);
    }

    /**
     * Mengecek apakah pembayaran masih menunggu verifikasi.
     */
    public boolean isPending() {
        return "PENDING".equals(this.status);
    }

    // ── Getter dan Setter ─────────────────────────────────────────────────

    public Long getId()                                { return id; }
    public int getJumlahToken()                        { return jumlahToken; }
    public void setJumlahToken(int jumlahToken)        { this.jumlahToken = jumlahToken; }
    public String getMetode()                          { return metode; }
    public void setMetode(String metode)               { this.metode = metode; }
    public String getStatus()                          { return status; }
    public void setStatus(String status)               { this.status = status; }
    public long getTotalHarga()                        { return totalHarga; }
    public void setTotalHarga(long totalHarga)         { this.totalHarga = totalHarga; }
    public Long getVerifiedBy()                        { return verifiedBy; }
    public void setVerifiedBy(Long verifiedBy)         { this.verifiedBy = verifiedBy; }
    public LocalDateTime getVerifiedAt()               { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt){ this.verifiedAt = verifiedAt; }
    public LocalDateTime getCreatedAt()                { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)  { this.createdAt = createdAt; }
    public User getUser()                              { return user; }
    public void setUser(User user)                     { this.user = user; }
}
