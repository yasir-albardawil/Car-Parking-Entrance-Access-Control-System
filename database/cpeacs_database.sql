-- phpMyAdmin SQL Dump
-- version 4.6.5.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 09, 2017 at 06:23 AM
-- Server version: 10.1.21-MariaDB
-- PHP Version: 7.1.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `cpeacs_database`
--

-- --------------------------------------------------------

--
-- Table structure for table `car_data`
--

CREATE TABLE `car_data` (
  `ID` int(20) NOT NULL,
  `idNo` varchar(20) NOT NULL,
  `OwnerName` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `PlateNumber` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `CarBrand` varchar(20) NOT NULL,
  `CarModel` varchar(20) NOT NULL,
  `VIN` varchar(30) NOT NULL,
  `ManufacturingYear` varchar(20) NOT NULL,
  `Color` varchar(20) NOT NULL,
  `ExpiryDate` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

--
-- Dumping data for table `car_data`
--

INSERT INTO `car_data` (`ID`, `idNo`, `OwnerName`, `PlateNumber`, `CarBrand`, `CarModel`, `VIN`, `ManufacturingYear`, `Color`, `ExpiryDate`) VALUES
(30, '1074112374', 'AHmed', 'XXXAAAA', 'Honda', 'Civic', '11111111111111111', '2010', 'Red', '02/05/2017');

-- --------------------------------------------------------

--
-- Table structure for table `login`
--

CREATE TABLE `login` (
  `ID` int(20) NOT NULL,
  `Username` varchar(20) NOT NULL,
  `Password` text NOT NULL,
  `Role` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `login`
--

INSERT INTO `login` (`ID`, `Username`, `Password`, `Role`) VALUES
(35, '1', 'OSXkBLNun7w=', 'Admin'),
(77, '2', '2Bsy6vgwRQE=', 'User');

-- --------------------------------------------------------

--
-- Table structure for table `settings`
--

CREATE TABLE `settings` (
  `ID` int(20) NOT NULL,
  `IPAddress` text NOT NULL,
  `Username` varchar(100) NOT NULL,
  `Password` varchar(100) NOT NULL,
  `COMPort` varchar(10) NOT NULL,
  `Theme` varchar(20) NOT NULL,
  `Mode` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `settings`
--

INSERT INTO `settings` (`ID`, `IPAddress`, `Username`, `Password`, `COMPort`, `Theme`, `Mode`) VALUES
(1, '192.168.1.20/video/mjpg.cgi', 'admin', 'yasir125', 'COM3', 'bootstrap3', 'Manual ');

-- --------------------------------------------------------

--
-- Table structure for table `vehicle_login_information`
--

CREATE TABLE `vehicle_login_information` (
  `ID` int(50) NOT NULL,
  `PlateNumber` varchar(10) NOT NULL,
  `Confidence` float NOT NULL,
  `Status` mediumblob NOT NULL,
  `Date` varchar(20) NOT NULL,
  `Image` mediumblob NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `car_data`
--
ALTER TABLE `car_data`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `login`
--
ALTER TABLE `login`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `settings`
--
ALTER TABLE `settings`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `vehicle_login_information`
--
ALTER TABLE `vehicle_login_information`
  ADD PRIMARY KEY (`ID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `car_data`
--
ALTER TABLE `car_data`
  MODIFY `ID` int(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;
--
-- AUTO_INCREMENT for table `login`
--
ALTER TABLE `login`
  MODIFY `ID` int(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=78;
--
-- AUTO_INCREMENT for table `vehicle_login_information`
--
ALTER TABLE `vehicle_login_information`
  MODIFY `ID` int(50) NOT NULL AUTO_INCREMENT;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
