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
import java.net.*;

public class Quake3ServerHandler {

  public ArrayList getData(String ip, String port) {
    ArrayList data = new ArrayList();
    int online = 0;

    try {
      InetAddress inetAddress = InetAddress.getByName(ip);
      DatagramSocket datagramSocket = new DatagramSocket();
      datagramSocket.setSoTimeout(1000);

      String command = "xxxxgetstatus\0";
      byte[] buffer = command.getBytes();
      byte oob = (byte) 0xff;

      for (int x = 0; x < 4; x++) {
        buffer[x] = oob;
      }

      DatagramPacket output = new DatagramPacket(buffer, buffer.length,
                                                 inetAddress,
                                                 Integer.parseInt(port));
      buffer = new byte[65507];
      DatagramPacket input = new DatagramPacket(buffer, buffer.length);

      datagramSocket.send(output);

      try {
        datagramSocket.receive(input);
        online = 1;
      }
      catch (Exception e) {
        datagramSocket.send(output);
        try {
          datagramSocket.receive(input);
          online = 1;
        }
        catch (Exception ee) {
          online = 0;
        }
      }

      if (online == 1) {
        String message = new String(input.getData(), 0, 0, input.getLength());
        StringTokenizer stringTokenizer = new StringTokenizer(message, "\r\n");

        ArrayList serverSettingList = new ArrayList();

        try {
          if (stringTokenizer.hasMoreTokens()) {
            stringTokenizer.nextToken(" \n");
            StringTokenizer servinfo = new StringTokenizer(stringTokenizer.
                nextToken("\r\n"), "\\");

            while (servinfo.hasMoreTokens()) {
              // full the arraylist with all servervariables
              String[] serverSettingInfo = new String[2];
              serverSettingInfo[0] = ( (servinfo.nextToken()).trim()).toLowerCase(); // key
              serverSettingInfo[1] = servinfo.nextToken(); // value
              if (serverSettingInfo[0].equals("sv_hostname")) {
                serverSettingInfo[1] = rem(serverSettingInfo[1]);
              }

              serverSettingList.add(serverSettingInfo);
            }
          }
        }
        catch (Exception e) { System.out.println(e); }

        ArrayList serverPlayerList = new ArrayList();

        int countedPlayers = 0;

        while (stringTokenizer.hasMoreTokens()) {
          String[] serverPlayerInfo = new String[3];
          serverPlayerInfo[0] = stringTokenizer.nextToken(" \n"); // score
          serverPlayerInfo[1] = stringTokenizer.nextToken(" "); // ping
          serverPlayerInfo[2] = remUser(stringTokenizer.nextToken("\r\n")); // nick
          serverPlayerList.add(serverPlayerInfo);
          countedPlayers++;
        }

        // server information
        String[] serverInfo = new String[5];
        serverInfo[0] = ip; // ip address
        serverInfo[1] = String.valueOf(port); // port
        serverInfo[2] = String.valueOf(online); // online or offline
        serverInfo[3] = String.valueOf(countedPlayers); //amount of players on this server
	serverInfo[4] = inetAddress.getHostAddress();

        data.add(serverInfo);
        data.add(serverSettingList);
        data.add(serverPlayerList);
      }
    }
    catch (Exception e) { System.out.println(e + " " + ip + ":" + port); }
    return data;

  }

  public String replace(String s, String f, String r) {
    /*
            if (s == null)return s;
            if (f == null)return s;
            if (r == null) r = "";
            int index01 = s.indexOf(f);
            while (index01 != -1) {
              s = s.substring(0, index01) + r + s.substring(index01 + f.length());
              index01 += r.length();
              index01 = s.indexOf(f, index01);
        }
    */
    if (s == null)return s;
    if (f == null)return s;
    if (r == null) r = "";
    int index01 = s.indexOf(f);
//    while (index01 != -1 && s.length() > 1) {
    while (index01 != -1) {
      s = s.substring(0, index01) + r + s.substring(index01 + f.length() + 1);
      index01 += r.length();
      index01 = s.indexOf(f, index01);
    }
    return s;
  }


  public String rem(String st) {
    st = replace(st, "^", "");
    return st;
  }

  public String remUser(String username) {
    username = rem(username);
    username = username.substring(2, (username.length() - 1));
    return username;
  }
}
