/**
 * <p>Title: z0rbot IRC client</p>
 * <p>Description: z0rbot</p>
 * <p>Copyright: Copyright (c) 2004 - 2005</p>
 * <p>Company: z0r.nl</p>
 * @author mdvz0r & Elvis
 * @version 2.2
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.*;

public class Z0rControl implements Runnable {

  // is bot allowed to connect by z0r server
  private boolean allowConnect = false;
  private String serverVersion = "2.0";
  private boolean allowVersion = false;
  private boolean allowRun = true;
  public String ip  = "";
  public int port = 0;
  public String qname = "";
  public String qpass = "";
  public String owner = "";

  private IrcControl ircControl;

  public String nick = "";

  //All Variables which are used in more then 1 method
  protected BufferedReader input;
  protected PrintWriter output;
  public Main main;

  //Method Z0rControl (Constructor): This method is empty because we first need ircControl
  public Z0rControl(Main main) {
    this.main = main;
    this.main.updateThreads();
  }

  //Method connect: This method makes the connection to the Z0r.nl Server
  public void run() {
    // aanmaken irc verbinding
    ircControl = new IrcControl(this);
    ircControl.setVerbose(true);

    while (!connect() && this.allowRun) { try { Thread.sleep(100); } catch (Exception e) { } }
    System.out.println("STOPPED");
  }

  private boolean connect() {
    try {
      Socket clientSocket = new Socket("localhost", 9999);
      input = new BufferedReader(new InputStreamReader(clientSocket.
          getInputStream()));
      output = new PrintWriter(new OutputStreamWriter(clientSocket.
          getOutputStream()));
      sendVersion();
      while (allowRun) {
        String sentence = null;
        sentence = input.readLine();
        System.out.println(sentence);
        if (sentence == null) {
          this.allowConnect = false;
          this.allowVersion = false;
          return false;
        }
        else {
          // filter information received from the server
          filter(sentence);
        }
      }
      try {
        input.close();
        output.close();
        try {
          this.ircControl.disconnect();
          this.ircControl.dispose();
        } catch (Exception ez) { System.out.println("NO MORE BOTS ALLOWED TO CONNECT: " + ez); }
      } catch (Exception e) { System.out.println(e); }
      return false;
    }
    catch (Exception e) {
      // if we can't connect to the z0rserver, we wont connect to irc as well
      this.allowConnect = false;
      this.allowVersion = false;
      return false;
    }
  }

  //Method filter: This method filters the command and the content into seperate strings
  public void filter(String sentence) {
    // filter information on: timestamp, command and content
    String timestamp = "";
    String command = "";
    String content = "";
    if (sentence.indexOf('\"') != -1) {
      StringTokenizer st = new StringTokenizer(sentence, "\"");
      timestamp = timestamp + st.nextToken();
      command = command + st.nextToken();
      // if there is more then one command, put all in the string command, we will read this later
      while (st.hasMoreTokens()) {
        if (!content.equals("")) {
          content = content + "\"" + st.nextToken();
        } else {
          content = content + st.nextToken();
        }
      }
    }
    else {
      command = sentence;
    }
    // execute this function
    command(timestamp, command, content);
  }

  //Method Command: This method calls another method based on the command
  public void command(String timestamp, String command, String content) {
    if (command.equals("VERSION")) getVersion(content);
    if (command.equals("CONNECT")) this.allowConnect = true;
    if (command.equals("CHANNEL")) getChannel(content);
    if (command.equals("STATS")) getStats(timestamp, content);
    if (command.equals("SEARCH")) getSearch(timestamp, content);
    if (command.equals("AUTH")) this.allowRun = getAuth(content);
//    if (command.equals("QUIT")) getQuit(timestamp, content);
    if (command.equals("PART")) getPart(timestamp, content);
    if (command.equals("JOIN")) getJoin(timestamp, content);
    if (command.equals("MSG")) getMsg(timestamp, content);
    if (command.equals("MSGOPERATOR")) getMsgOperator(timestamp, content);
    if (command.equals("MSGOUTPUT")) getMsgoutput(timestamp, content);
    if (command.equals("EMPTYCW")) getEmptyCw(timestamp, content);
    if (command.equals("CHECK")) checkChannels();
  }

//Method getBotChannel: This method will ask the server for the channels the bot belongs to

  public void getBotChannel() {
    output.println("CHANNEL");
    output.flush();
  }

  public void sendVersion() {
    System.out.println("Requested to check VERSION");
    // send Version
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"VERSION\""+ this.serverVersion );
    output.flush();
  }

  public void getVersion(String content) {
    // version is ok, so enable irc connection, else update
    if (content.equals("OK")) {
      System.out.println("Version is OK");
      this.allowVersion = true;
      sendAuth();
    }
    else if (content.equals("ERROR")) {
      System.out.println("You have an incorrect version of the z0rbot. Please download a new version @ http://www.z0r.nl/");
      System.exit(0);
    }
  }

  // send an AUTH request
  public void sendAuth() {
    System.out.println("Requested to AUTH");
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    if (this.nick.equals("")) {
      output.println(currentTime.getTime() + "\"AUTH");
    } else {
      output.println(currentTime.getTime() + "\"AUTH\"" + this.nick);
    }
    output.flush();
  }

  public boolean getAuth(String content) {
    String nick = "";
    // get AUTH and CONNECT to IRC at once
    try {
      if (content.indexOf('\"') != -1) {
        StringTokenizer st = new StringTokenizer(content, "\"");
        String stat = st.nextToken();
        if (stat.equals("OK")) {
          System.out.println("AUTH is OK");
	  // if nick is already known, request to join channels
	  if (this.nick != "") {
	    sendChannel();
	  }
          nick = st.nextToken();
          this.nick = nick;
          // check if bot is not already running
          if (this.main.alreadyExists(nick) && !this.ircControl.isConnected()) {
            return false;
          }
          this.nick = nick;
          this.ircControl.setIrcNick(nick);
          this.ircControl.setIrcLogin(st.nextToken());
          this.ip = st.nextToken();
          this.port = Integer.parseInt(st.nextToken());
          if (st.hasMoreTokens()) {
            this.qname = st.nextToken();
          }
          if (st.hasMoreTokens()) {
            this.qpass = st.nextToken();
          }
          this.allowConnect = true;
        } else if (stat.equals("ERROR")) {
          String error = st.nextToken();
          if (error.equals("MANY")) { System.out.println("You have reached the maximum of connected bots"); return false; }
          else if (error.equals("NONE")) { System.out.println("You are not allowed to use the z0r.nl services"); System.exit(0); }
        }
      }
      if (this.allowConnect && this.allowVersion && !this.ircControl.isConnected()) {
        ircControl.conn();
      } else if (this.allowConnect && this.allowVersion && this.ircControl.isConnected()) {
      // sendChannel();
      }
      return true;
    }
    catch (Exception e) { return false; }
  }

  // send a CHANNEL request
  public void sendChannel() {
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"CHANNEL");
    output.flush();
  }

  // join the CHANNEL the server send
  public void getChannel(String content) {
    try {
      if (content.indexOf('\"') != -1) {
        StringTokenizer st = new StringTokenizer(content, "\"");
        String channel = st.nextToken();
        String mode = st.nextToken();
        this.ircControl.receiveJoin("", channel, mode);
      }
    }
    catch (Exception e) {}
  }

  // Methode sendStats: This method will send a STATS request to the server
  public void sendStats(String player, String channel) {
    disconnectedFromServer(channel);
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"STATS\"" + channel + "\"" + player);
    output.flush();
  }

  // Methode sendFind: This method will send a SEARCH request to the server
  public void sendSearch(String player, String channel) {
    disconnectedFromServer(channel);
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"SEARCH\"" + channel + "\"" + player);
    output.flush();
  }


  // send the received stats information to main.ircControl
  public void getStats(String timestamp, String content) {
      this.ircControl.receiveStatus(timestamp, content);
  }

  // send the received stats information to main.ircControl
  public void getSearch(String timestamp, String content) {
      this.ircControl.receiveSearch(timestamp, content);
  }

  public void sendReceiveDCC(String sourceNick, String sourceLogin, String sourceHostname, String filename, String address, int port, long size) {
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"FILE\"" + sourceNick + "\"" + sourceLogin + "\"" + sourceHostname + "\"" + filename + "\"" + address + "\"" + port + "\"" + size);
    output.flush();
  }

  public void sendQuit(String botNick, String hostname) {
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"QUIT\"" + botNick + "\"" + hostname);
    output.flush();
  }

  public void getQuit(String timestamp, String content) {
    this.ircControl.disconnect();
    System.exit(0);
  }

  public void sendPart(String channel, String botNick, String login, String hostname, String sender) {
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"PART\"" + botNick + "\"" + channel + "\"" + login + "\"" + hostname +  "\"" + sender);
    output.flush();
  }

  public void getPart(String timestamp, String content) {
    this.ircControl.receivePart(timestamp, content);
  }

  public void sendJoin(String channel, String botNick, String login, String hostname, String sender) {
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"JOIN\"" + botNick + "\"" + channel + "\"" + login + "\"" + hostname + "\"" + sender);
    output.flush();
  }

  public void sendBan(String nick, String host, String login, String hostname, String sender) {
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"BAN\"" + nick + "\"" + host + "\"" + login + "\"" + hostname + "\"" + sender);
    output.flush();
  }

  public void sendServerBan(String nick, String ip, String login, String hostname, String sender, String reason) {
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"SERVERBAN\"" + nick + "\"" + ip + "\"" + login + "\"" + hostname + "\"" + sender + "\"" + reason);
    output.flush();
  }

  public void sendAddException(String nick, String ip, String login, String hostname, String sender, String mask) {
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    if (mask.equals("")) output.println(currentTime.getTime() + "\"ADDEXCEPTION\"" + nick + "\"" + ip + "\"" + login + "\"" + hostname + "\"" + sender + "\"0");
    else output.println(currentTime.getTime() + "\"ADDEXCEPTION\"" + nick + "\"" + ip + "\"" + login + "\"" + hostname + "\"" + sender + "\"" + mask);
    output.flush();
  }

  public void sendServerUnban(String ip, String login, String hostname, String sender) {
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"SERVERUNBAN\"" + ip + "\"" + login + "\"" + hostname + "\"" + sender);
    output.flush();
  }

  public void sendRemoveException(String nick, String login, String hostname, String sender) {
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"REMOVEEXCEPTION\"" + nick + "\"" + login + "\"" + hostname + "\"" + sender);
    output.flush();
  }

  public void sendAddRequired(String nick, String ip, String login, String hostname, String sender, String mask) {
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    if (mask.equals("")) output.println(currentTime.getTime() + "\"ADDREQUIRED\"" + nick + "\"" + ip + "\"" + login + "\"" + hostname + "\"" + sender + "\"0");
    else output.println(currentTime.getTime() + "\"ADDREQUIRED\"" + nick + "\"" + ip + "\"" + login + "\"" + hostname + "\"" + sender + "\"" + mask);
    output.flush();
  }

  public void sendRemoveNickReservation(String nick, String login, String hostname, String sender) {
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"REMOVENICKRESERVATION\"" + nick + "\"" + login + "\"" + hostname + "\"" + sender);
    output.flush();
  }

  public void sendAddNickReservation(String nick, String password, String login, String hostname, String sender) {
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"ADDNICKRESERVATION\"" + nick + "\"" + password + "\"" + login + "\"" + hostname + "\"" + sender);
    output.flush();
  }

  public void sendRemoveRequired(String nick, String login, String hostname, String sender) {
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"REMOVEREQUIRED\"" + nick + "\"" + login + "\"" + hostname + "\"" + sender);
    output.flush();
  }

  public void sendRconCmd(String login, String hostname, String sender, String server, String rconcmd) {
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"RCON\"" + login + "\"" + hostname + "\"" + sender + "\"" + server + "\"" + rconcmd);
    output.flush();
  }


  public void getJoin(String timestamp, String content) {
    this.ircControl.receiveJoin(timestamp, content, "1");
  }

  public void sendGame(String channel, String game, String login, String hostname) {
    disconnectedFromServer(channel);
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"SETGAME\"" + channel + "\"" + game + "\"" + login + "\"" + hostname);
    output.flush();
  }

  public void sendGamedir(String channel, String gamedir, String login, String hostname) {
    disconnectedFromServer(channel);
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"SETGAMEDIR\"" + channel + "\"" + gamedir + "\"" + login + "\"" + hostname);
    output.flush();
  }

  public void sendMsg(String channel, String sender, String msg, String hostname) {
    disconnectedFromServer(channel);
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"MSG\"" + channel + "\"" + sender + "\"" + hostname + "\"" + msg);
    output.flush();
  }

  public void sendMsgAdmin(String channel, String sender, String msg, String hostname, String login) {
    disconnectedFromServer(channel);
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"MSGADMIN\"" + channel + "\"" + sender + "\"" + hostname + "\"" + login + "\"" + msg);
    output.flush();
  }


  public void sendCw(String channel, String sender, String players, String type, String additional, String hostname) {
    disconnectedFromServer(channel);
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    if (additional.equals("")) additional = "none";
    output.println(currentTime.getTime() + "\"CW\"" + channel + "\"" + sender + "\"" + hostname + "\"" + players + "\"" + type + "\"" + additional);
    output.flush();
  }

  public void bookServer(String channel, String sender, String server, int time, String hostname) {
    disconnectedFromServer(channel);
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"BOOKSERVER\"" + channel + "\"" + sender + "\"" + hostname + "\"" + server + "\"" + time);
    output.flush();
  }

  public void unbookServer(String channel, String sender, String hostname) {
    disconnectedFromServer(channel);
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"UNBOOKSERVER\"" + channel + "\"" + sender + "\"" + hostname);
    output.flush();
  }

  public void unbookServerAdm(String channel, String sender, String hostname, String host, String port) {
    disconnectedFromServer(channel);
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"UNBOOKSERVERADM\"" + channel + "\"" + sender + "\"" + hostname + "\"" + host + "\"" + port);
    output.flush();
  }


  public void getMsg(String timestamp, String content) {
    try {
      if (content.indexOf('\"') != -1) {
        StringTokenizer st = new StringTokenizer(content, "\"");
        String channel = st.nextToken();
        String msg = st.nextToken();
        this.ircControl.receiveMsg(channel, msg);
      }
    }
    catch (Exception e) {}
  }

  public void getMsgOperator(String timestamp, String content) {
    try {
      if (content.indexOf('\"') != -1) {
        StringTokenizer st = new StringTokenizer(content, "\"");
        String channel = st.nextToken();
        String msg = st.nextToken();
        this.ircControl.receiveMsgOperator(channel, msg);
      }
    }
    catch (Exception e) {}
  }


  public void sendMsgoutput(String channel, String hostname) {
    disconnectedFromServer(channel);
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"MSGOUTPUT\"" + channel);
    output.flush();
  }

  public void getMsgoutput(String timestamp, String content) {
    try {
      if (content.indexOf('\"') != -1) {
        StringTokenizer st = new StringTokenizer(content, "\"");
        String channel = st.nextToken();
        String msg = st.nextToken();
        this.ircControl.receiveMsgoutput(channel, msg);
      }
    }
    catch (Exception e) {}
  }

  public void sendMsgserver(String channel, String hostname) {
    disconnectedFromServer(channel);
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"MSGSERVER\"" + channel);
    output.flush();
  }

  public void getMsgserver(String timestamp, String content) {
    try {
      if (content.indexOf('\"') != -1) {
        StringTokenizer st = new StringTokenizer(content, "\"");
        String channel = st.nextToken();
        String msg = st.nextToken();
        this.ircControl.receiveMsgserver(channel, msg);
      }
    }
    catch (Exception e) {}
  }

  public void sendMsgList(String channel) {
    disconnectedFromServer(channel);
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"MSGLIST\"" + channel);
    output.flush();
  }

  public void sendCwList(String channel) {
    disconnectedFromServer(channel);
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"CWLIST\"" + channel);
    output.flush();
  }


  public void checkChannels() {
    this.ircControl.checkChannels();
  }

  public void sendEmptyCw(String channel) {
    disconnectedFromServer(channel);
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"EMPTYCW\"" + channel);
    output.flush();
  }

  public void getEmptyCw(String timestamp, String content) {
    try {
      if (content.indexOf('\"') != -1) {
        StringTokenizer st = new StringTokenizer(content, "\"");
        String channel = st.nextToken();
        String msg = st.nextToken();
        this.ircControl.printEmptyCw(channel, msg);
      }
    }
    catch (Exception e) {}
  }

  public void sendDisconnected() {
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"NOTCONNECTED");
    output.flush();
  }

  public void sendMsgUsage(String channel, String mode) {
    disconnectedFromServer(channel);
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"MSGUSAGE\"" + channel + "\"" + mode);
    output.flush();
  }

  public void sendCwlimit(String channel, String sender, String min, String max) {
    disconnectedFromServer(channel);
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"CWLIMIT\"" + channel + "\"" + sender + "\"" + min + "\"" + max);
    output.flush();
  }

  public void sendz0rstatus(String channel) {
    disconnectedFromServer(channel);
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"Z0RSTATUS\"" + channel);
    output.flush();
  }

  public void disconnectedFromServer(String channel) {
    if (!this.allowConnect || !this.allowVersion) {
      this.ircControl.printDisconnected(channel);
    }
  }

  public boolean allowConnect() {
    return this.allowConnect;
  }

  public boolean allowVersion() {
    return this.allowVersion;
  }

  public String getQname() {
    return this.qname;
  }

  public String getQpass() {
    return this.qpass;
  }

  public String getNick() {
    return this.nick;
  }

  public String getIp() {
    return this.ip;
  }

  public int getPort() {
    return this.port;
  }

  public boolean isConnected() {
    return this.ircControl.isConnected();
  }

  public boolean isAllowedRun() {
    return this.allowRun;
  }

  public void restart(String botname) {
    this.main.restart(botname);
  }

  public void restartNow() {
    this.ircControl.restart();
  }

  public void quit(String botname) {
    this.main.quit(botname);
  }

  public void quitNow(String botname) {
    this.allowRun = false;
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    output.println(currentTime.getTime() + "\"QUIT\"" + botname + "\"z0r.nl");
    output.flush();
  }

  public void startBots() {
    this.main.setStartBot();
  }

  public void fileControl(String channel, String[] lines) {
    main.fileControl(channel, lines);
  }

  public void getEmptyChannels(String login, String hostname, String sender) {
    main.getEmptyChannels(login, hostname, sender);
  }

  public void checkPeopleInChannel(String login, String hostname, String sender) {
    ircControl.checkPeopleInChannel(this.nick, login, hostname, sender);
  }

  public void finger(String user) {
    this.main.finger(user);
  }

  public void execFinger(String user) {
    this.ircControl.finger(user);
  }
}
