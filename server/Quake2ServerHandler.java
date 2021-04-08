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

public class Quake2ServerHandler {

	public ArrayList getData(String ip, String port, String rcon) {
		ArrayList data = new ArrayList();
		int online = 0;

		try {
			InetAddress inetAddress = InetAddress.getByName(ip);
			DatagramSocket datagramSocket = new DatagramSocket();
			datagramSocket.setSoTimeout(1000);

			String command = "xxxxstatus";
			byte[] buffer = command.getBytes();
			byte oob = (byte) 0xff;

			for (int x = 0; x < 4; x++) {
				buffer[x] = oob;
			}

			DatagramPacket output = new DatagramPacket(buffer, buffer.length,
					inetAddress, Integer.parseInt(port));
			buffer = new byte[65507];
			DatagramPacket input = new DatagramPacket(buffer, buffer.length);

			datagramSocket.send(output);

			try {
				datagramSocket.receive(input);
				online = 1;
			} catch (Exception e) {
				datagramSocket.send(output);
				try {
					datagramSocket.receive(input);
					online = 1;
				} catch (Exception ee) {
					online = 0;
				}
			}

			if (online == 1) {
				String message = new String(input.getData(), 0, 0, input
						.getLength());
				StringTokenizer stringTokenizer = new StringTokenizer(message,
						"\r\n");

				ArrayList serverSettingList = new ArrayList();

				try {
					if (stringTokenizer.hasMoreTokens()) {
						stringTokenizer.nextToken(" \n");
						StringTokenizer servinfo = new StringTokenizer(
								stringTokenizer.nextToken("\r\n"), "\\");

						while (servinfo.hasMoreTokens()) {
							// full the arraylist with all servervariables
							String[] serverSettingInfo = new String[2];
							serverSettingInfo[0] = ((servinfo.nextToken())
									.trim()).toLowerCase(); // key
							serverSettingInfo[1] = servinfo.nextToken(); // value
							serverSettingList.add(serverSettingInfo);
						}
					}
				} catch (Exception e) {
				}

				ArrayList serverPlayerList = new ArrayList();
				int countedPlayers = 0;
				while (stringTokenizer.hasMoreTokens()) {
					String[] serverPlayerInfo;
					if (rcon.equals("")) serverPlayerInfo = new String[3];
					else serverPlayerInfo = new String[5];
					serverPlayerInfo[0] = stringTokenizer.nextToken(" \n"); // score
					serverPlayerInfo[1] = stringTokenizer.nextToken(" "); // ping
					serverPlayerInfo[2] = stringTokenizer.nextToken("\r\n"); // nick
					// [3] = ip
					// [4] = q2 client
					serverPlayerInfo[2] = (serverPlayerInfo[2]).substring(2,
							(serverPlayerInfo[2].length() - 1)); // remove useless things in nick
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

				if (!rcon.equals("")) {
					// met behulp van rcon lijst bijhouden van ips
					datagramSocket = new DatagramSocket();
					datagramSocket.setSoTimeout(1000);
					command = "xxxxrcon "+ rcon +" status";
					buffer = command.getBytes();
					oob = (byte) 0xff;
					for (int x = 0; x < 4; x++) {
						buffer[x] = oob;
					}
					output = new DatagramPacket(buffer, buffer.length, inetAddress,
							Integer.parseInt(port));
					buffer = new byte[65507];
					input = new DatagramPacket(buffer, buffer.length);
					datagramSocket.send(output);
					try {
						datagramSocket.receive(input);
						message = new String(input.getData(), 0, 0, input
								.getLength());
						stringTokenizer = new StringTokenizer(message, "\r\n");
						stringTokenizer.nextToken(); // blaat
						stringTokenizer.nextToken(); // map
						stringTokenizer.nextToken(); // top
						stringTokenizer.nextToken(); // seperator
						
						ArrayList rconList = new ArrayList();
						
						while (stringTokenizer.hasMoreTokens()) {
							String token = stringTokenizer.nextToken();
							//System.out.println(token);
							if (token.length() >= 60) {
								String[] rconInfo = new String[2];
								rconInfo[0] = token.substring(39,60).trim();
								rconInfo[1] = token.substring(14,30).trim();
								String[] ipport = rconInfo[0].split(":");
								if (ipport.length == 2) rconInfo[0] = ipport[0];
								
								for (int x = 0; x < serverPlayerList.size(); x++) {
									String[] serverPlayer = (String[]) serverPlayerList.get(x); 
									if (rconInfo[1].equals(serverPlayer[2])) {
										serverPlayer[3] = rconInfo[0];
										break;
									}
								}
							}
						}
					} catch (Exception e) {
//						System.out.println(e);
					        data.add(serverInfo);
						data.add(serverSettingList);
						data.add(serverPlayerList);
						return data;
					}
					// lets get the clients q2 version
					datagramSocket.close();
					datagramSocket = new DatagramSocket();
					datagramSocket.setSoTimeout(1000);
					command = "xxxxrcon "+ rcon +" status 1";
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
						message = new String(input.getData(), 0, 0, input
								.getLength());

						stringTokenizer = new StringTokenizer(message, "\r\n");
						int amountoftokens = stringTokenizer.countTokens() - 7;
						stringTokenizer.nextToken(); // blaat
						stringTokenizer.nextToken(); // map
						stringTokenizer.nextToken(); // top
						stringTokenizer.nextToken(); // seperator
						int currenttokens = 4;
						
						ArrayList rconList = new ArrayList();
						
						while (stringTokenizer.hasMoreTokens() && currenttokens < amountoftokens) {
							currenttokens++;
							String token = stringTokenizer.nextToken();
							if (token.length() >= 60) {
								String[] rconInfo = new String[2];
								rconInfo[0] = token.substring(19, 67).trim();
								rconInfo[1] = token.substring(3,19).trim();
								for (int x = 0; x < serverPlayerList.size(); x++) {
									String[] serverPlayer = (String[]) serverPlayerList.get(x); 
									if (rconInfo[1].equals(serverPlayer[2])) {
										serverPlayer[4] = rconInfo[0];
										break;
									}
								}
							}
						}
					} catch (Exception e) {
					System.out.println("NOOO: " + e);
					        data.add(serverInfo);
						data.add(serverSettingList);
						data.add(serverPlayerList);
						return data;
					}
				}

				data.add(serverInfo);
				data.add(serverSettingList);
				data.add(serverPlayerList);
			}
		    datagramSocket.close();
		} catch (Exception e) {
		    return data;
		}
		return data;
	}
}
