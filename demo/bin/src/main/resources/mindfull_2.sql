-- ============================================================
-- DATABASE: mindfull_2
-- Aplikasi: MindFull — Screen Time Tracker untuk Kesehatan Mental
-- Versi    : 2.0 (Refactored)
-- Dibuat   : 2026
-- ============================================================

-- Hapus database lama jika ada, buat yang baru
CREATE DATABASE IF NOT EXISTS `mindfull_2`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `mindfull_2`;

-- ============================================================
-- TABEL 1: roles
-- Menyimpan daftar role yang tersedia di sistem
-- ============================================================
CREATE TABLE IF NOT EXISTS `roles` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `nama_role`  VARCHAR(20)  NOT NULL COMMENT 'Nama role: USER atau ADMIN',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_nama_role` (`nama_role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Daftar role pengguna di sistem';

-- Data awal roles
INSERT INTO `roles` (`nama_role`) VALUES ('USER'), ('ADMIN');

-- ============================================================
-- TABEL 2: users
-- Menyimpan data akun pengguna
-- ============================================================
CREATE TABLE IF NOT EXISTS `users` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `nama_user`   VARCHAR(100) NOT NULL COMMENT 'Nama lengkap pengguna',
    `username`    VARCHAR(50)  NOT NULL COMMENT 'Username unik untuk login',
    `password`    VARCHAR(255) NOT NULL COMMENT 'Password pengguna',
    `token`       INT          NOT NULL DEFAULT 50 COMMENT 'Saldo token pengguna',
    `role`        VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT 'Role: USER atau ADMIN',
    `is_active`   TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '1=aktif, 0=nonaktif',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_username` (`username`),
    INDEX `idx_role` (`role`),
    INDEX `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Data akun pengguna aplikasi MindFull';

-- Data awal users (password disimpan plain text sesuai proyek)
INSERT INTO `users` (`nama_user`, `username`, `password`, `token`, `role`, `is_active`) VALUES
    ('Admin Operator', 'admin',  '12345',    105, 'ADMIN', 1),
    ('Budi Santoso',   'budi',   'budi123',  40,  'USER',  1),
    ('Siti Rahayu',    'siti',   'siti123',  50,  'USER',  1),
    ('Reval Auliansyah', 'reval', 'reval123', 117, 'USER',  1),
    ('Fadil Fenegar',  'fadil',  'fadil123', 50,  'USER',  1);

-- ============================================================
-- TABEL 3: admin_tokens
-- Menyimpan token rahasia untuk registrasi akun admin
-- ============================================================
CREATE TABLE IF NOT EXISTS `admin_tokens` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `token`      VARCHAR(100) NOT NULL COMMENT 'Token rahasia untuk registrasi admin',
    `is_active`  TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '1=token masih berlaku',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_token` (`token`),
    INDEX `idx_token_active` (`token`, `is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Token rahasia untuk registrasi akun admin';

-- Token admin default (bisa diganti sesuai kebutuhan)
INSERT INTO `admin_tokens` (`token`, `is_active`) VALUES
    ('MINDFULL-ADMIN-2026', 1),
    ('ADMIN-SECRET-KEY',    1);

-- ============================================================
-- TABEL 4: notifications
-- Menyimpan notifikasi untuk setiap pengguna
-- ============================================================
CREATE TABLE IF NOT EXISTS `notifications` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT       NOT NULL COMMENT 'Pemilik notifikasi',
    `judul`       VARCHAR(100) NOT NULL COMMENT 'Judul singkat notifikasi',
    `pesan`       TEXT         NOT NULL COMMENT 'Isi pesan notifikasi',
    `tipe`        VARCHAR(30)  NOT NULL DEFAULT 'INFO'
                  COMMENT 'Tipe: INFO, WARNING, SUCCESS, DANGER',
    `is_read`     TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '0=belum dibaca, 1=sudah dibaca',
    `url_target`  VARCHAR(200) DEFAULT NULL COMMENT 'URL tujuan saat notifikasi diklik',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `fk_notif_user` (`user_id`),
    INDEX `idx_notif_read` (`user_id`, `is_read`),
    CONSTRAINT `fk_notif_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Sistem notifikasi untuk setiap pengguna';

-- ============================================================
-- TABEL 5: aktivitas_digital
-- Menyimpan log penggunaan aplikasi digital
-- ============================================================
CREATE TABLE IF NOT EXISTS `aktivitas_digital` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`       BIGINT       NOT NULL COMMENT 'Pemilik aktivitas',
    `nama_aplikasi` VARCHAR(50)  NOT NULL COMMENT 'Nama aplikasi yang digunakan',
    `durasi_menit`  INT          NOT NULL COMMENT 'Durasi penggunaan dalam menit',
    `batas_durasi`  INT          NOT NULL COMMENT 'Batas durasi harian (menit)',
    `jam_mulai`     TIME         DEFAULT NULL COMMENT 'Jam mulai penggunaan',
    `tanggal`       DATE         NOT NULL COMMENT 'Tanggal aktivitas',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `fk_aktivitas_user` (`user_id`),
    INDEX `idx_aktivitas_tanggal` (`user_id`, `tanggal`),
    CONSTRAINT `fk_aktivitas_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Log penggunaan aplikasi digital pengguna';

-- Data awal aktivitas
INSERT INTO `aktivitas_digital` (`user_id`, `nama_aplikasi`, `durasi_menit`, `batas_durasi`, `tanggal`) VALUES
    (1, 'TikTok',    90,  60, '2026-05-06'),
    (1, 'YouTube',   45,  60, '2026-05-06'),
    (1, 'Instagram', 30,  45, '2026-05-06'),
    (1, 'WhatsApp',  20,  60, '2026-05-06'),
    (1, 'Netflix',   120, 90, '2026-05-06'),
    (2, 'Spotify',   60,  90, '2026-05-06'),
    (2, 'TikTok',    100, 60, '2026-05-06'),
    (5, 'Instagram', 12,  22, '2026-06-01'),
    (5, 'TikTok',    4,   60, '2026-06-01'),
    (5, 'YouTube',   60,  90, '2026-06-02'),
    (5, 'WhatsApp',  30,  60, '2026-06-03'),
    (5, 'Netflix',   90,  60, '2026-06-04');

-- ============================================================
-- TABEL 6: health_scores
-- Menyimpan riwayat skor kesehatan harian pengguna
-- ============================================================
CREATE TABLE IF NOT EXISTS `health_scores` (
    `id`           BIGINT  NOT NULL AUTO_INCREMENT,
    `user_id`      BIGINT  NOT NULL COMMENT 'Pemilik skor',
    `tanggal`      DATE    NOT NULL COMMENT 'Tanggal skor dihitung',
    `skor`         INT     NOT NULL COMMENT 'Nilai skor kesehatan (0-100)',
    `total_screen` INT     NOT NULL DEFAULT 0 COMMENT 'Total screen time pada hari tersebut (menit)',
    `kategori`     VARCHAR(30) NOT NULL DEFAULT 'SEHAT'
                   COMMENT 'Kategori: SEHAT, PERLU_PERHATIAN, BERBAHAYA',
    `created_at`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `fk_score_user` (`user_id`),
    UNIQUE KEY `uq_user_tanggal` (`user_id`, `tanggal`),
    CONSTRAINT `fk_score_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Riwayat skor kesehatan digital harian pengguna';

-- ============================================================
-- TABEL 7: payments (menggantikan topup)
-- Menyimpan riwayat transaksi pembayaran / top up token
-- ============================================================
CREATE TABLE IF NOT EXISTS `payments` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`          BIGINT       NOT NULL COMMENT 'Pengguna yang melakukan top up',
    `jumlah_token`     INT          NOT NULL COMMENT 'Jumlah token yang dibeli',
    `metode`           VARCHAR(50)  NOT NULL COMMENT 'Metode: QRIS, Bank Transfer, E-Wallet',
    `status`           VARCHAR(20)  NOT NULL DEFAULT 'PENDING'
                       COMMENT 'Status: PENDING, VERIFIED, REJECTED',
    `total_harga`      BIGINT       NOT NULL DEFAULT 0 COMMENT 'Harga total dalam rupiah',
    `verified_by`      BIGINT       DEFAULT NULL COMMENT 'ID admin yang memverifikasi',
    `verified_at`      DATETIME     DEFAULT NULL COMMENT 'Waktu verifikasi',
    `created_at`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `fk_payment_user` (`user_id`),
    KEY `fk_payment_verifier` (`verified_by`),
    INDEX `idx_payment_status` (`status`),
    CONSTRAINT `fk_payment_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_payment_verifier`
        FOREIGN KEY (`verified_by`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Riwayat transaksi pembayaran dan top up token';

-- Data awal payments
INSERT INTO `payments` (`user_id`, `jumlah_token`, `metode`, `status`, `total_harga`) VALUES
    (1, 50,  'QRIS (Instant)', 'VERIFIED', 50000),
    (1, 30,  'Bank Transfer',  'VERIFIED', 30000),
    (2, 20,  'E-Wallet',       'VERIFIED', 20000),
    (5, 50,  'QRIS (Instant)', 'PENDING',  50000),
    (5, 12,  'Bank Transfer',  'VERIFIED', 12000);

-- ============================================================
-- TABEL 8: daily_reports
-- Menyimpan laporan harian yang sudah di-generate
-- ============================================================
CREATE TABLE IF NOT EXISTS `daily_reports` (
    `id`             BIGINT    NOT NULL AUTO_INCREMENT,
    `user_id`        BIGINT    NOT NULL COMMENT 'Pemilik laporan',
    `tanggal`        DATE      NOT NULL COMMENT 'Tanggal laporan',
    `total_screen`   INT       NOT NULL DEFAULT 0 COMMENT 'Total screen time (menit)',
    `skor_kesehatan` INT       NOT NULL DEFAULT 100 COMMENT 'Skor kesehatan (0-100)',
    `kategori`       VARCHAR(30) NOT NULL DEFAULT 'SEHAT',
    `ringkasan`      TEXT      DEFAULT NULL COMMENT 'Teks ringkasan laporan',
    `rekomendasi`    TEXT      DEFAULT NULL COMMENT 'Rekomendasi untuk pengguna',
    `created_at`     DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `fk_report_user` (`user_id`),
    INDEX `idx_report_tanggal` (`user_id`, `tanggal`),
    CONSTRAINT `fk_report_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Laporan harian kesehatan digital pengguna';

-- ============================================================
-- TABEL 9: transactions (alias dari payments untuk histori)
-- Catatan: digunakan sebagai view untuk backward compatibility
-- ============================================================
-- Tabel ini adalah alias/view dari payments
CREATE OR REPLACE VIEW `transactions` AS
    SELECT
        p.id,
        p.user_id,
        u.nama_user,
        u.username,
        p.jumlah_token,
        p.metode,
        p.status,
        p.total_harga,
        p.verified_by,
        p.verified_at,
        p.created_at
    FROM `payments` p
    JOIN `users` u ON u.id = p.user_id;

-- ============================================================
-- SELESAI
-- ============================================================
-- Catatan migrasi dari mindfull_db:
-- 1. Jalankan script ini di HeidiSQL / MySQL Workbench
-- 2. Data lama dapat dimigrasikan dengan query berikut:
--
--    INSERT INTO mindfull_2.users (nama_user, username, password, token, role, is_active)
--    SELECT nama_user, username, password, token, 'USER', 1
--    FROM mindfull_db.users WHERE username != 'admin';
--
--    INSERT INTO mindfull_2.aktivitas_digital (user_id, nama_aplikasi, durasi_menit, batas_durasi, tanggal)
--    SELECT ad.user_id, ad.nama_aplikasi, ad.durasi_menit, ad.batas_durasi, ad.tanggal
--    FROM mindfull_db.aktivitas_digital ad
--    JOIN mindfull_2.users u ON u.username = (SELECT username FROM mindfull_db.users WHERE id = ad.user_id);
-- ============================================================
