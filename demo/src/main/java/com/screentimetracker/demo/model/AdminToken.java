package com.screentimetracker.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity AdminToken merepresentasikan tabel 'admin_tokens' di database.
 * Menyimpan token rahasia yang diperlukan untuk mendaftarkan akun admin.
 * Tanpa token yang valid, akun admin tidak dapat dibuat melalui UI.
 */
@Entity
@Table(name = "admin_tokens")
public class AdminToken {

    // Primary key, auto increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // String token rahasia (contoh: "MINDFULL-ADMIN-2026")
    @Column(nullable = false, unique = true)
    private String token;

    /**
     * Status aktif token.
     * true  = token masih berlaku dan bisa digunakan untuk registrasi admin.
     * false = token sudah tidak berlaku (sudah digunakan atau dicabut).
     */
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    // Waktu token dibuat
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // ── Konstruktor ──────────────────────────────────────────────────────

    // Konstruktor kosong wajib untuk JPA
    public AdminToken() {}

    // Konstruktor untuk membuat token baru
    public AdminToken(String token) {
        this.token     = token;
        this.isActive  = true;
        this.createdAt = LocalDateTime.now();
    }

    // ── Getter dan Setter ─────────────────────────────────────────────────

    public Long getId()                              { return id; }
    public String getToken()                         { return token; }
    public void setToken(String token)               { this.token = token; }
    public boolean isActive()                        { return isActive; }
    public void setActive(boolean active)            { isActive = active; }
    public LocalDateTime getCreatedAt()              { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt){ this.createdAt = createdAt; }
}
