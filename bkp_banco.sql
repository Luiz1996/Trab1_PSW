CREATE DATABASE  IF NOT EXISTS `bibliotec` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */;
USE `bibliotec`;
-- MySQL dump 10.13  Distrib 8.0.12, for Win64 (x86_64)
--
-- Host: localhost    Database: bibliotec
-- ------------------------------------------------------
-- Server version	8.0.12

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `emprestimo`
--

DROP TABLE IF EXISTS `emprestimo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `emprestimo` (
  `codemprestimo` int(11) NOT NULL AUTO_INCREMENT,
  `codusuario` int(11) NOT NULL,
  `codlivro` int(11) NOT NULL,
  `dataemp` date NOT NULL,
  `datadev` date NOT NULL,
  `dataalt` date DEFAULT NULL,
  `ativo` int(11) DEFAULT NULL,
  PRIMARY KEY (`codemprestimo`),
  UNIQUE KEY `codemprestimo_UNIQUE` (`codemprestimo`),
  KEY `emp-usuario_idx` (`codusuario`),
  KEY `emp-livro_idx` (`codlivro`),
  CONSTRAINT `emp-livro` FOREIGN KEY (`codlivro`) REFERENCES `livro` (`codlivro`),
  CONSTRAINT `emp-usuario` FOREIGN KEY (`codusuario`) REFERENCES `usuarios` (`codusuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `emprestimo`
--

LOCK TABLES `emprestimo` WRITE;
/*!40000 ALTER TABLE `emprestimo` DISABLE KEYS */;
/*!40000 ALTER TABLE `emprestimo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `livro`
--

DROP TABLE IF EXISTS `livro`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `livro` (
  `codlivro` int(11) NOT NULL AUTO_INCREMENT,
  `codcatalogacao` varchar(15) NOT NULL,
  `numchamada` varchar(15) NOT NULL,
  `titulo` varchar(60) NOT NULL,
  `autor` varchar(60) NOT NULL,
  `editora` varchar(60) NOT NULL,
  `anolancamento` varchar(4) NOT NULL,
  `cidade` varchar(60) NOT NULL,
  `volume` int(2) NOT NULL,
  `ativo` int(1) NOT NULL,
  `datacad` date NOT NULL,
  `dataalt` date DEFAULT NULL,
  `disponibilidade` int(11) DEFAULT NULL,
  PRIMARY KEY (`codlivro`,`codcatalogacao`,`numchamada`),
  UNIQUE KEY `codlivro_UNIQUE` (`codlivro`),
  UNIQUE KEY `codcatalogacao_UNIQUE` (`codcatalogacao`),
  UNIQUE KEY `numchamada_UNIQUE` (`numchamada`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `livro`
--

LOCK TABLES `livro` WRITE;
/*!40000 ALTER TABLE `livro` DISABLE KEYS */;
INSERT INTO `livro` VALUES (1,'08-09369','978853800365-8','Dom Casmurro','Machado de Assis','Giranda Cultural','2008','São Paulo',1,1,'2019-05-01',NULL,1),(2,'A568m','857175057-2','Macunaíma','Mário de Andrade','Venha Ler','2001','Belo Horizonte',1,1,'2019-05-01',NULL,1),(3,'A353i','859861010-0','Iracema','José de Alencar','Editora Avenida','2005','Jaraguá do Sul',1,1,'2019-05-01',NULL,1),(4,'Y71c','978854310468-3','A Cabana','William P. Young','Sextante','2016','Rio de Janeiro',1,1,'2019-05-01',NULL,1),(5,'L732t','978852541278-2','Triste Fim de Policarpo Quaresma','Lima Barreto','L&PM','2009','Porto Alegre',1,1,'2019-05-01',NULL,1),(6,'M121m','859855906-7','A Moreninha','Joaquim Manoel de Macedo','Clássicos da Literatura','1998','Maringá',1,1,'2019-05-01',NULL,1),(7,'L7531','853250813-8','Laços de Família','Clarice Lispector','Rocco','1998','Rio de Janeiro',1,1,'2019-05-01',NULL,1),(8,'NC','857054033-7','Algoritmos e Estruturas de Dados','Niklaus Wirth','PHB','1989','Rio de Janeiro',1,1,'2019-05-01',NULL,1),(9,'R143m','850100916-4','Memórias do Cárcere','Graciliano Ramos','Record','2005','São Paulo',2,1,'2019-05-01',NULL,1);
/*!40000 ALTER TABLE `livro` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `usuarios` (
  `codusuario` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL,
  `usuario` varchar(30) NOT NULL,
  `senha` varchar(30) NOT NULL,
  `nome` varchar(60) NOT NULL,
  `rg` varchar(10) NOT NULL,
  `cpf` varchar(15) NOT NULL,
  `endereco` varchar(100) NOT NULL,
  `cep` varchar(9) NOT NULL,
  `cidade` varchar(60) NOT NULL,
  `estado` varchar(20) NOT NULL,
  `permissao` int(11) NOT NULL,
  `ativo` int(11) NOT NULL,
  `datacad` date NOT NULL,
  `dataalt` date DEFAULT NULL,
  `datanasc` date NOT NULL,
  PRIMARY KEY (`codusuario`,`email`,`usuario`),
  UNIQUE KEY `codusuario_UNIQUE` (`codusuario`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  UNIQUE KEY `usuario_UNIQUE` (`usuario`),
  UNIQUE KEY `rg_UNIQUE` (`rg`),
  UNIQUE KEY `cpf_UNIQUE` (`cpf`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'2','2','2','Balconista','2','2','2','2','2','2',2,1,'2019-05-01',NULL,'1990-01-01'),(2,'1','1','1','Bibliotecário','1','1','1','1','1','1',1,1,'2019-05-01',NULL,'1990-01-01'),(3,'meu-email@meuemail.com.br','Luiz1996','senha','Luiz Flávio Pereira','1111111111','111111111111111','Rua A','87000000','Sarandi','Paraná',3,1,'2019-05-01',NULL,'1990-01-01');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'bibliotec'
--

--
-- Dumping routines for database 'bibliotec'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-05-01 22:46:18
