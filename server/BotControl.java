/**
 * <p>Title: z0rbot IRC server</p>
 * <p>Description: z0rbot</p>
 * <p>Copyright: Copyright (c) 2004 - 2005</p>
 * <p>Company: z0r.nl</p>
 * @author mdvz0r & Elvis
 * @version 2.2
 */


import java.net.*;
import java.util.*;
import java.sql.Timestamp;

public class BotControl extends Thread {

  protected int port;
  protected ArrayList botList = new ArrayList();
  protected List bookedServerList = Collections.synchronizedList(new ArrayList());
  protected Main main;
  protected List messageList = Collections.synchronizedList(new ArrayList());
  protected List cwList = Collections.synchronizedList(new ArrayList());

  public BotControl(Main main, int port) {
    try {
      this.main = main;
      this.port = port;
      this.bookedServerRestart();
      this.start();
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
  }

  public void run() {
    try {
      ServerSocket serverSocket = new ServerSocket(port);
      while (true) {
        Socket socket = serverSocket.accept();
        BotHandler botHandler = new BotHandler(this, socket);
      }
    }
    catch (Exception e) {
      ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
  }

  public void addBotToBotList(BotHandler botHandler) {
    botList.add(botHandler);
    this.main.updateBotStatus(1, botHandler.name, botHandler.number);
  }

  public void removeBotFromBotList(BotHandler botHandler) {
    botList.remove(botHandler);
    this.main.updateBotStatus(0, botHandler.name, botHandler.number);
  }

  public ArrayList getBotList() {
    return botList;
  }

  public ArrayList getMysqlBotListByIp(String ip) {
    return main.getMysqlBotListByIp(ip);
  }

  public ArrayList getMysqlChannelListByNameNumber(String name, String number) {
    return main.getMysqlChannelListByNameNumber(name, number);
  }
/*
  public String[] getMysqlGameByChannelNameNumber(String channel, String name, String number) {
    return main.getMysqlGameByChannelNameNumber(channel, name, number);
  }
*/
  public ArrayList getMysqlStatsListByGameGamedirNameYearWeek(String game,
      String gamedir, String name, String year, String week) {
    return main.getMysqlStatsListByGameGamedirNameYearWeek(game, gamedir, name,
        year, week);
  }

  public ArrayList getGameControlPlayer(String game, String gamedir,
                                        String search) {
    return main.getGameControlPlayer(game, gamedir, search);
  }

  public boolean isAdmin(String login, String host) {
    return main.isAdmin(login, host);
  }

  public boolean isServerAdmin(String login, String host) {
    return main.isServerAdmin(login, host);
  }

  public boolean isBotAdmin(String bot, String login, String host) {
    return main.isBotAdmin(bot, login, host);
  }


  public boolean isBanned(String host, String nick) {
    return main.isBanned(host, nick);
  }


  public void mysqlJoin(String botName, String botNumber, String channel, String game, String gamedir) {
    main.mysqlJoin(botName, botNumber, channel, game, gamedir);
  }

  public void mysqlPart(String botName, String botNumber, String channel) {
    main.mysqlPart(botName, botNumber, channel);
  }

  public void setGame(String channel, String game, String botName, String botNumber) {
    main.setGame(channel, game, botName, botNumber);
  }

  public void setGamedir(String channel, String gamedir, String botName, String botNumber) {
    main.setGamedir(channel, gamedir, botName, botNumber);
  }

  public void setAcceptMessages(String acceptMessage, String botName, String botNumber, String botChannel) {
    main.setAcceptMessages(acceptMessage, botName, botNumber, botChannel);
  }

  public void setAlsoOtherNetworks(String acceptMessage, String botName, String botNumber, String botChannel) {
    main.setAlsoOtherNetworks(acceptMessage, botName, botNumber, botChannel);
  }

  public void ban(String nick, String host) {
    main.ban(nick, host);
  }

  public boolean serverBan(String nick, String ip, String reason) {
    return main.serverBan(nick, ip, reason);
  }

  public boolean serverUnban(String ip) {
    return main.serverUnban(ip);
  }

  public boolean addException(String nick, String ip, String mask) {
    return main.addException(nick, ip, mask);
  }

  public boolean removeException(String nick) {
    return main.removeException(nick);
  }

  public boolean removeNickReservation(String nick) {
    return main.removeNickReservation(nick);
  }

  public boolean addNickReservation(String nick, String password) {
    return main.addNickReservation(nick, password);
  }


  public boolean addRequired(String nick, String ip, String mask) {
    return main.addRequired(nick, ip, mask);
  }

  public boolean removeRequired(String nick) {
    return main.removeRequired(nick);
  }

  public void addMsg(String channel, String sender, String msg, String network) {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    String[] msgarr = new String [5];
    msgarr[0] = channel;
    msgarr[1] = sender;
    msgarr[2] = msg;
    msgarr[3] = network;
    msgarr[4] = "" + (timestamp.getTime() / 1000);
    this.messageList.add(msgarr);
  }

  public void addCw(String channel, String sender, String players, String type, String additional, String network) {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    String[] msgarr = new String [7];
    msgarr[0] = channel;
    msgarr[1] = sender;
    msgarr[2] = players;
    msgarr[3] = type;
    msgarr[4] = additional;
    msgarr[5] = network;
    msgarr[6] = "" + (timestamp.getTime() / 1000);
    this.cwList.add(msgarr);
  }


  public ArrayList listMsg(String channel) {
    ArrayList list = new ArrayList();
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    for (int x = 0; x < this.messageList.size(); x++) {
      String[] tmpMsg = (String []) this.messageList.get(x);
      if (Long.parseLong(tmpMsg[4]) < ( (timestamp.getTime() / 1000) - 1800)) {
        this.messageList.remove(x);
      }
      else {
        if (!tmpMsg[0].equals(channel)) {
          // print teh shit :P
          list.add(tmpMsg);
        }
      }
    }
    return list;
  }

  public ArrayList listCw(String channel) {
    ArrayList list = new ArrayList();
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    for (int x = 0; x < this.cwList.size(); x++) {
      String[] tmpMsg = (String []) this.cwList.get(x);
      if (Long.parseLong(tmpMsg[6]) < ( (timestamp.getTime() / 1000) - 1800)) {
        this.cwList.remove(x);
      }
      else {
        if (!tmpMsg[0].equals(channel)) {
          list.add(tmpMsg);
        }
      }
    }
    return list;
  }


  public boolean isInChannel(String channel, String name, String number, String network) {
    return main.isInChannel(channel, name, number, network);
  }

  public void checkChannels() {
    ArrayList list = this.getBotList();
    for (int x = 0; x < list.size(); x++) {
      BotHandler c = (BotHandler) list.get(x);
      c.checkChannels();
    }
  }

  public ArrayList getEmptyCWList(String game, String gamedir) {
    return main.getEmptyCWList(game, gamedir);
  }

  public void updateMsgUsage(String channel, String mode, String botName, String botNumber) {
    main.updateMsgUsage(channel, mode, botName, botNumber);
  }

  public void updateCwLimit(String channel, String min, String max, String botName, String botNumber) {
    main.updateCwLimit(channel, min, max, botName, botNumber);
  }

  public String getBotnameFromChannel(String channel) {
    return this.main.getBotnameFromChannel(channel);
  }

  public String[] getMysqlServerListByIp( String ip , String port ) {
    return main.getMysqlServerListByIp( ip, port );
  }
  
  public String[] getMysqlServerListByShortname( String shortname ) {
    return main.getMysqlServerListByShortname( shortname );
  }

  public void setServerBooking(String ip, String port, String lrcon, long beginTime, int time, String nick, String hostname, String channel) {
    main.setServerBooking(ip, port, lrcon, beginTime, time, nick, hostname, channel);
  }

  public void createQuake2BookingHandler(String serverIp, String serverPort, String serverRcon, String password, String time, String bookerName, String bookerHostName, String bookerChannel, String onstart, String onstop) {
    this.killBookedServers(serverIp, serverPort);
    Quake2BookingHandler quake2BookingHandler = new Quake2BookingHandler(this, serverIp, serverPort, serverRcon, password, time, bookerName, bookerHostName, bookerChannel, onstart, onstop, false);
    bookedServerList.add(quake2BookingHandler);
    quake2BookingHandler.start();
  }

  public boolean checkServerBookedByPersonOrChannel(String channel, String nick, String host) {
    for(int x=0; x < bookedServerList.size(); x++) {
      Quake2BookingHandler tempQuake2BookingHandler = (Quake2BookingHandler) bookedServerList.get(x);
      if(tempQuake2BookingHandler.getBookerChannel().equals(channel) || tempQuake2BookingHandler.getBookerName().equals(nick) || tempQuake2BookingHandler.getBookerHostName().equals(host)) {
	return true;
      }
    }
    return false;    
  }

  public BotHandler getBotHandler(String channel) {
    for(int x=0; x < this.botList.size(); x++) {
      BotHandler tempBotHandler = (BotHandler) botList.get(x);
      if (tempBotHandler.doesBotHaveChannel(channel)) return tempBotHandler;
    }
    return null;
  }

  public void unbookServer(String nick, String host, String channel) {
    for(int x=0; x < bookedServerList.size(); x++) {
      Quake2BookingHandler tempQuake2BookingHandler = (Quake2BookingHandler) bookedServerList.get(x);
      if(tempQuake2BookingHandler.getBookerChannel().equals(channel) || tempQuake2BookingHandler.getBookerName().equals(nick) || tempQuake2BookingHandler.getBookerHostName().equals(host)) {
	BotHandler tempBotHandler = (BotHandler) this.getBotHandler(channel);
	tempQuake2BookingHandler.onDie();
//        tempQuake2BookingHandler.setDie();
        this.bookedServerList.remove(tempQuake2BookingHandler);
	break;
      }
    }
  }

  public void unbookServerIpPort(String ip, String port) {
    for(int x=0; x < bookedServerList.size(); x++) {
      Quake2BookingHandler tempQuake2BookingHandler = (Quake2BookingHandler) bookedServerList.get(x);
      if(tempQuake2BookingHandler.getIp().equals(ip) && tempQuake2BookingHandler.getPort().equals(port)) {
	tempQuake2BookingHandler.onDie();
        this.bookedServerList.remove(tempQuake2BookingHandler);
	break;
      }
    }
  }

 
  public void bookedServerRestart() {
    ArrayList mysqlBookedServerList = (ArrayList) main.getMysqlBookedServerList();
    for(int x=0; x < mysqlBookedServerList.size(); x++) {

      String bookedServer[] = (String[]) mysqlBookedServerList.get(x);
      long timeLeft = ((long) (Integer.valueOf(bookedServer[4]).intValue() + Integer.valueOf(bookedServer[5]).intValue())) - ( new Timestamp( System.currentTimeMillis() ) ).getTime() / 60000;
      if(timeLeft < 0) {
        timeLeft = 0;
      } 
System.out.println("Creating old booking handler: " + bookedServer[0] + " " + bookedServer[1] + " " + bookedServer[2] + " " + bookedServer[3] + " " + String.valueOf(timeLeft) + " " + bookedServer[7] + " " + bookedServer[8] + " " + bookedServer[6]);
      Quake2BookingHandler quake2BookingHandler = new Quake2BookingHandler(this, bookedServer[0], bookedServer[1], bookedServer[2], bookedServer[3], String.valueOf(timeLeft), bookedServer[7], bookedServer[8], bookedServer[6], bookedServer[9], bookedServer[10], true);
      bookedServerList.add(quake2BookingHandler);   
      quake2BookingHandler.start();

    }
  }

  public void sendServerCommand(String cmd, String ip, String port) {
    this.main.sendServerCommand(cmd, ip, port);
  }
    
  public void killBookedServers(String ip,String port) {
    for(int x=0; x < bookedServerList.size(); x++) {
      Quake2BookingHandler tempQuake2BookingHandler = (Quake2BookingHandler) bookedServerList.get(x);
      if(tempQuake2BookingHandler.getIp().equals(ip) && tempQuake2BookingHandler.getPort().equals(port)) {
 
        tempQuake2BookingHandler.setDie();

        this.bookedServerList.remove(tempQuake2BookingHandler);     
	break;

      }
    }
  }
  public String rconCmd(String host, String port, String cmd) {
    return this.main.rconCmd(host, port, cmd);
  }      

}
