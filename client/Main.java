/**
 * <p>Title: z0rbot IRC client</p>
 * <p>Description: z0rbot</p>
 * <p>Copyright: Copyright (c) 2004 - 2005</p>
 * <p>Company: z0r.nl</p>
 * @author mdvz0r
 * @version 2.2
 */

import java.util.*;
import java.io.*;

public class Main {

  // keep track of the amount of bots
  protected List botList = Collections.synchronizedList(new ArrayList());

  // allow bots to start
  private boolean startBots = true;

  // allow bots to connect to the irc
  private boolean connecting = false;

  private int threads = 0;

  public static void main(String[] args) throws Exception {
    new Main();
  }

  public void updateThreads() {
    this.threads++;
    System.out.println("THREADS STARTED:" + this.threads + "\n");
  }

  public Main() {
    this.startBot();
    this.updateThreads();
  }

  public void startBot() {
    // create connection to the z0r server
    Z0rControl z0rControl = new Z0rControl(this);
    Thread z0rControlThread = new Thread(z0rControl);
    z0rControlThread.start();
    this.addBotToBotList(z0rControl, z0rControlThread);
  }

  /* add a bot to the list and wait till the current one is
     connected before starting a new one */
  public void addBotToBotList(Z0rControl z0rControl, Thread z0rControlThread) {
    // wait 2 seconds for init
    try { Thread.sleep(2000); } catch (Exception e) {}
    try {
      // add this z0r connection to a list
      botList.add(z0rControl);
      // if the last bot could connect, try to start a new bot
      if (!z0rControl.nick.equals("")) {
        // try to start a new bot
//        this.startBot();
      } else {
        // if the bot couldnt connect, remove the last z0rconnection
//        botList.remove(z0rControl);
        this.startBots = false;
//        while (true) {
          // and wait till some operator requests a new bot to start
//          if (this.startBots) {
//            this.startBot();
//          }
          // check every 10 seconds if an operator gave a startbots command
//          try { Thread.sleep(10000); } catch (Exception e) {}
        }
//      }
    } catch (Exception e) { try { Thread.sleep(1000); } catch (Exception ex) { System.out.println(ex); }
      // if for some reason we came in this exception, retry
      botList.remove(z0rControl);
      addBotToBotList(z0rControl, z0rControlThread);
    }
  }

  /* on restart, disconnect the appropriate bot and reconnect */
  public void restart(String botname) {
    for (int x = 0; x < this.botList.size(); x++) {
      Z0rControl z0rControl = (Z0rControl) this.botList.get(x);
      if (z0rControl.getNick().equals(botname)) {
        z0rControl.restartNow();
      }
    }
  }

  /* on quit, disconnect the appropriate bot */
  public void quit(String botname) {
    for (int x = 0; x < this.botList.size(); x++) {
      Z0rControl z0rControl = (Z0rControl) this.botList.get(x);
      if (z0rControl.getNick().equals(botname)) {
        z0rControl.quitNow(botname);
        this.botList.remove(x);
        break;
      }
    }
  }
/*
  // conn = true, is ready to make a connection,
  // conn = false, wants to tell it's connected
  public synchronized boolean connecting(boolean conn) {
    if (conn) {
      // wants to connect
      if (this.connecting) {
        return false;
      }
      // now we will connect
      this.connecting = true;
      return true;
    } else {
      if (this.connecting) {
        // connection was made, an other bot can connect now
        this.connecting = false;
        return true;
      } else {
        System.out.println("ERROR IN Main.connecting");
        return true;
      }
    }
  }

  // check if the bot is allowed to connect //
  public void checkIfAllowedToConnect(boolean conn) {
    // wait till the connection is free
    while (!this.connecting(conn)) {
      try {
        Thread.sleep(1000);
      }
      catch (Exception ex) {
        System.out.println(ex);
      }
    }
  }
*/
  /* if the botname already exists, kill this one */
  public boolean alreadyExists(String botname) {
    for (int x = 0; x < this.botList.size(); x++) {
      Z0rControl z0rControl = (Z0rControl) this.botList.get(x);
      if (z0rControl.getNick().equals(botname)) {
        return true;
      }
    }
    return false;
  }

  /* write a list with channels that could possibly be abbandoned */
  public void fileControl(String channel, String[] lines) {
    try {
      PrintWriter printWriter = new PrintWriter(new FileWriter(
          "list.txt", true));
      printWriter.println(channel + "\t\t(" + lines.length + " users)");
      for (int x = 0; x < lines.length; x++) {
        printWriter.println("\t" + lines[x]);
      }
      printWriter.println("");
      printWriter.flush();
      printWriter.close();
    }
    catch (Exception eh) {
      System.out.println(eh.getMessage());
    }
  }

  /* get all empty channels */
  public void getEmptyChannels(String login, String hostname, String sender) {
    for (int x = 0; x < this.botList.size(); x++) {
      Z0rControl z0rControl = (Z0rControl) this.botList.get(x);
      z0rControl.checkPeopleInChannel(login, hostname, sender);
    }
  }

  /* finger a user for information */
  public void finger(String user) {
    for (int x = 0; x < this.botList.size(); x++) {
      Z0rControl z0rControl = (Z0rControl) this.botList.get(x);
      z0rControl.execFinger(user);
    }
  }

  /* start bot */
  public void setStartBot() {
    this.startBots = true;
  }
}
