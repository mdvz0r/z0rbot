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

public class GameControl extends Thread {

	protected Main main;

	private int aantal = 0;

	private int gemUur = 0;

	private int Pl = 0;

	private ArrayList currentGames = new ArrayList();

	private boolean accessCurrentGames = false;

	private int delayTimeMiliseconds = 10000;

	private int countTimeMiliseconds = 0;

	private boolean playerListIsReadable = true;

	private List playerList = Collections.synchronizedList(new ArrayList());

	private List tempPlayerList = Collections.synchronizedList(new ArrayList());

	private List serverList = Collections.synchronizedList(new ArrayList());

	private List tempServerList = Collections.synchronizedList(new ArrayList());

	private int numberOfGamesQuake2 = 0;

	private int numberOfGamesQuake3 = 0;

	//  private int numberOfGamesTremulous = 0;

	public GameControl(Main main) {
		this.main = main;

		// get a list of all current games;
		this.startCurrentGames();
		// create a new thread HourUpdate, which will update the average ever hour
		HourUpdate hourUpdate = new HourUpdate(this);
		Thread hourUpdateThread = new Thread(hourUpdate);
		hourUpdateThread.start();

		// keep checkking the servers
		this.start();

	}

	public void run() {

		try {
			while (true) {
				// get all games in an arraylist
				ArrayList mysqlGameList = (ArrayList) main.getMysqlGameList();
				// for each game
				for (int mysqlGameListCounter = 0; mysqlGameListCounter < mysqlGameList
						.size(); mysqlGameListCounter++) {
					// per game: get all servers in an arraylist
					ArrayList mysqlServerList = (ArrayList) main.getMysqlServerListByGame((String) mysqlGameList.get(mysqlGameListCounter));
					// for each server
					for (int mysqlServerListCounter = 0; mysqlServerListCounter < mysqlServerList
							.size(); mysqlServerListCounter++) {
						// get the String Array from each arraylist
						String[] mysqlServerInfo = (String[]) mysqlServerList.get(mysqlServerListCounter);
						// create per server a new gamehandler
						GameHandler gameHandler = new GameHandler(this,
								mysqlServerInfo[0], mysqlServerInfo[1],
								mysqlServerInfo[2], mysqlServerInfo[3],
								mysqlServerInfo[4], mysqlServerInfo[5],
								mysqlServerInfo[6], mysqlServerInfo[7]);
						// start this thread
						gameHandler.start();
						if (mysqlServerInfo[3].equals("quake2")) {
							this.numberOfGamesQuake2(1);
						} else if (mysqlServerInfo[3].equals("quake3")) {
							this.numberOfGamesQuake3(1);
							//            } else if (mysqlServerInfo[3].equals("tremulous")) {
							//	      this.numberOfGamesTremulous(1);
						}
					}
				}

				// wait 10 ms
				Thread.sleep(this.delayTimeMiliseconds);

				// check if all servers are finshed updating
				while (this.numberOfGamesQuake2(0) != 0
						|| this.numberOfGamesQuake3(0) != 0) {
						System.out.println("WAITING" + this.numberOfGamesQuake2(0) + " " + this.numberOfGamesQuake3(0));
					//        while (this.numberOfGamesQuake2(0) != 0 || this.numberOfGamesQuake3(0) != 0 || this.numberOfGamesHalfLife(0) != 0 || this.numberOfGamesCoD(0) != 0 || this.numberOfGamesSource(0) != 0) {
					try {
						Thread.sleep(100);
					} catch (Exception e) {
					}
				}
				// if they're finished updating
				this.playerListIsReadable = false;
				this.playerList.clear();
				this.playerList.addAll(this.tempPlayerList);
				this.tempPlayerList.clear();
				this.serverList.clear();
				this.serverList.addAll(this.tempServerList);
				this.tempServerList.clear();
				this.playerListIsReadable = true;

				// on 30000 miliseconds database should be update
				if (this.countTimeMiliseconds == 60000)
					this.countTimeMiliseconds = this.delayTimeMiliseconds;
				else
					this.countTimeMiliseconds = this.countTimeMiliseconds
							+ this.delayTimeMiliseconds;

				// count total players
				if (this.gemUur == 0) {
					this.gemUur = (this.Pl * 100);
				} else {
					this.gemUur = (((this.Pl * 100) + (gemUur * aantal)) / (aantal + 1));
				}
				// print total players
				System.out.println(Pl);

				this.aantal++;
				this.Pl = 0;
			}
		} catch (Exception e) {
			ExceptionHandler exceptionHandler = new ExceptionHandler(e);
		}

	}

	// set the online or offline status of the server
	public void setMysqlServerStatusByIpPort(String ip, String port,
			String status, String oip) {
		main.setMysqlServerStatusByIpPort(ip, port, status, oip);
	}

	// update server hostname
	public void setMysqlServerHostnameByIpPort(String ip, String port,
			String hostname) {
		main.setMysqlServerHostnameByIpPort(ip, port, hostname);
	}

	// update server gamedir
	public void setMysqlServerGamedirByIpPort(String ip, String port,
			String gamedir) {
		main.setMysqlServerGamedirByIpPort(ip, port, gamedir);
	}

	// add server message to the database
	public void addMysqlMessage(String ip, String port, String type) {
		main.addMysqlMessage(ip, port, type);
	}
	
	public synchronized boolean editAccessCurrentGames(boolean b, boolean s) {
	    if (s) {
		accessCurrentGames = b;
		return true;
	    }
	    else return accessCurrentGames;
	}

	// check if current game already exists
	public boolean existCurrentGame(String game) {
//		while (accessCurrentGames) {
		while (editAccessCurrentGames(false, false)) {
		}
//		accessCurrentGames = true; // now i'm using the vector
		boolean nouse = editAccessCurrentGames(true, true);
		for (int x = 0; x < currentGames.size(); x++) {
			if (((String) currentGames.get(x)).equals(game)) {
				//accessCurrentGames = false; // now i'm not using the vector
				nouse = editAccessCurrentGames(false, true);
				return false;
			}
		}
		//accessCurrentGames = false; // now i'm not using the vector
		editAccessCurrentGames(false, true);
		return true;
	}

	// create tables for current game
	public void addCurrentGame(int year, int week, String gamedir, String game) {
//		while (accessCurrentGames) { /* someone is using the vector */
		while (editAccessCurrentGames(false, false)) {
		}
//		accessCurrentGames = true; // now i'm using the vector
		boolean nouse = editAccessCurrentGames(true, true);
		currentGames.add(year + "_" + week + "_" + gamedir + "_" + game);
		//accessCurrentGames = false; // now i'm not using the vector
		nouse = editAccessCurrentGames(false, true);

		main.addCurrentGame(year, week, gamedir, game);
	}

	// get current games
	public void startCurrentGames() {
		currentGames = (ArrayList) main.startCurrentGames();
	}

	public String[] getMysqlPlayerByNickYearWeekGamedirGame(String nick,
			int year, int week, String gamedir, String game) {
		return main.getMysqlPlayerByNickYearWeekGamedirGame(nick, year, week,
				gamedir, game);
	}

	public void updateMysqlPlayerStats(int year, int week, String gamedir,
			String frags, String map, String ip, String port, long secspld,
			int totfrags, int lastfrags, double fpm, int ping, int factor,
			long time, int aantal, int id, String game, String playerip, String playerq2cl) {
		main.updateMysqlPlayerStats(year, week, gamedir, frags, map, ip, port,
				secspld, totfrags, lastfrags, fpm, ping, factor, time, aantal,
				id, game, playerip, playerq2cl);
	}

	public void updateMysqlPlayerStatsOnReset(int year, int week,
			String gamedir, String frags, String map, String ip, String port,
			long time, int ping, int factor, int aantal, int id, String game,
			String playerip, String playerq2cl) {
		main.updateMysqlPlayerStatsOnReset(year, week, gamedir, frags, map, ip,
				port, time, ping, factor, aantal, id, game, playerip, playerq2cl);
	}

	public void insertMysqlPlayerStats(int year, int week, String gamedir,
			String nick, String clan, String frags, String map, String ip,
			String port, long time, int ping_time, int factor, String game,
			String playerip, String playerq2cl) {
		main.insertMysqlPlayerStats(year, week, gamedir, nick, clan, frags,
				map, ip, port, time, ping_time, factor, game, playerip, playerq2cl);
	}

	public void updatePlayerCounter() {
		this.Pl++;
	}

	public int getCountedTime() {
		return this.countTimeMiliseconds;
	}

	public synchronized int numberOfGamesQuake2(int what) {
		if (what == 1) {
			this.numberOfGamesQuake2++;
		} else if (what == 2) {
			this.numberOfGamesQuake2--;
		}
		return this.numberOfGamesQuake2;
	}

	public synchronized int numberOfGamesQuake3(int what) {
		if (what == 1) {
			this.numberOfGamesQuake3++;
		} else if (what == 2) {
			this.numberOfGamesQuake3--;
		}
		return this.numberOfGamesQuake3;
	}

	//  public synchronized int numberOfGamesTremulous(int what) {
	//    if (what == 1) { this.numberOfGamesTremulous++; }
	//    else if (what == 2) { this.numberOfGamesTremulous--; }
	//    return this.numberOfGamesTremulous;
	//  }

	public synchronized void addPlayerToTempList(String nick, String server,
			String ipport, String game, String gamedir) {
		String[] tempList = new String[5];
		tempList[0] = nick;
		tempList[1] = server;
		tempList[2] = ipport;
		tempList[3] = game;
		tempList[4] = gamedir;
		this.tempPlayerList.add(tempList);
	}

	public void addServerToTempList(String ip, String port, String name,
			String players, String matchmode, String game, String gamedir,
			String rcon, String alias) {
		String[] tempList = new String[9];
		tempList[0] = name;
		tempList[1] = ip;
		tempList[2] = port;
		tempList[3] = players;
		tempList[4] = matchmode;
		tempList[5] = game;
		tempList[6] = gamedir;
		tempList[7] = rcon;
		tempList[8] = alias;
		this.tempServerList.add(tempList);
	}

	public ArrayList getGameControlPlayer(String game, String gamedir,
			String search) {
		ArrayList list = new ArrayList();
		ArrayList tmpList = new ArrayList();
		tmpList.addAll(this.playerList);
		for (int x = 0; x < tmpList.size(); x++) {
			if (((String[]) tmpList.get(x))[3].equals(game)
					&& ((String[]) tmpList.get(x))[4].equals(gamedir)) {
				if (((String[]) tmpList.get(x))[0].toLowerCase().indexOf(
						search.toLowerCase()) != -1) {
					list.add(tmpList.get(x));
				}
			}
		}
		return list;
	}

	public ArrayList getEmptyClanServer(String game, String gamedir) {
		ArrayList retList = new ArrayList();
		ArrayList tmpList = new ArrayList();
		tmpList.addAll(this.serverList);
		boolean first = true;
		String list = "";
		int counter = 0;
		for (int x = 0; x < tmpList.size(); x++) {
			String[] tmpSt = (String[]) tmpList.get(x);
			if (tmpSt[5].equals(game) && tmpSt[6].equals(gamedir)) {
				if (tmpSt[3].equals("0") && tmpSt[4].equals("1")) {
					if (!first) {
						list = list + " | ";
					} else {
						first = false;
					}
					counter++;
					list = list + tmpSt[1] + ":" + tmpSt[2];
					if (counter == 10) {
						retList.add(list);
						list = "";
						counter = 0;
						first = true;
					}
				}
			}
		}
		if (counter != 0) {
			retList.add(list);
		}
		return retList;
	}

	public String[] getServer(String ip, String port) {
		for (int x = 0; x < serverList.size(); x++) {
			String[] srvSt = (String[]) serverList.get(x);
			if (srvSt[1].equals(ip) && srvSt[2].equals(port)) {
				return srvSt;
			}
		}
		return null;
	}

	public boolean banUser(String nick, String ip, String reason) {
		for (int x = 0; x < serverList.size(); x++) {
			String[] srvSt = (String[]) serverList.get(x);
			// if there is a rcon password set:
				// even alleen testen op z0r servers
			if (!srvSt[7].equals("")) {
				// oude manier
				//sendServerCommand("rcon " + srvSt[7] + " sv !ban ip " + ip + " save", srvSt[1], srvSt[2]);
				//sendServerCommand(
				//		"rcon "
				//				+ srvSt[7]
				//				+ " addhole "
				//				+ ip
				//				+ " MESSAGE You are banned from this server. Contact an admin in #z0r.nl on Qnet.",
				//		srvSt[1], srvSt[2]);
				sendServerCommand("rcon " + srvSt[7] + " sv !ban ip " + ip, srvSt[1], srvSt[2]);
				sendServerCommand("rcon " + srvSt[7] + " addhole " + ip + " MESSAGE You are banned from this server. Contact an admin in #z0r.nl on Qnet.", srvSt[1], srvSt[2]);
				sendServerCommand("rcon " + srvSt[7] + " sv !reloadbanfile", srvSt[1], srvSt[2]);
			}
		}
		return true;
	}

	public boolean addNickReservation(String nick, String password) {
		for (int x = 0; x < serverList.size(); x++) {
			String[] srvSt = (String[]) serverList.get(x);
			if (!srvSt[7].equals("")) {
//System.out.println("rcon " + srvSt[7] + " sv !ban + NAME RE \""+ nick + "\" PASSWORD \""+ password + "\" msg \"Name reserved for hermanz0r by #q2admin @ quakenet\"");
//				sendServerCommand("rcon " + srvSt[7] + " sv !ban + NAME RE \""+ nick + "\" PASSWORD \""+ password + "\" msg \"Name reserved for hermanz0r by #q2admin @ quakenet\"", srvSt[1], srvSt[2]);
				sendServerCommand("rcon " + srvSt[7] + " sv !reloadbanfile", srvSt[1], srvSt[2]);
			}
		}
		return true;
	}




	public boolean unbanUser(String ip) {
		//sendServerCommand("rcon " + srvSt[7] + " sv !delban ip " + ip + " save", srvSt[1], srvSt[2]);
		//sendServerCommand("rcon " + srvSt[7] + " delhole " + ip + "", srvSt[1], srvSt[2]);
		//String banlist = sendServerCommand("rcon hermalientje sv !del", "aq2.z0r.nl",
		//		"27910");
		//System.out.println(banlist);
		for (int x = 0; x < serverList.size(); x++) {
			String[] srvSt = (String[]) serverList.get(x);
			if (!srvSt[7].equals("")) {
			    sendServerCommand("rcon " + srvSt[7] + " sv !reloadbanfile", srvSt[1], srvSt[2]);
			    sendServerCommand("rcon " + srvSt[7] + " delhole " + ip, srvSt[1], srvSt[2]);
			}
		}
		return true;
	}

	public boolean addException() {
		for (int x = 0; x < serverList.size(); x++) {
			String[] srvSt = (String[]) serverList.get(x);

			if (!srvSt[7].equals("")) {
				sendServerCommand("rcon " + srvSt[7] + " sv !reloadexceptionlist", srvSt[1], srvSt[2]);
			}
		}
		return true;
	}

	public boolean removeException(String ip, String mask) {
		int msk = Integer.parseInt(mask);
		for (int x = 0; x < serverList.size(); x++) {
			String[] srvSt = (String[]) serverList.get(x);
			if (!srvSt[7].equals("")) {
			    sendServerCommand("rcon " + srvSt[7] + " sv !reloadexceptionlist", srvSt[1], srvSt[2]);
			    if (msk == 0) {
				sendServerCommand("rcon " + srvSt[7] + " delacexception " + ip, srvSt[1], srvSt[2]);
			    } else {
				sendServerCommand("rcon " + srvSt[7] + " delacexception " + ip + "/" + mask, srvSt[1], srvSt[2]);
			    }
			}
		}
		return true;

	}

	public boolean removeNickReservation(String nick) {
		for (int x = 0; x < serverList.size(); x++) {
			String[] srvSt = (String[]) serverList.get(x);
			if (!srvSt[7].equals("")) {
			    sendServerCommand("rcon " + srvSt[7] + " sv !reloadbanfile", srvSt[1], srvSt[2]);
			}
		}
		return true;

	}


	public boolean addRequired() {
		for (int x = 0; x < serverList.size(); x++) {
			String[] srvSt = (String[]) serverList.get(x);

			if (!srvSt[7].equals("")) {
				sendServerCommand("rcon " + srvSt[7] + " sv !reloadanticheatlist", srvSt[1], srvSt[2]);
			}
		}
		return true;
	}

	public boolean removeRequired(String ip, String mask) {
		int msk = Integer.parseInt(mask);
		for (int x = 0; x < serverList.size(); x++) {
			String[] srvSt = (String[]) serverList.get(x);
			if (!srvSt[7].equals("")) {
			    sendServerCommand("rcon " + srvSt[7] + " sv !reloadanticheatlist", srvSt[1], srvSt[2]);
			    if (msk == 0) {
				sendServerCommand("rcon " + srvSt[7] + " delacrequirement " + ip, srvSt[1], srvSt[2]);
			    } else {
				sendServerCommand("rcon " + srvSt[7] + " delacrequirement " + ip + "/" + mask, srvSt[1], srvSt[2]);
			    }
			}
		}
		return true;

	}
	public String rconCmd(String host, String port, String cmd) {
	    for (int x = 0; x < serverList.size(); x++) {
		String[] srvSt = (String[]) serverList.get(x);
		if (!srvSt[7].equals("") && ((srvSt[1].equals(host) && srvSt[2].equals(port)) || srvSt[8].equals(host))) {
		    return this.sendServerCommand("rcon " + srvSt[7] + " " + cmd, srvSt[1], srvSt[2]);
		}
	    }
	    return "Did you use the correct hostname+ip or alias as listed on the z0r website??";
	}      

	public String sendServerCommand(String cmd, String ip, String port) {
		try {
			InetAddress inetAddress = InetAddress.getByName(ip);
			DatagramSocket datagramSocket = new DatagramSocket();
			datagramSocket.setSoTimeout(1000);

			String command = "xxxx" + cmd;
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
			datagramSocket.receive(input);
			String retval = new String(input.getData(), 0, 0, input.getLength());
			return retval;
		} catch (Exception e) {
			ExceptionHandler exceptionHandler = new ExceptionHandler(e);
			return "Server did not respond.";
		}
	}

	public int getGemUur() {
		return this.gemUur;
	}

	public void resetServerLoad() {
		this.gemUur = 0;
		this.aantal = 0;
	}

	public void serverload(int dag, int maand, int jaar, int uur, int gem) {
		main.serverload(dag, maand, jaar, uur, gem);
	}

	public void avgUpdate(int week, int year, int hour) {
		main.avgUpdate(week, year, hour);
	}

	public void checkChannels() {
		main.checkChannels();
	}

}
