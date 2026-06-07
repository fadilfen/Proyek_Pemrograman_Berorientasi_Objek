-- ============================================================
-- database.sql
-- Script SQL untuk membuat database proyek MindFull
-- Jalankan file ini di MySQL sebelum menjalankan aplikasi
-- ============================================================


-- ── Buat dan pilih database ───────────────────────────────────────────────

CREATE DATABASE IF NOT EXISTS mindfull_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE mindfull_db;


-- ── Tabel users ───────────────────────────────────────────────────────────
-- Menyimpan data akun pengguna aplikasi MindFull
-- Sesuai dengan entity User.java dan data di UserManager.java (proyek GUI)

CREATE TABLE IF NOT EXISTS users (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    nama_user   VARCHAR(100)    NOT NULL,               -- nama lengkap pengguna
    username    VARCHAR(50)     NOT NULL UNIQUE,        -- username unik untuk login
    password    VARCHAR(100)    NOT NULL,               -- password pengguna
    token       INT             NOT NULL DEFAULT 50,    -- saldo token awal = 50

    PRIMARY KEY (id)
);


-- ── Tabel aktivitas_digital ───────────────────────────────────────────────
-- Menyimpan riwayat aktivitas penggunaan aplikasi digital oleh pengguna
-- Sesuai dengan entity AktivitasDigital.java (proyek GUI)

CREATE TABLE IF NOT EXISTS aktivitas_digital (
    id              BIGINT      NOT NULL AUTO_INCREMENT,
    user_id         BIGINT      NOT NULL,               -- relasi ke tabel users
    nama_aplikasi   VARCHAR(50) NOT NULL,               -- nama aplikasi (TikTok, YouTube, dll)
    durasi_menit    INT         NOT NULL,               -- durasi penggunaan dalam menit
    batas_durasi    INT         NOT NULL,               -- batas durasi harian yang ditetapkan
    tanggal         DATE        NOT NULL,               -- tanggal aktivitas dilakukan

    PRIMARY KEY (id),
    CONSTRAINT fk_aktivitas_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE
);


-- ── Tabel topup ───────────────────────────────────────────────────────────
-- Menyimpan riwayat transaksi penambahan token oleh pengguna
-- Sesuai dengan entity TopUp.java (proyek GUI)

CREATE TABLE IF NOT EXISTS topup (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    user_id             BIGINT          NOT NULL,               -- relasi ke tabel users
    jumlah_koin         INT             NOT NULL,               -- jumlah token yang ditambahkan
    metode_pembayaran   VARCHAR(50)     NOT NULL,               -- QRIS, Bank Transfer, E-Wallet
    waktu_top_up        DATETIME        NOT NULL DEFAULT NOW(),  -- waktu transaksi dilakukan
    is_paid             BOOLEAN         NOT NULL DEFAULT TRUE,   -- status pembayaran (false jika QRIS belum dikonfirmasi)

    PRIMARY KEY (id),
    CONSTRAINT fk_topup_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE
);


-- ── Tabel notifikasi ──────────────────────────────────────────────────────
-- Menyimpan pesan notifikasi / peringatan screen time untuk pengguna
-- Sesuai dengan entity Notifikasi.java (proyek GUI)

CREATE TABLE IF NOT EXISTS notifikasi (
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    user_id     BIGINT      NOT NULL,               -- relasi ke tabel users
    pesan       TEXT        NOT NULL,               -- isi pesan notifikasi
    created_at  DATETIME    NOT NULL DEFAULT NOW(), -- waktu notifikasi dibuat

    PRIMARY KEY (id),
    CONSTRAINT fk_notifikasi_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE
);


-- ── Tabel laporan_harian ──────────────────────────────────────────────────
-- Menyimpan ringkasan laporan harian pengguna
-- Sesuai dengan entity LaporanHarian.java (proyek GUI)

CREATE TABLE IF NOT EXISTS laporan_harian (
    id              BIGINT  NOT NULL AUTO_INCREMENT,
    user_id         BIGINT  NOT NULL,               -- relasi ke tabel users
    total_durasi    INT     NOT NULL,               -- total screen time dalam menit
    skor_harian     INT     NOT NULL,               -- skor kesehatan digital (0-100)
    tanggal         DATE    NOT NULL,               -- tanggal laporan
    created_at      DATETIME NOT NULL DEFAULT NOW(),-- waktu laporan dibuat

    PRIMARY KEY (id),
    CONSTRAINT fk_laporan_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE
);


-- ============================================================
-- DATA AWAL (SEED DATA)
-- Sesuai dengan data hardcoded di UserManager.java proyek GUI
-- ============================================================

-- Akun admin default (username: admin, password: 12345)
INSERT INTO users (nama_user, username, password, token)
VALUES ('Admin Operator', 'admin', '12345', 50);

-- Contoh akun pengguna biasa untuk testing
INSERT INTO users (nama_user, username, password, token)
VALUES ('Budi Santoso', 'budi', 'budi123', 50);

INSERT INTO users (nama_user, username, password, token)
VALUES ('Siti Rahayu', 'siti', 'siti123', 50);


-- Contoh data aktivitas digital untuk user admin (id = 1)
INSERT INTO aktivitas_digital (user_id, nama_aplikasi, durasi_menit, batas_durasi, tanggal)
VALUES
    (1, 'TikTok',    90,  60,  CURDATE()),   -- melebihi batas (90 > 60) → OVER LIMIT
    (1, 'YouTube',   45,  60,  CURDATE()),   -- dalam batas (45 < 60)    → HEALTHY
    (1, 'Instagram', 30,  45,  CURDATE()),   -- dalam batas (30 < 45)    → HEALTHY
    (1, 'WhatsApp',  20,  60,  CURDATE()),   -- dalam batas (20 < 60)    → HEALTHY
    (1, 'Netflix',   120, 90,  CURDATE());   -- melebihi batas (120 > 90) → OVER LIMIT

-- Contoh data aktivitas digital untuk user budi (id = 2)
INSERT INTO aktivitas_digital (user_id, nama_aplikasi, durasi_menit, batas_durasi, tanggal)
VALUES
    (2, 'Spotify',   60,  90,  CURDATE()),   -- dalam batas → HEALTHY
    (2, 'TikTok',    100, 60,  CURDATE());   -- melebihi batas → OVER LIMIT

-- Sesuaikan token admin setelah log 5 aktivitas (5 aktivitas × 5 token = 25 token terpotong)
UPDATE users SET token = 25 WHERE username = 'admin';

-- Sesuaikan token budi setelah log 2 aktivitas (2 aktivitas × 5 token = 10 token terpotong)
UPDATE users SET token = 40 WHERE username = 'budi';


-- Contoh riwayat top up untuk user admin (id = 1)
INSERT INTO topup (user_id, jumlah_koin, metode_pembayaran, waktu_top_up, is_paid)
VALUES
    (1, 50, 'QRIS (Instant)',  NOW(), TRUE),
    (1, 30, 'Bank Transfer',   NOW(), TRUE);

-- Sesuaikan token admin setelah top up (25 + 50 + 30 = 105)
UPDATE users SET token = 105 WHERE username = 'admin';


-- Contoh data notifikasi untuk admin
INSERT INTO notifikasi (user_id, pesan, created_at)
VALUES
    (1, '⚠️ PERINGATAN: Screen time kamu sudah 305 menit (5 jam+). Pertimbangkan untuk beristirahat.', NOW()),
    (1, '✅ Top up berhasil! 50 token ditambahkan melalui QRIS (Instant).', NOW()),
    (1, '✅ Top up berhasil! 30 token ditambahkan melalui Bank Transfer.', NOW());


-- Contoh data laporan harian untuk admin (total=305, skor dihitung manual)
-- 305 menit total, 2 aktivitas melebihi batas → score = 100 - (305/10) - (2*20) = 100 - 30 - 40 = 30
INSERT INTO laporan_harian (user_id, total_durasi, skor_harian, tanggal, created_at)
VALUES (1, 305, 30, CURDATE(), NOW());
