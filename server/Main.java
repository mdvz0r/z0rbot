/**
 * <p>Title: z0rbot IRC server</p>
 * <p>Description: z0rbot</p>
 * <p>Copyright: Copyright (c) 2004 - 2005</p>
 * <p>Company: z0r.nl</p>
 * @author mdvz0r
 * @version 2.2
 */


import java.util.*;

public class Main {
  protected MysqlControl mysqlControl;
  protected GameControl gameControl;
  protected BotControl botControl;

  public static void main(String[] args) {
    System.setProperty("networkaddress.cache.ttl", "600");
    java.security.Security.setProperty("networkaddress.cache.ttl" , "0");
    try {
      // start de Main
      new Main();
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }

  }

  public Main() {
    try {
      // start mysql control
System.out.println("Starting mysqlControl");
      mysqlControl = new MysqlControl();
System.out.println("Starting gameControl");
      // start game control
      gameControl = new GameControl(this);
System.out.println("Starting botControl");
      // start bot control
      botControl = new BotControl(this, 9999);
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }

  }

  public ArrayList getMysqlBotListByIp(String ip) {
    return mysqlControl.getMysqlBotListByIp(ip);
  }

  public ArrayList getMysqlChannelListByNameNumber(String name, String number) {
    return mysqlControl.getMysqlChannelListByNameNumber(name, number);
  }
/*
  public String[] getMysqlGameByChannelNameNumber(String channel, String name, String number) {
    return mysqlControl.getMysqlGameByChannelNameNumber(channel, name, number);
  }
*/
  public ArrayList getMysqlStatsListByGameGamedirNameYearWeek(String game, String gamedir, String name, String year, String week) {
    return mysqlControl.getMysqlStatsListByGameGamedirNameYearWeek(game, gamedir, name, year, week);
  }

  // get all servers per game and return them
  public ArrayList getMysqlServerListByGame(String game) {
    return mysqlControl.getMysqlServerListByGame(game);
  }

  // get all games and return them
  public ArrayList getMysqlGameList() {
    return mysqlControl.getMysqlGameList();
  }

  // set the online or offline status of the server
  public void setMysqlServerStatusByIpPort(String ip, String port, String status, String oip) {
    mysqlControl.setMysqlServerStatusByIpPort(ip, port, status, oip);
  }

  // update server hostname
  public void setMysqlServerHostnameByIpPort(String ip, String port, String hostname) {
    mysqlControl.setMysqlServerHostnameByIpPort(ip, port, hostname);
  }

  // update server gamedir
  public void setMysqlServerGamedirByIpPort(String ip, String port, String gamedir) {
    mysqlControl.setMysqlServerGamedirByIpPort(ip, port, gamedir);
  }


  // add servermessage to the database
  public void addMysqlMessage(String ip, String port, String type) {
    mysqlControl.addMysqlMessage(ip, port, type);
  }

  // create current game in db
  public void addCurrentGame(int year, int week, String gamedir, String game) {
    mysqlControl.addCurrentGame(year, week, gamedir, game);
  }

  // get current created tables
  public ArrayList startCurrentGames() {
    return mysqlControl.startCurrentGames();
  }

  // get player information by nick, year, week, gamedir and game
  public String[] getMysqlPlayerByNickYearWeekGamedirGame(String nick, int year, int week, String gamedir, String game) {
    return mysqlControl.getMysqlPlayerByNickYearWeekGamedirGame(nick, year, week, gamedir, game);
  }

  // update player stats
  public void updateMysqlPlayerStats(int year, int week, String gamedir, String frags,
                                   String map, String ip, String port,
                                   long secspld, int totfrags, int lastfrags,
                                   double fpm, int ping, int factor, long time, int aantal, int id, String game, String playerip, String playerq2cl) {
  mysqlControl.updateMysqlPlayerStats(year, week, gamedir, frags,
                                               map, ip, port, secspld, totfrags,
                                               lastfrags, fpm, ping, factor, time,
                                               aantal, id, game, playerip, playerq2cl);
  }

  // update player stats on reset
  public void updateMysqlPlayerStatsOnReset(int year, int week, String gamedir,
                                            String frags,
                                            String map, String ip, String port,
                                            long time, int ping, int factor,
                                            int aantal, int id,
                                            String game, String playerip, String playerq2cl) {
    mysqlControl.updateMysqlPlayerStatsOnReset(year, week, gamedir, frags, map,
                                               ip, port, time, ping, factor,
                                               aantal, id, game, playerip, playerq2cl);
  }

  // insert a new player
  public void insertMysqlPlayerStats(int year, int week, String gamedir,
                                     String nick,
                                     String clan, String frags, String map,
                                     String ip,
                                     String port, long time, int ping_time,
                                     int factor, String game, String playerip, String playerq2cl) {
    mysqlControl.insertMysqlPlayerStats(year, week, gamedir, nick, clan,
                                       frags, map, ip, port, time,
                                       ping_time, factor,
                                       game, playerip, playerq2cl);
  }

  //
  public ArrayList getGameControlPlayer(String game, String gamedir,
                                        String search) {
    return gameControl.getGameControlPlayer(game, gamedir, search);
  }

  // set serverload
  public void serverload(int dag, int maand, int jaar, int uur, int gem) {
    mysqlControl.serverload(dag, maand, jaar, uur, gem);
  }

  // avgupdate
  public void avgUpdate(int week, int year, int hour) {
    mysqlControl.avgUpdate(week, year, hour);
  }

  public boolean isAdmin(String login, String host) {
    return mysqlControl.isAdmin(login, host);
  }

  public boolean isServerAdmin(String login, String host) {
    return mysqlControl.isServerAdmin(login, host);
  }

  public boolean isBotAdmin(String bot, String login, String host) {
    return mysqlControl.isBotAdmin(bot, login, host);
  }

  public boolean isBanned(String host, String nick) {
    return mysqlControl.isBanned(host, nick);
  }


  public void mysqlJoin(String botName, String botNumber, String channel, String game, String gamedir) {
    mysqlControl.mysqlJoin(botName, botNumber, channel, game, gamedir);
  }

  public void mysqlPart(String botName, String botNumber, String channel) {
    mysqlControl.mysqlPart(botName, botNumber, channel);
  }

  public void setGame(String channel, String game, String botName, String botNumber) {
    mysqlControl.setGame(channel, game, botName, botNumber);
  }

  public void setGamedir(String channel, String gamedir, String botName, String botNumber) {
    mysqlControl.setGamedir(channel, gamedir, botName, botNumber);
  }

  public void setAcceptMessages(String acceptMessage, String botName, String botNumber, String botChannel) {
    mysqlControl.setAcceptMessages(acceptMessage, botName, botNumber, botChannel);
  }

  public void setAlsoOtherNetworks(String acceptMessage, String botName, String botNumber, String botChannel) {
    mysqlControl.setAlsoOtherNetworks(acceptMessage, botName, botNumber, botChannel);
  }

  public void ban(String nick, String host) {
    mysqlControl.ban(nick, host);
  }

  public boolean serverBan(String nick, String ip, String reason) {
    // eerst mysql toevoegen
    if (mysqlControl.serverBan(nick, ip, reason)) {
	// daarna server banlist refreshen
	if (gameControl.banUser(nick, ip, reason)) {
	    return true;
	}
    } 
    return false;
  }

  public boolean serverUnban(String ip) {
    // eerst mysql toevoegen
    if (mysqlControl.serverUnban(ip)) {
	// daarna server banlist refreshen
	if (gameControl.unbanUser(ip)) {
	    return true;
	}
    } 
    return false;
  }

  public boolean addException(String nick, String ip, String mask) {
    // eerst mysql toevoegen
    if (mysqlControl.addException(nick, ip, mask)) {
	// daarna server banlist refreshen
	if (gameControl.addException()) {
	    return true;
	}
    } 
    return false;
  }

  public boolean removeException(String nick) {
    // hier moeten we even ip opvragen van nick
    String[] ipmask = mysqlControl.getIpMaskException(nick);
    if (!ipmask[0].equals("")) {
	// eerst mysql toevoegen
	if (mysqlControl.removeException(nick)) {
	    // daarna server banlist refreshen
	    if (gameControl.removeException(ipmask[0], ipmask[1])) {
		return true;
	    }
	}
    } 
    return false;
  }

  public boolean removeNickReservation(String nick) {
  // eerst mysql verwijderen
  if (mysqlControl.removeNickReservation(nick)) {
    // daarna server banlist refreshen
    if (gameControl.removeNickReservation(nick)) {
	return true;
	}
    } 
    return false;
  }

  public boolean addNickReservation(String nick, String password) {
  // eerst mysql toevoegen
  if (mysqlControl.addNickReservation(nick, password)) {
    // daarna server banlist refreshen
    if (gameControl.addNickReservation(nick, password)) {
	return true;
	}
    } 
    return false;
  }

  public boolean addRequired(String nick, String ip, String mask) {
    // eerst mysql toevoegen
    if (mysqlControl.addRequired(nick, ip, mask)) {
	// daarna server banlist refreshen
	if (gameControl.addRequired()) {
	    return true;
	}
    } 
    return false;
  }

  public boolean removeRequired(String nick) {
    // hier moeten we even ip opvragen van nick
    String[] ipmask = mysqlControl.getIpMaskRequired(nick);
    if (!ipmask[0].equals("")) {
	// eerst mysql toevoegen
	if (mysqlControl.removeRequired(nick)) {
	    // daarna server banlist refreshen
	    if (gameControl.removeRequired(ipmask[0], ipmask[1])) {
		return true;
	    }
	}
    } 
    return false;
  }

  public boolean isInChannel(String channel, String name, String number, String network) {
    return mysqlControl.isInChannel(channel, name, number, network);
  }

  public void checkChannels() {
    botControl.checkChannels();
  }

  public ArrayList getEmptyCWList(String game, String gamedir) {
    return gameControl.getEmptyClanServer(game, gamedir);
  }

  public void updateMsgUsage(String channel, String mode, String botName, String botNumber) {
    mysqlControl.updateMsgUsage(channel, mode, botName, botNumber);
  }

  public void updateCwLimit(String channel, String min, String max, String botName, String botNumber) {
    mysqlControl.updateCwLimit(channel, min, max, botName, botNumber);
  }

  public String getBotnameFromChannel(String channel) {
    return this.mysqlControl.getBotnameFromChannel(channel);
  }

  public void updateBotStatus(int status, String name, String number) {
    this.mysqlControl.updateBotStatus(status, name, number);
  }

  public String[] getMysqlServerListByIp(String ip , String port) {
    return mysqlControl.getMysqlServerListByIp(ip, port);
  }

  public String[] getMysqlServerListByShortname(String shortname) {
    return mysqlControl.getMysqlServerListByShortname(shortname);
  }

  public void setServerBooking(String ip, String port, String lrcon, long beginTime, int time, String nick, String hostname, String channel) {
    mysqlControl.setServerBooking(ip, port, lrcon, beginTime, time, nick, hostname, channel);
  }

  public ArrayList getMysqlBookedServerList() {
    return mysqlControl.getMysqlBookedServerList();
  }

  public void sendServerCommand(String cmd, String ip, String port) {
    this.gameControl.sendServerCommand(cmd, ip, port);
  }

  public String rconCmd(String host, String port, String cmd) {
    return this.gameControl.rconCmd(host, port, cmd);
  }      


}
