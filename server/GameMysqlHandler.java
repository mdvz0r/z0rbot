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
import java.text.*;

public class GameMysqlHandler
    extends MysqlHandler {

  protected String game;
  private boolean ircbot;
  private List q2ClientList = Collections.synchronizedList(new ArrayList());

  public GameMysqlHandler(String game, String address, boolean ircbot) {
    this.ircbot = ircbot;
    this.game = game;
    this.open(address);
    this.loadQ2ClientList();
  }
  
  private void loadQ2ClientList() {
    try {
	PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM q2client;");
        ResultSet resultSet = preparedStatement.executeQuery();
	while (resultSet.next()) {
    	    String[] q2client = new String[2];
	    q2client[0] = "" + resultSet.getInt("id");
    	    q2client[1] = resultSet.getString("name");
	    q2ClientList.add(q2client);
        }
	resultSet.close();
        preparedStatement.close();
    }
     catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
  }
  
  public int addQ2Client(String name) {
    String sql = "INSERT IGNORE INTO q2client (name) VALUES (?);";
    try {
	PreparedStatement preparedStatement = connection.prepareStatement(sql);
	preparedStatement.setString(1, name);
        int id = preparedStatement.executeUpdate();
        preparedStatement.close();
	String[] q2client = new String[2];
        q2client[0] = "" + id;
        q2client[1] = name;
        q2ClientList.add(q2client);
	return id;
    }
    catch (Exception e) {
	ExceptionHandler exceptionHandler = new ExceptionHandler(e);
	return 0;
    }
  }

  public int getQ2Client(String name) {
    int ret = 0;
    try {
	PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM q2client WHERE name = ?;");
	preparedStatement.setString(1, name);
        ResultSet resultSet = preparedStatement.executeQuery();
	if (resultSet.next()) {
	    ret = resultSet.getInt("id");
        }
	resultSet.close();
        preparedStatement.close();
    }
     catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
    return ret;
  }

  public int ClToId(String name) {
    try {
	if (name.equals("")) return 0;
	for (int x = 0; x < q2ClientList.size(); x++) {
	    String[] q2client = (String[]) q2ClientList.get(x);
	    if (q2client[1].equals(name)) return (Integer.parseInt(q2client[0]));
	}
	this.addQ2Client(name);
	int id = this.getQ2Client(name);
	System.out.println("NEW ID: " + id);
	return id;
    }
    catch (Exception e) {
	return 0;
    }
  }

  public boolean getIrcbot() {
    return this.ircbot;
  }

  public String getGame() {
    return game;
  }

  public ArrayList getMysqlStatsListByGamedirNameYearWeek(String gamedir, String name, String year, String week) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening getMysqlStatsListByGamedirNameYearWeek");

      PreparedStatement preparedStatement = connection.prepareStatement(
          "SELECT fpm FROM `average_" + year + "_" + week + "` WHERE gamedir = ?");

      preparedStatement.setString(1, gamedir);

      ResultSet resultSet = preparedStatement.executeQuery();
      double averageFpm = 1;
      if (resultSet.next()) {
        averageFpm = resultSet.getDouble("fpm") / 100;
      }
      ArrayList mysqlStatsList = new ArrayList();
      String sql = "SELECT nick, totfrags, fpm, secspld, middeler FROM `" +
          year + "_" +
          week + "_" + gamedir + "` WHERE secspld > 300 AND nick LIKE ? ORDER BY secspld DESC";
      preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1,"%" + name + "%");
      resultSet = preparedStatement.executeQuery();
      NumberFormat statsFormat = new DecimalFormat("0.00");
      while (resultSet.next()) {
        String[] mysqlStatsInfo = new String[5];
        mysqlStatsInfo[0] = resultSet.getString("nick");
        mysqlStatsInfo[1] = resultSet.getString("totfrags");
        mysqlStatsInfo[2] = String.valueOf( (resultSet.getInt("secspld") / 60));
        mysqlStatsInfo[3] = statsFormat.format( (resultSet.getDouble("middeler") /
                                                 1000) *
                                               (resultSet.getDouble("fpm") /
                                                100)) + " (" +
            statsFormat.format(resultSet.getDouble("fpm") / 100) + ")";
        double fpm = (resultSet.getDouble("fpm") / 100) *
            (resultSet.getDouble("middeler") / 1000);
        double minutes = resultSet.getDouble("secspld") / 60;
        double minutesMinimum = 100;
        mysqlStatsInfo[4] = statsFormat.format( (minutes /
                                                 (minutes + minutesMinimum)) *
                                               fpm +
                                               (minutesMinimum /
                                                (minutes + minutesMinimum)) *
                                               averageFpm);
        mysqlStatsList.add(mysqlStatsInfo);
      }
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing getMysqlStatsListByGamedirNameYearWeek");
      resultSet.close();
      preparedStatement.close();

      return mysqlStatsList;
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
      return new ArrayList();
    }

  }

  // create tables for current game
  public void addCurrentGame(int year, int week, String gamedir) {
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening addCurrentGame");
      String sql = "CREATE TABLE `" + year + "_" + week + "_" + gamedir + "` (`id` int( 11 ) NOT NULL AUTO_INCREMENT ,`nick` varchar( 35 ) NOT NULL default '',`clan` varchar( 8 ) NOT NULL default '',`frags` int( 11 ) NOT NULL default '0',`lastmap` varchar( 15 ) NOT NULL default '',`timestats` int( 11 ) NOT NULL default '0',`totfrags` int( 11 ) NOT NULL default '0',`secspld` int( 11 ) NOT NULL default '0',`lastserver` varchar( 100 ) NOT NULL default '',`lastfrags` int( 11 ) NOT NULL default '0',`fpm` int( 11 ) NOT NULL default '0',`ping` int( 11 ) NOT NULL default '0',`aantal` int( 11 ) NOT NULL default '0',`middeler` int( 11 ) NOT NULL default '0',PRIMARY KEY ( `id` ) ,KEY `id` ( `id` ) );";
      Statement stmt = connection.createStatement();
      stmt.executeUpdate(sql);
      sql = "CREATE TABLE `clans_" + year + "_" + week + "_" + gamedir + "` (`clan` varchar(20) NOT NULL default '',  `minutes` int(11) NOT NULL default '0',  `fpmx` int(11) NOT NULL default '0',  `fpm` int(11) NOT NULL default '0',  `wr` int(11) NOT NULL default '0',  `players` int(11) NOT NULL default '0');";
      stmt = connection.createStatement();
      stmt.executeUpdate(sql);
      sql = "CREATE TABLE IF NOT EXISTS `average_" + year + "_" + week + "` (`fpm` int( 11 ) NOT NULL default '0', `gamedir` varchar( 50 ) NOT NULL default '');";
      stmt = connection.createStatement();
      stmt.executeUpdate(sql);
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing addCurrentGame");
      stmt.close();
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
  }

  public String[] getMysqlPlayerByNickYearWeekGamedirGame(String nick, int year,
      int week, String gamedir) {
    String sql = "SELECT * FROM `" + year + "_" + week + "_" + gamedir +
        "` WHERE nick = ? ORDER BY id";

    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening getMysqlPlayerByNickYearWeekGamedirGame");
      // Execute the query
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, nick);

      preparedStatement.setEscapeProcessing(true);
      ResultSet rs = preparedStatement.executeQuery();

      if (rs.next()) {
        String[] mysqlPlayer = new String[10];
        mysqlPlayer[0] = rs.getString("timestats");
        mysqlPlayer[1] = rs.getString("secspld");
        mysqlPlayer[2] = rs.getString("aantal");
        mysqlPlayer[3] = rs.getString("ping");
        mysqlPlayer[4] = rs.getString("middeler");
        mysqlPlayer[5] = rs.getString("lastserver");
        mysqlPlayer[6] = rs.getString("lastmap");
        mysqlPlayer[7] = rs.getString("frags");
        mysqlPlayer[8] = rs.getString("totfrags");
        mysqlPlayer[9] = rs.getString("id");
	
	//addition 29-05-2006
        exceptionHandler = new ExceptionHandler("Closing getMysqlPlayerByNickYearWeekGamedirGame");
	rs.close();
        preparedStatement.close();

        return mysqlPlayer;
      } else {
        //addition 29-05-2006
	exceptionHandler = new ExceptionHandler("Closing getMysqlPlayerByNickYearWeekGamedirGame");
        rs.close();
	preparedStatement.close();
      }
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
    return null;
  }

  public void updateCl (int year, int week, int q2id, int id, long ip) {
	  if (q2id != 0 && id != 0) {
		  String sql = "INSERT IGNORE INTO q2client_user (year, week, q2id, id, ip) VALUES (?,?,?,?,?);";
		  try {
			  PreparedStatement preparedStatement = connection.prepareStatement(sql);
			  preparedStatement.setInt(1, year);
			  preparedStatement.setInt(2, week);
			  preparedStatement.setInt(3, q2id);
			  preparedStatement.setInt(4, id);
			  preparedStatement.setLong(5, ip);
			  preparedStatement.executeUpdate();
			  preparedStatement.close();
		  }
		  catch (Exception e) {
			  ExceptionHandler exceptionHandler = new ExceptionHandler(e);
		  }
	  }
  }
  
  public void updateIp (int year, int week, long ip, int id) {
	  if (ip != 0 && id != 0) {
		  String sql = "INSERT IGNORE INTO ip (year, week, ip, id) VALUES (?,?,?,?);";
		  try {
			  PreparedStatement preparedStatement = connection.prepareStatement(sql);
			  preparedStatement.setInt(1, year);
			  preparedStatement.setInt(2, week);
			  preparedStatement.setLong(3, ip);
			  preparedStatement.setInt(4, id);
			  preparedStatement.executeUpdate();
			  preparedStatement.close();
		  }
		  catch (Exception e) {
			  ExceptionHandler exceptionHandler = new ExceptionHandler(e);
		  }
	  }
  }

  public void updateMysqlPlayerStats(int year, int week, String gamedir,
                                     String frags,
                                     String map, String ip, String port,
                                     long secspld, int totfrags, int lastfrags,
                                     double fpm, int ping, int factor,
                                     long time, int aantal, int id, String playerip, String playerq2cl) {
    String sql = "UPDATE `" + year + "_" + week + "_" + gamedir +
        "` SET frags = " + frags +
        ",lastmap='" + map + "',lastserver='" + ip + ":" + port +
        "', timestats=" + time + ", secspld=" +
        secspld +
        ", totfrags=" + totfrags + ", lastfrags= " + lastfrags +
        ", fpm= " + fpm +
        ", ping= " + ping + ", middeler= " + factor + ", aantal= " +
        aantal +
        " WHERE id = ? ";

    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening updateMysqlPlayerStats");
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setInt(1, id);
      preparedStatement.executeUpdate();
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing updateMysqlPlayerStats");
      preparedStatement.close();
      if (gamedir.equals("action teamplay")) {
	long ipx = ipToLong(playerip);
	updateIp(year, week, ipx , id);
	updateCl(year, week, ClToId(playerq2cl), id, ipx);
      }
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
  }

  public void updateMysqlPlayerStatsOnReset(int year, int week, String gamedir,
                                            String frags,
                                            String map, String ip, String port,
                                            long time, int ping, int factor,
                                            int aantal, int id, String playerip, String playerq2cl) {
    String sql = "UPDATE `" + year + "_" + week + "_" + gamedir +
        "` SET frags = " + frags +
        ",lastmap='" + map + "',lastserver='" + ip + ":" + port +
        "', timestats=" + time + ", lastfrags=0" +
        ", ping= " + ping + ", middeler= " + factor +
        ", aantal= " + aantal +
        " WHERE id = ? ";

    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening updateMysqlPlayerStatsOnReset");
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setInt(1, id);
      preparedStatement.executeUpdate();
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing updateMysqlPlayerStatsOnReset");
      preparedStatement.close();
      if (gamedir.equals("action teamplay")) {
	long ipx = ipToLong(playerip);
	updateIp(year, week, ipx, id);
	updateCl(year, week, ClToId(playerq2cl), id, ipx);
      }
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
     
    }
  }

  public void insertMysqlPlayerStats(int year, int week, String gamedir,
                                     String nick,
                                     String clan, String frags, String map,
                                     String ip,
                                     String port, long time, int ping_time,
                                     int factor, String playerip, String playerq2cl) {
    String sql = "INSERT INTO `" + year + "_" + week + "_" + gamedir + "` (nick, clan, frags, lastmap, lastserver, timestats, totfrags,secspld, fpm, ping, middeler, aantal) VALUES ( ? , ? ," +
        frags + ",'" + map + "','" + ip + ":" + port + "'," +
        time + ",0,0,0," +
        ping_time + "," + factor + ",1)";

    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening insertMysqlPlayerStats");
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, nick);
      preparedStatement.setString(2, clan);
      int id = preparedStatement.executeUpdate();
      //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing insertMysqlPlayerStats");
      preparedStatement.close();
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
  }

  public void avgUpdate(String gamedir, int year, int week, int hour) {
    String sql = "SELECT * FROM `" + year + "_" + week + "_" + gamedir +
        "` ORDER BY id";
    // Execute the query
    try {
      ExceptionHandler exceptionHandler = new ExceptionHandler("Opening avgUpdate");
      PreparedStatement preparedStatement = connection.prepareStatement(sql);

      preparedStatement.setEscapeProcessing(true);
      ResultSet rs = preparedStatement.executeQuery();

      int counter = 0;
      int fpm = 0;

      while (rs.next()) {
        counter++;
        fpm = fpm + rs.getInt("fpm");
      }
      if ( (fpm != 0) && (counter != 0)) {
        sql = "SELECT * FROM `average_" + year + "_" + week +
            "` WHERE gamedir = '" + gamedir + "'";
        // Execute the query
        preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setEscapeProcessing(true);
        rs = preparedStatement.executeQuery();
        fpm = fpm / counter;

        if (rs.next()) {
          sql = "UPDATE `average_" + year + "_" + week + "` SET fpm = " + fpm +
              " WHERE gamedir = '" + gamedir + "'";
        }
        else {
          sql =
              "INSERT INTO `average_" + year + "_" + week +
              "` (fpm, gamedir) VALUES (" +
              fpm + ",'" + gamedir + "')";
        }
//	    System.out.println(sql);
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.executeUpdate();
      }

      // update clan stats

      if (hour == 3) {
        System.out.println("Updating clan stats!");

        // zoek welke clan tags er allemaal in het systeem staan voor deze week en dit jaar
        sql = "SELECT DISTINCT clan FROM `" + year + "_" + week + "_" + gamedir +
            "` WHERE clan <> '';";

        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setEscapeProcessing(true);
        rs = preparedStatement.executeQuery();

        while (rs.next()) {
//        System.out.println(rs.getString("clan"));
          float C = 0;
          sql = "SELECT * FROM `average_" + year + "_" + week +
              "` WHERE gamedir = ?";

          preparedStatement = connection.prepareStatement(sql);
          preparedStatement.setString(1, gamedir);

          ResultSet rs2 = preparedStatement.executeQuery();

          if (rs2.next()) {
            C = rs2.getFloat("fpm") / 100;
          }

          sql =
              "SELECT * FROM `" + year + "_" + week + "_" + gamedir +
              "` WHERE secspld <> 0 AND middeler <> 0 AND clan = ?";
          preparedStatement = connection.prepareStatement(sql);
          preparedStatement.setString(1, rs.getString("clan"));
          rs2 = preparedStatement.executeQuery();

          int minutes = 0;
          int frags = 0;
          fpm = 0;
          int wr = 0;
          int plr = 0;
          float middeler = 0;
          int aantal = 0;
          while (rs2.next()) {
//          System.out.println("   " + rs2.getString("nick") + "\t" + (rs2.getInt("secspld") / 60) + "\t" + (rs2.getInt("fpm")*(rs2.getInt("secspld") / 60)));
            minutes += rs2.getInt("secspld") / 60;
            middeler += rs2.getInt("middeler") * rs2.getInt("aantal");
            aantal += rs2.getInt("aantal");
            frags += rs2.getInt("fpm") * (rs2.getInt("secspld") / 60);
            plr++;
          }
          if (middeler != 0 && aantal != 0) middeler /= aantal;
          if (frags != 0 && minutes != 0) fpm = (frags / minutes * 1000);
          float fpmx = fpm * (middeler / 1000);
          if (fpm != 0) fpm /= 1000;
          if (fpmx != 0) fpmx /= 1000;

          float v = minutes;
          float m = 100;
          float R = fpmx;
//        System.out.println(v + " " + m + " " + R + " " + C);
          if (v != 0 && m != 0 && R != 0 && C != 0) {
            float avg = ( (v / (v + m)) * R + (m / (v + m)) * C);
            wr = (int) (avg);
          }

          sql = "SELECT * FROM `clans_" + year + "_" + week + "_" + gamedir +
              "` WHERE clan = ?;";

          preparedStatement = connection.prepareStatement(sql);
          preparedStatement.setString(1, rs.getString("clan"));
          preparedStatement.setEscapeProcessing(true);
          rs2 = preparedStatement.executeQuery();

          if (rs2.next()) {
            sql = "UPDATE `clans_" + year + "_" + week + "_" + gamedir +
                "` SET minutes = ?, fpmx = ?, fpm = ?, wr = ?, players = ? WHERE clan = ?;";
          }
          else {
            sql = "INSERT INTO `clans_" + year + "_" + week + "_" + gamedir +
                "` (minutes, fpmx, fpm, wr, players, clan) VALUES ( ?, ?, ?, ?, ?, ?)";
          }

          preparedStatement = connection.prepareStatement(sql);
          preparedStatement.setInt(1, minutes);
          preparedStatement.setInt(2, (int) fpmx);
          preparedStatement.setInt(3, fpm);
          preparedStatement.setInt(4, wr);
          preparedStatement.setInt(5, plr);
          preparedStatement.setString(6, rs.getString("clan"));
          preparedStatement.executeUpdate();

          //addition 29-05-2006
          rs2.close();
        }
      } else { System.out.println("Clan stats will NOT be updated, time is: " + hour + " hr."); }

    //addition 29-05-2006
      exceptionHandler = new ExceptionHandler("Closing avgUpdate");
      rs.close();
      preparedStatement.close();
	
    }
    catch (Exception e) {}
  }
}
