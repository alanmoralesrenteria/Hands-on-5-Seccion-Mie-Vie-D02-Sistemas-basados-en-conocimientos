-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 08-12-2020 a las 21:55:16
-- Versión del servidor: 10.4.13-MariaDB
-- Versión de PHP: 7.2.32

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `barnesandnoble`
--

DELIMITER $$
--
-- Procedimientos
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `reserveItem` (IN `idItem` INT, IN `quantity` INT)  NO SQL
BEGIN
SET @currentstock := (SELECT stock FROM catalogitems WHERE catalogitems.partNumber = idItem);

UPDATE catalogitems SET stock=@currentStock-quantity WHERE catalogitems.partNumber=idItem;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `catalogitems`
--

CREATE TABLE `catalogitems` (
  `partNumber` int(11) NOT NULL,
  `description` varchar(255) NOT NULL,
  `price` double NOT NULL,
  `nameCatalogItems` varchar(255) NOT NULL,
  `stock` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Volcado de datos para la tabla `catalogitems`
--

INSERT INTO `catalogitems` (`partNumber`, `description`, `price`, `nameCatalogItems`, `stock`) VALUES
(420, 'Pack_el_señor_de_los_anillos', 1200, 'BarnesAndNobleAgent@192.168.56.1:1099/JADE', 50),
(421, 'Sideways_stories_from_wayside_school', 399.99, 'BarnesAndNobleAgent@192.168.56.1:1099/JADE', 50),
(422, 'Hello_Universe', 29.99, 'BarnesAndNobleAgent@192.168.56.1:1099/JADE', 50),
(423, 'The_Book_Wanderer', 70, 'BarnesAndNobleAgent@192.168.56.1:1099/JADE', 50),
(424, 'Isolation', 100, 'BarnesAndNobleAgent@192.168.56.1:1099/JADE', 50);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `catalogitems`
--
ALTER TABLE `catalogitems`
  ADD PRIMARY KEY (`partNumber`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
