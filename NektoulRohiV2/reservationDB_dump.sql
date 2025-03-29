-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: reservationDB
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation` (
  `ID_Reservation` varchar(20) NOT NULL,
  `date_Heure_Reservation` datetime NOT NULL,
  `Nom_Employe` varchar(255) NOT NULL,
  `Duree_Reservation` int NOT NULL,
  `Code_Salle` char(3) NOT NULL,
  PRIMARY KEY (`ID_Reservation`),
  KEY `idx_reservation_salle` (`Code_Salle`),
  CONSTRAINT `reservation_ibfk_1` FOREIGN KEY (`Code_Salle`) REFERENCES `salle` (`Code_Salle`) ON DELETE CASCADE,
  CONSTRAINT `reservation_chk_1` CHECK ((`Duree_Reservation` > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation`
--

LOCK TABLES `reservation` WRITE;
/*!40000 ALTER TABLE `reservation` DISABLE KEYS */;
INSERT INTO `reservation` VALUES ('RES-1743178499053','2025-03-28 12:22:00','Moualek Mehdi Racim',60,'A26'),('RES-1743180235313','2025-03-28 12:11:00','Besbes ines',60,'A99'),('RES-1743180701708','2025-03-28 12:11:00','Nacher Zahra',123,'A12'),('RES-1743195993347','2025-03-28 12:30:00','Meziane sarah',180,'M34'),('RES-1743201670213','2025-03-28 12:30:00','Bennaoum Aymen',60,'A67'),('RES-1743201830279','2025-03-28 12:30:00','Ouhassine Louai',90,'A27');
/*!40000 ALTER TABLE `reservation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `salle`
--

DROP TABLE IF EXISTS `salle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `salle` (
  `Code_Salle` char(3) NOT NULL,
  PRIMARY KEY (`Code_Salle`),
  CONSTRAINT `salle_chk_1` CHECK (((char_length(`Code_Salle`) = 3) and regexp_like(left(`Code_Salle`,1),_utf8mb4'^[A-Z]$') and regexp_like(right(`Code_Salle`,2),_utf8mb4'^[0-9]{2}$')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `salle`
--

LOCK TABLES `salle` WRITE;
/*!40000 ALTER TABLE `salle` DISABLE KEYS */;
INSERT INTO `salle` VALUES ('A12'),('A22'),('A23'),('A24'),('A26'),('A27'),('A28'),('A31'),('A32'),('A47'),('A67'),('A89'),('A99'),('B13'),('M34');
/*!40000 ALTER TABLE `salle` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-03-29 16:10:52
