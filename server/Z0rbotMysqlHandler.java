/**
 * <p>Title: z0rbot IRC server</p>
 * <p>Description: z0rbot</p>
 * <p>Copyright: Copyright (c) 2004 - 2005</p>
 * <p>Company: z0r.nl</p>
 * @author mdvz0r
 * @version 2.2
 */


import java.sql.*;
import java.util.*;

public class Z0rbotMysqlHandler extends MysqlHandler {

  public Z0rbotMysqlHandler(String address) {
    this.open(address);
  }

  public ArrayList getMysqlBotListByIp(String ip) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening getMysqlBotListByIp");

      ArrayList mysqlBotList = new ArrayList();
      PreparedStatement preparedStatement = connection.prepareStatement("SELECT DISTINCT bot.name, bot.number, bot.owner, bot.server, bot.port, bot.network, bot.qname, bot.qpass FROM bot, channel WHERE bot.ip = ? ORDER BY bot.number");
      preparedStatement.setString(1, ip);
      ResultSet resultSet = preparedStatement.executeQuery();

      while (resultSet.next()) {

        String[] mysqlBotInfo = new String[8];
        mysqlBotInfo[0] = resultSet.getString("bot.name");
        mysqlBotInfo[1] = resultSet.getString("bot.number");
        mysqlBotInfo[2] = resultSet.getString("bot.owner");
        mysqlBotInfo[3] = resultSet.getString("bot.server");
        mysqlBotInfo[4] = resultSet.getString("bot.port");
        mysqlBotInfo[5] = resultSet.getString("bot.network");
        mysqlBotInfo[6] = resultSet.getString("bot.qname");
        mysqlBotInfo[7] = resultSet.getString("bot.qpass");
        mysqlBotList.add(mysqlBotInfo);

      }
      
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing getMysqlBotListByIp");
      resultSet.close();
      preparedStatement.close();

      return mysqlBotList;
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
      return null;
    }
  }

  public ArrayList getMysqlChannelListByNameNumber(String name, String number) {

    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening getMysqlChannelListByNameNumber");

      ArrayList mysqlChannelList = new ArrayList();
      String sql = "SELECT channel.minvs, channel.maxvs, channel.whoCanAsk, channel.name, channel.acceptMessages, channel.alsoOtherNetworks, bot.network, channel.game, channel.gamedir FROM channel, bot WHERE channel.botName = ? AND channel.botNumber = ? AND channel.botName = bot.name AND channel.botNumber = bot.number ORDER BY channel.name;";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);

      preparedStatement.setString(1, name);
      preparedStatement.setString(2, number);

      ResultSet resultSet = preparedStatement.executeQuery();

      while (resultSet.next()) {
        String[] chan = new String[9];
        chan[0] = resultSet.getString("channel.name");
        chan[1] = resultSet.getString("channel.acceptMessages");
        chan[2] = resultSet.getString("bot.network");
        chan[3] = resultSet.getString("channel.alsoOtherNetworks");
        chan[4] = resultSet.getString("channel.game");
        chan[5] = resultSet.getString("channel.gamedir");
        chan[6] = resultSet.getString("channel.whoCanAsk");
        chan[7] = resultSet.getString("channel.minvs");
        chan[8] = resultSet.getString("channel.maxvs");
        mysqlChannelList.add(chan);
      }
      
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing getMysqlChannelListByNameNumber");
      resultSet.close();
      preparedStatement.close();


      return mysqlChannelList;
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
      return null;
    }

  }


  // get all servers by game and put them in an arraylist
  public ArrayList getMysqlServerListByGame(String game) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening getMysqlServerListByGame");
      
      ArrayList mysqlServerList = new ArrayList();
      PreparedStatement preparedStatement = connection.prepareStatement("SELECT DISTINCT server.alias, server.rcon, server.name, server.ip, server.port, server.game, server.status, server.gamedir FROM server WHERE server.game = ?");

      preparedStatement.setString(1, game);
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        String[] mysqlServerInfo = new String[8];
        mysqlServerInfo[0] = resultSet.getString("server.name");
        mysqlServerInfo[1] = resultSet.getString("server.ip");
        mysqlServerInfo[2] = resultSet.getString("server.port");
        mysqlServerInfo[3] = resultSet.getString("server.game");
        mysqlServerInfo[4] = resultSet.getString("server.status");
        mysqlServerInfo[5] = resultSet.getString("server.gamedir");
        mysqlServerInfo[6] = resultSet.getString("server.rcon");
	mysqlServerInfo[7] = resultSet.getString("server.alias");
        mysqlServerList.add(mysqlServerInfo);
      }
      
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing getMysqlServerListByGame");
      resultSet.close();
      preparedStatement.close();

     
      return mysqlServerList;
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
      return null;
    }
  }

  // get all games and put them in an arraylist
  public ArrayList getMysqlGameList() {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening getMysqlGameList");
      ArrayList mysqlGameList = new ArrayList();
      PreparedStatement preparedStatement = connection.prepareStatement("SELECT DISTINCT server.game FROM server");
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        String mysqlGameName = resultSet.getString("server.game");
        mysqlGameList.add(mysqlGameName);
      }
      
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing getMysqlGameList");
      resultSet.close();
      preparedStatement.close();

      return mysqlGameList;
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
      return null;
    }
  }

  // set the online or offlinestatus in the database
  public void setMysqlServerStatusByIpPort(String ip, String port, String status, String oip) {
    if (oip.equals("")) this.setMysqlServerStatusByIpPortNoOip(ip, port, status);
    else {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening setMysqlServerStatusByIpPort");
      PreparedStatement preparedStatement = connection.prepareStatement("UPDATE server SET status = ?, oip = ? WHERE ip = ? AND port = ?");

      preparedStatement.setString(1, status);
      preparedStatement.setString(2, oip);
      preparedStatement.setString(3, ip);
      preparedStatement.setString(4, port);
      preparedStatement.executeUpdate();
      
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing setMysqlServerStatusByIpPort");
      preparedStatement.close();
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
    }
  }

  // set the online or offlinestatus in the database
  public void setMysqlServerStatusByIpPortNoOip(String ip, String port, String status) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening setMysqlServerStatusByIpPortNoOip");
      PreparedStatement preparedStatement = connection.prepareStatement("UPDATE server SET status = ? WHERE ip = ? AND port = ?");

      preparedStatement.setString(1, status);
      preparedStatement.setString(2, ip);
      preparedStatement.setString(3, port);
      preparedStatement.executeUpdate();
      
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing setMysqlServerStatusByIpPortNoOip");
      preparedStatement.close();
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
  }




  // update server hostname
  public void setMysqlServerHostnameByIpPort(String ip, String port, String hostname) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening setMysqlServerHostnameByIpPort");
      
      PreparedStatement preparedStatement = connection.prepareStatement(
          "UPDATE server SET name = ? WHERE ip = ? AND port = ?");
      preparedStatement.setString(1, hostname);
      preparedStatement.setString(2, ip);
      preparedStatement.setString(3, port);
      preparedStatement.executeUpdate();
      
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing setMysqlServerStatusByIpPort");
      preparedStatement.close();

    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
  }

  // update server hostname
  public void setMysqlServerGamedirByIpPort(String ip, String port, String gamedir) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening setMysqlServerGamedirByIpPort");
      
      PreparedStatement preparedStatement = connection.prepareStatement(
          "UPDATE server SET gamedir = ? WHERE ip = ? AND port = ?");
      preparedStatement.setString(1, gamedir);
      preparedStatement.setString(2, ip);
      preparedStatement.setString(3, port);
      preparedStatement.executeUpdate();
      
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing setMysqlServerGamedirByIpPort");
      preparedStatement.close();
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
  }


  // add server message to the database
  public void addMysqlMessage(String ip, String port, String type) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening addMysqlMessage");
      
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO message VALUES(?, ?, ?, ?)");

      preparedStatement.setString(1, "" + (timestamp.getTime() / 1000));
      preparedStatement.setString(2, ip);
      preparedStatement.setString(3, port);
      preparedStatement.setString(4, type);

      preparedStatement.executeUpdate();
      
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing addMysqlMessage");
      preparedStatement.close();
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
  }

  // get current tables in stats
  public ArrayList startCurrentGames() {
    ArrayList currentGames = new ArrayList();
    String sql = "SELECT * FROM stats;";
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening startCurrentGames");
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      ResultSet rs = preparedStatement.executeQuery();
      while (rs.next()) {
        currentGames.add(rs.getInt("year") + "_" + rs.getInt("week") + "_" + rs.getString("gamedir") + "_" + rs.getString("game"));
      }
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing startCurrentGames");
      rs.close();
      preparedStatement.close();
    }
    catch (Exception e) {}
    return currentGames;
  }

  public void addCurrentGame(int year, int week, String gamedir, String game) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening addCurrentGame");
      String sql = "INSERT INTO stats (year, week, game, gamedir) VALUES (" +
          year +
          ", " + week + ", '" + game + "', '" + gamedir + "');";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.executeUpdate();

      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing addCurrentGame");
      preparedStatement.close();
    }

    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
  }
  public void serverload(int dag, int maand, int jaar, int uur, int gem) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening serverload");

      String sql = "INSERT INTO serverload (dag, maand, jaar, uur, spelers) VALUES ( ? , ? , ? , ? , ? )";


      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setInt(1, dag);
      preparedStatement.setInt(2, maand);
      preparedStatement.setInt(3, jaar);
      preparedStatement.setInt(4, uur);
      preparedStatement.setInt(5, gem);
      preparedStatement.executeUpdate();

      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing addCurrentGame");
      preparedStatement.close();
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
  }

  public ArrayList getMysqlGameGamedirFromStats() {
    String sql = "SELECT DISTINCT game, gamedir FROM stats";

    ArrayList list = new ArrayList();

    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening getMysqlGameGamedirFromStats");
      // Execute the query
      PreparedStatement preparedStatement = connection.prepareStatement(sql);

      preparedStatement.setEscapeProcessing(true);
      ResultSet rs = preparedStatement.executeQuery();
      while (rs.next()) {
        String[] st  = new String[2];
        st[0] = rs.getString("game");
        st[1] = rs.getString("gamedir");
        list.add(st);
      }
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing getMysqlGameGamedirFromStats");
      rs.close();
      preparedStatement.close();
    } catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }

    return list;
  }

  public boolean isAdmin(String login, String host) {
    boolean isAdmin = false;
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening isAdmin");

      String sql = "SELECT * FROM operator WHERE host = ? AND ident = ? AND power = 9;";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setEscapeProcessing(true);
      preparedStatement.setString(1, host);
      preparedStatement.setString(2, login);

      ResultSet rs = preparedStatement.executeQuery();
      if (rs.next()) {
        isAdmin = true;
      }
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing isAdmin");
      rs.close();
      preparedStatement.close();
      
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
    return isAdmin;
  }

  public boolean isServerAdmin(String login, String host) {
    boolean isAdmin = false;
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening isServerAdmin");

      String sql = "SELECT * FROM operator WHERE host = ? AND ident = ? AND power >= 8;";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setEscapeProcessing(true);
      preparedStatement.setString(1, host);
      preparedStatement.setString(2, login);

      ResultSet rs = preparedStatement.executeQuery();
      if (rs.next()) {
        isAdmin = true;
      }
      exceptionHandler = new ExceptionHandler("Closing isServerAdmin");
      rs.close();
      preparedStatement.close();
      
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
    return isAdmin;
  }


  public boolean isBotAdmin(String bot, String login, String host) {
    boolean isAdmin = false;
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening isBotAdmin");
    
      String sql = "SELECT operator.power, operator.id FROM operator WHERE operator.host = ? AND operator.ident = ?;";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setEscapeProcessing(true);
      preparedStatement.setString(1, host);
      preparedStatement.setString(2, login);

      ResultSet rs = preparedStatement.executeQuery();
      if (rs.next()) {
	if (rs.getInt("operator.power") == 9) {
	  isAdmin = true;
	} else {
	  sql = "SELECT bot.name, bot.number FROM bot WHERE bot.operator = ?;";
	  preparedStatement = connection.prepareStatement(sql);
          preparedStatement.setEscapeProcessing(true);
	  preparedStatement.setInt(1, rs.getInt("operator.id"));
	  rs = preparedStatement.executeQuery();
	  while (rs.next()) {
	    if ((rs.getString("bot.name") + rs.getString("bot.number")).equals(bot)) {
              isAdmin = true;
	    }
	  }
	}
      }
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing isBotAdmin");
      rs.close();
      preparedStatement.close();
			
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
    return isAdmin;
  }

  public boolean isBanned(String host, String nick) {
    boolean isBanned = false;
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening isBanned");
      String sql = "";
      if (nick.equals("")) {
        sql = "SELECT * FROM admin WHERE (host = ? AND `right` = 0;";
      } else {
        sql = "SELECT * FROM admin WHERE (host = ? OR nick LIKE ?) AND `right` = 0;";
      }
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setEscapeProcessing(true);
      preparedStatement.setString(1, "" + host);
      if (!nick.equals("")) preparedStatement.setString(2,"%" + nick + "%");

      ResultSet rs = preparedStatement.executeQuery();
      if (rs.next()) {
        isBanned = true;
      }
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing isBanned");
      rs.close();
      preparedStatement.close();
			
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
      isBanned = true;
    }
    return isBanned;
  }

  public void mysqlJoin(String botName, String botNumber, String channel, String game, String gamedir) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening mysqlJoin");
      String sql = "INSERT IGNORE INTO channel (botName, botNumber, name, game, gamedir) VALUES ( ? , ? , ? , ? , ? )";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, botName);
      preparedStatement.setString(2, botNumber);
      preparedStatement.setString(3, channel);
      preparedStatement.setString(4, game);
      preparedStatement.setString(5, gamedir);
      preparedStatement.executeUpdate();
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing mysqlJoin");
      preparedStatement.close();
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
  }

  public void mysqlPart(String botName, String botNumber, String channel) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening mysqlPart");
      String sql = "DELETE FROM channel WHERE botName = ? AND botNumber = ? AND name = ?;";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, botName);
      preparedStatement.setString(2, botNumber);
      preparedStatement.setString(3, channel);
      preparedStatement.executeUpdate();
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing mysqlPart");
      preparedStatement.close();
		  
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
  }

  public void setGame(String channel, String game, String botName, String botNumber) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening setGame");
      String sql = "UPDATE channel SET game = ? WHERE name = ? AND botName = ? AND botNumber = ?;";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, game);
      preparedStatement.setString(2, channel);
      preparedStatement.setString(3, botName);
      preparedStatement.setString(4, botNumber);
      preparedStatement.executeUpdate();
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing setGame");
      preparedStatement.close();
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
  }

  public void setGamedir(String channel, String gamedir, String botName, String botNumber) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening setGamedir");
      String sql = "UPDATE channel SET gamedir = ? WHERE name = ? AND botName = ? AND botNumber = ?;";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, gamedir);
      preparedStatement.setString(2, channel);
      preparedStatement.setString(3, botName);
      preparedStatement.setString(4, botNumber);
      preparedStatement.executeUpdate();
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing setGamedir");
      preparedStatement.close();
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
  }

  public void setAcceptMessages(String acceptMessage, String botName, String botNumber, String botChannel) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening setAcceptMessages");
      String sql = "UPDATE channel SET acceptMessages = ? WHERE botName = ? AND botNumber = ? AND name = ?;";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, acceptMessage);
      preparedStatement.setString(2, botName);
      preparedStatement.setString(3, botNumber);
      preparedStatement.setString(4, botChannel);
      preparedStatement.executeUpdate();
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing setAcceptMessages");
      preparedStatement.close();
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
  }

  public void setAlsoOtherNetworks(String acceptMessage, String botName, String botNumber, String botChannel) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening setAlsoOtherNetworks");
      String sql = "UPDATE channel SET alsoOtherNetworks = ? WHERE botName = ? AND botNumber = ? AND name = ?;";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, acceptMessage);
      preparedStatement.setString(2, botName);
      preparedStatement.setString(3, botNumber);
      preparedStatement.setString(4, botChannel);
      preparedStatement.executeUpdate();

      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing setAlsoOtherNetworks");
      preparedStatement.close();
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
  }

  public void ban(String nick, String host) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening ban");
      String sql = "INSERT IGNORE INTO admin (nick, host, `right`) VALUES ( ? , ? , 0)";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, nick);
      preparedStatement.setString(2, host);
      preparedStatement.executeUpdate();

      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing ban");
      preparedStatement.close();

    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
  }

  public boolean serverBan(String nick, String ip, String reason) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening serverBan");
      String sql = "INSERT IGNORE INTO serverban (ip, name, reason, timestamp) VALUES ( ? , ? , ?, ?)";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setLong(1, ipToLong(ip));
      preparedStatement.setString(2, nick);
      preparedStatement.setString(3, reason);
      preparedStatement.setLong(4, ((new Timestamp(System.currentTimeMillis())).getTime() / 1000));
      preparedStatement.executeUpdate();

      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing serverBan");
      preparedStatement.close();
      return true;
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
      return false;
    }
  }

  public boolean serverUnban(String ip) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening serverUnban");
      String sql = "DELETE FROM serverban WHERE ip = ?;";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setLong(1, ipToLong(ip));
      preparedStatement.executeUpdate();

      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing serverUnban");
      preparedStatement.close();
      return true;
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
      return false;
    }
  }

  public boolean addException(String nick, String ip, String mask) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening addException");
      String sql = "INSERT IGNORE INTO serverexception (ip, mask, name) VALUES ( ? , ? , ?)";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setLong(1, ipToLong(ip));
      preparedStatement.setInt(2, Integer.parseInt(mask));
      preparedStatement.setString(3, nick);
      preparedStatement.executeUpdate();

      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing addException");
      preparedStatement.close();
      return true;
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
      return false;
    }
  }

  public boolean removeException(String nick) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening removeException");
      String sql = "DELETE FROM serverexception WHERE name = ?;";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, nick);
      preparedStatement.executeUpdate();

      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing removeException");
      preparedStatement.close();
      return true;
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
      return false;
    }
  }

  public boolean removeNickReservation(String nick) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening removeNickReservation");
      String sql = "DELETE FROM servernickreservation WHERE nick = ?;";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, nick);
      preparedStatement.executeUpdate();

      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing removeNickReservation");
      preparedStatement.close();
      return true;
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
      return false;
    }
  }

  public boolean addNickReservation(String nick, String password) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening addNickReservation");
      String sql = "INSERT IGNORE INTO servernickreservation (nick, password, timestamp) VALUES ( ? , ? , UNIX_TIMESTAMP())";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, nick);
      preparedStatement.setString(2, password);
      preparedStatement.executeUpdate();

      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing addNickReservation");
      preparedStatement.close();
      return true;
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
      return false;
    }
  }


  public boolean addRequired(String nick, String ip, String mask) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening addRequired");
      String sql = "INSERT IGNORE INTO serverrequired (ip, mask, name) VALUES ( ? , ? , ?)";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setLong(1, ipToLong(ip));
      preparedStatement.setInt(2, Integer.parseInt(mask));
      preparedStatement.setString(3, nick);
      preparedStatement.executeUpdate();

      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing addRequired");
      preparedStatement.close();
      return true;
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
      return false;
    }
  }


  public boolean removeRequired(String nick) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening removeRequired");
      String sql = "DELETE FROM serverrequired WHERE name = ?;";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, nick);
      preparedStatement.executeUpdate();

      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing removeRequired");
      preparedStatement.close();
      return true;
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
      return false;
    }
  }

  public String[] getIpMaskException(String nick) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening getIpMaskException");

      String sql = "SELECT * FROM serverexception WHERE name = ?;";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setEscapeProcessing(true);
      preparedStatement.setString(1, nick);

      ResultSet rs = preparedStatement.executeQuery();
      if (rs.next()) {
        String[] st  = new String[2];
        st[0] = longToIp(rs.getLong("ip"));
        st[1] = rs.getString("mask");
	return st;
      }
      exceptionHandler = new ExceptionHandler("Closing getIpMaskException");
      rs.close();
      preparedStatement.close();
      
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
    String[] st  = new String[2];
    st[0] = "";
    st[1] = "";
    return st;
  }


  public String[] getIpMaskRequired(String nick) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening getIpMaskRequired");

      String sql = "SELECT * FROM serverrequired WHERE name = ?;";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setEscapeProcessing(true);
      preparedStatement.setString(1, nick);

      ResultSet rs = preparedStatement.executeQuery();
      if (rs.next()) {
        String[] st  = new String[2];
        st[0] = longToIp(rs.getLong("ip"));
        st[1] = rs.getString("mask");
	return st;
      }
      exceptionHandler = new ExceptionHandler("Closing getIpMaskRequired");
      rs.close();
      preparedStatement.close();
      
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
    String[] st  = new String[2];
    st[0] = "";
    st[1] = "";
    return st;
  }

  public boolean isInChannel(String channel, String name, String number, String network) {
    boolean isInChannel = false;
    String sql = "SELECT * FROM bot, channel WHERE channel.name = ? AND (channel.botName != ? OR channel.botNumber != ?) AND bot.network = ? AND bot.name = channel.botName AND bot.number = channel.botNumber;";
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening isInChannel");
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setEscapeProcessing(true);
      preparedStatement.setString(1, channel);
      preparedStatement.setString(2, name);
      preparedStatement.setString(3, number);
      preparedStatement.setString(4, network);
      ResultSet rs = preparedStatement.executeQuery();
      if (rs.next()) {
        isInChannel = true;
      }
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing isInChannel");
      rs.close();
      preparedStatement.close();


    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }

    return isInChannel;
  }

  public void updateMsgUsage(String channel, String mode, String botName, String botNumber) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening updateMsgUsage");
      String sql = "UPDATE channel SET whoCanAsk = ? WHERE botName = ? AND botNumber = ? AND name = ?;";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, mode);
      preparedStatement.setString(2, botName);
      preparedStatement.setString(3, botNumber);
      preparedStatement.setString(4, channel);
      preparedStatement.executeUpdate();

      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing updateMsgUsage");
      preparedStatement.close();

    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
  }

  public void updateCwLimit(String channel, String min, String max, String botName, String botNumber) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening updateCwLimit");
      String sql = "UPDATE channel SET minvs = ?, maxvs = ? WHERE botName = ? AND botNumber = ? AND name = ?;";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      System.out.println(min + " " + max);
      preparedStatement.setString(1, min);
      preparedStatement.setString(2, max);
      preparedStatement.setString(3, botName);
      preparedStatement.setString(4, botNumber);
      preparedStatement.setString(5, channel);
      preparedStatement.executeUpdate();

      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing setAlsoOtherNetworks");
      preparedStatement.close();

    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
  }

  public String getBotnameFromChannel(String channel) {
    String sql = "SELECT * FROM channel WHERE name = ?;";
    String bot = "";
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening getBotnameFromChannel");
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setEscapeProcessing(true);
      preparedStatement.setString(1, channel);
      ResultSet rs = preparedStatement.executeQuery();
      if (rs.next()) {
        bot = rs.getString("botName") + rs.getString("botNumber");
        System.out.println(bot);
      }
      if (rs.next()) {
        return "%";
      }
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing getBotnameFromChannel");
      rs.close();
      preparedStatement.close();
    } catch (Exception e) { System.out.println(e); }
    return bot;
  }

  public void updateBotStatus(int status, String name, String number) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening updateBotStatus");
      String sql = "UPDATE bot SET status = ? WHERE name = ? AND number = ?;";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setInt(1, status);
      preparedStatement.setString(2, name);
      preparedStatement.setString(3, number);
      preparedStatement.executeUpdate();
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing getBotnameFromChannel");
      preparedStatement.close();
    } catch (Exception e) { System.out.println(e); }
  }

  // get all servers by game and put them in an arraylist
  public String[] getMysqlServerListByIp(String ip, String port) {
    try {
      
      PreparedStatement preparedStatement = connection.prepareStatement("SELECT DISTINCT server.onstrt, server.onstop, server.name, server.rcon, server.begintime, server.time, server.shortname FROM server WHERE server.ip = ? and server.port = ?");

      preparedStatement.setString(1, ip);
      preparedStatement.setString(2, port);
      ResultSet resultSet = preparedStatement.executeQuery();
 
      String[] mysqlServerInfo = new String[7];

      while (resultSet.next()) {

      	mysqlServerInfo[0] = resultSet.getString("server.name");
      	mysqlServerInfo[1] = resultSet.getString("server.rcon");
      	mysqlServerInfo[2] = resultSet.getString("server.begintime");
      	mysqlServerInfo[3] = resultSet.getString("server.time");
      	mysqlServerInfo[4] = resultSet.getString("server.shortname");
      	mysqlServerInfo[5] = resultSet.getString("server.onstrt");
      	mysqlServerInfo[6] = resultSet.getString("server.onstop");
      }

      resultSet.close();
      preparedStatement.close();

      return mysqlServerInfo;
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
      return null;
    }
  }


  // get all servers by game and put them in an arraylist
  public String[] getMysqlServerListByShortname(String shortname) {
    try {
      
      PreparedStatement preparedStatement = connection.prepareStatement("SELECT DISTINCT server.onstrt, server.onstop, server.name, server.ip, server.port, server.rcon, server.lrcon, server.begintime, server.time FROM server WHERE server.shortname = ? and server.status = 1 order by (server.begintime + server.time) asc limit 1");

      preparedStatement.setString(1, shortname);
      ResultSet resultSet = preparedStatement.executeQuery();
      String[] mysqlServerInfo = new String[8];

      if (resultSet.next()) {

      	mysqlServerInfo[0] = resultSet.getString("server.name");
      	mysqlServerInfo[1] = resultSet.getString("server.ip");
      	mysqlServerInfo[2] = resultSet.getString("server.port");
      	mysqlServerInfo[3] = resultSet.getString("server.rcon");
      	mysqlServerInfo[4] = resultSet.getString("server.begintime");
      	mysqlServerInfo[5] = resultSet.getString("server.time");
	mysqlServerInfo[6] = resultSet.getString("server.onstrt");
      	mysqlServerInfo[7] = resultSet.getString("server.onstop");
        resultSet.close();
        preparedStatement.close();
        return mysqlServerInfo;
      } else {
        resultSet.close();
        preparedStatement.close();
        return null;
      }

    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
      return null;
    }
  }

  public void setServerBooking(String ip, String port, String lrcon, long beginTime, int time, String nick, String hostname, String channel) {
    try {

      String sql = "UPDATE server SET server.lrcon = ?, server.begintime = ?, server.time = ?, server.book_nick = ?, server.book_hostname = ?, server.book_channel = ? WHERE server.ip = ? AND server.port = ?;";
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, lrcon);
      preparedStatement.setLong(2, beginTime);
      preparedStatement.setInt(3, time);
      preparedStatement.setString(4, nick);
      preparedStatement.setString(5, hostname);
      preparedStatement.setString(6, channel);
      preparedStatement.setString(7, ip);
      preparedStatement.setString(8, port);
      preparedStatement.executeUpdate();

      preparedStatement.close();
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
  }


  public ArrayList getMysqlBookedServerList() {
    try {
      
      PreparedStatement preparedStatement = connection.prepareStatement("SELECT DISTINCT server.ip, server.port, server.rcon, server.lrcon, server.begintime, server.time, server.book_channel, server.book_nick, server.book_hostname, server.onstrt, server.onstop FROM server WHERE server.time > 0");

      ResultSet resultSet = preparedStatement.executeQuery();
      ArrayList mysqlBookedServerList = new ArrayList();
      
      while (resultSet.next()) {

        String[] mysqlBookedServerInfo = new String[11];
      	mysqlBookedServerInfo[0] = resultSet.getString("server.ip");
      	mysqlBookedServerInfo[1] = resultSet.getString("server.port");
      	mysqlBookedServerInfo[2] = resultSet.getString("server.rcon");
      	mysqlBookedServerInfo[3] = resultSet.getString("server.lrcon");
      	mysqlBookedServerInfo[4] = resultSet.getString("server.begintime");
      	mysqlBookedServerInfo[5] = resultSet.getString("server.time");
      	mysqlBookedServerInfo[6] = resultSet.getString("server.book_channel");
      	mysqlBookedServerInfo[7] = resultSet.getString("server.book_nick");
      	mysqlBookedServerInfo[8] = resultSet.getString("server.book_hostname");
      	mysqlBookedServerInfo[9] = resultSet.getString("server.onstrt");
      	mysqlBookedServerInfo[10] = resultSet.getString("server.onstop");
        mysqlBookedServerList.add(mysqlBookedServerInfo);

      }

      resultSet.close();
      preparedStatement.close();

      return mysqlBookedServerList;
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
      return null;
    }
  }
}

