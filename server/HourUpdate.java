/**
 * <p>Title: z0rbot IRC server</p>
 * <p>Description: z0rbot</p>
 * <p>Copyright: Copyright (c) 2004 - 2005</p>
 * <p>Company: z0r.nl</p>
 * @author mdvz0r
 * @version 2.2
 */


import java.util.*;
import java.sql.*;

public class HourUpdate implements Runnable {

  GameControl gameControl;

  public HourUpdate(GameControl gameControl) {
    System.out.println("Hourupdate started");
    // recursive connection to gameControl
    this.gameControl = gameControl;

    // update the clanstats
    //gameControl.avgUpdate();
  }

  public void run() {
    // current hour
    Calendar newCal = new GregorianCalendar();
    Calendar oldCal = newCal;
    while (true) {
      // new time
      newCal = new GregorianCalendar();
      if (newCal.get(Calendar.HOUR_OF_DAY) != oldCal.get(Calendar.HOUR_OF_DAY)) {
//        System.out.println(statsbot.addSpaces("Noticed a change of hour, new hour is: " + newCal.get(Calendar.HOUR_OF_DAY), true));
        // update serverload
        gameControl.serverload(oldCal.get(Calendar.DAY_OF_MONTH),
                               (oldCal.get(Calendar.MONTH) + 1),
                               oldCal.get(Calendar.YEAR),
                               oldCal.get(Calendar.HOUR_OF_DAY),
                               gameControl.getGemUur());
        // update playerstats and clanstats
        gameControl.avgUpdate(oldCal.get(Calendar.WEEK_OF_YEAR), oldCal.get(Calendar.YEAR), oldCal.get(Calendar.HOUR_OF_DAY));
//        System.out.println(statsbot.addSpaces("Average of " + oldCal.get(Calendar.HOUR_OF_DAY) + " hour was: " + (statsbot.gemUur / 100), true));
        oldCal = newCal;
        // reset serverload
        gameControl.resetServerLoad();
        gameControl.checkChannels();
      }
      try {
        Thread.sleep(60000);
      }
      catch (Exception e) {
        System.out.println("Error 1: " + e);
      }
    }
  }
}
