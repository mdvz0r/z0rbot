-- phpMyAdmin SQL Dump
-- version 2.11.8.1deb5+lenny3
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generatie Tijd: 17 Nov 2011 om 17:28
-- Server versie: 5.0.51
-- PHP Versie: 5.2.6-1+lenny8

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Database: `z0rbot4`
--

-- --------------------------------------------------------

--
-- Tabel structuur voor tabel `channel`
--

CREATE TABLE IF NOT EXISTS `channel` (
  `id` int(5) NOT NULL auto_increment,
  `network_id` int(2) NOT NULL,
  `name` varchar(25) NOT NULL default '',
  `clanname` varchar(30) NOT NULL,
  `password` varchar(20) NOT NULL,
  `game` varchar(20) NOT NULL default 'none',
  `submitmsg` varchar(3) NOT NULL default 'off',
  `displaymsg` varchar(3) NOT NULL default 'on',
  `submitpcw` varchar(3) NOT NULL default 'on',
  `displaypcw` varchar(3) NOT NULL default 'on',
  `submitcw` varchar(3) NOT NULL default 'on',
  `displaycw` varchar(3) NOT NULL default 'on',
  `submitringer` varchar(3) NOT NULL default 'on',
  `displayringer` varchar(3) NOT NULL default 'on',
  `submitrecruit` varchar(3) NOT NULL default 'on',
  `displayrecruit` varchar(3) NOT NULL default 'on',
  `quakenet` varchar(3) NOT NULL default 'on',
  `freenode` varchar(3) NOT NULL default 'on',
  `gamesurge` varchar(3) NOT NULL default 'on',
  `enterthegame` varchar(3) NOT NULL default 'on',
  `skill` int(11) NOT NULL default '1000',
  `active` varchar(3) NOT NULL default 'on',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `network_id` (`network_id`,`name`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1436 ;

-- --------------------------------------------------------

--
-- Tabel structuur voor tabel `cwresult`
--

CREATE TABLE IF NOT EXISTS `cwresult` (
  `id` int(20) NOT NULL auto_increment,
  `date` date NOT NULL,
  `team1_channel_id` int(11) NOT NULL,
  `team1_score` int(11) NOT NULL,
  `team2_channel_id` int(11) NOT NULL,
  `team2_score` int(11) NOT NULL,
  `status` varchar(3) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `date` (`date`,`team1_channel_id`,`team2_channel_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=93 ;

-- --------------------------------------------------------

--
-- Tabel structuur voor tabel `gameplayer`
--

CREATE TABLE IF NOT EXISTS `gameplayer` (
  `id` int(20) NOT NULL auto_increment,
  `gameserver_id` int(9) NOT NULL,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=755735 ;

-- --------------------------------------------------------

--
-- Tabel structuur voor tabel `gameserver`
--

CREATE TABLE IF NOT EXISTS `gameserver` (
  `id` int(20) NOT NULL auto_increment,
  `protocol_name` varchar(20) NOT NULL,
  `protocol_version` varchar(20) NOT NULL,
  `address` varchar(15) NOT NULL,
  `port` int(10) NOT NULL,
  `name` varchar(200) NOT NULL,
  `game` varchar(30) NOT NULL,
  `manual` int(20) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `address` (`address`,`port`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=355674 ;

-- --------------------------------------------------------

--
-- Tabel structuur voor tabel `log`
--

CREATE TABLE IF NOT EXISTS `log` (
  `id` int(12) NOT NULL auto_increment,
  `timestamp` varchar(20) NOT NULL,
  `type` varchar(20) NOT NULL,
  `channel_id` int(5) NOT NULL,
  `user_id` int(10) NOT NULL,
  `nick` varchar(30) NOT NULL,
  `content` varchar(255) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=221057 ;

-- --------------------------------------------------------

--
-- Tabel structuur voor tabel `network`
--

CREATE TABLE IF NOT EXISTS `network` (
  `id` int(2) NOT NULL auto_increment,
  `name` varchar(30) NOT NULL,
  `perform` varchar(250) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=5 ;

-- --------------------------------------------------------

--
-- Tabel structuur voor tabel `protocolmasterserver`
--

CREATE TABLE IF NOT EXISTS `protocolmasterserver` (
  `id` int(20) NOT NULL auto_increment,
  `protocol_name` varchar(20) NOT NULL,
  `protocol_version` varchar(20) NOT NULL,
  `address` varchar(100) NOT NULL,
  `port` int(10) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `address` (`address`,`port`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=11 ;

-- --------------------------------------------------------

--
-- Tabel structuur voor tabel `protocol_game`
--

CREATE TABLE IF NOT EXISTS `protocol_game` (
  `id` int(5) NOT NULL auto_increment,
  `protocol_name` varchar(30) NOT NULL,
  `protocol_version` varchar(30) NOT NULL,
  `gamedir` varchar(30) NOT NULL,
  `game` varchar(30) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=3 ;

-- --------------------------------------------------------

--
-- Tabel structuur voor tabel `server`
--

CREATE TABLE IF NOT EXISTS `server` (
  `id` int(2) NOT NULL auto_increment,
  `network_id` int(2) NOT NULL,
  `host` varchar(30) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `network` (`network_id`,`host`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=43 ;

-- --------------------------------------------------------

--
-- Tabel structuur voor tabel `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `id` int(10) NOT NULL auto_increment,
  `network_id` int(3) NOT NULL,
  `name` varchar(20) NOT NULL,
  `active` varchar(3) NOT NULL default 'on',
  `country` varchar(3) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `network_id` (`network_id`,`name`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=36285 ;

-- --------------------------------------------------------

--
-- Tabel structuur voor tabel `user_channel`
--

CREATE TABLE IF NOT EXISTS `user_channel` (
  `user_id` int(10) NOT NULL,
  `channel_id` int(11) NOT NULL,
  `operator` tinyint(1) NOT NULL,
  `friend` tinyint(1) NOT NULL default '0',
  `function` int(11) NOT NULL default '0',
  `gamename` varchar(20) default NULL,
  PRIMARY KEY  (`user_id`,`channel_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
