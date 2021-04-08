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

public class HalflifeServerHandler {

  public ArrayList getData(String ip, String port) {
    ArrayList data = new ArrayList();
    int online = 0;

    try {
      InetAddress inetAddress = InetAddress.getByName(ip);
      DatagramSocket datagramSocket = new DatagramSocket();
      datagramSocket.setSoTimeout(1000);

      String command = "xxxxinfostring\0";
      byte[] buffer = command.getBytes();
      byte oob = (byte) 0xff;

      for (int x = 0; x < 4; x++) {
        buffer[x] = oob;
      }

      DatagramPacket output = new DatagramPacket(buffer, buffer.length, inetAddress, Integer.parseInt(port));
      buffer = new byte[65507];
      DatagramPacket input = new DatagramPacket(buffer, buffer.length);

      datagramSocket.send(output);

      try {
        datagramSocket.receive(input);
        online = 1;
      }
      catch (Exception e) {
        datagramSocket.send(output);
        online = 0;
      }

      if (online == 1) {
        String message = new String(input.getData(), 0, 0, input.getLength());
        StringTokenizer stringTokenizer = new StringTokenizer(message, "\\");

        ArrayList serverSettingList = new ArrayList();

        try {
          if (stringTokenizer.hasMoreTokens()) {
            stringTokenizer.nextToken();
            while (stringTokenizer.hasMoreTokens()) {
              // full the arraylist with all servervariables
              String[] serverSettingInfo = new String[2];
              serverSettingInfo[0] = ( (stringTokenizer.nextToken()).trim()).toLowerCase(); // key
              serverSettingInfo[1] = stringTokenizer.nextToken(); // value
              serverSettingList.add(serverSettingInfo);
            }
          }
        }
        catch (Exception e) { System.out.println(e); }

        command = "xxxxplayers\0";
        buffer = command.getBytes();
        oob = (byte) 0xff;

        for (int x = 0; x < 4; x++) {
          buffer[x] = oob;
        }

        output = new DatagramPacket(buffer, buffer.length, inetAddress, Integer.parseInt(port));
        buffer = new byte[65507];
        input = new DatagramPacket(buffer, buffer.length);

        datagramSocket.send(output);

        try {
          datagramSocket.receive(input);
          online = 1;
        }
        catch (Exception e) {
          datagramSocket.send(output);
          online = 0;
        }

        ArrayList serverPlayerList = new ArrayList();
        int top = 0;
        if (online == 1) {
          byte[] b = input.getData();
          // in byte 5 we found the amount of players on this server
          top = b[5];
          int reader = 6;

          for (int x = 0; x <= (top-1); x++) {
            String[] serverPlayerInfo = new String[3];

            // get player id
            int id = b[reader];
            reader++;

            String playerNick = "";

            boolean dow = true; // do while

            while (dow) {
              if (b[reader] == 00) {
                dow = false;
              }
              else {
                char a = (char) b[reader];
                playerNick = playerNick + a;
              }
              reader++;
            }
            serverPlayerInfo[2] = playerNick;

            // get the frags:
            double tot = 0;

            if (b[reader] < 0) {
              double tmp = 256 + b[reader];
              tot += tmp;
            } else {
              tot += b[reader];
            }
            reader++;

            if (b[reader] < 0) {
              double tmp = 256 + b[reader];
              tmp *= 256;
              tot += tmp;
            } else {
              tot += b[reader];
            }
            reader++;

            if (b[reader] < 0) {
              double tmp = 256+ b[reader];
              tmp *= 65536;
              tot += tmp;
            } else {
              tot += b[reader];
            }
            reader++;

            if (b[reader] < 0) {
              double tmp = 256 + b[reader];
              tmp *= 16777216;
              tot += tmp;
            } else {
              tot += b[reader];
            }
            reader++;

            if (tot >= 16777216) {
              tot += -4294967296f;
            }
            int totalFrags = (int) tot;

            serverPlayerInfo[0] = String.valueOf(totalFrags);

            // get player time:
            reader++;reader++;reader++;reader++;
            serverPlayerInfo[1] = "-1";
            serverPlayerList.add(serverPlayerInfo);
          }
        }

        // server information
        String[] serverInfo = new String[5];
        serverInfo[0] = ip; // ip address
        serverInfo[1] = String.valueOf(port); // port
        serverInfo[2] = String.valueOf(online); // online or offline
        serverInfo[3] = String.valueOf(top); //amount of players on this server
	serverInfo[4] = inetAddress.getHostAddress();

        data.add(serverInfo);
        data.add(serverSettingList);
        data.add(serverPlayerList);
      }
    }
    catch (Exception e) { System.out.println(e); }
    return data;

  }
}
