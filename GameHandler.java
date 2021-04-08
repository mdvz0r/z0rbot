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

public class GameHandler extends Thread {

  protected Quake2ServerHandler quake2ServerHandler = new Quake2ServerHandler();
  protected Quake3ServerHandler quake3ServerHandler = new Quake3ServerHandler();
//  protected Quake3ServerHandler tremulousServerHandler = new Quake3ServerHandler();
  protected GameControl gameControl;
  protected String name;
  protected String ip;
  protected String port;
  protected String status;
  protected String game;
  protected String oldgamedir;
  protected PlayerHandler playerHandler;
  protected String rcon;
  protected String alias;

  public GameHandler(GameControl gameControl, String name, String ip, String port, String game, String status, String oldgamedir, String rcon, String alias) {
    this.gameControl = gameControl;
    this.name = name;
    this.ip = ip;
    this.port = port;
    this.game = game;
    this.status = status;
    this.oldgamedir = oldgamedir;
    this.rcon = rcon;
    this.alias = alias;
  }

  public void run() {
    try {
      // array that will be filled with server information
      ArrayList data = new ArrayList();

      // choose which game should be chosen
      if (game.equals("quake2")) {
        // get information with the quake2 protocol
        data = quake2ServerHandler.getData(this.ip, this.port, this.rcon);
      }
      if (game.equals("quake3")) {
        data = quake3ServerHandler.getData(this.ip, this.port);
      }
//      if (game.equals("tremulous")) {
//        data = tremulousServerHandler.getData(this.ip, this.port);
//      }

      // if data is filled
      if (data.size() > 0) {


        // server information
        String[] serverInfo = (String[]) data.get(0);

        // server settings
        ArrayList serverSettingList = (ArrayList) data.get(1);

        // server players
        ArrayList serverPlayerList = (ArrayList) data.get(2);
        
        // update the online status of this server in the database
        this.updateServerStatus("1", serverInfo[4]);

        // declare all vars
        String map = "", frags = "", ping = "", gamedir = "", matchmode = "",
            ctf = "", hostname = "", deathmatch = "", teamplay = "", scripts = "";

        // for all settings:
        for (int serverSettingListCounter = 0; serverSettingListCounter < serverSettingList.size(); serverSettingListCounter++) {

          // get first key and value in a string
          String[] serverSettingInfo = (String[]) serverSettingList.get(serverSettingListCounter);

          // if...
          if (serverSettingInfo[0].equals("mapname")) {
            map = serverSettingInfo[1];
          }
          if (serverSettingInfo[0].equals("frags")) {
            frags = serverSettingInfo[1];
          }
          if (serverSettingInfo[0].equals("ping_time")) {
            ping = serverSettingInfo[1];
          }
          if (serverSettingInfo[0].equals("matchmode")) {
            matchmode = serverSettingInfo[1];
          }
          if (serverSettingInfo[0].equals("gamedir") || serverSettingInfo[0].equals("gamename")) {
            gamedir = serverSettingInfo[1].toLowerCase();
          }
          if (serverSettingInfo[0].equals("ctf")) {
            ctf = serverSettingInfo[1];
          }
          if (serverSettingInfo[0].equals("teamplay")) {
            teamplay = serverSettingInfo[1];
          }
          if (serverSettingInfo[0].equals("deathmatch")) {
            deathmatch = serverSettingInfo[1];
          }
          if (serverSettingInfo[0].equals("hostname") || serverSettingInfo[0].equals("sv_hostname")) {
            hostname = serverSettingInfo[1];
            // update server hostname
            this.updateServerHostname(this.ip, this.port, hostname);
          }
          if (serverSettingInfo[0].equals("scripts")) {
            scripts = serverSettingInfo[1];
          }
        }
        if (!scripts.equals("1")) {
          // if gamedir isnt empty
          if (! (gamedir.equals(""))) {
            // check if game is ctf
            if (ctf.equals("1")) {
              gamedir = gamedir + " ctf";
            }
            // check if game is teamplay
            if (teamplay.equals("1")) {
              gamedir = gamedir + " teamplay";
            }
            // check if teamplay is set to zero, then it's deathmatch, if teamplay isnt set then deathmatch wont be set as well
            if (teamplay.equals("0")) {
              gamedir = gamedir + " deathmatch";
            }
            // get a factor
            int factor = 1000;
            String cwServer = "0";
            if (matchmode.equals("1") && teamplay.equals("1")) {
              factor = 1250;
              cwServer = "1";
            }

            // if gamedir differs from gamedir in database, change it.
            if (!gamedir.equals(this.oldgamedir)) {
              updateServerGamedir(this.ip, this.port, gamedir);
            }
            int amountOfPlayers = 0;
            // for all players
            ArrayList playerList = new ArrayList();
            for (int serverPlayerListCounter = 0; serverPlayerListCounter < serverPlayerList.size(); serverPlayerListCounter++) {
              String[] serverPlayerInfo = (String[]) serverPlayerList.get(serverPlayerListCounter);
              String playerip = "";
	      String playerq2cl = "";
	      if (serverPlayerInfo.length >= 4) {
	        playerip = serverPlayerInfo[3]; 
		playerq2cl = serverPlayerInfo[4];
	      }
              // if ping isnt zero, record stats, else dont (player is connecting when ping = zero)
              if (! (serverPlayerInfo[1].equals("0"))) {
                if (serverPlayerInfo[1].equals("-1")) serverPlayerInfo[1] = "0";
                // update player
                if (gameControl.getCountedTime() == 30000) {
                  boolean ins = true;
                  for (int x = 0; x < playerList.size(); x++) {
                    if (serverPlayerInfo[2].equals( (String) playerList.get(x))) {
                      ins = false;
                    }
                  }
                  if (ins) {
                    playerList.add(serverPlayerInfo[2]);
                    if (!game.equals("half-life")) {
                      playerHandler = new PlayerHandler(this, ip, port, game,
                          gamedir,
                          map, factor,
                          serverPlayerInfo[2],
                          serverPlayerInfo[0],
                          serverPlayerInfo[1], playerip, playerq2cl);
                    }
                  }
                }
                gameControl.addPlayerToTempList( (String) serverPlayerInfo[2],
                                                name, ip + ":" + port, game,
                                                gamedir);
                gameControl.updatePlayerCounter();
                amountOfPlayers++;
              }
            }
            gameControl.addServerToTempList(this.ip, this.port, this.name,
                                            "" + amountOfPlayers, cwServer,
                                            this.game, gamedir, this.rcon, this.alias);
          }
        }

      }
      else {
        // update the offline status of this server in the database
        this.updateServerStatus("0", "");
      }
      if (game.equals("quake2")) {
        gameControl.numberOfGamesQuake2(2);
      }
      else if (game.equals("quake3")) {
        gameControl.numberOfGamesQuake3(2);
      }
      else {
        System.out.println("THIS IS WHERE IT GOES WRONG");
      }
//      else if (game.equals("tremulous")) {
//        gameControl.numberOfGamesTremulous(2);
//      }
    }
    catch (Exception e) {
      if (game.equals("quake2")) {
        gameControl.numberOfGamesQuake2(2);
      }
      else if (game.equals("quake3")) {
        gameControl.numberOfGamesQuake3(2);
      }
      else {
        System.out.println("OR THIS IS WHERE IT GOES WRONG");
      }
//      else if (game.equals("tremulous")) {
//        gameControl.numberOfGamesTremulous(2);
//      }
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
      System.out.println(e);
    }
  }

  // update the online or offline status in database
  public void updateServerStatus(String online, String oip) {
    // if current status is offline and server is online then:
    if (this.status.equals("0") && online.equals("1")) {
      gameControl.setMysqlServerStatusByIpPort(ip, port, "1", oip);
      gameControl.addMysqlMessage(ip, port, "1");
    }
    // if current status is maybe offline and server is offline then:
    if (this.status.equals("2") && online.equals("0")) {
      gameControl.setMysqlServerStatusByIpPort(ip, port, "0", oip);
      gameControl.addMysqlMessage(ip, port, "0");
    }
    // if current status is online and server is offline then:
    if (this.status.equals("1") && online.equals("0")) {
      gameControl.setMysqlServerStatusByIpPort(ip, port, "2", oip);
    }
    // if current status is maybe offline and the server is online then:
    if (this.status.equals("2") && online.equals("1")) {
      gameControl.setMysqlServerStatusByIpPort(ip, port, "1", oip);
    }

  }

  // update server hostname
  public void updateServerHostname(String ip, String port, String hostname) {
    // if server hostname is not equal to server hostname in database
    if (!(this.name.equals(hostname))) {
      // then update it
      gameControl.setMysqlServerHostnameByIpPort(ip, port, hostname);
      gameControl.addMysqlMessage(ip, port, "2");
    }
  }

  // update server hostname
  public void updateServerGamedir(String ip, String port, String gamedir) {
    gameControl.setMysqlServerGamedirByIpPort(ip, port, gamedir);
    gameControl.addMysqlMessage(ip, port, "3");
  }


  // check if current game already exists
  public boolean existCurrentGame(String game) {
    return gameControl.existCurrentGame(game);
  }

  // create tables for current game
  public void addCurrentGame(int year, int week, String gamedir, String game) {
    gameControl.addCurrentGame(year, week, gamedir, game);
  }

  public String[] getMysqlPlayerByNickYearWeekGamedirGame(String nick, int year, int week, String gamedir, String game) {
    return gameControl.getMysqlPlayerByNickYearWeekGamedirGame(nick, year, week, gamedir, game);
  }

  public void updateMysqlPlayerStats(int year, int week, String gamedir,
                                     String frags,
                                     String map, String ip, String port,
                                     long secspld, int totfrags, int lastfrags,
                                     double fpm, int ping, int factor,
                                     long time, int aantal, int id,
                                     String game, String playerip, String playerq2cl) {
    gameControl.updateMysqlPlayerStats(year, week, gamedir, frags,
                                        map, ip, port, secspld, totfrags,
                                        lastfrags, fpm, ping, factor, time,
                                        aantal, id, game, playerip, playerq2cl);
  }

  public void updateMysqlPlayerStatsOnReset(int year, int week, String gamedir,
                                            String frags,
                                            String map, String ip, String port,
                                            long time, int ping, int factor,
                                            int aantal, int id,
                                            String game, String playerip, String playerq2cl) {
    gameControl.updateMysqlPlayerStatsOnReset(year, week, gamedir, frags, map,
                                               ip, port, time, ping, factor,
                                               aantal, id, game, playerip, playerq2cl);
  }

  public void insertMysqlPlayerStats(int year, int week, String gamedir,
                                     String nick,
                                     String clan, String frags, String map,
                                     String ip,
                                     String port, long time, int ping_time,
                                     int factor, String game, String playerip, String playerq2cl) {
    gameControl.insertMysqlPlayerStats(year, week, gamedir, nick, clan, frags, map, ip, port, time, ping_time, factor, game, playerip, playerq2cl);
  }
}
