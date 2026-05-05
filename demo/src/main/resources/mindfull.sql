-- --------------------------------------------------------
-- Host:                         localhost
-- Server version:               8.0.30 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.11.0.7065
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for mindfull_db
CREATE DATABASE IF NOT EXISTS `mindfull_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `mindfull_db`;

-- Dumping structure for table mindfull_db.aktivitas_digital
CREATE TABLE IF NOT EXISTS `aktivitas_digital` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `nama_aplikasi` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `durasi_menit` int NOT NULL,
  `batas_durasi` int NOT NULL,
  `tanggal` date NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_aktivitas_user` (`user_id`),
  CONSTRAINT `fk_aktivitas_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table mindfull_db.aktivitas_digital: ~16 rows (approximately)
INSERT INTO `aktivitas_digital` (`id`, `user_id`, `nama_aplikasi`, `durasi_menit`, `batas_durasi`, `tanggal`) VALUES
	(1, 1, 'TikTok', 90, 60, '2026-05-06'),
	(2, 1, 'YouTube', 45, 60, '2026-05-06'),
	(3, 1, 'Instagram', 30, 45, '2026-05-06'),
	(4, 1, 'WhatsApp', 20, 60, '2026-05-06'),
	(5, 1, 'Netflix', 120, 90, '2026-05-06'),
	(6, 2, 'Spotify', 60, 90, '2026-05-06'),
	(7, 2, 'TikTok', 100, 60, '2026-05-06'),
	(8, 1, 'TikTok', 90, 60, '2026-05-06'),
	(9, 1, 'YouTube', 45, 60, '2026-05-06'),
	(10, 1, 'Instagram', 30, 45, '2026-05-06'),
	(11, 1, 'WhatsApp', 20, 60, '2026-05-06'),
	(12, 1, 'Netflix', 120, 90, '2026-05-06'),
	(13, 2, 'Spotify', 60, 90, '2026-05-06'),
	(14, 2, 'TikTok', 100, 60, '2026-05-06'),
	(15, 13, 'Instagram', 12, 22, '2026-05-06'),
	(16, 13, 'TikTok', 4, 1, '2026-05-06');

-- Dumping structure for table mindfull_db.topup
CREATE TABLE IF NOT EXISTS `topup` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `jumlah_koin` int NOT NULL,
  `metode_pembayaran` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `waktu_top_up` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_topup_user` (`user_id`),
  CONSTRAINT `fk_topup_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table mindfull_db.topup: ~13 rows (approximately)
INSERT INTO `topup` (`id`, `user_id`, `jumlah_koin`, `metode_pembayaran`, `waktu_top_up`) VALUES
	(1, 1, 50, 'QRIS (Instant)', '2026-05-06 04:40:35'),
	(2, 1, 30, 'Bank Transfer', '2026-05-06 04:40:35'),
	(3, 1, 50, 'QRIS (Instant)', '2026-05-06 04:44:44'),
	(4, 1, 30, 'Bank Transfer', '2026-05-06 04:44:44'),
	(5, 13, 0, 'QRIS (Instant)', '2026-05-06 05:39:08'),
	(6, 13, 1, 'Bank Transfer', '2026-05-06 05:39:16'),
	(7, 13, 11, 'QRIS (Instant)', '2026-05-06 05:39:21'),
	(8, 13, 0, 'QRIS (Instant)', '2026-05-06 05:39:27'),
	(9, 13, 1, 'QRIS (Instant)', '2026-05-06 05:45:48'),
	(10, 13, 50, 'QRIS (Instant)', '2026-05-06 05:46:05'),
	(11, 13, 1, 'QRIS (Instant)', '2026-05-06 05:46:22'),
	(12, 13, 1, 'QRIS (Instant)', '2026-05-06 05:46:28'),
	(13, 13, 12000, 'QRIS (Instant)', '2026-05-06 05:52:22');

-- Dumping structure for table mindfull_db.users
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nama_user` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `token` int NOT NULL DEFAULT '50',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table mindfull_db.users: ~5 rows (approximately)
INSERT INTO `users` (`id`, `nama_user`, `username`, `password`, `token`) VALUES
	(1, 'Admin Operator', 'admin', '12345', 105),
	(2, 'Budi Santoso', 'budi', 'budi123', 40),
	(3, 'Siti Rahayu', 'siti', 'siti123', 50),
	(13, 'Reval Auliansyah', 'reval', 'reval123', 117),
	(14, 'Fadil Fenegar', 'fadil', 'fadil123', 50);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
