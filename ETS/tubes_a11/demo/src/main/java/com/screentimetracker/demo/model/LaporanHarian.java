package com.screentimetracker.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity LaporanHarian merepresentasikan tabel 'laporan_harian' di database.
 * Menyimpan ringkasan harian screen time dan skor kesehatan pengguna,
 * sesuai dengan kelas LaporanHarian.java di proyek GUI.
 */
@Entity
@Table(name = "laporan_harian")
public class LaporanHarian {

    // Primary key, auto increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Total durasi screen time pada hari ini (menit)
    private int totalDurasi;

    // Skor kesehatan digital harian (0–100)
    private int skorHarian;

    // Tanggal laporan
    private LocalDate tanggal;

    // Waktu laporan dibuat
    private LocalDateTime createdAt;

    // Relasi many-to-one ke tabel users (setiap laporan milik satu user)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Konstruktor kosong wajib untuk JPA
    public LaporanHarian() {}

    // Konstruktor untuk membuat laporan harian baru
    public LaporanHarian(int totalDurasi, int skorHarian, LocalDate tanggal, User user) {
        this.totalDurasi = totalDurasi;
        this.skorHarian  = skorHarian;
        this.tanggal     = tanggal;
        this.user        = user;
        this.createdAt   = LocalDateTime.now();
    }

    /**
     * Menghasilkan teks laporan harian lengkap.
     * Logika sama dengan generateLaporan() di proyek GUI.
     *
     * @param aktivitasList Daftar aktivitas digital pengguna
     * @return String berisi laporan harian yang terformat
     */
    public String generateLaporan(List<AktivitasDigital> aktivitasList) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== LAPORAN HARIAN ===\n");
        sb.append("Nama User         : ").append(user != null ? user.getNamaUser() : "-").append("\n");
        sb.append("Tanggal           : ").append(tanggal).append("\n");
        sb.append("------------------------------\n");
        sb.append("Detail Aplikasi:\n");
        for (AktivitasDigital a : aktivitasList) {
            sb.append("- ").append(a.getNamaAplikasi())
              .append(" : ").append(a.getDurasiMenit()).append(" menit");
            if (a.melebihiBatas()) sb.append(" ⚠️ OVER LIMIT");
            sb.append("\n");
        }
        sb.append("------------------------------\n");
        sb.append("Total Screen Time : ").append(totalDurasi).append(" menit\n");
        sb.append("Skor Harian       : ").append(skorHarian).append("\n");
        sb.append("Status            : ").append(skorHarian >= 70 ? "Sehat ✅" : "Kurangi Screen Time ⚠️");
        return sb.toString();
    }

    // ── Getter dan Setter ─────────────────────────────────────────────────

    public Long getId()                          { return id; }
    public int getTotalDurasi()                  { return totalDurasi; }
    public void setTotalDurasi(int totalDurasi)  { this.totalDurasi = totalDurasi; }
    public int getSkorHarian()                   { return skorHarian; }
    public void setSkorHarian(int skorHarian)    { this.skorHarian = skorHarian; }
    public LocalDate getTanggal()                { return tanggal; }
    public void setTanggal(LocalDate tanggal)    { this.tanggal = tanggal; }
    public LocalDateTime getCreatedAt()          { return createdAt; }
    public void setCreatedAt(LocalDateTime dt)   { this.createdAt = dt; }
    public User getUser()                        { return user; }
    public void setUser(User user)               { this.user = user; }
}
