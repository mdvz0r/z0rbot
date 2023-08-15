-- phpMyAdmin SQL Dump
-- version 4.6.6deb4+deb9u2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: 09.04.2021 klo 19:16
-- Palvelimen versio: 10.1.48-MariaDB-0+deb9u2
-- PHP Version: 7.0.33-0+deb9u10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `z0rbot`
--

-- --------------------------------------------------------

--
-- Rakenne taululle `admin`
--

CREATE TABLE `admin` (
  `id` int(11) NOT NULL,
  `host` varchar(50) NOT NULL DEFAULT '',
  `nick` varchar(50) NOT NULL DEFAULT '',
  `right` int(1) NOT NULL DEFAULT '0',
  `password` varchar(20) NOT NULL DEFAULT ''
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Rakenne taululle `anp_ip2country`
--

CREATE TABLE `anp_ip2country` (
  `ip_from` bigint(10) NOT NULL DEFAULT '0',
  `ip_to` bigint(10) NOT NULL DEFAULT '0',
  `iso_code` char(2) NOT NULL DEFAULT ''
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Rakenne taululle `bot`
--

CREATE TABLE `bot` (
  `ip` varchar(15) NOT NULL DEFAULT '',
  `name` varchar(15) NOT NULL DEFAULT 'z0rbot',
  `number` char(2) NOT NULL DEFAULT '',
  `owner` varchar(15) NOT NULL DEFAULT '',
  `network` varchar(50) NOT NULL DEFAULT 'irc.quakenet.org',
  `server` varchar(25) NOT NULL DEFAULT 'irc.dk.quakenet.eu.org',
  `port` varchar(5) NOT NULL DEFAULT '6667',
  `qname` varchar(20) NOT NULL DEFAULT '',
  `qpass` varchar(20) NOT NULL DEFAULT '',
  `operator` int(11) NOT NULL DEFAULT '0',
  `status` int(1) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Rakenne taululle `channel`
--

CREATE TABLE `channel` (
  `botName` varchar(15) NOT NULL DEFAULT '',
  `botNumber` char(2) NOT NULL DEFAULT '',
  `name` varchar(25) NOT NULL DEFAULT '',
  `game` varchar(20) NOT NULL DEFAULT 'quake2',
  `gamedir` varchar(30) NOT NULL DEFAULT 'action teamplay',
  `acceptMessages` char(1) NOT NULL DEFAULT '1',
  `alsoOtherNetworks` char(1) NOT NULL DEFAULT '1',
  `whoCanAsk` char(1) NOT NULL DEFAULT '1',
  `minvs` char(1) NOT NULL DEFAULT '0',
  `maxvs` char(1) NOT NULL DEFAULT '9',
  `password` varchar(20) NOT NULL DEFAULT '',
  `checkbot` int(1) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Rakenne taululle `country`
--

CREATE TABLE `country` (
  `ISO_Code` char(2) NOT NULL DEFAULT '',
  `Country` varchar(255) NOT NULL DEFAULT '',
  `Region` varchar(255) NOT NULL DEFAULT '',
  `Capital` varchar(255) NOT NULL DEFAULT '',
  `Currency` varchar(255) NOT NULL DEFAULT ''
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Rakenne taululle `message`
--

CREATE TABLE `message` (
  `timestamp` varchar(14) NOT NULL DEFAULT '',
  `ip` varchar(50) NOT NULL DEFAULT '',
  `port` varchar(5) NOT NULL DEFAULT '',
  `status` char(1) NOT NULL DEFAULT ''
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Rakenne taululle `operator`
--

CREATE TABLE `operator` (
  `id` int(11) NOT NULL,
  `nick` varchar(25) NOT NULL DEFAULT '',
  `firstname` varchar(250) NOT NULL DEFAULT '',
  `host` varchar(55) NOT NULL DEFAULT '',
  `ident` varchar(50) NOT NULL DEFAULT '',
  `email` varchar(250) NOT NULL DEFAULT '',
  `function` varchar(250) NOT NULL DEFAULT '',
  `country` char(2) NOT NULL DEFAULT 'NL',
  `birthyear` int(11) NOT NULL DEFAULT '0',
  `foto` varchar(250) NOT NULL DEFAULT '',
  `games` varchar(250) NOT NULL DEFAULT '',
  `password` varchar(15) NOT NULL DEFAULT '',
  `power` int(1) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Rakenne taululle `request`
--

CREATE TABLE `request` (
  `id` int(11) NOT NULL,
  `channel` varchar(50) NOT NULL DEFAULT '',
  `network` varchar(50) NOT NULL DEFAULT '',
  `game` varchar(50) NOT NULL DEFAULT '',
  `gamedir` varchar(50) NOT NULL DEFAULT '',
  `qauth` varchar(50) NOT NULL DEFAULT '',
  `email` varchar(50) NOT NULL DEFAULT '',
  `www` varchar(255) NOT NULL DEFAULT '',
  `timestamp` int(11) NOT NULL DEFAULT '0',
  `deleted` int(1) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Rakenne taululle `server`
--

CREATE TABLE `server` (
  `name` varchar(255) NOT NULL DEFAULT '',
  `ip` varchar(50) NOT NULL DEFAULT '',
  `oip` varchar(15) NOT NULL DEFAULT '',
  `port` varchar(5) NOT NULL DEFAULT '27910',
  `alias` varchar(5) NOT NULL,
  `serveradmin` varchar(30) NOT NULL DEFAULT '',
  `game` varchar(255) NOT NULL DEFAULT 'quake2',
  `gamedir` varchar(255) NOT NULL DEFAULT 'action teamplay',
  `status` char(1) NOT NULL DEFAULT '0',
  `country` char(2) NOT NULL DEFAULT '',
  `rcon` varchar(28) NOT NULL DEFAULT '',
  `lrcon` varchar(16) NOT NULL DEFAULT '',
  `begintime` int(13) NOT NULL DEFAULT '0',
  `time` int(4) NOT NULL DEFAULT '0',
  `shortname` varchar(8) NOT NULL DEFAULT '',
  `book_channel` varchar(26) NOT NULL DEFAULT '',
  `book_nick` varchar(26) NOT NULL DEFAULT '',
  `book_hostname` varchar(200) NOT NULL DEFAULT '',
  `onstrt` text NOT NULL,
  `onstop` text NOT NULL,
  `q2admin` bigint(20) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Rakenne taululle `serverban`
--

CREATE TABLE `serverban` (
  `ip` bigint(20) NOT NULL DEFAULT '0',
  `mask` int(11) NOT NULL DEFAULT '0',
  `name` varchar(50) NOT NULL DEFAULT '',
  `reason` text NOT NULL,
  `timestamp` bigint(14) NOT NULL DEFAULT '0',
  `days` int(11) NOT NULL DEFAULT '0',
  `banner` varchar(50) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Rakenne taululle `serverexception`
--

CREATE TABLE `serverexception` (
  `ip` bigint(20) NOT NULL DEFAULT '0',
  `dyn` varchar(200) NOT NULL DEFAULT '',
  `mask` int(11) NOT NULL DEFAULT '0',
  `name` varchar(255) NOT NULL DEFAULT '',
  `timestamp` int(14) NOT NULL DEFAULT '0',
  `reason` text NOT NULL,
  `banner` varchar(50) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Rakenne taululle `serverload`
--

CREATE TABLE `serverload` (
  `dag` int(11) NOT NULL DEFAULT '0',
  `maand` int(11) NOT NULL DEFAULT '0',
  `jaar` int(11) NOT NULL DEFAULT '0',
  `uur` int(11) NOT NULL DEFAULT '0',
  `spelers` int(11) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Rakenne taululle `servernickreservation`
--

CREATE TABLE `servernickreservation` (
  `nick` varchar(255) NOT NULL DEFAULT '',
  `password` varchar(255) NOT NULL DEFAULT '',
  `timestamp` int(14) NOT NULL DEFAULT '0',
  `addedbyip` bigint(20) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Rakenne taululle `serverrequired`
--

CREATE TABLE `serverrequired` (
  `ip` bigint(20) NOT NULL DEFAULT '0',
  `mask` int(11) NOT NULL DEFAULT '0',
  `name` varchar(255) NOT NULL DEFAULT '',
  `timestamp` int(14) NOT NULL DEFAULT '0',
  `reason` text NOT NULL,
  `banner` varchar(50) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Rakenne taululle `servertagreservation`
--

CREATE TABLE `servertagreservation` (
  `tag` text NOT NULL,
  `password` varchar(255) NOT NULL DEFAULT '',
  `timestamp` int(14) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Rakenne taululle `stats`
--

CREATE TABLE `stats` (
  `week` int(11) NOT NULL DEFAULT '0',
  `year` int(11) NOT NULL DEFAULT '0',
  `game` varchar(50) NOT NULL DEFAULT '',
  `gamedir` varchar(50) NOT NULL DEFAULT ''
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Rakenne taululle `todo`
--

CREATE TABLE `todo` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL DEFAULT '',
  `text` longtext NOT NULL,
  `timestamp` int(14) NOT NULL DEFAULT '0',
  `finished` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `anp_ip2country`
--
ALTER TABLE `anp_ip2country`
  ADD PRIMARY KEY (`ip_from`,`ip_to`),
  ADD UNIQUE KEY `k_ipfrom` (`ip_from`,`ip_to`);

--
-- Indexes for table `bot`
--
ALTER TABLE `bot`
  ADD PRIMARY KEY (`name`,`number`);

--
-- Indexes for table `channel`
--
ALTER TABLE `channel`
  ADD PRIMARY KEY (`botName`,`botNumber`,`name`);

--
-- Indexes for table `country`
--
ALTER TABLE `country`
  ADD PRIMARY KEY (`ISO_Code`);

--
-- Indexes for table `operator`
--
ALTER TABLE `operator`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `request`
--
ALTER TABLE `request`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `server`
--
ALTER TABLE `server`
  ADD PRIMARY KEY (`ip`,`port`);

--
-- Indexes for table `serverban`
--
ALTER TABLE `serverban`
  ADD PRIMARY KEY (`ip`);

--
-- Indexes for table `servernickreservation`
--
ALTER TABLE `servernickreservation`
  ADD PRIMARY KEY (`nick`);

--
-- Indexes for table `serverrequired`
--
ALTER TABLE `serverrequired`
  ADD UNIQUE KEY `ip` (`ip`);

--
-- Indexes for table `stats`
--
ALTER TABLE `stats`
  ADD PRIMARY KEY (`week`,`year`,`game`,`gamedir`);

--
-- Indexes for table `todo`
--
ALTER TABLE `todo`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admin`
--
ALTER TABLE `admin`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=545;
--
-- AUTO_INCREMENT for table `operator`
--
ALTER TABLE `operator`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=60;
--
-- AUTO_INCREMENT for table `request`
--
ALTER TABLE `request`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8009;
--
-- AUTO_INCREMENT for table `todo`
--
ALTER TABLE `todo`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
