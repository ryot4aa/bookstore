-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versi server:                 8.0.30 - MySQL Community Server - GPL
-- OS Server:                    Win64
-- HeidiSQL Versi:               12.1.0.6537
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Membuang struktur basisdata untuk bookstore
CREATE DATABASE IF NOT EXISTS `bookstore` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `bookstore`;

-- membuang struktur untuk table bookstore.book
CREATE TABLE IF NOT EXISTS `book` (
  `book_id` bigint NOT NULL AUTO_INCREMENT,
  `author` varchar(255) NOT NULL,
  `price` double DEFAULT NULL,
  `stock` int NOT NULL,
  `title` varchar(255) NOT NULL,
  `total_sold` int NOT NULL DEFAULT '0',
  `last_restock_date` datetime(6) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`book_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Membuang data untuk tabel bookstore.book: ~13 rows (lebih kurang)
INSERT INTO `book` (`book_id`, `author`, `price`, `stock`, `title`, `total_sold`, `last_restock_date`, `image_url`) VALUES
	(1, 'Andrea Hirata', 85000, 49, 'Laskar Pelangi', 8, '2025-06-06 18:54:11.446393', '/images/books/1.jpg'),
	(2, 'Tere Liye', -10000, 48, 'Pulang', 4, '2025-06-05 04:42:22.607491', '/images/books/3.jpg'),
	(3, 'Pramoedya Ananta Toer', 110000, 22, 'Bumi Manusia', 4, NULL, '/images/books/2.jpg'),
	(4, 'Dewi Lestari', 78000, 57, 'Supernova: Kesatria, Puteri, & Bintang Jatuh', 3, NULL, '/images/books/4.jpg'),
	(5, 'Raditya Dika', 65000, 69, 'Kambing Jantan', 1, NULL, '/images/books/5.jpg'),
	(6, 'Fiersa Besari', 70000, 55, 'Garis Waktu', 0, NULL, '/images/books/6.jpg'),
	(8, 'J.K. Rowling', 105000, 40, 'Harry Potter and the Sorcerer\'s Stone', 0, NULL, '/images/books/7.jpg'),
	(9, 'Haruki Murakami', 98000, 35, 'Norwegian Wood', 0, NULL, '/images/books/8.jpg'),
	(10, 'Agatha Christie', 75000, 65, 'And Then There Were None', 0, NULL, '/images/books/9jpg.jpg'),
	(13, 'Crut Cobain', 5000000, 0, 'Nirvana', 20, '2025-06-06 18:38:23.881471', '/images/books/10.jpg'),
	(20, 'Joko Salim, S.kom,SE', 990000, 19, 'Bermodal Mepet Anda Jadi Miliuner', 1, '2025-06-11 03:47:11.561784', '/images/books/z.jpg'),
	(21, 'sjkdbg', 82365, 1284, 'hgsvdh', 0, '2025-06-11 04:42:16.908802', '/images/books/default_book.png'),
	(22, 'Joko Salim, S.kom,SE', 999000, 99, 'Bermodal Mepet Anda Jadi Miliuner', 0, '2025-06-11 04:43:02.074783', '/images/books/z.jpg');

-- membuang struktur untuk table bookstore.books
CREATE TABLE IF NOT EXISTS `books` (
  `book_id` bigint NOT NULL DEFAULT '0',
  `title` varchar(255) NOT NULL,
  `author` varchar(255) NOT NULL,
  `price` double DEFAULT NULL,
  `stock` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`book_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Membuang data untuk tabel bookstore.books: ~0 rows (lebih kurang)

-- membuang struktur untuk table bookstore.book_restock_history
CREATE TABLE IF NOT EXISTS `book_restock_history` (
  `restock_id` bigint NOT NULL AUTO_INCREMENT,
  `book_id` bigint NOT NULL,
  `restock_quantity` int NOT NULL,
  `restock_date` datetime(6) NOT NULL,
  `restocked_by_user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`restock_id`),
  KEY `FK_restock_book` (`book_id`),
  KEY `FK_restock_user` (`restocked_by_user_id`),
  CONSTRAINT `FK_restock_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`),
  CONSTRAINT `FK_restock_user` FOREIGN KEY (`restocked_by_user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Membuang data untuk tabel bookstore.book_restock_history: ~6 rows (lebih kurang)
INSERT INTO `book_restock_history` (`restock_id`, `book_id`, `restock_quantity`, `restock_date`, `restocked_by_user_id`) VALUES
	(1, 1, 5, '2025-06-05 04:29:07.876581', 6),
	(2, 1, 5, '2025-06-05 04:31:47.600972', 6),
	(3, 2, 10, '2025-06-05 04:42:22.624794', 6),
	(4, 1, 5, '2025-06-05 04:52:38.438497', 6),
	(5, 1, 5, '2025-06-05 13:45:55.098338', 2),
	(6, 1, 2, '2025-06-06 18:54:11.457594', 2);

-- membuang struktur untuk table bookstore.transaction
CREATE TABLE IF NOT EXISTS `transaction` (
  `transaction_id` bigint NOT NULL AUTO_INCREMENT,
  `quantity` int NOT NULL,
  `total_price` double DEFAULT NULL,
  `transaction_date` datetime(6) DEFAULT NULL,
  `book_id` bigint NOT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`transaction_id`),
  KEY `FK8hddvclv2iqa3sg1dm8295pqw` (`book_id`),
  KEY `FKsg7jp0aj6qipr50856wf6vbw1` (`user_id`),
  CONSTRAINT `FK8hddvclv2iqa3sg1dm8295pqw` FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`),
  CONSTRAINT `FKsg7jp0aj6qipr50856wf6vbw1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Membuang data untuk tabel bookstore.transaction: ~23 rows (lebih kurang)
INSERT INTO `transaction` (`transaction_id`, `quantity`, `total_price`, `transaction_date`, `book_id`, `user_id`) VALUES
	(2, 2, 170000, '2025-06-05 03:24:41.557364', 1, 1),
	(3, 2, 220000, '2025-06-05 03:24:41.583344', 3, 1),
	(4, 2, 170000, '2025-06-05 03:38:55.168478', 1, 1),
	(5, 3, 276000, '2025-06-05 03:38:55.289423', 2, 1),
	(7, 2, 220000, '2025-06-05 04:44:16.918299', 3, 1),
	(9, 2, 184000, '2025-06-05 13:47:30.238835', 2, 3),
	(10, 1, 85000, '2025-06-06 19:00:26.711891', 1, 3),
	(11, 1, 5000000, '2025-06-06 19:00:26.805399', 13, 3),
	(12, 1, 92000, '2025-06-06 19:04:07.288275', 2, 3),
	(13, 1, 85000, '2025-06-06 19:06:35.966768', 1, 3),
	(14, 1, 85000, '2025-06-06 19:08:33.127307', 1, 3),
	(15, 1, 85000, '2025-06-06 19:09:52.386391', 1, 7),
	(16, 1, 5000000, '2025-06-08 23:02:25.752630', 13, 3),
	(18, 1, 5000000, '2025-06-09 02:43:01.124702', 13, 3),
	(19, 1, 110000, '2025-06-10 15:21:32.154243', 3, 1),
	(20, 1, 990000, '2025-06-11 04:12:01.195139', 20, 1),
	(21, 1, 85000, '2025-06-11 04:39:22.197671', 1, 8),
	(22, 1, 92000, '2025-06-11 04:39:22.229254', 2, 8),
	(23, 1, 110000, '2025-06-11 04:39:22.242445', 3, 8),
	(24, 1, 78000, '2025-06-11 04:40:04.270761', 4, 8),
	(25, 1, 65000, '2025-06-11 04:40:04.288788', 5, 8),
	(26, 3, 255000, '2025-06-11 04:41:05.084587', 1, 8),
	(27, 17, 85000000, '2025-06-11 04:41:30.995657', 13, 8);

-- membuang struktur untuk table bookstore.transactions
CREATE TABLE IF NOT EXISTS `transactions` (
  `transaction_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `book_id` bigint NOT NULL DEFAULT '0',
  `quantity` int NOT NULL,
  `total_price` double DEFAULT NULL,
  `transaction_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`transaction_id`) USING BTREE,
  KEY `user_id` (`user_id`),
  KEY `book_id` (`book_id`),
  CONSTRAINT `transaction_book_id` FOREIGN KEY (`book_id`) REFERENCES `books` (`book_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `transaction_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Membuang data untuk tabel bookstore.transactions: ~0 rows (lebih kurang)

-- membuang struktur untuk table bookstore.user
CREATE TABLE IF NOT EXISTS `user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `password` varchar(255) NOT NULL,
  `role` enum('admin','user') NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UKsb8bbouer5wak8vyiiy4pf2bx` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Membuang data untuk tabel bookstore.user: ~7 rows (lebih kurang)
INSERT INTO `user` (`user_id`, `password`, `role`, `username`) VALUES
	(1, 'ryouta123', 'user', 'Ryouta'),
	(2, 'seijiro123', 'admin', 'Seijiro'),
	(3, 'supernayr123', 'user', 'superNayr'),
	(4, 'admin', 'admin', 'adminpass'),
	(6, 'jim123', 'admin', 'JimGiovani'),
	(7, 'akashi123', 'user', 'Akashi'),
	(8, '12345', 'user', 'apinganteng');

-- membuang struktur untuk table bookstore.users
CREATE TABLE IF NOT EXISTS `users` (
  `user_id` bigint NOT NULL DEFAULT '0',
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL DEFAULT '0',
  `role` enum('admin','user') NOT NULL,
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Membuang data untuk tabel bookstore.users: ~1 rows (lebih kurang)
INSERT INTO `users` (`user_id`, `username`, `password`, `role`) VALUES
	(5, 'Admin', 'adminpass', 'admin');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
