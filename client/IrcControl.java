/**
 * <p>Title: z0rbot IRC client</p>
 * <p>Description: z0rbot</p>
 * <p>Copyright: Copyright (c) 2004 - 2005</p>
 * <p>Company: z0r.nl</p>
 * @author mdvz0r
 * @version 2.2
 */

// All imports which are used in the whole class
import org.jibble.pircbot.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.util.regex.*;
import java.text.SimpleDateFormat;

public class IrcControl extends PircBot {
    // For logging //
    private static final Pattern urlPattern = Pattern.compile("(?i:\\b((http|https|ftp|irc)://[^\\s]+))");

    public static final String GREEN = "irc-green";
    public static final String BLACK = "irc-black";
    public static final String BROWN = "irc-brown";
    public static final String NAVY = "irc-navy";
    public static final String BRICK = "irc-brick";
    public static final String RED = "irc-red";
    private String setIrcNick = "";

    private File outDir;
    // end logging //

    private Z0rControl z0rControl;
    private ArrayList chanList = new ArrayList();

    public IrcControl(Z0rControl z0rControl) {
      try {
        this.setEncoding("iso-8859-1");
      } catch (Exception e) { System.out.println(e); }
      // for logging //
      this.setVerbose(true);
      this.outDir = new File("log/");
      outDir.mkdirs();
      if (!outDir.isDirectory()) {
        System.out.println("Cannot make output directory (" + outDir + ")");
        System.exit(1);
      }
     // end logging //

      this.z0rControl = z0rControl;

      this.setName("z"+((int)(10.0 * Math.random()) + 1)+((int)(10.0 * Math.random()) + 1)+((int)(10.0 * Math.random()) + 1)+((int)(10.0 * Math.random()) + 1)+((int)(10.0 * Math.random()) + 1)+((int)(10.0 * Math.random()) + 1)+((int)(10.0 * Math.random()) + 1)+((int)(10.0 * Math.random()) + 1));
      this.setVersion("visit our website: www.z0r.nl !");
      this.setLogin("-");
      this.setMessageDelay(1000);

      this.z0rControl.main.updateThreads();
    }

    // for logging //
    public void append(String color, String line) {
        line = Colors.removeFormattingAndColors(line);

        line = line.replaceAll("&", "&amp;");
        line = line.replaceAll("<", "&lt;");
        line = line.replaceAll(">", "&gt;");

        Matcher matcher = urlPattern.matcher(line);
        line = matcher.replaceAll("<a href=\"$1\">$1</a>");

        try {
            Date now = new Date();
            SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("H:mm");
            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
            String date = DATE_FORMAT.format(now);
            String time = TIME_FORMAT.format(now);
            File file = new File(outDir, date + "_" + this.setIrcNick + ".log");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            String entry = "<span class=\"irc-date\">[" + time + "]</span> <span class=\"" + color + "\">" + line + "</span><br />";
            writer.write(entry);
            writer.newLine();
            writer.flush();
            writer.close();
        }
        catch (IOException e) {
            System.out.println("Could not write to log: " + e);
        }
    }

    public void onAction(String sender, String login, String hostname, String target, String action) {
      append(BRICK, "* " + sender + " (" + login + "@" + hostname + ") " + action);
    }

    public void onJoin(String channel, String sender, String login, String hostname) {
        append(GREEN, "* " + sender + " (" + login + "@" + hostname + ") has joined " + channel);
    }

    public void onMode(String channel, String sourceNick, String sourceLogin, String sourceHostname, String mode) {
        append(GREEN, "* " + sourceNick + " (" + sourceLogin + "@" + sourceHostname + ") % " + channel + " sets mode " + mode);
    }

    public void onNickChange(String oldNick, String login, String hostname, String newNick) {
        append(GREEN, "* " + oldNick + " (" + login + "@" + hostname + ") is now known as " + newNick);
    }

    public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice) {
        append(BROWN, "-" + sourceNick + " (" + sourceLogin + "@" + sourceHostname + ")" + "- " + notice);
    }

    public void onPart(String channel, String sender, String login, String hostname) {
        append(GREEN, "* " + sender + " (" + login + "@" + hostname + ") has left " + channel);
    }

    public void onPing(String sourceNick, String sourceLogin, String sourceHostname, String target, String pingValue) {
        append(RED, "[" + sourceNick + " (" + sourceLogin + "@" + sourceHostname + ")" + " PING]");
    }

    public void onPrivateMessage(String sender, String login, String hostname, String message) {
      if (sender.equals(this.getNick()) && hostname.equals(z0rControl.getQname() + ".users.quakenet.org")) {
        this.setName(this.setIrcNick);
	this.sendRawLine("NICK " + this.setIrcNick);
	try { Thread.sleep(10000); } catch (Exception ez) {}
        this.joinChannel("#z0r.nl");
	this.z0rControl.sendChannel();
    } else if (sender.equals(this.getNick()) && !hostname.equals(z0rControl.getQname() + ".users.quakenet.org")) {
	if (z0rControl.getQname().equals("") && z0rControl.getQpass().equals("")) {
	    this.setName(this.setIrcNick);
	    this.sendRawLine("NICK " + this.setIrcNick);
	    try { Thread.sleep(10000); } catch (Exception ez) {}
	    this.joinChannel("#z0r.nl");
	    this.z0rControl.sendChannel();
	} else {
	    this.disconnect();
	    try { Thread.sleep(2000); } catch (Exception ez) {}
    	    this.conn();
	}
      }
         append(BLACK, "<- *" + sender + " (" + login + "@" + hostname + ") " + "* " + message);
    }

    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
        append(NAVY, "* " + sourceNick + " (" + sourceLogin + "@" + sourceHostname + ") Quit (" + reason + ")");
    }

    public void onTime(String sourceNick, String sourceLogin, String sourceHostname, String target) {
        append(RED, "[" + sourceNick + " (" + sourceLogin + "@" + sourceHostname + ") " + " TIME]");
    }

    public void onFinger(String sourceNick, String sourceLogin, String sourceHostname, String target) {
      append(RED, "[" + sourceNick + " (" + sourceLogin + "@" + sourceHostname + ") " + " FINGER]");
    }

    public void onVersion(String sourceNick, String sourceLogin, String sourceHostname, String target) {
        append(RED, "[" + sourceNick + " (" + sourceLogin + "@" + sourceHostname + ") " + " VERSION]");
    }

    public void onTopic(String channel, String topic, String setBy, long date, boolean changed) {
        if (changed) {
            append(GREEN, "* " + setBy + " % " + channel + " changes topic to '" + topic + "'");
        }
        else {
            append(GREEN, "* Topic is '" + topic + "'");
            append(GREEN, "* Set by " + setBy + " % " + channel + " on " + new Date(date));
        }
    }

    public void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
        append(GREEN, "* " + recipientNick + " was kicked from " + channel + " by " + kickerNick + " (" + kickerLogin + "@" + kickerHostname + ")");
        if (recipientNick.equalsIgnoreCase(getNick())) {
            joinChannel(channel);
        }
    }

    public static void copy(File source, File target) throws IOException {
      BufferedInputStream input = new BufferedInputStream(new FileInputStream(
          source));
      BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(
          target));
      int bytesRead = 0;
      byte[] buffer = new byte[1024];
      while ( (bytesRead = input.read(buffer, 0, buffer.length)) != -1) {
        output.write(buffer, 0, bytesRead);
      }
      output.flush();
      output.close();
      input.close();
    }
    // end logging //

    protected void onDisconnect() {
      try {
        //Try to connect again to the irc server
        this.conn();
      }
      catch (Exception e) {
        System.out.println(e);
      }
    }


    public void onIncomingFileTransfer(DccFileTransfer transfer) {
       System.out.println("Sending file from client to server");
//       z0rControl.sendReceiveDCC(transfer.getNick(), transfer.getLogin(), transfer.getHostname(), transfer.getFile().toString(), transfer.getAddress(), transfer.getPort(), transfer.getSize());
     }

     protected void onFileTransferFinished(DccFileTransfer transfer, Exception e) {
       System.out.println("file finished transfer");
     }

    /* check if a user is allowed to send */
     private boolean isAllowedToSend(String channel, String sender) {
       String ch = "2";
       for (int y = 0; y < this.chanList.size(); y++) {
         String st[] = (String []) this.chanList.get(y);
         if (st[0].toLowerCase().equals(channel.toLowerCase())) {
           ch = (String) st[1];
         }
       }
       User[] users = this.getUsers(channel);
       for (int x = 0; x < users.length; x++) {
         if (users[x].getNick().equals(sender) && users[x].isOp() && ch.equals("2")) {
           return true;
         }
         if (users[x].getNick().equals(sender) && (users[x].hasVoice() || users[x].isOp()) && ch.equals("1")) {
           return true;
         }
         if (users[x].getNick().equals(sender) && ch.equals("0")) {
           return true;
         }
       }
       if (ch.equals("2")) this.sendNotice(sender, "In " + channel + " only (@) Operators are allowed to use this function.");
       if (ch.equals("1")) this.sendNotice(sender, "In " + channel + " only (@) Operators and (+) Voiced users are allowed to use this function.");
       return false;
     }

    /* check if a users is an operator of a channel */
     private boolean isOps(String channel, String sender) {
       User[] users = this.getUsers(channel);
       for (int x = 0; x < users.length; x++) {
         if (users[x].getNick().equals(sender) && users[x].isOp()) {
           return true;
         }
       }
       this.sendNotice(sender, "In " + channel + " only (@) Operators are allowed to use this function.");
       return false;
     }

    /* receive a ** message from irc, parse it and execute the appropriate function */
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
      append(BLACK, "<" + sender + " (" + login + "@" + hostname + ") % " + channel + "> " + message);
      for (int z = 0; z < this.chanList.size(); z++) {
        String st[] = (String[])this.chanList.get(z);
        if (st[0].toLowerCase().equals(channel.toLowerCase())) {
          // search on command
          String[] msg = message.split(" ");

          // get rest of msg and put it in msgz
          String msgz = "";
          if (msg.length > 1) {
            msgz = msg[1];
            for (int x = 2; x < msg.length; x++) {
              msgz = msgz + " " + msg[x];
            }
            msgz = msgz.trim();
          }

          if (msg[0].equals("!help")) {
            this.sendNotice(sender,
                            "Check http://www.z0r.nl/index.php?link=z0rbot");
          }

          if (msg[0].equals("!restart")) {
            if (( login.equals("mdv") || login.equals("migiel") ) && hostname.equals("z0r.nl")) {
              this.z0rControl.restart(msg[1]);
            }
          }

          if (msg[0].equals("!getEmptyChannels")) {
            if (( login.equals("mdv") || login.equals("migiel") ) && hostname.equals("z0r.nl")) {
              this.z0rControl.getEmptyChannels(login, hostname, sender);
            }
          }

          if (msg[0].equals("!finger")) {
            if (( login.equals("mdv") || login.equals("migiel") ) && hostname.equals("z0r.nl")) {
              this.z0rControl.finger(msg[1]);
            }
          }

          // on stats, request stats
          if (msg[0].equalsIgnoreCase("!stats")) {
            if (isAllowedToSend(channel, sender)) {
              onStats(msgz, channel);
            }
          }
          // on search, request search
          if (msg[0].equalsIgnoreCase("!search")) {
            if (isAllowedToSend(channel, sender)) {
              onSearch(msgz, channel);
            }
          }
          if (msg[0].equalsIgnoreCase("!find")) {
            if (isAllowedToSend(channel, sender)) {
              onSearch(msgz, channel);
            }
          }
          // on quit, request disconnect
          if (msg[0].equalsIgnoreCase("!quit")) {
            if (( login.equals("mdv") || login.equals("migiel") ) && hostname.equals("z0r.nl")) {
              this.z0rControl.quit(msg[1]);
            }
          }
          if (msg[0].equalsIgnoreCase("!startbots")) {
            if (( login.equals("mdv") || login.equals("migiel") ) && hostname.equals("z0r.nl")) {
              this.z0rControl.startBots();
            }
          }
          // on part, request part
          if (msg[0].equalsIgnoreCase("!part")) {
            if (msg.length == 3) {
              onPartMsg(msg[1], msg[2], login, hostname, sender);
            } else if (msg.length == 2) {
              onPartMsg(msg[1], "", login, hostname, sender);
            }
          }
          // on join, request join
          if (msg[0].equalsIgnoreCase("!join")) {
            if (msg.length == 3) {
              onJoinMsg(msg[1], msg[2], login, hostname, sender);
            } else if (msg.length == 2) {
              onJoinMsg(msg[1], "", login, hostname, sender);
            }
          }
          // on ban, ban him
          if (msg[0].equalsIgnoreCase("!ban")) {
            if (msg.length == 3) {
              onBanMsg(msg[1], msg[2], login, hostname, sender);
            }
          }

          if (msg[0].equalsIgnoreCase("!serverban")) {
            if (msg.length >= 3) {
                String additional = "";
    		for (int x = 3; x < msg.length; x++) {
            	    additional = additional + msg[x] + " ";
                }
                additional = additional.trim();

              onServerBanMsg(msg[1], msg[2], login, hostname, sender, additional);
            } else this.sendNotice(sender, "usage: !serverban <nick> <ip> <reason optional>");
          }

          if (msg[0].equalsIgnoreCase("!serverunban")) {
            if (msg.length == 2) {
              onServerUnbanMsg(msg[1], login, hostname, sender);
            } else this.sendNotice(sender, "usage: !serverunban <ip>");
          }

          if (msg[0].equalsIgnoreCase("!addexception")) {
            if (msg.length == 3) {
              onAddExceptionMsg(msg[1], msg[2], login, hostname, sender, "");
            } else if (msg.length == 4) {
              onAddExceptionMsg(msg[1], msg[2], login, hostname, sender, msg[3]);
	    } else this.sendNotice(sender, "usage: !addexception <nick> <ip> <mask optional>");
          }

          if (msg[0].equalsIgnoreCase("!removeexception")) {
            if (msg.length == 2) {
              onRemoveExceptionMsg(msg[1], login, hostname, sender);
            } else this.sendNotice(sender, "usage: !removeexception <name>");
          }

          if (msg[0].equalsIgnoreCase("!addrequired")) {
            if (msg.length == 3) {
              onAddRequiredMsg(msg[1], msg[2], login, hostname, sender, "");
            } else if (msg.length == 4) {
              onAddRequiredMsg(msg[1], msg[2], login, hostname, sender, msg[3]);
	    } else this.sendNotice(sender, "usage: !addrequired <nick> <ip> <mask optional>");
          }

          if (msg[0].equalsIgnoreCase("!removerequired")) {
            if (msg.length == 2) {
              onRemoveRequiredMsg(msg[1], login, hostname, sender);
            } else this.sendNotice(sender, "usage: !removerequired <name>");
          }

          if (msg[0].equalsIgnoreCase("!removenickreservation")) {
            if (msg.length == 2) {
              onRemoveNickReservationMsg(msg[1], login, hostname, sender);
            } else this.sendNotice(sender, "usage: !removenickreservation <nick>");
          }

          if (msg[0].equalsIgnoreCase("!addnickreservation")) {
            if (msg.length == 3) {
              onAddNickReservationMsg(msg[1], msg[2], login, hostname, sender);
            } else this.sendNotice(sender, "usage: !addnickreservation <nick> <password>");
          }

	  if (msg[0].equalsIgnoreCase("!rcon")) {
	    if (msg.length >= 3) {
	      String additional = "";
              for (int x = 2; x < msg.length; x++) {
                additional = additional + msg[x] + " ";
              }
              additional = additional.trim();
	      onRcon(login, hostname, sender, msg[1], additional);
	    } else this.sendNotice(sender, "usage: !rcon <host:port> <command>");
	  }

          if (msg[0].equalsIgnoreCase("!setgame")) {
            onSetGame(channel, msgz, login, hostname);
          }
          if (msg[0].equalsIgnoreCase("!setgamedir")) {
            onSetGamedir(channel, msgz, login, hostname);
          }
          if (msg[0].equals("!msg")) {
            if (isOps(channel, sender)) {
              if (!msgz.equals("")) {
                onSendMsg(channel, sender, msgz, hostname);
              }
              else this.sendNotice(sender, "usage: !msg <message>");
            }
          }
          if (msg[0].equals("!msgadmin")) {
            if (isOps(channel, sender)) {
              if (!msgz.equals("")) {
                onSendMsgAdmin(channel, sender, msgz, hostname, login);
              }
              else this.sendNotice(sender, "usage: !msgadmin <message>");
            }
          }
          if (msg[0].equals("!cw")) {
            if (isOps(channel, sender)) {
              if (!msgz.equals("")) {
                String additional = "";
                if (msg.length >= 3) {
                  for (int x = 3; x < msg.length; x++) {
                    additional = additional + msg[x] + " ";
                  }
                  additional = additional.trim();
                }
                onSendCw(channel, sender, msg[1], msg[2], additional, hostname);
              }
              else this.sendNotice(sender, "usage: !cw <players> <[off]icial/[u]n[o]fficial>, Example: !cw 4 off got server - !cw 4 uo got server");
            }
          }
          if (msg[0].equals("!bookserver")) {
	    if (isOps(channel, sender)) {
	      if (!msgz.equals("") && msg.length == 3) {
		onBookServer(channel, sender, msg[1], msg[2], hostname);
	      }
	      else this.sendNotice(sender, "usage: !bookserver <part of hostname or server name> <time in minutes>, Example: !bookserver z0r 45");
	    }
	  }
          if (msg[0].equals("!unbookserver")) {
	    if (isOps(channel, sender)) {
	      z0rControl.unbookServer(channel, sender, hostname);
	    }
	  }
          if (msg[0].equals("!unbookserveradm")) {
	    if (!msgz.equals("") && msg.length == 3) {
	      z0rControl.unbookServerAdm(channel, login, hostname, msg[1], msg[2]);
	    }
	    else {
	      this.sendNotice(sender, "usage: !unbookserveradm <ip or host> <port>");
	    }
	  }
          if (msg[0].equals("!cwlimit")) {
            if (isOps(channel, sender)) {
              if (!msgz.equals("")) {
                String additional = "";
                onSendCwlimit(channel, sender, msg[1], msg[2]);
              }
              else this.sendNotice(sender, "usage: !cw <players> <[off]icial/[u]n[o]fficial>, Example: !cw 4 off got server - !cw 4 uo got server");
            }
          }

          if (msg[0].equals("!msgoutput")) {
            if (isOps(channel, sender)) {
              onSendMsgoutput(channel, hostname);
            }
          }

          if (msg[0].equals("!msgnetwork")) {
            if (isOps(channel, sender)) {
              onSendMsgserver(channel, hostname);
            }
          }
          if (msg[0].equals("!msglist")) {
            if (isOps(channel, sender)) {
              onMsglist(channel);
            }
          }
          if (msg[0].equals("!cwlist")) {
            if (isOps(channel, sender)) {
              onCwlist(channel);
            }
          }
          if (msg[0].equals("!emptycw")) {
            if (isOps(channel, sender)) {
              onEmptyCw(channel);
            }
          }
          if (msg[0].equals("!msgusage")) {
            if (isOps(channel, sender)) {
              if (msgz.equals("all") || msgz.equals("op") || msgz.equals("voice")) {
                onMsgUsage(channel, msgz);
              }
              else this.sendNotice(sender,
                                   "use: !msgusage all , !msgusage voice, !msgusage op");
            }
          }
          if (msg[0].equals("!z0rstatus")) {
            if (isOps(channel, sender)) {
              onz0rstatus(channel);
            }
          }
        }
      }
    }

    /* receive a msgusage message from irc, update information in the z0r server */
    private void onMsgUsage(String channel, String msg) {
      if (msg.equals("all")) {
        z0rControl.sendMsgUsage(channel, "0");
        this.setIsAllowedToSend(channel, "0");
        this.sendMessage(channel, "Everyone in " + channel + " can use z0rbot stats and search now!");
      }
      if (msg.equals("voice")) {
        z0rControl.sendMsgUsage(channel, "1");
        this.setIsAllowedToSend(channel, "1");
        this.sendMessage(channel, "Only users with voice and operator status in " + channel + " can use z0rbot stats and search now!");
      }
      if (msg.equals("op")) {
        z0rControl.sendMsgUsage(channel, "2");
        this.setIsAllowedToSend(channel, "2");
        this.sendMessage(channel, "Only users with operator status in " + channel + " can use z0rbot stats and search now!");
      }
    }

    /* receive a z0rstatus message from irc, update information in the z0r server */
    private void onz0rstatus(String channel) {
      z0rControl.sendz0rstatus(channel);
    }

    /* receive a emptycw message from irc, update information in the z0r server */
    private void onEmptyCw(String channel) {
      z0rControl.sendEmptyCw(channel);
    }

    /* receive a msglist message from irc, update information in the z0r server */
    private void onMsglist(String channel) {
      z0rControl.sendMsgList(channel);
    }

    /* receive a cwlist message from irc, update information in the z0r server */
    private void onCwlist(String channel) {
      z0rControl.sendCwList(channel);
    }

    /* receive a sendmsgserver message from irc, update information in the z0r server */
    private void onSendMsgserver(String channel, String hostname) {
      z0rControl.sendMsgserver(channel, hostname);
    }

    /* receive a msgoutput message from irc, update information in the z0r server */
    private void onSendMsgoutput(String channel, String hostname) {
      z0rControl.sendMsgoutput(channel, hostname);
    }

    /* receive a sendmsg message from irc, update information in the z0r server */
    private void onSendMsg(String channel, String sender,  String msg, String hostname) {
      char ch1 = "\"".charAt(0);
      char ch2 = "'".charAt(0);
      msg = msg.replace(ch1, ch2);
      if (!msg.equals("")) {
        z0rControl.sendMsg(channel, sender, msg, hostname);
      }
    }

    /* receive a sendmsg message from irc, update information in the z0r server */
    private void onSendMsgAdmin(String channel, String sender,  String msg, String hostname, String login) {
      char ch1 = "\"".charAt(0);
      char ch2 = "'".charAt(0);
      msg = msg.replace(ch1, ch2);
      if (!msg.equals("")) {
        z0rControl.sendMsgAdmin(channel, sender, msg, hostname, login);
      }
    }

    private void onBookServer(String channel, String sender, String server, String time, String hostname) {
      try {
	char ch1 = "\"".charAt(0);
	char ch2 = "'".charAt(0);
	server = server.replace(ch1, ch2);
        int plr = 8;
        int tim = Integer.parseInt(time);
	if (plr < 2) {
	  this.sendNotice(sender, "minimal amount of players is 2");
	} else {
	    if (tim < 10) {
	      this.sendNotice(sender, "minimal time is 10 minutes");
	    } else {
	        if (tim > 90) {
		  this.sendNotice(sender, "maximal time is 90 minutes");
		} else {
		  if (plr > 16) {
		    this.sendNotice(sender, "maximal amount of players is 16");
		  } else {
		    z0rControl.bookServer(channel, sender, server, tim, hostname);
		  }
		}
	    }
	}
      } catch (Exception e) {
	this.sendNotice(sender, "usage: !bookserver <part of hostname or server name> <time in minutes>, Example: !bookserver z0r 45");
      }
    }

    /* receive a send cw message from irc, update information in the z0r server */
    private void onSendCw(String channel, String sender,  String players, String type, String additional, String hostname) {
      char ch1 = "\"".charAt(0);
      char ch2 = "'".charAt(0);
      additional = additional.replace(ch1, ch2);
      try {
        int tmp = Integer.parseInt(players);
        if (tmp == 0) {
          this.sendNotice(sender, "ehm 0 vs 0 ? :p");
        } else {
          if (tmp >= 10) {
            this.sendNotice(sender, "9 vs 9 is the maximum");
          } else {
            if (type.toLowerCase().equals("uo") ||
                type.toLowerCase().equals("off")) {
              z0rControl.sendCw(channel, sender, players, type, additional, hostname);
            }
            else {
              this.sendNotice(sender, "usage: !cw <players> <[off]icial/[u]n[o]fficial>, Example: !cw 4 off got server - !cw 4 uo got server");
            }
          }
        }
      } catch (Exception e) {
        this.sendNotice(sender, "usage: !cw <players> <[off]icial/[u]n[o]fficial>, Example: !cw 4 off got server - !cw 4 uo got server");
      }
    }

    /* receive a send cw limit message from irc, update information in the z0r server */
    private void onSendCwlimit(String channel, String sender,  String min, String max) {
      try {
        int mintmp = Integer.parseInt(min);
        int maxtmp = Integer.parseInt(max);
        if (mintmp >= 1 && maxtmp <= 9) {
          if (mintmp <= maxtmp) {
            z0rControl.sendCwlimit(channel, sender, min, max);
            if (mintmp == maxtmp) {
              this.sendMessage(channel, "This channel will now only receive CW messages for " + min + " players!");
            } else {
              this.sendMessage(channel, "This channel will now only receive CW messages for " + min + " to " + max + " players!");
            }
          } else {
            this.sendNotice(sender, "please swap values");
          }
        } else {
          this.sendNotice(sender, "minimum value for min and max = 1, maximum value for min and max= 9");
        }
      } catch (Exception e) {
        this.sendNotice(sender, "usage: !cwlimit <min> <max>, Example: !cwlimit 3 5 (this will only show 3on3, 4on4 and 5on5 cw's");
      }
    }

    /* receive a ban message from irc, update information in the z0r server */
    private void onBanMsg(String nick, String host, String login, String hostname, String sender) {
      z0rControl.sendBan(nick, host, login, hostname, sender);
    }

    private void onServerBanMsg(String nick, String ip, String login, String hostname, String sender, String reason) {
      z0rControl.sendServerBan(nick, ip, login, hostname, sender, reason);
    }

    private void onAddExceptionMsg(String nick, String ip, String login, String hostname, String sender, String mask) {
      z0rControl.sendAddException(nick, ip, login, hostname, sender, mask);
    }

    private void onServerUnbanMsg(String ip, String login, String hostname, String sender) {
      z0rControl.sendServerUnban(ip, login, hostname, sender);
    }

    private void onRemoveExceptionMsg(String nick, String login, String hostname, String sender) {
      z0rControl.sendRemoveException(nick, login, hostname, sender);
    }

    private void onAddRequiredMsg(String nick, String ip, String login, String hostname, String sender, String mask) {
      z0rControl.sendAddRequired(nick, ip, login, hostname, sender, mask);
    }

    private void onRemoveRequiredMsg(String nick, String login, String hostname, String sender) {
      z0rControl.sendRemoveRequired(nick, login, hostname, sender);
    }

    private void onRemoveNickReservationMsg(String nick, String login, String hostname, String sender) {
      z0rControl.sendRemoveNickReservation(nick, login, hostname, sender);
    }

    private void onAddNickReservationMsg(String nick, String password, String login, String hostname, String sender) {
      z0rControl.sendAddNickReservation(nick, password, login, hostname, sender);
    }

    private void onRcon(String login, String hostname, String sender, String server, String rconcmd) {
      z0rControl.sendRconCmd(login, hostname, sender, server, rconcmd);
    }

    /* receive a join message from irc, update information in the z0r server */
    private void onJoinMsg(String chan, String bot, String login, String hostname, String sender) {
      z0rControl.sendJoin(chan, bot, login, hostname, sender);
    }

    /* receive a game message from irc, update information in the z0r server */
    private void onSetGame(String chan, String game, String login, String hostname) {
      z0rControl.sendGame(chan, game, login, hostname);
    }

    /* receive a gamedir message from irc, update information in the z0r server */
    private void onSetGamedir(String chan, String gamedir, String login, String hostname) {
      z0rControl.sendGamedir(chan, gamedir, login, hostname);
    }

    /* receive a part message from irc, update information in the z0r server */
    private void onPartMsg(String chan, String bot, String login, String hostname, String sender) {
      z0rControl.sendPart(chan, bot, login, hostname, sender);
    }

    /* receive a stats from irc and send it to the z0r server */
    private void onStats(String msgz, String channel) {
      // is msg length more then 2 chars ?
      if (msgz.length() >= 2) {
        try {
          // verstuur commando naar z0rControl
          char ch1 = "\"".charAt(0);
          char ch2 = "'".charAt(0);
          msgz = msgz.replace(ch1, ch2);
          z0rControl.sendStats(msgz.toLowerCase(), channel);
        }
        catch (Exception ex) {
          System.out.println("An exception has been intercepted");
          ex.printStackTrace();
        }
      }
      else if (msgz.length() > 0) {
        sendMessage(channel, "Too many records were returned.");
      }
    }

    /* receive a search from irc and send it to the z0r server */
    private void onSearch(String msgz, String channel) {
      // is msg length more then 2 chars ?
      if (msgz.length() >= 2) {
        try {
          // verstuur commando naar z0rControl
          char ch1 = "\"".charAt(0);
          char ch2 = "'".charAt(0);
          msgz = msgz.replace(ch1, ch2);
          z0rControl.sendSearch(msgz.toLowerCase(), channel);
        }
        catch (Exception ex) {
          System.out.println("An exception has been intercepted");
          ex.printStackTrace();
        }
      }
      else if (msgz.length() > 0) {
        sendMessage(channel, "Too many records were returned.");
      }
    }

// Function makeLength: adds spaces to a String (str). at the left (abc = true) or right (abc = false) to get the specified length (lengte)
    public String makeLength(String str, int lengte, boolean abc) {
      // when length isnt reached yet.
        if (str.length() < lengte) {
          // if abc = true, add spaces at the right
            if (abc) {
                    for (int x = str.length(); x < lengte; x++) {
                    str = str + " ";
                }
            }
            // if abc = false, add spaces at the left
            else {
                for (int x = str.length(); x < lengte; x++) {
                    str = " " + str;
                }
            }
        }
        return str;
    }

    // value which keeps if there's already a header send
    private String currentReceiveStatus = "";

    /* receive a stats from z0r server and print it to irc */
    public void receiveStatus (String timestamp, String info) {
      // check if the info string is real
      if (info.indexOf('\"') != -1) {
        String statusMessage = "", channel = "", plr = "", fpm = "", frags = "", mins = "", wr = "", week = "", year = "";
        StringTokenizer st = new StringTokenizer(info, "\"");

        if (st.hasMoreTokens()) statusMessage = st.nextToken();
        if (st.hasMoreTokens()) channel = st.nextToken();
        if (st.hasMoreTokens()) plr = st.nextToken();
        if (st.hasMoreTokens()) frags = st.nextToken();
        if (st.hasMoreTokens()) mins = st.nextToken();
        if (st.hasMoreTokens()) fpm = st.nextToken();
        if (st.hasMoreTokens()) wr = st.nextToken();
        if (st.hasMoreTokens()) year = st.nextToken();
        if (st.hasMoreTokens()) week = st.nextToken();
        // if status is OK:
        if (statusMessage.equals("OK") && !plr.equals("") && !fpm.equals("") && !frags.equals("") && !mins.equals("") && !wr.equals("") && !channel.equals("")) {
          // print stats information
          sendMessage(channel, "[STATS] ["+ year +"/"+ week + "] " + makeLength(plr, 15, true) + " => Mins : "+ makeLength(mins, 4, false) +" , Frags: "+ makeLength(frags, 4, false) +" , FPM: "+ makeLength(fpm, 8, false) +" , Weighted Rank: "+ makeLength(wr, 4, false) +"");
        }
        // if status is ERROR:
        else if (statusMessage.equals("ERROR")) {
          if (plr.equals("MANY")) {
            sendMessage(channel, "Too many records were returned, printing 5 players with highest amount of minutes.");
          } else if (plr.equals("NONE")) {
            sendMessage(channel,"Nothing found");
          }
        }
      }
    }

    /* receive a search from the z0r server and print it to irc */
    public void receiveSearch (String timestamp, String info) {
      // check if the info string is real
      if (info.indexOf('\"') != -1) {
        String statusMessage = "", channel = "", servername = "", serverip = "", playername = "";
        StringTokenizer st = new StringTokenizer(info, "\"");

        if (st.hasMoreTokens()) statusMessage = st.nextToken();
        if (st.hasMoreTokens()) channel = st.nextToken();
        if (st.hasMoreTokens()) servername = st.nextToken();
        if (st.hasMoreTokens()) serverip = st.nextToken();
        if (st.hasMoreTokens()) playername = st.nextToken();
        // if status is OK:
        if (statusMessage.equals("OK") && !servername.equals("") && !serverip.equals("") &&  !playername.equals("")) {
          // print stats information
          sendMessage(channel, "[SEARCH] " + makeLength(servername, 32, true) + " " + makeLength("(" + serverip + ")",23, false) + " => " + makeLength(playername, 15, true));
        }
        // if status is ERROR:
        else if (statusMessage.equals("ERROR")) {
          if (servername.equals("MANY")) {
            sendMessage(channel, "Too many records were returned.");
          } else if (servername.equals("NONE")) {
            sendMessage(channel,"Nothing found");
          }
        }
      }
    }

    /* receive a part from the z0r server and part a channel */
    public void receivePart(String timestamp, String info) {
      this.partChannel(info);
      for (int x = 0; x < this.chanList.size(); x++) {
        String[] st = (String[]) this.chanList.get(x);
        if (st[0].equals(info)) {
          this.chanList.remove(x);
        }
      }
    }

    /* receive a join from the z0r server and join a channel on irc */
    public void receiveJoin(String timestamp, String channel, String mode) {
      this.joinChannel(channel);
      boolean allowJoin = true;
      for (int x = 0; x < this.chanList.size(); x++) {
        String[] st = (String[]) this.chanList.get(x);
        if ((st[0].toLowerCase()).equals(channel.toLowerCase())) {
          allowJoin = false; // already exists
        }
      }
      if (allowJoin) {
        String[] st = new String[2];
        st[0] = channel;
        st[1] = mode;
        this.chanList.add(st);
      }
    }

    /* receive a message and send it to irc */
    public void receiveMsg(String channel, String info) {
      this.sendMessage(channel, info);
    }

    /* send a message to a z0rbot operator */
    public void receiveMsgOperator(String user, String info) {
      this.sendNotice(user, info);
    }

    /* on a broadcast message print it to irc */
    public void receiveMsgoutput(String channel, String info) {
      this.sendMessage(channel, info);
    }
    /* receive a message from the z0r server and print it to irc */
    public void receiveMsgserver(String channel, String info) {
      this.sendMessage(channel, info);
    }

    /* set the irc nick name */
    public void setIrcNick(String nick) {
      this.setIrcNick = nick;
    }

    /* set the irclogin */
    public void setIrcLogin(String login) {
//      this.setLogin(login);
	this.setLogin("z0rbot");
    }

    /* check current channels for this bot, if one of the channels is missing, try to join it */
    public void checkChannels() {
      if (!this.isConnected()) {
       z0rControl.sendDisconnected();
       this.conn();
      } else {
        this.joinChannel("#z0r.nl");
	this.setName(this.setIrcNick);
        String[] chan1 = this.getChannels();
        ArrayList chan2 = this.chanList;
        for (int x = 0; x < chan2.size(); x++) {
          String st[] = (String[]) chan2.get(x);
          boolean joinChannel = true;
          for (int y = 0; y < chan1.length; y++) {
            if ( (st[0].toLowerCase()).equals(chan1[y].toLowerCase()) ) {
              joinChannel = false;
            }
          }
          if (joinChannel) {
            this.joinChannel(st[0]);
            this.sendMessage("#z0r.nl.priv", "Trying to rejoin: " + st[0]);
          }
        }
      }
    }

    /* print all empty clanwar servers, only used by quake2 at this moment */
    public void printEmptyCw(String channel, String msg) {
      this.sendMessage(channel, msg);
    }

    /* change what users in a channel are allowed to send */
    public void setIsAllowedToSend(String channel, String mode) {
      for (int x = 0; x < this.chanList.size(); x++) {
        String st[] = (String[]) this.chanList.get(x);
        if (st[0].toLowerCase().equals(channel.toLowerCase())) {
          st[1] = mode;
          this.chanList.set(x, st);
        }
      }
    }

    /* if this client is disconnected from the z0r server, but still connected
       to irc, print an error message if someone does a request */
    public void printDisconnected(String channel) {
      int character;
      InputStream bin;
      String print = "";
      try {
        URL url = new URL("http://www.z0r.nl/update.txt");
        bin = (InputStream)url.getContent();
        while ((character = bin.read()) > 0) print += (char)character;
        this.sendMessage(channel, print);
      } catch (IOException e) {
        this.sendMessage(channel, "z0rbot is currently disconnected from the z0r.nl server. Please contact an operator in #z0r.nl");
      }
    }

    /* call the restart function of this bot, disconnect and reconnect again */
    public void restart() {
      this.disconnect();
      // [21~onDisconnect handles the rest
    }

    /* connect or reconnect to an irc server */
    public void conn() {
      this.setName("z"+((int)(10.0 * Math.random()) + 1)+((int)(10.0 * Math.random()) + 1)+((int)(10.0 * Math.random()) + 1)+((int)(10.0 * Math.random()) + 1)+((int)(10.0 * Math.random()) + 1)+((int)(10.0 * Math.random()) + 1)+((int)(10.0 * Math.random()) + 1)+((int)(10.0 * Math.random()) + 1));
      try {
        Thread.sleep( 60000 - ( ( ( System.currentTimeMillis() /1000 ) %60 ) * 1000 ) );
      } catch (Exception e) { System.out.println(e); }
      if (z0rControl.allowConnect() && z0rControl.allowVersion()) {
	System.out.println("CONNECT TO IRC BY: " + this.setIrcNick);
        //this.startIdentServer();
        try { this.connect(z0rControl.getIp(), z0rControl.getPort()); this.reconnect(); }
        catch (Exception e) { System.out.println("FUCKED"); }
        this.sendMessage("Q@CServe.quakenet.org", "auth " + z0rControl.getQname() + " " + z0rControl.getQpass());
        this.sendRawLineViaQueue("mode "+ this.getNick()+ " +x");
        try { Thread.sleep(15000); } catch (Exception ez) {}
        this.sendMessage(this.getNick(), "what is my host?");
      }
    }

    /* get all users in a channel and return them in an string[] */
    public String[] getAllUsers(String channel) {
      User[] u = this.getUsers(channel);
      String[] users = new String[ (u.length - 1)];
      int y = 0;
      for (int x = 0; x < u.length; x++) {
        if (u[x].toString().indexOf(z0rControl.getNick()) == -1) {
          users[y] = u[x].toString();
          y++;
        }
      }
      return users;
    }

    /* check how many persons are in this channel
       this way we can check if this channel is still
       in use */
    public void checkPeopleInChannel(String bot, String login, String hostname, String sender) {
      for (int x = 0; x < this.chanList.size(); x++) {
        String st[] = (String []) this.chanList.get(x);
        String[] users = this.getAllUsers(st[0].toLowerCase());
        z0rControl.fileControl(st[0], users);

        if (users.length > 5) {
//          this.sendMessage("#z0r.nl.priv", "Found " + users.length + " users in " + st[0]);
        } else {
	    if (users.length < 3) {
		this.sendMessage("#z0r.nl.priv", "Found " + users.length + " users in " + st[0] + " (channel removed)");
		this.onPartMsg(st[0], bot, login, hostname, sender);
	    }    else {
        	this.sendMessage("#z0r.nl.priv", "Found " + users.length + " users in " + st[0] + "");
	    }
        }
      }
    }

    /* finger a user, to spoof information */
    public void finger(String user) {
      this.finger(user);
    }
}
