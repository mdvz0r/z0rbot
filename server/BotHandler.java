/**
 * <p>Title: z0rbot IRC server</p>
 * <p>Description: z0rbot</p>
 * <p>Copyright: Copyright (c) 2004 - 2005</p>
 * <p>Company: z0r.nl</p>
 * @author mdvz0r & Elvis
 * @version 2.2
 */



import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import java.sql.Timestamp;


public class BotHandler extends Thread {

    protected BotControl botControl;
    protected Socket socket;
    protected LineHandler lineHandler;
    protected BufferedReader input;
    protected PrintWriter output;
    protected String name;
    protected String number;
    protected String network;
    protected List channels = Collections.synchronizedList(new ArrayList());
    protected boolean versionChecked = false;
    protected boolean authChecked = false;
    protected boolean channelChecked = false;


    public BotHandler(BotControl botControl, Socket socket) {

	try {
	    this.botControl = botControl;
	    this.socket = socket;
	    this.lineHandler = new LineHandler(this);
	    this.start();
	}
	catch(Exception e) {
	    ExceptionHandler exceptionHandler = new ExceptionHandler(e);
	}
    }

    public void run() {

	try {
	    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
	    while(true) {
		String readLine = input.readLine();
		System.out.println(readLine);
	        if(readLine == null) {
		    if(authChecked == true) {
			botControl.removeBotFromBotList(this);
		    }
		    break;
		}
		else {
		    interrogate(readLine);
		}
	    }
	}
	catch(Exception e) {
	    if(authChecked == true) {
		botControl.removeBotFromBotList(this);
	    }
	    ExceptionHandler exceptionHandler = new ExceptionHandler(e);
	}

    }


    public void interrogate(String readLine) {

	try {
	    String readLineSplit[] = readLine.split("\"");
	    if(readLineSplit[1].equals("VERSION")) {
		if(lineHandler.version(readLineSplit[2])) {
		    output.println(readLineSplit[0] + "\"VERSION\"OK");
		    output.flush();
		    versionChecked = true;
		}
		else {
		    output.println(readLineSplit[0] + "\"VERSION\"ERROR");
		    output.flush();
		    socket.close();
		}
	    }
	    if(versionChecked) {
		if(readLineSplit[1].equals("AUTH")) {
                  String[] auth;
                  if (readLineSplit.length == 3) { auth = lineHandler.reauth(socket.getInetAddress().getHostAddress(), readLineSplit[2]); }
                  else { auth  = lineHandler.auth(socket.getInetAddress().getHostAddress()); }
		    if(auth[0].equals("OK")) {
			output.println(readLineSplit[0] + "\"AUTH\"OK\"" + auth[1]  + "\"" + auth[2] + "\"" + auth[3] + "\"" + auth[4] + "\"" + auth[6] + "\"" + auth[7]);
	    		output.flush();
			authChecked = true;
		    }
		    else {
			output.println(readLineSplit[0] + "\"AUTH\"ERROR\"" + auth[1]);
			output.flush();
			socket.close();
		    }
		}
 		if(authChecked) {
   		    if(readLineSplit[1].equals("CHANNEL")) {
			ArrayList mysqlChannelList = lineHandler.channel(name, number);
			for(int mysqlChannelListCounter = 0; mysqlChannelListCounter < mysqlChannelList.size(); mysqlChannelListCounter++) {
			    String[] mysqlChannel = (String[]) mysqlChannelList.get(mysqlChannelListCounter);
                            String[] newMysqlChannel = new String[9];
                            newMysqlChannel[0] = mysqlChannel[0].toLowerCase();
                            newMysqlChannel[1] = mysqlChannel[1];
                            newMysqlChannel[3] = mysqlChannel[2];
                            newMysqlChannel[4] = mysqlChannel[3];
                            newMysqlChannel[2] = "0";
                            newMysqlChannel[5] = mysqlChannel[4];
                            newMysqlChannel[6] = mysqlChannel[5];
                            newMysqlChannel[7] = mysqlChannel[7];
                            newMysqlChannel[8] = mysqlChannel[8];
			    
			    boolean allowJoin = true;
			    for (int x = 0; x < this.channels.size(); x++) {
			        String[] st = (String[]) this.channels.get(x);
	    			if ((st[0].toLowerCase()).equals(newMysqlChannel[0].toLowerCase())) {
		        	    allowJoin = false; // already exists
//				    System.out.println(st[0] + " " + newMysqlChannel[0] + " -- already exist, so no new entry");
			        }
			    }
			    if (allowJoin) {
//				System.out.println(newMysqlChannel[0] + " -- don't exist, so new entry");
                        	this.channels.add(newMysqlChannel);
			    }
			    output.println(readLineSplit[0] + "\"CHANNEL\"" + mysqlChannel[0] + "\"" + mysqlChannel[6]);
	    		    output.flush();
			}
			channelChecked = true;
		    }
		    if(channelChecked) {
			if(readLineSplit[1].equals("STATS")) {
			    Calendar statsCalendar = new GregorianCalendar(new Locale("fi"));
			    ArrayList mysqlStatsList = lineHandler.stats(readLineSplit[2].toLowerCase(), readLineSplit[3], name, number, String.valueOf(statsCalendar.get(Calendar.YEAR)), String.valueOf(statsCalendar.get(Calendar.WEEK_OF_YEAR)));

			    if(mysqlStatsList.size() > 5) {
				output.println(readLineSplit[0] + "\"STATS\"ERROR\"" + readLineSplit[2] + "\"MANY");
				output.flush();
			    }
			    if(mysqlStatsList.size() == 0) {
				output.println(readLineSplit[0] + "\"STATS\"ERROR\"" + readLineSplit[2] + "\"NONE");
				output.flush();
			    }
//  			    if(mysqlStatsList.size() > 0 && mysqlStatsList.size() < 6) {
    		    	        for(int mysqlStatsListCounter = 0; mysqlStatsListCounter < mysqlStatsList.size(); mysqlStatsListCounter++) {
				    String[] mysqlStatsInfo = (String[]) mysqlStatsList.get(mysqlStatsListCounter);
                                    String wr = "";
                                    if (mysqlStatsInfo[1].equals("0")) {
                                      wr = "N/A";
                                    } else {
                                      wr = mysqlStatsInfo[4];
                                    }
                                    if (mysqlStatsListCounter < 5) {
                                      output.println(readLineSplit[0] + "\"STATS\"OK\"" +
                                                     readLineSplit[2] + "\"" + mysqlStatsInfo[0] +
                                                     "\"" + mysqlStatsInfo[1] + "\"" +
                                                     mysqlStatsInfo[2] + "\"" + mysqlStatsInfo[3] +
                                                     "\"" + wr + "\"" +
                                                     String.valueOf(statsCalendar.get(Calendar.YEAR)) +
                                                     "\"" +
                                                     String.valueOf(statsCalendar.get(Calendar.
                                          WEEK_OF_YEAR)));
                                      output.flush();
                                    }
				}
//			    }
			}
			if(readLineSplit[1].equals("SEARCH")) {
			    ArrayList mysqlSearchList = lineHandler.search(readLineSplit[2].toLowerCase(), readLineSplit[3], name, number);
                            System.out.println(mysqlSearchList.size());
			    if(mysqlSearchList.size() > 7) {
				output.println(readLineSplit[0] + "\"SEARCH\"ERROR\"" + readLineSplit[2] + "\"MANY");
				output.flush();
			    }
			    if(mysqlSearchList.size() == 0) {
				output.println(readLineSplit[0] + "\"SEARCH\"ERROR\"" + readLineSplit[2] + "\"NONE");
				output.flush();
			    }
    			    if(mysqlSearchList.size() > 0 && mysqlSearchList.size() < 8) {
    		    	        for(int mysqlSearchListCounter = 0; mysqlSearchListCounter < mysqlSearchList.size(); mysqlSearchListCounter++) {
				    String[] mysqlSearchInfo = (String[]) mysqlSearchList.get(mysqlSearchListCounter);
				    output.println(readLineSplit[0] + "\"SEARCH\"OK\"" + readLineSplit[2] + "\"" + mysqlSearchInfo[1] + "\"" + mysqlSearchInfo[2] + "\"" + mysqlSearchInfo[0]);
	    			    output.flush();
				}
			    }
			}
                        if (readLineSplit[1].equals("BAN")) {
                          lineHandler.ban(readLineSplit[2], readLineSplit[3], readLineSplit[4], readLineSplit[5], readLineSplit[6]);
                        }


                        if (readLineSplit[1].equals("SERVERBAN")) {
			 	  if (readLineSplit.length == 8) {
                            lineHandler.serverBan(readLineSplit[2], readLineSplit[3], readLineSplit[4], readLineSplit[5], readLineSplit[6], readLineSplit[7]);
			  	  } else {
                            lineHandler.serverBan(readLineSplit[2], readLineSplit[3], readLineSplit[4], readLineSplit[5], readLineSplit[6], "");
			     	  }
                        }
                        if (readLineSplit[1].equals("SERVERUNBAN")) {
			  	  if (readLineSplit.length == 6) {
                            lineHandler.serverUnban(readLineSplit[2], readLineSplit[3], readLineSplit[4], readLineSplit[5]);
			  	  }
                        }

                        if (readLineSplit[1].equals("ADDEXCEPTION")) {
			 	  if (readLineSplit.length == 8) {
                            lineHandler.addException(readLineSplit[2], readLineSplit[3], readLineSplit[4], readLineSplit[5], readLineSplit[6], readLineSplit[7]);
			     	  }
                        }
                        if (readLineSplit[1].equals("REMOVEEXCEPTION")) {
			  	  if (readLineSplit.length == 6) {
                            lineHandler.removeException(readLineSplit[2], readLineSplit[3], readLineSplit[4], readLineSplit[5]);
			  	  }
                        }

                        if (readLineSplit[1].equals("REMOVENICKRESERVATION")) {
			  	  if (readLineSplit.length == 6) {
                            lineHandler.removeNickReservation(readLineSplit[2], readLineSplit[3], readLineSplit[4], readLineSplit[5]);
			  	  }
                        }

                        if (readLineSplit[1].equals("ADDNICKRESERVATION")) {
			  	  if (readLineSplit.length == 7) {
                            lineHandler.addNickReservation(readLineSplit[2], readLineSplit[3], readLineSplit[4], readLineSplit[5], readLineSplit[6]);
			  	  }
                        }

                        if (readLineSplit[1].equals("RCON")) {
			  	  if (readLineSplit.length == 7) {
                            lineHandler.rconCmd(readLineSplit[2], readLineSplit[3], readLineSplit[4], readLineSplit[5], readLineSplit[6]);
			  	  }
                        }

                        if (readLineSplit[1].equals("ADDREQUIRED")) {
			 	  if (readLineSplit.length == 8) {
                            lineHandler.addRequired(readLineSplit[2], readLineSplit[3], readLineSplit[4], readLineSplit[5], readLineSplit[6], readLineSplit[7]);
			     	  }
                        }
                        if (readLineSplit[1].equals("REMOVEREQUIRED")) {
			  	  if (readLineSplit.length == 6) {
                            lineHandler.removeRequired(readLineSplit[2], readLineSplit[3], readLineSplit[4], readLineSplit[5]);
			  	  }
                        }
                        
				if (readLineSplit[1].equals("JOIN")) {
                          if (readLineSplit.length == 7) {
                            lineHandler.join(readLineSplit[2], readLineSplit[3].toLowerCase(),
                                             readLineSplit[4], readLineSplit[5],
                                             readLineSplit[6], "quake2", "action teamplay");
                          } else {
                            lineHandler.join(readLineSplit[2], readLineSplit[3].toLowerCase(),
                                             readLineSplit[4], readLineSplit[5],
                                             readLineSplit[6], readLineSplit[7], readLineSplit[8]);
                          }
                        }
                        if (readLineSplit[1].equals("PART")) {
                          lineHandler.part(readLineSplit[2], readLineSplit[3].toLowerCase(), readLineSplit[4], readLineSplit[5], readLineSplit[6]);
                        }
                        if (readLineSplit[1].equals("SETGAME")) {
                          lineHandler.setGame(readLineSplit[2].toLowerCase(), readLineSplit[3], readLineSplit[4], readLineSplit[5]);
                        }
                        if (readLineSplit[1].equals("SETGAMEDIR")) {
                          lineHandler.setGamedir(readLineSplit[2].toLowerCase(), readLineSplit[3], readLineSplit[4], readLineSplit[5]);
                        }
                        if (readLineSplit[1].equals("MSG")) {
                          lineHandler.processMsg(readLineSplit[2].toLowerCase(), readLineSplit[3], readLineSplit[4], readLineSplit[5], getBotNetwork());
                        }
                        if (readLineSplit[1].equals("MSGADMIN")) {
                          lineHandler.processMsgAdmin(readLineSplit[2].toLowerCase(), readLineSplit[3], readLineSplit[4], readLineSplit[5], readLineSplit[6], getBotNetwork());
                        }
                        if (readLineSplit[1].equals("CW")) {
                          lineHandler.processCw(readLineSplit[2].toLowerCase(), readLineSplit[3], readLineSplit[4], readLineSplit[5], readLineSplit[6], readLineSplit[7], getBotNetwork());
                        }
                        if (readLineSplit[1].equals("CWLIMIT")) {
                          lineHandler.updateCwLimit(readLineSplit[2].toLowerCase(), readLineSplit[4].toLowerCase(), readLineSplit[5].toLowerCase(), this.getBotName(), this.getBotNumber());
                        }
                        if (readLineSplit[1].equals("MSGOUTPUT")) {
                          lineHandler.processMsgoutput(readLineSplit[2].toLowerCase());
                        }
                        if (readLineSplit[1].equals("MSGSERVER")) {
                          lineHandler.processMsgserver(readLineSplit[2].toLowerCase());
                        }
                        if (readLineSplit[1].equals("MSGLIST")) {
                          this.getList(readLineSplit[2].toLowerCase());
                        }
                        if (readLineSplit[1].equals("CWLIST")) {
                          this.getCw(readLineSplit[2].toLowerCase());
                        }
                        if (readLineSplit[1].equals("NOTCONNECTED")) {
                          lineHandler.botNotConnected();
                        }
                        if (readLineSplit[1].equals("EMPTYCW")) {
                          String[] st = getGameGamedirByChannel(readLineSplit[2].toLowerCase());
                          if (st[0].equals("quake2") && st[1].equals("action teamplay")) {
                            ArrayList emptyCWList = lineHandler.getEmptyCWList(name, number,
                                readLineSplit[2].toLowerCase());
                            if (emptyCWList.size() == 0) {
                              output.println(readLineSplit[0] + "\"EMPTYCW\"" +
                                             readLineSplit[2].toLowerCase() +
                                             "\"There are currently no empty CW Servers (or the z0r server has just restarted, try again in 30 seconds).");
                              output.flush();
                            }
                            else {
                              for (int x = 0; x < emptyCWList.size(); x++) {
                                output.println(readLineSplit[0] + "\"EMPTYCW\"" +
                                               readLineSplit[2].toLowerCase() + "\"" +
                                               emptyCWList.get(x));
                                output.flush();
                              }
                            }
                          } else {
                            output.println(readLineSplit[0] + "\"EMPTYCW\"" + readLineSplit[2].toLowerCase() + "\"This function is currently not available for this game.");
                            output.flush();
                          }
                        }
                        if (readLineSplit[1].equals("MSGUSAGE")) {
                          botControl.updateMsgUsage(readLineSplit[2].toLowerCase(), readLineSplit[3].toLowerCase(), this.getBotName(), this.getBotNumber());
                        }
                        if (readLineSplit[1].equals("QUIT")) {
//                          lineHandler.checkQuit(readLineSplit[3], readLineSplit[2]);
                        }
                        if (readLineSplit[1].equals("Z0RSTATUS")) {
                          lineHandler.getz0rStatus(readLineSplit[2].toLowerCase());
                        }
			if (readLineSplit[1].equals("BOOKSERVER")) {
 			  String[] st = getGameGamedirByChannel(readLineSplit[2].toLowerCase());
                          if (st[0].equals("quake2") && st[1].equals("action teamplay")) {
                            
           		    lineHandler.processBookserver(readLineSplit[2].toLowerCase(), readLineSplit[3].toLowerCase(), readLineSplit[4].toLowerCase(), readLineSplit[5].toLowerCase(), "8", readLineSplit[6]);

                          } else {
			    this.sendMsg(readLineSplit[2].toLowerCase(), "[BOOKSERVER] This function is not available for this game.");
                          }
			}
			if (readLineSplit[1].equals("UNBOOKSERVER")) {
			  String[] st = getGameGamedirByChannel(readLineSplit[2].toLowerCase());
			  if (st[0].equals("quake2") && st[1].equals("action teamplay")) {
			    //channel, username, hostname
                            lineHandler.processUnbookserver(readLineSplit[2].toLowerCase(), readLineSplit[3].toLowerCase(), readLineSplit[4].toLowerCase());
                          } else {
                            this.sendMsg(readLineSplit[2].toLowerCase(), "^[UNBOOKSERVER]^ This function is not available for this game.");
                          }
			}
			if (readLineSplit[1].equals("UNBOOKSERVERADM")) {
			    //channel, username, hostname
                            lineHandler.processUnbookserverAdm(readLineSplit[2].toLowerCase(), readLineSplit[3].toLowerCase(), readLineSplit[4].toLowerCase(), readLineSplit[5].toLowerCase(), readLineSplit[6].toLowerCase());
			}
		    }
		}
	    }
	}
	catch(Exception e) {
	    ExceptionHandler exceptionHandler = new ExceptionHandler(e);
            System.out.println(" * * * " + socket.getInetAddress() + " * * * ");
            e.printStackTrace();
	}

    }

    public void setBotNetwork(String network) {
      this.network = network;
    }

    public String getBotNetwork() {
      return this.network;
    }

    public void setBotName(String name) {

	this.name = name;

    }


    public String getBotName() {

	return name;

    }


    public void setBotNumber(String number) {

	this.number = number;

    }


    public String getBotNumber() {

	return number;

    }


    // THIS ARE METHODS WHICH ARE COMING FROM BOTCONTROL


    public void addBotToBotList() {

	botControl.addBotToBotList(this);

    }


    public void removeBotFromBotList() {

	botControl.removeBotFromBotList(this);

    }


    public ArrayList getBotList() {

	return botControl.getBotList();

    }


    // THIS ARE METHODS WHICH ARE COMING FROM OTHER CLASSES


    public ArrayList getMysqlBotListByIp(String ip) {

	return botControl.getMysqlBotListByIp(ip);

    }


    public ArrayList getMysqlChannelListByNameNumber(String name, String number) {

	return botControl.getMysqlChannelListByNameNumber(name, number);

    }

/*
    public String[] getMysqlGameByChannelNameNumber(String channel, String name, String number) {

	return botControl.getMysqlGameByChannelNameNumber(channel, name, number);

    }
*/
    public String[] getGameGamedirByChannel(String channel) {
      String[] st = new String[2];
      for (int x = 0; x < this.channels.size(); x++) {
        String[] chanArray = (String[]) this.channels.get(x);
        if (chanArray[0].equals(channel)) {
          st[0] = chanArray[5];
          st[1] = chanArray[6];
          return st;
        }
      }
      return st;
    }


    public ArrayList getMysqlStatsListByGameGamedirNameYearWeek(String game, String gamedir, String name, String year, String week) {

	return botControl.getMysqlStatsListByGameGamedirNameYearWeek(game, gamedir, name, year, week);

    }

    public ArrayList getGameControlPlayer(String game, String gamedir, String search) {
      return botControl.getGameControlPlayer(game, gamedir, search);
    }

    public boolean isAdmin(String login, String host) {
      return botControl.isAdmin(login, host);
    }

    public boolean isServerAdmin(String login, String host) {
      return botControl.isServerAdmin(login, host);
    }


    public boolean isBotAdmin(String bot, String login, String host) {
      return botControl.isBotAdmin(bot, login, host);
    }


    public boolean isBanned(String host, String nick) {
      return botControl.isBanned(host, nick);
    }


    public void join(String channel, String game, String gamedir) {
      // send join to the bot
      output.println("0\"JOIN\""+channel);
      output.flush();
      // put it in database
      botControl.mysqlJoin(this.getBotName(), this.getBotNumber(), channel, game, gamedir);
      boolean add = true;
      for (int x = 0; x < this.channels.size(); x++) {
        String[] chan = (String []) this.channels.get(x);
        if (chan[0].equals(channel)) {
          add = false;
        }
      }
      if (add) {
        String[] st = new String[9];
        st[0] = channel;
        st[1] = "1";
        st[2] = "0";
        st[3] = this.getBotNetwork();
        st[4] = "1";
        st[5] = game;
        st[6] = gamedir;
        st[7] = "0";
        st[8] = "9";
        this.channels.add(st);
      }
    }

    public void part(String channel) {
      // send join to the bot
      output.println("0\"PART\""+channel);
      output.flush();
      // put it in database
      botControl.mysqlPart(this.getBotName(), this.getBotNumber(), channel);
      for (int x = 0; x < this.channels.size(); x++) {
        String[] chan = (String []) this.channels.get(x);
        if (chan[0].equals(channel)) {
          this.channels.remove(x);
        }
      }
    }


    public void setGame(String channel, String game, String botName, String botNumber) {
      for (int x = 0; x < this.channels.size(); x++) {
        String[] chanArray = (String[]) this.channels.get(x);
        if (chanArray[0].equals(channel)) {
          String[] st = new String[9];
          st[0] = chanArray[0];
          st[1] = chanArray[1];
          st[2] = chanArray[2];
          st[3] = chanArray[3];
          st[4] = chanArray[4];
          st[5] = game;
          st[6] = chanArray[6];
          st[7] = chanArray[7];
          st[8] = chanArray[8];
          this.channels.set(x, st);
        }
      }
      botControl.setGame(channel, game, botName, botNumber);
    }

    public void setGamedir(String channel, String gamedir, String botName, String botNumber) {
      for (int x = 0; x < this.channels.size(); x++) {
        String[] chanArray = (String[])this.channels.get(x);
        if (chanArray[0].equals(channel)) {
          String[] st = new String[9];
          st[0] = chanArray[0];
          st[1] = chanArray[1];
          st[2] = chanArray[2];
          st[3] = chanArray[3];
          st[4] = chanArray[4];
          st[5] = chanArray[5];
          st[6] = gamedir;
          st[7] = chanArray[7];
          st[8] = chanArray[8];
          this.channels.set(x, st);
        }
      }
      botControl.setGamedir(channel, gamedir, botName, botNumber);
    }

    public void sendMsg(String channel, String msg) {
      output.println("0\"MSG\"" + channel + "\"" + msg);
      output.flush();
    }

    public void msgOperator(String user, String msg) {
      output.println("0\"MSGOPERATOR\"" + user + "\"" + msg);
      output.flush();
    }


    public synchronized int checkChannel(String channel, String acceptMessage) {
      for (int x = 0; x < this.channels.size(); x++) {
        String[] chanArray = (String[]) this.channels.get(x);
        if (chanArray[0].equals(channel)) {
          // als acceptmessage leeg is dan gaat het om een check om tijd
          if (acceptMessage.equals("")) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            long tmpLong = 0;
            try {
              tmpLong = Long.parseLong(chanArray[2]);
            } catch (Exception e) {}
            if (tmpLong < ( (timestamp.getTime() / 1000) - 600)) {
              String[] st = new String[9];
              st[0] = chanArray[0];
              st[1] = chanArray[1];
              st[2] = Long.toString(timestamp.getTime() / 1000);
              st[3] = chanArray[3];
              st[4] = chanArray[4];
              st[5] = chanArray[5];
              st[6] = chanArray[6];
              st[7] = chanArray[7];
              st[8] = chanArray[8];
              this.channels.set(x, st);
              return 0;
            }
            else {
              return (int) (Long.parseLong(chanArray[2]) - ( (timestamp.getTime() / 1000) - 600));
            }
          }
          else {
            if (Integer.parseInt(acceptMessage) <= 1) {
              // acceptMessage
              String[] st = new String[9];
              st[0] = chanArray[0];
              st[1] = acceptMessage;
              st[2] = chanArray[2];
              st[3] = chanArray[3];
              st[4] = chanArray[4];
              st[5] = chanArray[5];
              st[6] = chanArray[6];
              st[7] = chanArray[7];
              st[8] = chanArray[8];
              this.channels.set(x, st);
            } else {
              // alsoOtherNetworks
              String[] st = new String[9];
              st[0] = chanArray[0];
              st[1] = chanArray[1];
              st[2] = chanArray[2];
              st[3] = chanArray[3];
              st[4] = ""+(Integer.parseInt(acceptMessage) -2);
              st[5] = chanArray[5];
              st[6] = chanArray[6];
              st[7] = chanArray[7];
              st[8] = chanArray[8];
              this.channels.set(x, st);
              }
            return 0;
          }
        }
      }
      return -1;
    }

    public void setAcceptMessages(String acceptMessage, String botChannel) {
      this.checkChannel(botChannel, acceptMessage);
      botControl.setAcceptMessages(acceptMessage, this.name, this.number, botChannel);
    }

    public void setAlsoOtherNetworks(String acceptMessage, String botChannel) {
      this.checkChannel(botChannel, ""+(Integer.parseInt(acceptMessage) +2));
      botControl.setAlsoOtherNetworks(acceptMessage, this.name, this.number, botChannel);
    }


    public void sendMsgoutput(String channel, String msg) {
      output.println("0\"MSGOUTPUT\"" + channel + "\"" + msg);
      output.flush();
    }

    public void quit() {
      output.println("0\"QUIT");
      output.flush();
      output.close();
      try {
        input.close();
      } catch (Exception e) {}
    }

    public void ban(String nick, String host) {
      botControl.ban(nick, host);
    }

    public boolean serverBan(String nick, String ip, String reason) {
      return botControl.serverBan(nick, ip, reason);
    }

    public boolean serverUnban(String ip) {
      return botControl.serverUnban(ip);
    }

    public boolean addException(String nick, String ip, String mask) {
      return botControl.addException(nick, ip, mask);
    }

    public boolean removeException(String nick) {
      return botControl.removeException(nick);
    }

    public boolean removeNickReservation(String nick) {
      return botControl.removeNickReservation(nick);
    }

    public boolean addNickReservation(String nick, String password) {
      return botControl.addNickReservation(nick, password);
    }

    public boolean addRequired(String nick, String ip, String mask) {
      return botControl.addRequired(nick, ip, mask);
    }

    public boolean removeRequired(String nick) {
      return botControl.removeRequired(nick);
    }

    public void addMsgList(String channel, String sender, String msg, String network) {
      botControl.addMsg(channel, sender, msg, network);
    }

    public void addCwList(String channel, String sender, String players, String type, String additional, String network) {
      botControl.addCw(channel, sender, players, type, additional, network);
    }


    public void getList(String channel) {
      boolean msgSend = false;

      String game = "";
      String gamedir = "";
      for (int y = 0; y < this.channels.size(); y++) {
        String[] stChan = (String[]) this.channels.get(y);
        if (channel.equals(stChan[0])) {
          game = stChan[5];
          gamedir = stChan[6];
        }
      }
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      ArrayList list = botControl.listMsg(channel);
      for (int x = 0; x < list.size(); x++) {
        String[] st = (String[]) list.get(x);
        for (int z = 0; z < botControl.botList.size(); z++) {
          BotHandler b = (BotHandler) botControl.botList.get(z);
          for (int y = 0; y < b.channels.size(); y++) {
            String[] stChan = (String[])b.channels.get(y);
            if (st[0].equals(stChan[0])) {
              if (game.equals(stChan[5]) && gamedir.equals(stChan[6])) {
                long minsAgo = ( (timestamp.getTime() / 1000) -
                                Long.parseLong(st[4])) / 60;
                String msgChannel = st[0];
                if (!st[3].equals(this.getBotNetwork())) {
                  msgChannel = msgChannel + " @ " + network;
                }
//                String msg = "[MSG] " + msgChannel + " - " + st[1] + " - " +
                String msg = "[MSG] " + msgChannel + " - " + st[1] + " - " +
                    st[2] + " - " + minsAgo + " minutes ago";
                this.sendMsg(channel, msg);
                msgSend = true;
              }
            }
          }
        }

      }
      if (!msgSend) {
        this.sendMsg(channel, "No messages from 30 minutes or less");
      }
    }

    public void getCw(String channel) {
      boolean msgSend = false;
      String game = "";
      String gamedir = "";
      for (int y = 0; y < this.channels.size(); y++) {
        String[] stChan = (String[]) this.channels.get(y);
        if (channel.equals(stChan[0])) {
          game = stChan[5];
          gamedir = stChan[6];
        }
      }

      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      ArrayList list = botControl.listCw(channel);
      for (int x = 0; x < list.size(); x++) {
        String[] st = (String[]) list.get(x);
        for (int z = 0; z < botControl.botList.size(); z++) {
          BotHandler b = (BotHandler) botControl.botList.get(z);
          for (int y = 0; y < b.channels.size(); y++) {
            String[] stChan = (String[])b.channels.get(y);
            if (st[0].equals(stChan[0])) {
              if (game.equals(stChan[5]) && gamedir.equals(stChan[6])) {

                long minsAgo = ( (timestamp.getTime() / 1000) -
                                Long.parseLong(st[6])) / 60;
                String msgChannel = st[0];
                if (!st[6].equals(this.getBotNetwork())) {
                  msgChannel = msgChannel + " @ " + network;
                }
//                String msg = "[CW] " + msgChannel + " - " + st[1] +
                String msg = "[CW] " + msgChannel + " - " + st[1] +
                    " - Requested a " + st[2] + "on" + st[2] + " " + st[3] +
                    " (Additional info: " + st[4] + ")" + " - " + minsAgo +
                    " minutes ago";
                this.sendMsg(channel, msg);
                msgSend = true;
              }
            }
          }
        }
      }
      if (!msgSend) {
        this.sendMsg(channel, "No cw from 30 minutes or less");
      }
    }


    public boolean maxBotReached(String channel) {
      for (int x = 0; x < channels.size(); x++) {
        String[] st = (String []) this.channels.get(x);
        if (st[0].equals(channel)) return false;
      }
      if (this.channels.size() >= 14) return true;
      else return false;
    }

    public void checkChannels() {
      output.println("0\"CHECK");
      output.flush();
      System.out.println("CHECK!");
    }

    public boolean isInChannel(String channel) {
      return botControl.isInChannel(channel, this.getBotName(), this.getBotNumber(), this.getBotNetwork());
    }

    public ArrayList getEmptyCWList(String game, String gamedir) {
      return botControl.getEmptyCWList(game, gamedir);
    }

    public void sendZ0r() {
      this.sendMsg("#z0r.nl", this.getBotName() + this.getBotNumber() + " is disconnected!");
    }

    public void updateCwLimit(String channel, String min, String max, String botName, String botNumber) {
      for (int x = 0; x < this.channels.size(); x++) {
        String[] chanArray = (String[]) this.channels.get(x);
        if (chanArray[0].equals(channel)) {
          String[] st = new String[9];
          st[0] = chanArray[0];
          st[1] = chanArray[1];
          st[2] = chanArray[2];
          st[3] = chanArray[3];
          st[4] = chanArray[4];
          st[5] = chanArray[5];
          st[6] = chanArray[6];
          st[7] = min;
          st[8] = max;
          this.channels.set(x, st);
        }
      }
      botControl.updateCwLimit(channel, min, max, botName, botNumber);
    }

    public String getBotnameFromChannel(String channel) {
      return this.botControl.getBotnameFromChannel(channel);
    }

  public String[] getMysqlServerListByIp( String ip, String port ) {
    return botControl.getMysqlServerListByIp( ip , port);
  }

  public String[] getMysqlServerListByShortname( String shortname ) {
    return botControl.getMysqlServerListByShortname( shortname );
  }

  public void setServerBooking(String ip, String port, String lrcon, long beginTime, int time, String nick, String hostname, String channel) {
    botControl.setServerBooking(ip, port, lrcon, beginTime, time, nick, hostname, channel);
  }

  public void createQuake2BookingHandler(String serverIp, String serverPort, String serverRcon, String password, String time, String bookerName, String bookerHostName, String bookerChannel, String onstart, String onstop) {
    botControl.createQuake2BookingHandler(serverIp, serverPort, serverRcon, password, time, bookerName, bookerHostName, bookerChannel, onstart, onstop);
  }

  public void removeQuake2BookingHandler(String bookerName, String bookerHostName, String bookerChannel) {
    botControl.unbookServer(bookerName, bookerHostName, bookerChannel);
  }

  public void removeQuake2BookingHandlerIpPort(String ip, String port) {
    botControl.unbookServerIpPort(ip, port);
  }

  public boolean checkServerBookedByPersonOrChannel(String channel, String nick, String host) {
    return botControl.checkServerBookedByPersonOrChannel(channel, nick, host);
  }

  public boolean doesBotHaveChannel(String channel) {
    for (int x = 0; x < this.channels.size(); x++) {
      String[] chanArray = (String[]) this.channels.get(x);
      if (chanArray[0].equals(channel)) {
	return true;
      }
    }
    return false;
  }

  public BotHandler getBotHandler(String channel) {
    return this.botControl.getBotHandler(channel);
  }

  public String rconCmd(String host, String port, String cmd) {
    return this.botControl.rconCmd(host, port, cmd);
  }


}
