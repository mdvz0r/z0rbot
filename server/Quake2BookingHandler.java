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
import java.net.*;

public class Quake2BookingHandler extends Thread {

  BotControl botControl;
  String ip = "";
  String port = "";
  String rcon = "";
  String lrcon = "";
  String time = "";
  String bookerName = "";
  String bookerHostname = "";
  String bookerChannel = "";
  boolean isStart; // if this value is true, the server is just starting.

  boolean die = false;

  String[] onstrt;
  String[] onstop;


  public Quake2BookingHandler(BotControl botControl, String ip, String port, String rcon, String lrcon, String time, String bookerName, String bookerHostname, String bookerChannel, String onstrt, String onstop, boolean isStart) {

    this.botControl = botControl;
    this.ip = ip;
    this.port = port;
    this.rcon = rcon;
    this.lrcon = lrcon;
    this.time = time;
    this.bookerName = bookerName;
    this.bookerHostname = bookerHostname;
    this.bookerChannel = bookerChannel;
    this.isStart = isStart;
    this.onstrt = onstrt.split("\n");
    this.onstop = onstop.split("\n");
  }

  public void run() {

    try {


      if(!isStart) {
	for (int x = 0; x < onstrt.length; x++) {
	    botControl.sendServerCommand("rcon " + rcon + " " + onstrt[x], ip, port);
	}
        botControl.sendServerCommand("rcon " + rcon + " set lrcon_password " + lrcon, ip, port);
	Thread.sleep(2000);
        botControl.sendServerCommand("rcon " + rcon + " say This server has been booked by " + bookerName + " from " + bookerChannel + " for the next " + time + " minutes!", ip, port);
      }
      
      if(Integer.valueOf(time).intValue() > 5) {
        Thread.sleep((Integer.valueOf(time).intValue() - 5) * 60000);
        if(!die) {
          botControl.sendServerCommand("rcon " + rcon + " say This server has 5 more minutes of booking time remaining!", ip, port);
          Thread.sleep(300000);
        }
      }
      else {
        Thread.sleep(Integer.valueOf(time).intValue() * 60000);
      }
      if(!die) {
	this.onDie();
      }
    }
    catch (Exception e) {

      ExceptionHandler exceptionHandler = new ExceptionHandler(e);

    }

  }

  public void onDie() {
    try {
	BotHandler tempBotHandler = (BotHandler) this.botControl.getBotHandler(bookerChannel);
	if (tempBotHandler != null) tempBotHandler.sendMsg(bookerChannel, "[BOOKSERVER] Server booking of "+ ip + ":" + port + " ended.");
	if (!bookerChannel.equals("#z0r.nl.priv")) {
	    BotHandler tempBotHandler2 = (BotHandler) this.botControl.getBotHandler("#z0r.nl.priv");
	    if (tempBotHandler != null) tempBotHandler2.sendMsg("#z0r.nl.priv", "[BOOKSERVER] Server booking of "+ ip + ":" + port + " ended.");
	}

        botControl.sendServerCommand("rcon " + rcon + " say Server booking time is over, if you want to rebook use the z0rbot !bookserver command!", ip, port);

	Thread.sleep(2000);
	for (int x = 0; x < onstop.length; x++) {
	    botControl.sendServerCommand("rcon " + rcon + " " + onstop[x], ip, port);
	}

	botControl.killBookedServers(ip,port);
        botControl.setServerBooking(ip, port, rcon, 0, 0, "", "", "");
        botControl.sendServerCommand("rcon " + rcon + " set lrcon_password " + rcon, ip, port);
    }
    catch (Exception e) {
	System.out.println(e);
	ExceptionHandler exceptionHandler = new ExceptionHandler(e);
    }
  }

  public void setDie() {
    this.die = true;
  }

  public String getIp() {
    return ip;
  }

  public String getPort() {
    return port;
  }

  public String getRcon() {
    return rcon;
  }

  public String getBookerName() {
    return bookerName;
  }

  public String getBookerHostName() {
    return bookerName;
  }

  public String getBookerChannel() {
    return bookerChannel;
  }
}