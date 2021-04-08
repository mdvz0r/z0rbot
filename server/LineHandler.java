/**
 * <p>Title: z0rbot IRC server</p>
 * <p>Description: z0rbot</p>
 * <p>Copyright: Copyright (c) 2004 - 2005</p>
 * <p>Company: z0r.nl</p>
 * @author mdvz0r & Elvis
 * @version 2.2
 */

import java.lang.*;
import java.util.*;
import java.security.SecureRandom;
import java.sql.Timestamp;


public class LineHandler {

  protected BotHandler botHandler;

  public LineHandler(BotHandler botHandler) {
    this.botHandler = botHandler;
  }

  public boolean version(String version) {
    if (version.equals("2.0")) {
      return true;
    }
    else {
      return false;
    }
  }

  public String[] auth(String ip) {
    ArrayList mysqlBotList = botHandler.getMysqlBotListByIp(ip);
    String[] authInfo = new String[8];
    String[] mysqlBotInfo = new String[8];
    boolean botFound = false;
    boolean useMysqlBotInfo = false;

    if (mysqlBotList.size() == 0) {
      authInfo[0] = "ERROR";
      authInfo[1] = "NONE";
    }
    else {
      ArrayList botList = botHandler.getBotList();
      if (botList.size() == 0) {
        mysqlBotInfo = (String[]) mysqlBotList.get(0);
        authInfo[0] = "OK";
        authInfo[1] = mysqlBotInfo[0] + mysqlBotInfo[1];
        authInfo[2] = mysqlBotInfo[2];
        authInfo[3] = mysqlBotInfo[3];
        authInfo[4] = mysqlBotInfo[4];
        authInfo[5] = mysqlBotInfo[5];
        authInfo[6] = mysqlBotInfo[6];
        authInfo[7] = mysqlBotInfo[7];
        botHandler.setBotName(mysqlBotInfo[0]);
        botHandler.setBotNumber(mysqlBotInfo[1]);
        botHandler.setBotNetwork(mysqlBotInfo[5]);
        botHandler.addBotToBotList();
      }
      else {
        for (int mysqlBotListCounter = 0; mysqlBotListCounter < mysqlBotList.size(); mysqlBotListCounter++) {
          mysqlBotInfo = (String[]) mysqlBotList.get(mysqlBotListCounter);
          for (int botListCounter = 0; botListCounter < botList.size();
               botListCounter++) {
            BotHandler botHandler = (BotHandler) botList.get(botListCounter);
            if (botHandler.getBotName().equals(mysqlBotInfo[0]) &&
                botHandler.getBotNumber().equals(mysqlBotInfo[1])) {
              botFound = true;
            }
          }
          if (botFound == false) {
            useMysqlBotInfo = true;
            break;
          }
          else {
            botFound = false;
          }
        }
        if (useMysqlBotInfo == true) {
          authInfo[0] = "OK";
          authInfo[1] = mysqlBotInfo[0] + mysqlBotInfo[1];
          authInfo[2] = mysqlBotInfo[2];
          authInfo[3] = mysqlBotInfo[3];
          authInfo[4] = mysqlBotInfo[4];
          authInfo[5] = mysqlBotInfo[5];
          authInfo[6] = mysqlBotInfo[6];
          authInfo[7] = mysqlBotInfo[7];
          botHandler.setBotName(mysqlBotInfo[0]);
          botHandler.setBotNumber(mysqlBotInfo[1]);
          botHandler.setBotNetwork(mysqlBotInfo[5]);
          botHandler.addBotToBotList();
        }
        else {
          authInfo[0] = "ERROR";
          authInfo[1] = "MANY";
        }
      }
    }
    return authInfo;
  }

  public String[] reauth(String ip, String nick) {
    ArrayList mysqlBotListTmp = botHandler.getMysqlBotListByIp(ip);
    ArrayList mysqlBotList = new ArrayList();

    for (int x = 0; x < mysqlBotListTmp.size(); x++) {
      String[] mysqlBotInfo = (String[]) mysqlBotListTmp.get(x);
      if ((mysqlBotInfo[0] + mysqlBotInfo[1]).equals(nick)) {
        mysqlBotList.add(mysqlBotListTmp.get(x));
      }
    }

    String[] authInfo = new String[8];
    String[] mysqlBotInfo = new String[8];
    boolean botFound = false;
    boolean useMysqlBotInfo = false;

    if (mysqlBotList.size() == 0) {
      authInfo[0] = "ERROR";
      authInfo[1] = "NONE";
    }
    else {
      ArrayList botList = botHandler.getBotList();
      if (botList.size() == 0) {
        mysqlBotInfo = (String[]) mysqlBotList.get(0);
        authInfo[0] = "OK";
        authInfo[1] = mysqlBotInfo[0] + mysqlBotInfo[1];
        authInfo[2] = mysqlBotInfo[2];
        authInfo[3] = mysqlBotInfo[3];
        authInfo[4] = mysqlBotInfo[4];
        authInfo[5] = mysqlBotInfo[5];
        authInfo[6] = mysqlBotInfo[6];
        authInfo[7] = mysqlBotInfo[7];
        botHandler.setBotName(mysqlBotInfo[0]);
        botHandler.setBotNumber(mysqlBotInfo[1]);
        botHandler.setBotNetwork(mysqlBotInfo[5]);
        botHandler.addBotToBotList();
      }
      else {
        for (int mysqlBotListCounter = 0; mysqlBotListCounter < mysqlBotList.size(); mysqlBotListCounter++) {
          mysqlBotInfo = (String[]) mysqlBotList.get(mysqlBotListCounter);
          for (int botListCounter = 0; botListCounter < botList.size();
               botListCounter++) {
            BotHandler botHandler = (BotHandler) botList.get(botListCounter);
            if (botHandler.getBotName().equals(mysqlBotInfo[0]) &&
                botHandler.getBotNumber().equals(mysqlBotInfo[1])) {
              botFound = true;
            }
          }
          if (botFound == false) {
            useMysqlBotInfo = true;
            break;
          }
          else {
            botFound = false;
          }
        }
        if (useMysqlBotInfo == true) {
          authInfo[0] = "OK";
          authInfo[1] = mysqlBotInfo[0] + mysqlBotInfo[1];
          authInfo[2] = mysqlBotInfo[2];
          authInfo[3] = mysqlBotInfo[3];
          authInfo[4] = mysqlBotInfo[4];
          authInfo[5] = mysqlBotInfo[5];
          authInfo[6] = mysqlBotInfo[6];
          authInfo[7] = mysqlBotInfo[7];
          botHandler.setBotName(mysqlBotInfo[0]);
          botHandler.setBotNumber(mysqlBotInfo[1]);
          botHandler.setBotNetwork(mysqlBotInfo[5]);
          botHandler.addBotToBotList();
        }
        else {
          authInfo[0] = "ERROR";
          authInfo[1] = "MANY";
        }
      }
    }
    return authInfo;
  }


  public ArrayList channel(String name, String number) {
    return botHandler.getMysqlChannelListByNameNumber(name, number);
  }

  public ArrayList stats(String channel, String stats, String name, String number, String year, String week) {
//    String[] mysqlGameInfo = botHandler.getMysqlGameByChannelNameNumber(channel, name, number);
    String[] mysqlGameInfo = this.botHandler.getGameGamedirByChannel(channel);
    return botHandler.getMysqlStatsListByGameGamedirNameYearWeek(mysqlGameInfo[0], mysqlGameInfo[1], stats, year, week);
  }

  public ArrayList search(String channel, String search, String name, String number) {
//    String[] mysqlGameInfo = botHandler.getMysqlGameByChannelNameNumber(channel, name, number);
    String[] mysqlGameInfo = this.botHandler.getGameGamedirByChannel(channel);
    return botHandler.getGameControlPlayer(mysqlGameInfo[0], mysqlGameInfo[1], search);
  }

  public void join(String botname, String channel, String login, String host, String nick, String game, String gamedir) {
    // check if user is allowed to give command
    boolean isAdmin = this.botHandler.isAdmin(login, host);
    boolean isFull = false;
    boolean notInChannel = false;
    // check if bot is full
    ArrayList botList = botHandler.getBotList();

    if (botname.equals("")) {
      for (int botListCounter = 0; botListCounter < botList.size(); botListCounter++) {
        BotHandler botHandler = (BotHandler) botList.get(botListCounter);
        if (!botHandler.maxBotReached(channel) && this.botHandler.getBotNetwork().equals(botHandler.getBotNetwork())) {
          botname = botHandler.getBotName() + botHandler.getBotNumber();
          break;
        }
      }
    }

    // check if user has access to this bot
    boolean isBotAdmin = this.botHandler.isBotAdmin(botname, login, host);

    for (int botListCounter = 0; botListCounter < botList.size(); botListCounter++) {
      BotHandler botHandler = (BotHandler) botList.get(botListCounter);
      if ( (botHandler.getBotName() + botHandler.getBotNumber()).equals(botname)) {
        isFull = !botHandler.maxBotReached(channel);
        notInChannel = !botHandler.isInChannel(channel);
      }
    }

    // check if channel isnt already visited by z0rbot
    if (!isFull && !notInChannel) {
      this.botHandler.msgOperator(nick, "Wtf! Are you out of your mind?");
    } else if (!isAdmin) {
      this.botHandler.msgOperator(nick, "You're not authorized!");
    } else if (!isBotAdmin) {
      this.botHandler.msgOperator(nick, "You do not own the selected bot.");
    } else if (!isFull) {
     this.botHandler.msgOperator(nick, botname + " is full, use an other bot");
    } else if (!notInChannel) {
      this.botHandler.msgOperator(nick, "Another bot is already in this channel!");
    }
    System.out.println(isAdmin + " " + isFull + " " + notInChannel + " " + isBotAdmin);
    // if user is allowed
    if (isAdmin && isFull && notInChannel && isBotAdmin) {

      botList = botHandler.getBotList();
      for (int botListCounter = 0; botListCounter < botList.size(); botListCounter++) {
        BotHandler botHandler = (BotHandler) botList.get(botListCounter);
        if ( (botHandler.getBotName() + botHandler.getBotNumber()).equals(botname)) {
          // join the channel
          botHandler.join(channel, game, gamedir);
          this.botHandler.msgOperator(nick, "Trying to join: " + channel);
        }
      }
    }
  }


  public void ban(String nick, String host, String login, String hostname, String admin) {
    // check if user is allowed to give command
    boolean isAdmin = this.botHandler.isAdmin(login, hostname);
    // if user is allowed
    if (isAdmin) {
      this.botHandler.msgOperator(admin, "Trying to ban: " +  nick + " " + host);
      botHandler.ban(nick, host);
      this.botHandler.sendMsg("#z0r.nl.pr", admin + " is trying to ban: " +  nick + " " + host);
    }
  }

  public void serverUnban(String ip, String login, String hostname, String admin) {
    // check if user is allowed to give command^
    boolean isAdmin = this.botHandler.isServerAdmin(login, hostname);
    // if user is allowed
    if (isAdmin) {
	// check IP
	String[] ip_split = ip.split("\\.");
	int[] ip_split_int = new int[4];
	if (ip_split.length == 4) {
	    boolean ip_ok = true;
	    for (int x = 0; x < ip_split.length; x++) {
		ip_split_int[x] = Integer.parseInt(ip_split[x]);
		if (ip_split_int[x] > 255) ip_ok = false;
	    }
	    if (ip_ok) {
		if (botHandler.serverUnban(ip)) {
		    this.botHandler.msgOperator(admin, "Unbanned " + ip_split[0] + "." + ip_split[1]  + "." + ip_split[2] + "." + ip_split[3]  + " on all active servers");
		    this.botHandler.sendMsg("#z0r.nl.pr", admin + " unbanned: " + ip_split[0] + "." + ip_split[1]  + "." + ip_split[2] + "." + ip_split[3]  + " on all active servers");
		} else {
		    this.botHandler.msgOperator(admin, "An error has occurred while trying to ban: " + ip + " please contact a z0r operator.");
		}
	    } else {
		this.botHandler.msgOperator(admin, "Incorrect ip to ban for: " + ip);
	    }	
	}
	else {
	  this.botHandler.msgOperator(admin, "Failed to ban: " + ip);
        }
    }
  }

  public void serverBan(String nick, String ip, String login, String hostname, String admin, String reason) {
    // check if user is allowed to give command
    boolean isAdmin = this.botHandler.isServerAdmin(login, hostname);
    System.out.println("WEE " + isAdmin);
    // if user is allowed
    if (isAdmin) {
	// check IP
	String[] ip_split = ip.split("\\.");
	int[] ip_split_int = new int[4];
	if (ip_split.length == 4) {
	    boolean ip_ok = true;
	    for (int x = 0; x < ip_split.length; x++) {
		ip_split_int[x] = Integer.parseInt(ip_split[x]);
		if (ip_split_int[x] > 255) ip_ok = false;
	    }
	    if (ip_ok) {
		if (botHandler.serverBan(nick, ip, reason)) {
		    this.botHandler.msgOperator(admin, "Banned " +  nick + " " + ip_split[0] + "." + ip_split[1]  + "." + ip_split[2] + "." + ip_split[3]  + " on all active servers");
		    this.botHandler.sendMsg("#z0r.nl.pr", admin + " banned: " + nick + " " + ip_split[0] + "." + ip_split[1]  + "." + ip_split[2] + "." + ip_split[3]  + " on all active servers");
		} else {
		    this.botHandler.msgOperator(admin, "An error has occurred while trying to ban: " +  nick + " " + ip + " please contact a z0r operator.");
		}
	    } else {
		this.botHandler.msgOperator(admin, "Incorrect ip to ban for: " +  nick + " " + ip);
	    }	
	}
	else {
	  this.botHandler.msgOperator(admin, "Failed to ban: " +  nick + " " + ip);
        }
    }
  }

  public void addException(String nick, String ip, String login, String hostname, String admin, String mask) {
    // check if user is allowed to give command
    boolean isAdmin = this.botHandler.isServerAdmin(login, hostname);

    // if user is allowed
    if (isAdmin) {
	// check IP
	String[] ip_split = ip.split("\\.");
	int[] ip_split_int = new int[4];
	if (ip_split.length == 4) {
	    boolean ip_ok = true;
	    for (int x = 0; x < ip_split.length; x++) {
		ip_split_int[x] = Integer.parseInt(ip_split[x]);
		if (ip_split_int[x] > 255) ip_ok = false;
	    }
	    if (ip_ok) {
		if (botHandler.addException(nick, ip, mask)) {
		    this.botHandler.msgOperator(admin, "Added exception for " +  nick + " " + ip_split[0] + "." + ip_split[1]  + "." + ip_split[2] + "." + ip_split[3]  + " on all active servers");
		    this.botHandler.sendMsg("#z0r.nl.pr", admin + " added exception for " +  nick + " " + ip_split[0] + "." + ip_split[1]  + "." + ip_split[2] + "." + ip_split[3]  + " on all active servers");
		} else {
		    this.botHandler.msgOperator(admin, "An error has occurred while trying to add an Exception for:" +  nick + " " + ip + " please contact a z0r operator.");
		}
	    } else {
		this.botHandler.msgOperator(admin, "Incorrect ip to add an exception for: " +  nick + " " + ip);
	    }	
	}
	else {
	  this.botHandler.msgOperator(admin, "Failed to add an exception for: " +  nick + " " + ip);
        }
    }
  }

  public void removeException(String nick, String login, String hostname, String admin) {
    // check if user is allowed to give command^
    boolean isAdmin = this.botHandler.isServerAdmin(login, hostname);
    // if user is allowed
    if (isAdmin) {
	if (botHandler.removeException(nick)) {
	    this.botHandler.msgOperator(admin, "Removed an Exception for " + nick  + " on all active servers");
	    this.botHandler.sendMsg("#z0r.nl.pr", admin + " removed an Exception for " + nick  + " on all active servers");
	} else {
	    this.botHandler.msgOperator(admin, "An error has occurred while trying to remove an exception for " + nick + " please contact a z0r operator.");
	}
    }
  }

  public void removeNickReservation(String nick, String login, String hostname, String admin) {
    // check if user is allowed to give command^
    boolean isAdmin = this.botHandler.isServerAdmin(login, hostname);
    // if user is allowed
    if (isAdmin) {
	if (botHandler.removeNickReservation(nick)) {
	    this.botHandler.msgOperator(admin, "Removed a Reservation for " + nick);
	    this.botHandler.sendMsg("#z0r.nl.pr", admin + " removed a Reservation for " + nick);
	} else {
	    this.botHandler.msgOperator(admin, "An error has occurred while trying to remove a reservation for " + nick + " please contact a z0r operator.");
	}
    }
  }

  public void addNickReservation(String nick, String password, String login, String hostname, String admin) {
    // check if user is allowed to give command^
    boolean isAdmin = this.botHandler.isServerAdmin(login, hostname);
    // if user is allowed
    if (isAdmin) {
	if (botHandler.addNickReservation(nick, password)) {
	    this.botHandler.msgOperator(admin, "Added a Reservation for " + nick);
	    this.botHandler.sendMsg("#z0r.nl.pr", admin + " added a Reservation for " + nick);
	} else {
	    this.botHandler.msgOperator(admin, "An error has occurred while trying to add a reservation for " + nick + " please contact a z0r operator.");
	}
    }
  }

  public void rconCmd(String login, String hostname, String admin, String server, String cmd) {
    // check if user is allowed to give command^
    boolean isAdmin = this.botHandler.isServerAdmin(login, hostname);
    // if user is allowed
    if (isAdmin) {
      if (cmd.indexOf("rcon_password") == -1) {
        String[] server_s = server.split(":");
        String retval = "Wrong server address: " + server;
        if (server_s.length == 2) retval = this.botHandler.rconCmd(server_s[0], server_s[1],cmd);
        else if (server_s.length == 1) retval = this.botHandler.rconCmd(server_s[0], "",cmd);
        retval = retval.replace('"', '\'');
        String[] retval_s = retval.split("\n");
        for(int x = 0; x < retval_s.length; x++) 
          this.botHandler.msgOperator(admin, retval_s[x]);
        this.botHandler.sendMsg("#z0r.nl.pr", admin + ", " + server + ", rcon " + cmd);
      } else {
        this.botHandler.sendMsg("#z0r.nl.pr", "LAME: " + admin + ", " + server + ", rcon " + cmd);
        this.botHandler.msgOperator(admin, "This command is not allowed.");
      }
    }

  }

  public void addRequired(String nick, String ip, String login, String hostname, String admin, String mask) {
    // check if user is allowed to give command
    boolean isAdmin = this.botHandler.isServerAdmin(login, hostname);

    // if user is allowed
    if (isAdmin) {
	// check IP
	String[] ip_split = ip.split("\\.");
	int[] ip_split_int = new int[4];
	if (ip_split.length == 4) {
	    boolean ip_ok = true;
	    for (int x = 0; x < ip_split.length; x++) {
		ip_split_int[x] = Integer.parseInt(ip_split[x]);
		if (ip_split_int[x] > 255) ip_ok = false;
	    }
	    if (ip_ok) {
		if (botHandler.addRequired(nick, ip, mask)) {
		    this.botHandler.msgOperator(admin, "Added a required for " +  nick + " " + ip_split[0] + "." + ip_split[1]  + "." + ip_split[2] + "." + ip_split[3]  + " on all active servers");
		    this.botHandler.sendMsg("#z0r.nl.pr", admin + " added a required for " +  nick + " " + ip_split[0] + "." + ip_split[1]  + "." + ip_split[2] + "." + ip_split[3]  + " on all active servers");
		} else {
		    this.botHandler.msgOperator(admin, "An error has occurred while trying to add a Required for:" +  nick + " " + ip + " please contact a z0r operator.");
		}
	    } else {
		this.botHandler.msgOperator(admin, "Incorrect ip to add an required for: " +  nick + " " + ip);
	    }	
	}
	else {
	  this.botHandler.msgOperator(admin, "Failed to add an required for: " +  nick + " " + ip);
        }
    }
  }

  public void removeRequired(String nick, String login, String hostname, String admin) {
    // check if user is allowed to give command^
    boolean isAdmin = this.botHandler.isServerAdmin(login, hostname);
    // if user is allowed
    if (isAdmin) {
	if (botHandler.removeRequired(nick)) {
	    this.botHandler.msgOperator(admin, "Removed a Required for " + nick  + " on all active servers");
	    this.botHandler.sendMsg("#z0r.nl.pr", admin + " removed a Required for " + nick  + " on all active servers");
	} else {
	    this.botHandler.msgOperator(admin, "An error has occurred while trying to remove a required for " + nick + " please contact a z0r operator.");
	}
    }
  }

  public void part(String botname, String channel, String login, String host, String admin) {
    // check if user is allowed to give command
    boolean isAdmin = this.botHandler.isAdmin(login, host);
    // if user is allowed
    if (isAdmin) {
      if (botname.equals("")) {
        // get bot from this channel
        botname = this.botHandler.getBotnameFromChannel(channel);
    
      }
      boolean isBotAdmin = this.botHandler.isBotAdmin(botname, login, host);
      if (isBotAdmin) {
        if (botname.equals("%")) {
          this.botHandler.msgOperator(admin, "This channel " + channel + " is on more networks, please specify the bot number");
        }
        else {
          boolean isParted = false;
          ArrayList botList = botHandler.getBotList();
          for (int botListCounter = 0; botListCounter < botList.size(); botListCounter++) {
            BotHandler botHandler = (BotHandler) botList.get(botListCounter);
            if ( (botHandler.getBotName() + botHandler.getBotNumber()).equals(botname)) {
              // part the channel
              botHandler.part(channel);
              this.botHandler.msgOperator(admin, "Trying to part: " +  channel);
              isParted = true;
            }
          }
          if (!isParted) {
            if (botname.equals("")) {
              this.botHandler.msgOperator(admin, channel + " doesn't exist in database.");
            } else {
              this.botHandler.msgOperator(admin, botname + " doesn't seem to be in " + channel);
            }
	  }
        }
      } else {
	this.botHandler.msgOperator(admin, "You do not own the selected bot, can not part: " +  channel);
      }
    }
  }

  public void setGame(String channel, String game, String login, String host) {
    // check if user is allowed to give command
    boolean isAdmin = this.botHandler.isAdmin(login, host);
    // if user is allowed
    if (isAdmin) {
      botHandler.setGame(channel, game, this.botHandler.getBotName(), this.botHandler.getBotNumber());
    }
  }

  public void setGamedir(String channel, String gamedir, String login, String host) {
    // check if user is allowed to give command
    boolean isAdmin = this.botHandler.isAdmin(login, host);
    // if user is allowed
    if (isAdmin) {
      botHandler.setGamedir(channel, gamedir, this.botHandler.getBotName(), this.botHandler.getBotNumber());
    }
  }

  public void updateCwLimit(String channel, String min, String max, String botName, String botNumber) {
    botHandler.updateCwLimit(channel, min, max, botName, botNumber);
  }

  public void processMsg(String channel, String sender, String host, String msg, String network) {
    if (msg.length() > 100) msg = msg.substring(0, 99);
    boolean isBanned = this.botHandler.isBanned(host, sender);
    if (!isBanned) {
      int waittime = botHandler.checkChannel(channel, "");
      if (waittime == 0) {
        ArrayList botList = botHandler.getBotList();

        int countChannels = 0;
	int countChannelsMax = 0;
        for (int botListCounter2 = 0; botListCounter2 < botList.size();
             botListCounter2++) {
          BotHandler botHandler2 = (BotHandler) botList.get(botListCounter2);
          for (int x = 0; x < botHandler2.channels.size(); x++) {
            String[] chan = (String[]) botHandler2.channels.get(x);
            if (chan[1].equals("1") && ! (chan[0].equals(channel) && chan[3].equals(network))) {
              if (chan[3].equals(network) || chan[4].equals("1")) {
                String[] st = this.botHandler.getGameGamedirByChannel(channel);
                if (st[0].equals(chan[5]) && st[1].equals(chan[6])) {
                  countChannels++;
                }
		countChannelsMax++;
              }
            }
          }
        }
        this.botHandler.sendMsg(channel,
                                "Message was sent to " + countChannels + "/" + countChannelsMax +" channels!");

        for (int botListCounter = 0; botListCounter < botList.size();
             botListCounter++) {
          BotHandler botHandler = (BotHandler) botList.get(botListCounter);
          for (int botChannelCounter = 0; botChannelCounter < botHandler.channels.size(); botChannelCounter++) {
            String[] chan = (String[]) botHandler.channels.get(botChannelCounter);
            if (chan[1].equals("1") && ! ((chan[0].equals(channel) && chan[3].equals(network))) && (!chan[0].equals("#z0r.nl.pr"))) {
              if (chan[3].equals(network) || chan[4].equals("1")) {
                // send msg to bots
                String msgChannel = channel;
                if (!chan[3].equals(network)) {
                  msgChannel = msgChannel + " @ " + network;
                }
                String[] st = this.botHandler.getGameGamedirByChannel(channel);
                if (st[0].equals(chan[5]) && st[1].equals(chan[6])) {
//                  botHandler.sendMsg( (String) ( (String[]) botHandler.channels.get(botChannelCounter))[0],"[MSG] " + msgChannel + " - " + sender + " - " + msg);
                  botHandler.sendMsg( (String) ( (String[]) botHandler.channels.get(botChannelCounter))[0],"[MSG] " + msgChannel + " - " + sender + " - " + msg);
                }
              }
            } if ((chan[0].equals("#z0r.nl.pr")) && (chan[3].equals(network))) {
              // send msg to bots
                String msgChannel = channel;
                if (!chan[3].equals(network)) {
                  msgChannel = msgChannel + " @ " + network;
                }
                String[] st = this.botHandler.getGameGamedirByChannel(channel);
//                botHandler.sendMsg( (String) ( (String[]) botHandler.channels.get(botChannelCounter))[0],"[MSG " + st[0] + " - " + st[1] + "] " + msgChannel + " - " + sender + " - " + msg);
                botHandler.sendMsg( (String) ( (String[]) botHandler.channels.get(botChannelCounter))[0],"[MSG " + st[0] + " - " + st[1] + "] " + msgChannel + " - " + sender + " - " + msg);
            }
          }
        }
        botHandler.addMsgList(channel, sender, msg, network);
      }
      else {
        botHandler.sendMsg(channel,
                           "You have already broadcasted a cw or message, please wait " +
                           waittime +
                           " more seconds, before sending a new message..");
      }
    } else {
      botHandler.sendMsg(channel, "[BAN] You are not allowed to use this function.");
    }
  }

  public void processMsgAdmin(String channel, String sender, String host, String login, String msg, String network) {
    if (msg.length() > 100) msg = msg.substring(0, 99);
    boolean isAdmin = this.botHandler.isServerAdmin(login, host);
    if (isAdmin) {
      int waittime = botHandler.checkChannel(channel, "");
      if (true) {
        ArrayList botList = botHandler.getBotList();

        int countChannels = 0;
	int countChannelsMax = 0;
        for (int botListCounter2 = 0; botListCounter2 < botList.size();
             botListCounter2++) {
          BotHandler botHandler2 = (BotHandler) botList.get(botListCounter2);
          for (int x = 0; x < botHandler2.channels.size(); x++) {
            String[] chan = (String[]) botHandler2.channels.get(x);
            if (! (chan[0].equals(channel) && chan[3].equals(network))) {
              if (chan[3].equals(network) || chan[4].equals("1")) {
                String[] st = this.botHandler.getGameGamedirByChannel(channel);
                if (st[0].equals(chan[5]) && st[1].equals(chan[6])) {
                  countChannels++;
		  countChannelsMax++;
                }
              }
            }
          }
        }
        this.botHandler.sendMsg(channel,
                                "Message was sent to " + countChannels + "/" + countChannelsMax +" channels!");

        for (int botListCounter = 0; botListCounter < botList.size();
             botListCounter++) {
          BotHandler botHandler = (BotHandler) botList.get(botListCounter);
          for (int botChannelCounter = 0; botChannelCounter < botHandler.channels.size(); botChannelCounter++) {
            String[] chan = (String[]) botHandler.channels.get(botChannelCounter);
            if (! ((chan[0].equals(channel) && chan[3].equals(network))) && (!chan[0].equals("#z0r.nl.pr"))) {
              if (chan[3].equals(network) || chan[4].equals("1")) {
                // send msg to bots
                String msgChannel = channel;
                if (!chan[3].equals(network)) {
                  msgChannel = msgChannel + " @ " + network;
                }
                String[] st = this.botHandler.getGameGamedirByChannel(channel);
                if (st[0].equals(chan[5]) && st[1].equals(chan[6])) {
//                  botHandler.sendMsg( (String) ( (String[]) botHandler.channels.get(botChannelCounter))[0],"[MSG] " + msgChannel + " - " + sender + " - " + msg);
                  botHandler.sendMsg( (String) ( (String[]) botHandler.channels.get(botChannelCounter))[0],"[ADM] " + msgChannel + " - " + sender + " - " + msg);
                }
              }
            } if ((chan[0].equals("#z0r.nl.pr")) && (chan[3].equals(network))) {
              // send msg to bots
                String msgChannel = channel;
                if (!chan[3].equals(network)) {
                  msgChannel = msgChannel + " @ " + network;
                }
                String[] st = this.botHandler.getGameGamedirByChannel(channel);
//                botHandler.sendMsg( (String) ( (String[]) botHandler.channels.get(botChannelCounter))[0],"[MSG " + st[0] + " - " + st[1] + "] " + msgChannel + " - " + sender + " - " + msg);
                botHandler.sendMsg( (String) ( (String[]) botHandler.channels.get(botChannelCounter))[0],"[ADM " + st[0] + " - " + st[1] + "] " + msgChannel + " - " + sender + " - " + msg);
            }
          }
        }
        botHandler.addMsgList(channel, sender, msg, network);
      }
      else {
        botHandler.sendMsg(channel,
                           "You have already broadcasted a cw or message, please wait " +
                           waittime +
                           " more seconds, before sending a new message..");
      }
    } else {
      botHandler.sendMsg(channel, "ADMINN] You are not allowed to use this function.");
    }
  }


  public void processCw(String channel, String sender, String host, String players, String type, String additional, String network) {
    if (additional.length() > 100) additional = additional.substring(0, 99);
    if (type.equals("off")) type = "Official";
    if (type.equals("uo")) type = "Unofficial";
    boolean isBanned = this.botHandler.isBanned(host, sender);
    if (!isBanned) {
      int waittime = botHandler.checkChannel(channel, "");
      if (waittime == 0) {
        ArrayList botList = botHandler.getBotList();

        int countChannels = 0;
        int countChannelsMax = 0;
        for (int botListCounter2 = 0; botListCounter2 < botList.size();
             botListCounter2++) {
          BotHandler botHandler2 = (BotHandler) botList.get(botListCounter2);
          for (int x = 0; x < botHandler2.channels.size(); x++) {
            String[] chan = (String[]) botHandler2.channels.get(x);
            if (chan[1].equals("1") && ! (chan[0].equals(channel) && chan[3].equals(network))) {
              if (chan[3].equals(network) || chan[4].equals("1")) {
                String[] st = this.botHandler.getGameGamedirByChannel(channel);
                if (st[0].equals(chan[5]) && st[1].equals(chan[6])) {
                  if (Integer.parseInt(chan[7]) <= Integer.parseInt(players) && Integer.parseInt(players) <= Integer.parseInt(chan[8])) {
                    countChannels++;
                  }
                }
		countChannelsMax++;
              }
            }
          }
        }
        this.botHandler.sendMsg(channel,
                                "Message was sent to " + countChannels + "/"+ countChannelsMax +" channels!");

        for (int botListCounter = 0; botListCounter < botList.size();
             botListCounter++) {
          BotHandler botHandler = (BotHandler) botList.get(botListCounter);
          for (int botChannelCounter = 0; botChannelCounter < botHandler.channels.size(); botChannelCounter++) {
            String[] chan = (String[]) botHandler.channels.get(botChannelCounter);
            if (chan[1].equals("1") && (!(chan[0].equals(channel) && chan[3].equals(network))) && (!chan[0].equals("#z0r.nl.pr"))) {
              if (chan[3].equals(network) || chan[4].equals("1")) {
                // send msg to bots
                String msgChannel = channel;
                if (!chan[3].equals(network)) {
                  msgChannel = msgChannel + " @ " + network;
                }
                String[] st = this.botHandler.getGameGamedirByChannel(channel);
                if (st[0].equals(chan[5]) && st[1].equals(chan[6])) {
                  if (Integer.parseInt(chan[7]) <= Integer.parseInt(players) && Integer.parseInt(players) <= Integer.parseInt(chan[8])) {
//                    botHandler.sendMsg( (String) ( (String[]) botHandler.channels.get(botChannelCounter))[0],"[CW] " + msgChannel + " - " + sender + " - Requested a " + players + "on" + players + " " + type + " (Additional info: " + additional + ")");
                    botHandler.sendMsg( (String) ( (String[]) botHandler.channels.get(botChannelCounter))[0],"[CW] " + msgChannel + " - " + sender + " - Requested a " + players + "on" + players + " " + type + " (Additional info: " + additional + ")");
                  }
                }
              }
            } if ((chan[0].equals("#z0r.nl.pr")) && (chan[3].equals(network))) {
              // send msg to bots
                String msgChannel = channel;
                if (!chan[3].equals(network)) {
                  msgChannel = msgChannel + " @ " + network;
                }
                if (Integer.parseInt(chan[7]) <= Integer.parseInt(players) && Integer.parseInt(players) <= Integer.parseInt(chan[8])) {
                  String[] st = this.botHandler.getGameGamedirByChannel(channel);
//                  botHandler.sendMsg( (String) ( (String[]) botHandler.channels.get(botChannelCounter))[0],"[CW " + st[0] + " - " + st[1] + "] " + msgChannel + " - " + sender + " - Requested a " + players + "on" + players + " " + type + " (Additional info: " + additional + ")");
                  botHandler.sendMsg( (String) ( (String[]) botHandler.channels.get(botChannelCounter))[0],"[CW " + st[0] + " - " + st[1] + "] " + msgChannel + " - " + sender + " - Requested a " + players + "on" + players + " " + type + " (Additional info: " + additional + ")");
                }
            }
          }
        }
        botHandler.addCwList(channel, sender, players, type, additional, network);
      }
      else {
        botHandler.sendMsg(channel,
                           "You have already broadcasted a cw or message, please wait " +
                           waittime +
                           " more seconds, before sending a new message..");
      }
    } else {
      botHandler.sendMsg(channel, "[BAN] You are not allowed to use this function.");
    }
  }


  public void processMsgoutput(String channel) {
    for (int x = 0; x < botHandler.channels.size(); x++) {
      String[] chan = (String[]) botHandler.channels.get(x);
      if (chan[0].equals(channel)) {
        if (chan[1].equals("1")) {
          // currently enabled, now set to disabled
          this.botHandler.setAcceptMessages("0", channel);
          this.botHandler.sendMsgoutput(channel, "Message output disabled for " + channel);
        }
        else {
          // currently disabled now set to enabled
          this.botHandler.setAcceptMessages("1", channel);
          this.botHandler.sendMsgoutput(channel, "Message output enabled for " + channel);
        }
      }
    }
  }

  public void processMsgserver(String channel) {
    for (int x = 0; x < botHandler.channels.size(); x++) {
      String[] chan = (String[]) botHandler.channels.get(x);
      if (chan[0].equals(channel)) {
        if (chan[4].equals("1")) {
          // currently enabled, now set to disabled
          this.botHandler.setAlsoOtherNetworks("0", channel);
          this.botHandler.sendMsgoutput(channel, channel + " will now only receive messages from this network: " + chan[3]);
        }
        else {
          // currently disabled now set to enabled
          this.botHandler.setAlsoOtherNetworks("1", channel);
          this.botHandler.sendMsgoutput(channel, channel + " will now receive messages from all networks");
        }
      }
    }
  }

/*
  public void checkQuit(String host, String botNick) {
    // check if user is allowed to give command
    boolean isAdmin = this.botHandler.isAdmin(host);
// if user is allowed
    if (isAdmin) {
      ArrayList botList = botHandler.getBotList();
      for (int botListCounter = 0; botListCounter < botList.size(); botListCounter++) {
        BotHandler botHandler = (BotHandler) botList.get(botListCounter);
        if ((botHandler.getBotName()+botHandler.getBotNumber()).equals(botNick) || botNick.equals("ALL")) {
          botHandler.quit();
        }
      }
    }
  }
*/
  public ArrayList getEmptyCWList(String name, String number, String channel) {
//    String[] mysqlGameInfo = botHandler.getMysqlGameByChannelNameNumber(channel, name, number);
    String[] mysqlGameInfo = this.botHandler.getGameGamedirByChannel(channel);
    return botHandler.getEmptyCWList(mysqlGameInfo[0], mysqlGameInfo[1]);
  }

  public void botNotConnected() {
    ArrayList botList = botHandler.getBotList();
    for (int x = 0; x < botList.size(); x++) {
      BotHandler botHandler = (BotHandler) botList.get(x);
      for (int y = 0; y < botHandler.channels.size(); y++) {
        String[] st = (String[]) botHandler.channels.get(y);
        if (st[0].equals("#z0r.nl")) {
          botHandler.sendZ0r();
        }
      }
    }
  }

  public void getz0rStatus(String channel) {
    for (int x = 0; x < botHandler.channels.size(); x++) {
      String[] st = (String[]) botHandler.channels.get(x);
      if (st[0].equals(channel)) {
        String acceptMessages = "not printed here";
        if (st[1].equals("1")) {
          acceptMessages = "printed here";
          System.out.println(st[4]);
          if (st[4].equals("0")) {
            acceptMessages = acceptMessages + " (only (CW) messages from " + st[3] + ")";
          } else {
            acceptMessages = acceptMessages + " (accepting (CW) messages from all networks)";
          }
        }
        this.botHandler.sendMsgoutput(channel, "This channel: " + channel + " uses the game: " + st[5] + ", with mod: " + st[6] + ". " +
                                      "(CW) Messages are " + acceptMessages + ". All CW messages matching " + st[7] + " till " + st[8] + " players will be printed.");
      }
    }
  }

  public void processBookserver(String channel, String bookerName, String hostname, String server, String amount, String time) {
    boolean cont = true;
    boolean isBanned = this.botHandler.isBanned(hostname, bookerName);
    if (!isBanned) {

    // check if person or channel has not already registered a server
    if (!botHandler.checkServerBookedByPersonOrChannel(channel, bookerName, hostname)) {

    String serverIp = "";
    String serverPort = "";
    String serverRcon = "";
    String onstrt = "";
    String onstop = "";
    int serverBeginTime = 0;
    int serverTime = 0;

    ArrayList emptyCWList = botHandler.getEmptyCWList("quake2", "action teamplay");
    for (int x = 0; x < emptyCWList.size(); x++) {
      String serverSplit[] = ((String)emptyCWList.get(x)).split(" \\| ");
      for(int y = 0; y < serverSplit.length; y++) {
        String ipSplit[] = serverSplit[y].split(":");
        String serverInfo[] = (String[]) botHandler.getMysqlServerListByIp(ipSplit[0], ipSplit[1]);
        if(serverInfo[4].equals(server)) {

	  if(( new Timestamp( System.currentTimeMillis() ) ).getTime() / 60000 > (long) (Integer.valueOf(serverInfo[2]).intValue() + 5) ) {

            serverIp = ipSplit[0];
            serverPort = ipSplit[1];
            serverRcon = serverInfo[1];
	    onstrt = serverInfo[5];
	    onstop = serverInfo[6];
            break;
          }

        }
      }
      if(!serverIp.equals("")) {
        break;
      }
    }

    if(serverIp.equals("")) {

      String serverInfo[] = (String[]) botHandler.getMysqlServerListByShortname( server );
      if (serverInfo != null) {
        if(Integer.parseInt(serverInfo[4]) > 0) {
         serverBeginTime = Integer.parseInt(serverInfo[4]);
         serverTime = Integer.parseInt(serverInfo[5]);
         serverIp = serverInfo[1];
         serverPort = serverInfo[2];
         onstrt = serverInfo[6];
         onstop = serverInfo[7];
        }
        else {
         serverIp = serverInfo[1];
         serverPort = serverInfo[2];
         serverRcon = serverInfo[3];
         onstrt = serverInfo[6];
         onstop = serverInfo[7];
        }
      } else {
	botHandler.sendMsg(channel, "[BOOKSERVER] Unknown server, check http://www.z0r.nl/index.php?link=book for the server ID's.");
	cont = false;
      }
    
    }

    if (cont) {
    if(serverBeginTime != 0) {

      botHandler.sendMsg(channel, "[BOOKSERVER] Next server available will be " + serverIp + ":" + serverPort + " in " + ((serverBeginTime + serverTime) - (( new Timestamp( System.currentTimeMillis() ) ).getTime() / 60000)) + " minutes.");

    }
    else {

      botHandler.sendMsg(channel, "[BOOKSERVER] " + serverIp + ":" + serverPort + " selected. Rcon will be sent in PM to requester.");
      if (!channel.equals("#z0r.nl.pr")) {
          BotHandler tempBotHandler = (BotHandler) this.botHandler.getBotHandler("#z0r.nl.pr");
	  if (tempBotHandler != null) tempBotHandler.sendMsg("#z0r.nl.pr", "[BOOKSERVER] " + serverIp + ":" + serverPort + " selected by "+ bookerName + " (" +  hostname + ")" +" @ "+ channel +".");
      }
      
      try {

        Random rand = SecureRandom.getInstance("SHA1PRNG");
        int length = rand.nextInt(6) + 6;	// tussen de 6 en 12 tekens
        char[] password = new char[length];
        for (int x = 0; x < length; x++) {
          int randDecimalAsciiVal = rand.nextInt(25) + 97; // ascii characters tussen 97 en 122
          password[x] = (char) randDecimalAsciiVal;
        }

        botHandler.setServerBooking(serverIp, serverPort, String.valueOf(password), (( new Timestamp( System.currentTimeMillis() ) ).getTime() / 60000), (Integer.valueOf(time).intValue()), bookerName, hostname, channel);
        botHandler.sendMsg(bookerName, "[BOOKSERVER] " + serverIp + ":" + serverPort + " selected. Rcon is '" + String.valueOf(password) + "'.");
        botHandler.createQuake2BookingHandler(serverIp, serverPort, serverRcon, String.valueOf(password), time, bookerName, hostname, channel, onstrt, onstop);

      }
      catch (Exception e) {

        ExceptionHandler exceptionHandler = new ExceptionHandler(e);

      }
    }
    }
  } else {
    botHandler.sendMsg(channel, "[BOOKSERVER] You or your channel has already registered a server, you can unregister with !unbookserver");
  }
  } else {
    botHandler.sendMsg(channel, "[BAN] You are not allowed to use this function.");
  }
  }

  public void processUnbookserver(String channel, String bookerName, String hostname) {
    boolean isBanned = this.botHandler.isBanned(hostname, bookerName);
    if (!isBanned) {
	botHandler.removeQuake2BookingHandler(bookerName, hostname, channel);
    } else {
      botHandler.sendMsg(channel, "[BAN] You are not allowed to use this function.");
    }
  }

  public void processUnbookserverAdm(String channel, String login, String hostname, String host, String port) {
    boolean isAdmin = this.botHandler.isServerAdmin(login, hostname);
System.out.println(login + " " + hostname);
    if (isAdmin) {
	botHandler.removeQuake2BookingHandlerIpPort(host, port);
    }
  }
}