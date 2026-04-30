package com.screentimetracker.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity TopUp merepresentasikan tabel 'topup' di database.
 * Menyimpan riwayat transaksi penambahan token oleh pengguna,
 * sesuai dengan kelas TopUp.java di proyek GUI.
 */
@Entity
@Table(name = "topup")
public class TopUp {

    // Primary key, auto increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Jumlah token yang ditambahkan
    private int jumlahKoin;

    // Metode pembayaran yang dipilih (QRIS, Bank Transfer, E-Wallet)
    private String metodePembayaran;

    // Waktu transaksi top up dilakukan
    private LocalDateTime waktuTopUp;

    // Relasi many-to-one ke tabel users (setiap top up milik satu user)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Konstruktor kosong wajib untuk JPA
    public TopUp() {}

    // Konstruktor untuk membuat transaksi top up baru
    public TopUp(int jumlahKoin, String metodePembayaran, User user) {
        this.jumlahKoin        = jumlahKoin;
        this.metodePembayaran  = metodePembayaran;
        this.user              = user;
        this.waktuTopUp        = LocalDateTime.now();
    }

    /**
     * Memvalidasi bahwa jumlah koin yang di-top up lebih dari nol.
     * Logika sama dengan validasiPembayaran() di proyek GUI.
     */
    public boolean validasiPembayaran() {
        return jumlahKoin > 0;
    }

    // ── Getter dan Setter ─────────────────────────────────────────────────

    public Long getId()                                    { return id; }
    public int getJumlahKoin()                             { return jumlahKoin; }
    public void setJumlahKoin(int jumlahKoin)              { this.jumlahKoin = jumlahKoin; }
    public String getMetodePembayaran()                    { return metodePembayaran; }
    public void setMetodePembayaran(String metode)         { this.metodePembayaran = metode; }
    public LocalDateTime getWaktuTopUp()                   { return waktuTopUp; }
    public void setWaktuTopUp(LocalDateTime waktuTopUp)    { this.waktuTopUp = waktuTopUp; }
    public User getUser()                                  { return user; }
    public void setUser(User user)                         { this.user = user; }
}
