-- ========================================
-- DATABASE SCHEMA UPDATED - Screen Time Tracker
-- Mendukung Parent-Child Account System
-- ========================================

CREATE DATABASE IF NOT EXISTS `mindfull_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `mindfull_db`;

-- ========================================
-- TABEL: users
-- Role: 'parent' atau 'child'
-- parent_id: NULL jika parent, berisi id parent jika child
-- ========================================
CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `nama_user` VARCHAR(100) NOT NULL,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(100) NOT NULL,
  `token` INT NOT NULL DEFAULT 50,
  `role` ENUM('parent', 'child') NOT NULL DEFAULT 'parent',
  `parent_id` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_parent` (`parent_id`),
  CONSTRAINT `fk_parent` FOREIGN KEY (`parent_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- TABEL: app_timers
-- Setting timer aplikasi untuk setiap child
-- start_time: jam mulai tracking (format HH:mm:ss)
-- duration_minutes: durasi yang diizinkan dalam menit
-- end_time dihitung dari start_time + duration_minutes
-- is_tracking: status apakah sedang di-track (1) atau tidak (0)
-- ========================================
CREATE TABLE IF NOT EXISTS `app_timers` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `child_id` BIGINT NOT NULL,
  `app_name` VARCHAR(50) NOT NULL,
  `duration_minutes` INT NOT NULL,
  `start_time` TIME NOT NULL,
  `end_time` TIME NOT NULL,
  `is_tracking` TINYINT(1) NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_timer_child` (`child_id`),
  CONSTRAINT `fk_timer_child` FOREIGN KEY (`child_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- TABEL: aktivitas_digital
-- Sama seperti sebelumnya
-- ========================================
CREATE TABLE IF NOT EXISTS `aktivitas_digital` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `nama_aplikasi` VARCHAR(50) NOT NULL,
  `durasi_menit` INT NOT NULL,
  `batas_durasi` INT NOT NULL,
  `tanggal` DATE NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_aktivitas_user` (`user_id`),
  CONSTRAINT `fk_aktivitas_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- TABEL: topup
-- Sama seperti sebelumnya
-- ========================================
CREATE TABLE IF NOT EXISTS `topup` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `jumlah_koin` INT NOT NULL,
  `metode_pembayaran` VARCHAR(50) NOT NULL,
  `waktu_top_up` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_topup_user` (`user_id`),
  CONSTRAINT `fk_topup_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- DATA SAMPLE
-- ========================================
INSERT INTO `users` (`id`, `nama_user`, `username`, `password`, `token`, `role`, `parent_id`) VALUES
(1, 'John Doe', 'john', '12345', 100, 'parent', NULL),
(2, 'Jane Doe', 'jane', 'jane123', 0, 'child', 1);
