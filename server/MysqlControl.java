/**
 * <p>Title: z0rbot IRC server</p>
 * <p>Description: z0rbot</p>
 * <p>Copyright: Copyright (c) 2004 - 2005</p>
 * <p>Company: z0r.nl</p>
 * @author mdvz0r
 * @version 2.2
 */


import java.util.*;
import java.lang.*;

public class MysqlControl {

  protected String mysqlIp = "localhost";
  protected String mysqlUser = "stats";
  protected String mysqlPass = "mdvonly";
  protected String mysqlData = "z0rbot";

  protected Z0rbotMysqlHandler z0rbotMysqlHandler;
  protected ArrayList gameMysqlHandlerList = new ArrayList();

  public MysqlControl() {
    try {
      // create an instance of the Main database (mysqlData)
System.out.println("creating connection to the main database");
      z0rbotMysqlHandler = new Z0rbotMysqlHandler("jdbc:mysql://"+ mysqlIp + "/" + mysqlData + "?user=" + mysqlUser + "&password=" + mysqlPass+"&autoReconnect=true&dontTrackOpenResources=true");
System.out.println("connection with main database OK");
      // get an array with all games from the main database
System.out.println("creating game list");
      ArrayList mysqlGameList = z0rbotMysqlHandler.getMysqlGameList();
System.out.println("game list OK");
      // for each game open a new database connection
      for (int mysqlGameListCounter = 0; mysqlGameListCounter < mysqlGameList.size(); mysqlGameListCounter++) {
        System.out.println((String) mysqlGameList.get(mysqlGameListCounter) + " <-- CONN");
        GameMysqlHandler gameMysqlHandler = new GameMysqlHandler( ( (String)mysqlGameList.get(mysqlGameListCounter)), "jdbc:mysql://" + mysqlIp + "/" + ( (String) mysqlGameList.get(mysqlGameListCounter)) + "?user=" + mysqlUser + "&password=" + mysqlPass+"&autoReconnect=true&dontTrackOpenResources=true", false);
        // add the new database connection to an arraylist
        gameMysqlHandlerList.add(gameMysqlHandler);

        // for the ircbot an other connection
	//&dontTrackOpenResources=true
        gameMysqlHandler = new GameMysqlHandler( ( (String)mysqlGameList.get(mysqlGameListCounter)), "jdbc:mysql://" + mysqlIp + "/" + ( (String) mysqlGameList.get(mysqlGameListCounter)) + "?user=" + mysqlUser + "&password=" + mysqlPass+"&autoReconnect=true&dontTrackOpenResources=true", true);
        // add the new database connection to an arraylist
        gameMysqlHandlerList.add(gameMysqlHandler);
      }
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }

  }

  public ArrayList getMysqlBotListByIp(String ip) {

    return z0rbotMysqlHandler.getMysqlBotListByIp(ip);

  }

  public ArrayList getMysqlChannelListByNameNumber(String name, String number) {

    return z0rbotMysqlHandler.getMysqlChannelListByNameNumber(name, number);

  }
/*
  public String[] getMysqlGameByChannelNameNumber(String channel, String name, String number) {
    return z0rbotMysqlHandler.getMysqlGameByChannelNameNumber(channel, name, number);
  }
*/
  public ArrayList getMysqlStatsListByGameGamedirNameYearWeek(String game, String gamedir, String name, String year, String week) {
    GameMysqlHandler gameMysqlHandler = null;
    for (int gameMysqlHandlerListCounter = 0; gameMysqlHandlerListCounter < gameMysqlHandlerList.size(); gameMysqlHandlerListCounter++) {
      gameMysqlHandler = (GameMysqlHandler) gameMysqlHandlerList.get(gameMysqlHandlerListCounter);
      if ( (gameMysqlHandler.getGame()).equals(game) && (gameMysqlHandler.getIrcbot())) {
        return gameMysqlHandler.getMysqlStatsListByGamedirNameYearWeek(gamedir, name, year, week);
      }
    }
    return null;
  }

  // get all servers by game and return them
  public ArrayList getMysqlServerListByGame(String game) {
    return z0rbotMysqlHandler.getMysqlServerListByGame(game);
  }

  // get all games and return them
  public ArrayList getMysqlGameList() {
    return z0rbotMysqlHandler.getMysqlGameList();
  }

  // set the online or offlinestatus of the server
  public void setMysqlServerStatusByIpPort(String ip, String port, String status, String oip) {
    z0rbotMysqlHandler.setMysqlServerStatusByIpPort(ip, port, status, oip);
  }

  // update server hostname
  public void setMysqlServerHostnameByIpPort(String ip, String port, String status) {
    z0rbotMysqlHandler.setMysqlServerHostnameByIpPort(ip, port, status);
  }

  // update server gamedir
  public void setMysqlServerGamedirByIpPort(String ip, String port, String gamedir) {
    z0rbotMysqlHandler.setMysqlServerGamedirByIpPort(ip, port, gamedir);
  }


  // add servermessage to the database
  public void addMysqlMessage(String ip, String port, String type) {
    z0rbotMysqlHandler.addMysqlMessage(ip, port, type);
  }

  // add currentgame
  public void addCurrentGame(int year, int week, String gamedir, String game) {
    GameMysqlHandler gameMysqlHandler = null;
    for (int gameMysqlHandlerListCounter = 0; gameMysqlHandlerListCounter < gameMysqlHandlerList.size(); gameMysqlHandlerListCounter++) {
      gameMysqlHandler = (GameMysqlHandler) gameMysqlHandlerList.get(gameMysqlHandlerListCounter);
      if (gameMysqlHandler.game.equals(game) && (!gameMysqlHandler.getIrcbot())) {
        gameMysqlHandler.addCurrentGame(year, week, gamedir);
        z0rbotMysqlHandler.addCurrentGame(year, week, gamedir, game);
      }
    }
  }

  public ArrayList startCurrentGames() {
    return z0rbotMysqlHandler.startCurrentGames();
  }

  public String[] getMysqlPlayerByNickYearWeekGamedirGame(String nick, int year, int week, String gamedir, String game) {
    GameMysqlHandler gameMysqlHandler = null;
    for (int gameMysqlHandlerListCounter = 0; gameMysqlHandlerListCounter < gameMysqlHandlerList.size(); gameMysqlHandlerListCounter++) {
      gameMysqlHandler = (GameMysqlHandler) gameMysqlHandlerList.get(gameMysqlHandlerListCounter);
      if (gameMysqlHandler.game.equals(game) && (!gameMysqlHandler.getIrcbot())) {
        return gameMysqlHandler.getMysqlPlayerByNickYearWeekGamedirGame(nick, year, week, gamedir);
      }
    }
    return null;
  }

  public void updateMysqlPlayerStats(int year, int week, String gamedir, String frags,
                                     String map, String ip, String port,
                                     long secspld, int totfrags, int lastfrags,
                                     double fpm, int ping, int factor, long time, int aantal, int id, String game, String playerip, String playerq2cl) {

    GameMysqlHandler gameMysqlHandler = null;
    for (int gameMysqlHandlerListCounter = 0; gameMysqlHandlerListCounter < gameMysqlHandlerList.size(); gameMysqlHandlerListCounter++) {
      gameMysqlHandler = (GameMysqlHandler) gameMysqlHandlerList.get(gameMysqlHandlerListCounter);
      if (gameMysqlHandler.game.equals(game) && (!gameMysqlHandler.getIrcbot())) {
        gameMysqlHandler.updateMysqlPlayerStats(year, week, gamedir, frags,
                                               map, ip, port, secspld, totfrags,
                                               lastfrags, fpm, ping, factor, time,
                                               aantal, id, playerip, playerq2cl);
      }
    }
  }

  public void updateMysqlPlayerStatsOnReset(int year, int week, String gamedir,
                                            String frags,
                                            String map, String ip, String port,
                                            long time, int ping, int factor,
                                            int aantal, int id,
                                            String game, String playerip, String playerq2cl) {
    GameMysqlHandler gameMysqlHandler = null;
    for (int gameMysqlHandlerListCounter = 0; gameMysqlHandlerListCounter < gameMysqlHandlerList.size(); gameMysqlHandlerListCounter++) {
      gameMysqlHandler = (GameMysqlHandler) gameMysqlHandlerList.get(gameMysqlHandlerListCounter);
      if (gameMysqlHandler.game.equals(game) && (!gameMysqlHandler.getIrcbot())) {
        gameMysqlHandler.updateMysqlPlayerStatsOnReset(year, week, gamedir, frags, map,
                                                      ip, port, time, ping, factor,
                                                      aantal, id, playerip, playerq2cl);
      }
    }
  }

  public void insertMysqlPlayerStats(int year, int week, String gamedir,
                                     String nick,
                                     String clan, String frags, String map,
                                     String ip,
                                     String port, long time, int ping_time,
                                     int factor, String game, String playerip, String playerq2cl) {
    GameMysqlHandler gameMysqlHandler = null;
    for (int gameMysqlHandlerListCounter = 0; gameMysqlHandlerListCounter < gameMysqlHandlerList.size(); gameMysqlHandlerListCounter++) {
      gameMysqlHandler = (GameMysqlHandler) gameMysqlHandlerList.get(gameMysqlHandlerListCounter);
      if (gameMysqlHandler.game.equals(game) && (!gameMysqlHandler.getIrcbot())) {
        gameMysqlHandler.insertMysqlPlayerStats(year, week, gamedir, nick, clan,
                                        frags, map, ip, port, time,
                                        ping_time, factor, playerip, playerq2cl);

      }
    }
  }

  public void serverload(int dag, int maand, int jaar, int uur, int gem) {
    z0rbotMysqlHandler.serverload(dag, maand, jaar, uur, gem);
  }

  public void avgUpdate(int week, int year, int hour) {
    ArrayList list = (ArrayList) z0rbotMysqlHandler.getMysqlGameGamedirFromStats();
    for (int x=0; x < list.size(); x++) {
      GameMysqlHandler gameMysqlHandler = null;
      for (int gameMysqlHandlerListCounter = 0; gameMysqlHandlerListCounter < gameMysqlHandlerList.size(); gameMysqlHandlerListCounter++) {
        gameMysqlHandler = (GameMysqlHandler) gameMysqlHandlerList.get(gameMysqlHandlerListCounter);
        if (gameMysqlHandler.game.equals( ((String[]) list.get(x))[0] ) && (!gameMysqlHandler.getIrcbot())) {
          System.out.println( gameMysqlHandler.game + " " + ((String[]) list.get(x))[1] );
          gameMysqlHandler.avgUpdate(((String[]) list.get(x))[1], year, week, hour);
        }
      }
    }
  }

  public boolean isAdmin(String login, String host) {
    return z0rbotMysqlHandler.isAdmin(login, host);
  }

  public boolean isServerAdmin(String login, String host) {
    return z0rbotMysqlHandler.isServerAdmin(login, host);
  }

  public boolean isBotAdmin(String bot, String login, String host) {
    return z0rbotMysqlHandler.isBotAdmin(bot, login, host);
  }

  public boolean isBanned(String host, String nick) {
    return z0rbotMysqlHandler.isBanned(host, nick);
  }

  public void mysqlJoin(String botName, String botNumber, String channel, String game, String gamedir) {
    z0rbotMysqlHandler.mysqlJoin(botName, botNumber, channel, game, gamedir);
  }

  public void mysqlPart(String botName, String botNumber, String channel) {
    z0rbotMysqlHandler.mysqlPart(botName, botNumber, channel);
  }

  public void setGame(String channel, String game, String botName, String botNumber) {
    z0rbotMysqlHandler.setGame(channel, game, botName, botNumber);
  }

  public void setGamedir(String channel, String gamedir, String botName, String botNumber) {
    z0rbotMysqlHandler.setGamedir(channel, gamedir, botName, botNumber);
  }

  public void setAcceptMessages(String acceptMessage, String botName, String botNumber, String botChannel) {
    z0rbotMysqlHandler.setAcceptMessages(acceptMessage, botName, botNumber, botChannel);
  }

  public void setAlsoOtherNetworks(String acceptMessage, String botName, String botNumber, String botChannel) {
    z0rbotMysqlHandler.setAlsoOtherNetworks(acceptMessage, botName, botNumber, botChannel);
  }

  public void ban(String nick, String host) {
    z0rbotMysqlHandler.ban(nick, host);
  }

  public boolean serverBan(String nick, String ip, String reason) {
    return z0rbotMysqlHandler.serverBan(nick, ip, reason);
  }

  public boolean serverUnban(String ip) {
    return z0rbotMysqlHandler.serverUnban(ip);
  }

  public boolean addException(String nick, String ip, String mask) {
    return z0rbotMysqlHandler.addException(nick, ip, mask);
  }

  public boolean removeException(String nick) {
    return z0rbotMysqlHandler.removeException(nick);
  }

  public boolean removeNickReservation(String nick) {
    return z0rbotMysqlHandler.removeNickReservation(nick);
  }

  public boolean addNickReservation(String nick, String password) {
    return z0rbotMysqlHandler.addNickReservation(nick, password);
  }

  public boolean addRequired(String nick, String ip, String mask) {
    return z0rbotMysqlHandler.addRequired(nick, ip, mask);
  }

  public boolean removeRequired(String nick) {
    return z0rbotMysqlHandler.removeRequired(nick);
  }

  public boolean isInChannel(String channel, String name, String number, String network) {
    return z0rbotMysqlHandler.isInChannel(channel, name, number, network);
  }

  public void updateMsgUsage(String channel, String mode, String botName, String botNumber) {
    z0rbotMysqlHandler.updateMsgUsage(channel, mode, botName, botNumber);
  }

  public void updateCwLimit(String channel, String min, String max, String botName, String botNumber) {
    z0rbotMysqlHandler.updateCwLimit(channel, min, max, botName, botNumber);
  }

  public String getBotnameFromChannel(String channel) {
    return this.z0rbotMysqlHandler.getBotnameFromChannel(channel);
  }

  public void updateBotStatus(int status, String name, String number) {
    this.z0rbotMysqlHandler.updateBotStatus(status, name, number);
  }

  // get all servers by game and return them
  public String[] getMysqlServerListByIp(String ip, String port) {
    return z0rbotMysqlHandler.getMysqlServerListByIp(ip, port );
  }

  // get all servers by game and return them
  public String[] getMysqlServerListByShortname(String shortname) {
    return z0rbotMysqlHandler.getMysqlServerListByShortname(shortname);
  }

  public void setServerBooking(String ip, String port, String lrcon, long beginTime, int time, String nick, String hostname, String channel) {
    z0rbotMysqlHandler.setServerBooking(ip, port, lrcon, beginTime, time, nick, hostname, channel);
  }

  public ArrayList getMysqlBookedServerList() {
    return z0rbotMysqlHandler.getMysqlBookedServerList();
  }

  public String[] getIpMaskException(String name) {
    return z0rbotMysqlHandler.getIpMaskException(name);
  }

  public String[] getIpMaskRequired(String name) {
    return z0rbotMysqlHandler.getIpMaskRequired(name);
  }

}
