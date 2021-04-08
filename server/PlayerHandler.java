/**
 * <p>Title: z0rbot IRC server</p>
 * <p>Description: z0rbot</p>
 * <p>Copyright: Copyright (c) 2004 - 2005</p>
 * <p>Company: z0r.nl</p>
 * @author mdvz0r & Elvis
 * @version 2.2
 */


import java.util.*;
import java.lang.*;
import java.sql.Timestamp;

public class PlayerHandler {

  protected GameHandler gameHandler;

  public PlayerHandler(GameHandler gameHandler, String ip, String port, String game, String gamedir, String map, int factor, String name, String frags, String ping_time, String playerip, String playerq2cl) {
    this.gameHandler = gameHandler;

    int ping = 0;
    try {

      // Haal de huidige tijd op
      Timestamp vandaag = new Timestamp(System.currentTimeMillis());

      // Haal jaar en maand op
      Calendar cal = new GregorianCalendar();
      int week = cal.get(Calendar.WEEK_OF_YEAR);
      int year = cal.get(Calendar.YEAR);

      // check if table is already in the database
      if (gameHandler.existCurrentGame(year + "_" + week + "_" + gamedir + "_" + game)) {
        // if not, add this table
        gameHandler.addCurrentGame(year, week, gamedir, game);
      }

      // get information about this player
      String[] playerInformation = (String[]) gameHandler.getMysqlPlayerByNickYearWeekGamedirGame(name, year, week, gamedir, game);

      if (playerInformation != null) {
        int id = Integer.parseInt(playerInformation[9]);
        long secspld;
        int totfrags, lastfrags;

        secspld = ( (vandaag.getTime() / 1000) - Long.parseLong(playerInformation[0])) + Long.parseLong(playerInformation[1]);
        // if player's ping = 0
        if (Integer.parseInt(playerInformation[2]) == 0) ping = Integer.parseInt(ping_time) * 100;
        // else
        else {
          ping = (Integer.parseInt(ping_time) * 100 + (Integer.parseInt(playerInformation[3]) * Integer.parseInt(playerInformation[2]))) / (Integer.parseInt(playerInformation[2]) + 1);
          factor = (factor + (Integer.parseInt(playerInformation[4]) * Integer.parseInt(playerInformation[2]))) / (Integer.parseInt(playerInformation[2]) + 1);
        }

        if ( (playerInformation[5].equals(ip + ":" + port)) &&
            (playerInformation[6].equals(map)) &&
            ( (Integer.parseInt(playerInformation[0]) + 180) >= (vandaag.getTime() / 1000))) {
          lastfrags = Integer.parseInt(frags) - Integer.parseInt(playerInformation[7]);
          // if not plummed
          if ( (lastfrags > -1)) {
            totfrags = lastfrags + Integer.parseInt(playerInformation[8]);
            double fpm = 0;
            String s = Float.toString(secspld);
            double d = Double.valueOf(s).doubleValue();
            double f = totfrags;
            if ( (f != 0) && (d != 0)) {
              fpm = ( ( (f) / ( (d) / 60)) * 100);
            }
            // Update fpm
            long time = (vandaag.getTime() / 1000);
            int aantal = (Integer.parseInt(playerInformation[2]) + 1);

            gameHandler.updateMysqlPlayerStats(year, week, gamedir, frags,
                                               map, ip, port, secspld, totfrags,
                                               lastfrags, fpm, ping, factor, time,
                                               aantal, id, game, playerip, playerq2cl);
          }
          else {
            // plumb
            long time = (vandaag.getTime() / 1000);
            int aantal = (Integer.parseInt(playerInformation[2]) + 1);
            gameHandler.updateMysqlPlayerStatsOnReset(year, week, gamedir, frags, map,
                                                      ip, port, time, ping, factor,
                                                      aantal, id, game, playerip, playerq2cl);
           }
        }
        else {
          // player change
          long time = (vandaag.getTime() / 1000);
          int aantal = (Integer.parseInt(playerInformation[2]) + 1);
          gameHandler.updateMysqlPlayerStatsOnReset(year, week, gamedir, frags, map,
                                                    ip, port, time, ping, factor,
                                                    aantal, id, game, playerip, playerq2cl);

        }
      }
      else {
        long time = (vandaag.getTime() / 1000);
        gameHandler.insertMysqlPlayerStats(year, week, gamedir, name, clan(name), frags, map, ip, port, time, Integer.parseInt(ping_time), factor, game, playerip, playerq2cl);
      }
    }
    catch (Exception ex) {
      System.out.println("An exception has been intercepted");
      ex.printStackTrace();
    }
  }

  public String clan(String nick) {
    String clan = "";
    String[] tagb = new String[19];
    String[] tage = new String[19];
    tagb[0] = " ";
    tage[0] = " ";
    tagb[1] = "(";
    tage[1] = ")";
    tagb[2] = "<";
    tage[2] = ">";
    tagb[3] = ":";
    tage[3] = ":";
    tagb[4] = "{";
    tage[4] = "}";
    tagb[5] = "#";
    tage[5] = "#";
    tagb[6] = "!";
    tage[6] = "!";
    tagb[7] = "^";
    tage[7] = "^";
    tagb[8] = "-";
    tage[8] = "-";
    tagb[9] = "=";
    tage[9] = "=";
    tagb[10] = "\"";
    tage[10] = "\"";
    tagb[11] = "'";
    tage[11] = "'";
    tagb[12] = "`";
    tage[12] = "`";
    tagb[13] = ".";
    tage[13] = ".";
    tagb[14] = ",";
    tage[14] = ",";
    tagb[15] = "[";
    tage[15] = "]";
    tagb[16] = ">";
    tage[16] = "<";
    tagb[17] = "/";
    tage[17] = "/";
    tagb[18] = "|";
    tage[18] = "|";

    for (int x = 0; x < tagb.length; x++) {
      int nickb = nick.indexOf(tagb[x], 0);
      int nicke = -1;
      if (nickb != -1) {
        if (nick.length() > (nickb + 1)) {
          nicke = nick.indexOf(tage[x], nickb + 1);
        }
      }

      if ( (nickb != -1) && (nicke != -1)) {
        if ( (nicke - nickb) < 7) {
          if (nick.length() != (nick.substring( (nickb), (nicke + 1))).length())
            return nick.substring( (nickb), (nicke + 1));
        }
      }
    }

    // begin bij 1 en neem de spaties niet mee
    for (int x = 1; x < tagb.length; x++) {
      for (int y = 1; y < tage.length; y++) {
        int nickb = nick.indexOf(tagb[x], 0);
        int nicke = -1;
        if (nickb != -1) {
          if (nick.length() > (nickb + 1)) {
            nicke = nick.indexOf(tage[y], nickb + 1);
          }
        }

        if ( (nickb != -1) && (nicke != -1)) {
          if ( (nicke - nickb) < 7) {
            if (nick.length() != (nick.substring( (nickb), (nicke + 1))).length())
              return nick.substring( (nickb), (nicke + 1));
          }
        }
      }
    }

    if (clan == "") {
      for (int x = 0; x < tagb.length; x++) {
        int nickb = nick.indexOf(tagb[x], 0);
        int nicke = nick.indexOf(tage[x], 0);
        if (nickb != -1) {
          if (nick.length() != (nick.substring(0, (nickb + 1))).length())
            return nick.substring(0, (nickb + 1));
        }
        if (nicke != -1) {
          if (nick.length() != (nick.substring(0, (nicke + 1))).length())
            return nick.substring(0, (nicke + 1));
        }
      }
    }
    return "";

  }
}
