-- --------------------------------------------------------
-- Host:                         100.115.30.79
-- Versión del servidor:         8.0.40 - MySQL Community Server - GPL
-- SO del servidor:              Win64
-- HeidiSQL Versión:             12.8.0.6908
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Volcando estructura de base de datos para app_fitxar
CREATE DATABASE IF NOT EXISTS `app_fitxar` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `app_fitxar`;

-- Volcando estructura para tabla app_fitxar.fitxatges
CREATE TABLE IF NOT EXISTS `fitxatges` (
  `id_fitxatge` int NOT NULL AUTO_INCREMENT,
  `hora_entrada` time DEFAULT NULL,
  `hora_sortida` time DEFAULT NULL,
  `recompte_hores` time DEFAULT NULL,
  `usuari_id` int unsigned NOT NULL DEFAULT '0',
  `data` date DEFAULT NULL,
  `gps` text,
  PRIMARY KEY (`id_fitxatge`) USING BTREE,
  KEY `usuari_id` (`usuari_id`) USING BTREE,
  CONSTRAINT `fitxatges_ibfk_1` FOREIGN KEY (`usuari_id`) REFERENCES `usuaris` (`id_usuari`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Volcando datos para la tabla app_fitxar.fitxatges: ~2 rows (aproximadamente)
INSERT INTO `fitxatges` (`id_fitxatge`, `hora_entrada`, `hora_sortida`, `recompte_hores`, `usuari_id`, `data`, `gps`) VALUES
	(1, '09:06:29', '10:29:11', '00:34:13', 33, '2025-02-20', '41.63058098,0.89232254'),
	(2, '10:29:19', '05:00:00', NULL, 33, '2025-02-20', '41.63038774,0.8925008'),
	(3, '08:28:15', '08:29:11', '00:00:00', 33, '2025-02-25', '41.63045225,0.89227452'),
	(4, '08:29:21', '08:29:27', '00:00:00', 33, '2025-02-25', '41.63043508,0.89236397'),
	(5, '08:31:30', NULL, NULL, 33, '2025-02-25', 'No GPS'),
	(6, '10:04:21', '10:05:49', '00:01:30', 41, '2025-02-25', '41.63044114,0.89241252'),
	(7, '10:06:04', '10:06:14', '00:00:10', 41, '2025-02-25', '41.63042192,0.89247286');

-- Volcando estructura para tabla app_fitxar.horarios_clase
CREATE TABLE IF NOT EXISTS `horarios_clase` (
  `id_horario` int NOT NULL AUTO_INCREMENT,
  `usuari_id` int unsigned NOT NULL,
  `dia` varchar(10) NOT NULL,
  `hora_inicio` time NOT NULL,
  `hora_fin` time NOT NULL,
  PRIMARY KEY (`id_horario`),
  KEY `usuari_id` (`usuari_id`),
  CONSTRAINT `horarios_clase_ibfk_1` FOREIGN KEY (`usuari_id`) REFERENCES `usuaris` (`id_usuari`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=528 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Volcando datos para la tabla app_fitxar.horarios_clase: ~15 rows (aproximadamente)
INSERT INTO `horarios_clase` (`id_horario`, `usuari_id`, `dia`, `hora_inicio`, `hora_fin`) VALUES
	(399, 34, 'DILLUNS', '07:00:00', '08:10:00'),
	(400, 34, 'DIMARTS', '07:00:00', '08:10:00'),
	(401, 34, 'DILLUNS', '09:55:00', '10:50:00'),
	(402, 34, 'DIJOUS', '09:55:00', '10:50:00'),
	(403, 34, 'DIVENDRES', '09:55:00', '10:50:00'),
	(404, 34, 'DIMARTS', '12:10:00', '13:00:00'),
	(405, 34, 'DIJOUS', '13:00:00', '13:55:00'),
	(406, 34, 'DIMECRES', '16:05:00', '17:00:00'),
	(407, 34, 'DIVENDRES', '17:00:00', '17:55:00'),
	(408, 34, 'DIMARTS', '18:20:00', '19:15:00'),
	(409, 34, 'DIMECRES', '20:10:00', '21:00:00'),
	(410, 34, 'DILLUNS', '21:00:00', '22:00:00'),
	(411, 34, 'DIMARTS', '21:00:00', '22:00:00'),
	(412, 34, 'DIVENDRES', '22:00:00', '23:00:00'),
	(421, 40, 'DILLUNS', '08:10:00', '09:00:00'),
	(422, 40, 'DIJOUS', '09:00:00', '09:55:00'),
	(423, 40, 'DIMECRES', '09:55:00', '10:50:00'),
	(424, 40, 'DILLUNS', '13:00:00', '13:55:00'),
	(425, 40, 'DIJOUS', '13:00:00', '13:55:00'),
	(426, 40, 'DILLUNS', '20:10:00', '21:00:00'),
	(427, 40, 'DIVENDRES', '20:10:00', '21:00:00'),
	(428, 40, 'DIMARTS', '22:00:00', '23:00:00'),
	(429, 37, 'DILLUNS', '07:00:00', '08:10:00'),
	(430, 37, 'DIMARTS', '07:00:00', '08:10:00'),
	(431, 37, 'DILLUNS', '09:55:00', '10:50:00'),
	(432, 37, 'DIJOUS', '09:55:00', '10:50:00'),
	(433, 37, 'DIVENDRES', '09:55:00', '10:50:00'),
	(434, 37, 'DIMARTS', '12:10:00', '13:00:00'),
	(435, 37, 'DIJOUS', '13:00:00', '13:55:00'),
	(436, 37, 'DIMECRES', '16:05:00', '17:00:00'),
	(437, 37, 'DIVENDRES', '17:00:00', '17:55:00'),
	(438, 37, 'DIMARTS', '18:20:00', '19:15:00'),
	(439, 37, 'DIMECRES', '20:10:00', '21:00:00'),
	(440, 37, 'DILLUNS', '21:00:00', '22:00:00'),
	(441, 37, 'DIMARTS', '21:00:00', '22:00:00'),
	(442, 37, 'DIVENDRES', '22:00:00', '23:00:00'),
	(470, 33, 'DILLUNS', '07:00:00', '08:10:00'),
	(471, 33, 'DIMARTS', '07:00:00', '08:10:00'),
	(472, 33, 'DILLUNS', '09:55:00', '10:50:00'),
	(473, 33, 'DIJOUS', '09:55:00', '10:50:00'),
	(474, 33, 'DIVENDRES', '09:55:00', '10:50:00'),
	(475, 33, 'DIMARTS', '12:10:00', '13:00:00'),
	(476, 33, 'DIJOUS', '12:10:00', '13:00:00'),
	(477, 33, 'DIJOUS', '13:00:00', '13:55:00'),
	(478, 33, 'DIMECRES', '16:05:00', '17:00:00'),
	(479, 33, 'DILLUNS', '17:00:00', '17:55:00'),
	(480, 33, 'DIVENDRES', '17:00:00', '17:55:00'),
	(481, 33, 'DIMECRES', '19:15:00', '20:10:00'),
	(482, 33, 'DILLUNS', '21:00:00', '22:00:00'),
	(483, 33, 'DIJOUS', '21:00:00', '22:00:00'),
	(484, 33, 'DIVENDRES', '22:00:00', '23:00:00'),
	(485, 36, 'DILLUNS', '07:00:00', '08:10:00'),
	(486, 36, 'DIMARTS', '07:00:00', '08:10:00'),
	(487, 36, 'DILLUNS', '09:55:00', '10:50:00'),
	(488, 36, 'DIJOUS', '09:55:00', '10:50:00'),
	(489, 36, 'DIVENDRES', '09:55:00', '10:50:00'),
	(490, 36, 'DIMARTS', '13:00:00', '13:55:00'),
	(491, 36, 'DIJOUS', '13:00:00', '13:55:00'),
	(492, 36, 'DIMECRES', '16:05:00', '17:00:00'),
	(493, 36, 'DIVENDRES', '17:00:00', '17:55:00'),
	(494, 36, 'DIMARTS', '18:20:00', '19:15:00'),
	(495, 36, 'DIMECRES', '20:10:00', '21:00:00'),
	(496, 36, 'DILLUNS', '21:00:00', '22:00:00'),
	(497, 36, 'DIMARTS', '21:00:00', '22:00:00'),
	(498, 36, 'DIVENDRES', '22:00:00', '23:00:00'),
	(499, 41, 'DIMARTS', '08:10:00', '09:00:00'),
	(500, 41, 'DIMARTS', '09:00:00', '09:55:00'),
	(501, 41, 'DIMECRES', '09:00:00', '09:55:00'),
	(502, 41, 'DIVENDRES', '09:00:00', '09:55:00'),
	(503, 41, 'DIMARTS', '09:55:00', '10:50:00'),
	(504, 41, 'DIMECRES', '09:55:00', '10:50:00'),
	(505, 41, 'DIJOUS', '09:55:00', '10:50:00'),
	(506, 41, 'DIVENDRES', '09:55:00', '10:50:00'),
	(507, 41, 'DIMARTS', '11:15:00', '12:10:00'),
	(508, 41, 'DIMECRES', '11:15:00', '12:10:00'),
	(509, 41, 'DIJOUS', '11:15:00', '12:10:00'),
	(510, 41, 'DIVENDRES', '11:15:00', '12:10:00'),
	(511, 41, 'DIMARTS', '12:10:00', '13:00:00'),
	(512, 41, 'DIMECRES', '12:10:00', '13:00:00'),
	(513, 41, 'DILLUNS', '15:10:00', '16:05:00'),
	(514, 41, 'DIMARTS', '15:10:00', '16:05:00'),
	(515, 41, 'DILLUNS', '16:05:00', '17:00:00'),
	(516, 41, 'DIMARTS', '16:05:00', '17:00:00'),
	(517, 41, 'DILLUNS', '17:00:00', '17:55:00'),
	(518, 41, 'DILLUNS', '18:20:00', '19:15:00'),
	(519, 41, 'DIMARTS', '18:20:00', '19:15:00'),
	(520, 41, 'DIJOUS', '18:20:00', '19:15:00'),
	(521, 41, 'DILLUNS', '19:15:00', '20:10:00'),
	(522, 41, 'DIMARTS', '19:15:00', '20:10:00'),
	(523, 41, 'DIMECRES', '21:00:00', '22:00:00'),
	(524, 41, 'DIVENDRES', '21:00:00', '22:00:00'),
	(525, 41, 'DIMARTS', '22:00:00', '23:00:00'),
	(526, 41, 'DIMECRES', '22:00:00', '23:00:00'),
	(527, 41, 'DIVENDRES', '22:00:00', '23:00:00');

-- Volcando estructura para tabla app_fitxar.incidencies
CREATE TABLE IF NOT EXISTS `incidencies` (
  `id_incidencia` int NOT NULL AUTO_INCREMENT,
  `descripcio` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `usuari_id` int unsigned NOT NULL DEFAULT '0',
  `id_tipus_incidencia` int DEFAULT NULL,
  `id_horari` int NOT NULL,
  `data_incidencia` date DEFAULT NULL,
  PRIMARY KEY (`id_incidencia`) USING BTREE,
  KEY `usuari_id` (`usuari_id`) USING BTREE,
  KEY `id_tipus_incidencia` (`id_tipus_incidencia`) USING BTREE,
  KEY `fk_id_horari` (`id_horari`),
  CONSTRAINT `fk_id_horari` FOREIGN KEY (`id_horari`) REFERENCES `horarios_clase` (`id_horario`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `incidencies_ibfk_1` FOREIGN KEY (`usuari_id`) REFERENCES `usuaris` (`id_usuari`),
  CONSTRAINT `incidencies_ibfk_2` FOREIGN KEY (`id_tipus_incidencia`) REFERENCES `tipus_incidencia` (`id_tipus_incidencia`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Volcando datos para la tabla app_fitxar.incidencies: ~6 rows (aproximadamente)
INSERT INTO `incidencies` (`id_incidencia`, `descripcio`, `usuari_id`, `id_tipus_incidencia`, `id_horari`, `data_incidencia`) VALUES
	(7, 'febra', 41, 7, 500, '2025-02-25');

-- Volcando estructura para tabla app_fitxar.rols_usuaris
CREATE TABLE IF NOT EXISTS `rols_usuaris` (
  `id_rol` int NOT NULL AUTO_INCREMENT,
  `nom_rol` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id_rol`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Volcando datos para la tabla app_fitxar.rols_usuaris: ~3 rows (aproximadamente)
INSERT INTO `rols_usuaris` (`id_rol`, `nom_rol`) VALUES
	(1, 'ADMIN'),
	(2, 'PROFESSOR');

-- Volcando estructura para tabla app_fitxar.tipus_incidencia
CREATE TABLE IF NOT EXISTS `tipus_incidencia` (
  `id_tipus_incidencia` int NOT NULL AUTO_INCREMENT,
  `nom_tipus_incidencia` varchar(255) NOT NULL,
  PRIMARY KEY (`id_tipus_incidencia`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Volcando datos para la tabla app_fitxar.tipus_incidencia: ~6 rows (aproximadamente)
INSERT INTO `tipus_incidencia` (`id_tipus_incidencia`, `nom_tipus_incidencia`) VALUES
	(1, 'Vacances programades'),
	(2, 'Baixa mèdica'),
	(3, 'Permis maternitat/paternitat'),
	(4, 'Permis enfermetat familiar'),
	(5, 'Excedència laboral'),
	(6, 'Citació judicial/administrativa'),
	(7, 'Indisposició'),
	(8, 'Altres');

-- Volcando estructura para tabla app_fitxar.usuaris
CREATE TABLE IF NOT EXISTS `usuaris` (
  `id_usuari` int unsigned NOT NULL AUTO_INCREMENT,
  `nom` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `cognoms` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `nom_usuari` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `rol` int DEFAULT NULL,
  `id_empresa` int DEFAULT NULL,
  `dni` varchar(9) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `teHorari` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id_usuari`) USING BTREE,
  UNIQUE KEY `users_email_unique` (`email`) USING BTREE,
  UNIQUE KEY `users_nom_usuari_unique` (`nom_usuari`) USING BTREE,
  UNIQUE KEY `dni` (`dni`),
  KEY `fk_rol` (`rol`) USING BTREE,
  KEY `fk_id_empresa` (`id_empresa`) USING BTREE,
  CONSTRAINT `fk_id_empresa` FOREIGN KEY (`id_empresa`) REFERENCES `empreses` (`id_empresa`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_rol` FOREIGN KEY (`rol`) REFERENCES `rols_usuaris` (`id_rol`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla app_fitxar.usuaris: ~6 rows (aproximadamente)
INSERT INTO `usuaris` (`id_usuari`, `nom`, `cognoms`, `email`, `nom_usuari`, `password`, `rol`, `id_empresa`, `dni`, `teHorari`) VALUES
	(33, 'ale', 'musicaria', 'ale@ale.cat', 'ale', '$2y$10$O5FFoxyygc/dqNsvfxL5SuDsUMnSimpFKll8lFOdxuKoC1xRqgCvm', 1, NULL, '12345678Q', 1),
	(34, 'test', 'test', 'test@test.cat', 'test', '$2a$10$MYGNR76ODIC5iBKr0An7x.7aDu0d7ieuQD.Bz7aKeddzYIbqzLNry', 1, NULL, '12345678W', 1),
	(36, 'marc', 'sanchez', 'marc@marc.cat', 'marc', '$2a$10$IrlNt5Xg2JXY3NH7YguAAu0wpiBRDjTjS/s58q5vMUEdDYNoEtZj6', 1, NULL, '12345678E', 1),
	(37, 'Joan', 'Perico', 'joanperico@gmail.com', 'jperico', '$2a$10$5hbWvyDMTbPCp6LEu34tsOgcglhUNoGP/Vq4rUYZ6teZO6KvzJSla', 2, NULL, '45678456P', 1),
	(40, 'sdafasdf', 'asdf', 'asdf@asfd.cat', 'asdf', '$2a$10$kFZHhsNfjlCdN9DlX9KyHus.j2Y0WVpeFwAJ0LKkrytsU8JQQjyl2', 1, NULL, '12345678b', 1),
	(41, 'Carles', 'Roura', 'croura@lasalle.cat', 'croura', '$2y$10$Srbr3ucr6FdiPZ9tVMphCumCacjTCfG3XF/vbLiMTOqj.1eeqeL76', 2, NULL, '12345677K', 1);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
