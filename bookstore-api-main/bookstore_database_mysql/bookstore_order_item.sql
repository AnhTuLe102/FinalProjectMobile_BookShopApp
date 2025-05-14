-- MySQL dump 10.13  Distrib 8.0.29, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: bookstore
-- ------------------------------------------------------
-- Server version	8.0.29

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name_product` varchar(255) DEFAULT NULL,
  `price` double NOT NULL,
  `quantity` int DEFAULT NULL,
  `customer_order_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK7a0l2xeopiensbnhixxdbod07` (`customer_order_id`),
  CONSTRAINT `FK7a0l2xeopiensbnhixxdbod07` FOREIGN KEY (`customer_order_id`) REFERENCES `customer_order` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
INSERT INTO `order_item` VALUES (9,'Khi Mọi Điều Không Như Ý',190000,1,9),(10,'Khi Mọi Điều Không Như Ý',190000,2,10),(11,'Khi Mọi Điều Không Như Ý',190000,1,11),(12,'Định Luật Murphy - Mọi Bí Mật Tâm Lý Thao Túng Cuộc Đời Bạn',89250,1,11),(13,'Về với gia đình',80000,1,12),(14,'Toán học - Khái lược những tư tưởng lớn',360000,2,12),(15,'Về với gia đình',80000,1,13),(16,'Khi Mọi Điều Không Như Ý',190000,2,13),(17,'Thám Tử Lừng Danh Conan - Tập 22',33000,1,14),(18,'Thám Tử Lừng Danh Conan - Tập 21',33000,1,14),(19,'Thám Tử Lừng Danh Conan - Tập 24',33000,1,15),(20,'Thám Tử Lừng Danh Conan - Tập 23',33000,1,15),(21,'Về với gia đình',80000,1,16),(22,'Khi Mọi Điều Không Như Ý',190000,1,16),(23,'Thám Tử Lừng Danh Conan - Tập 24',33000,1,17),(24,'Thám Tử Lừng Danh Conan - Tập 21',33000,1,17);
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-14 18:26:41
