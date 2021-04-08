/**
 * <p>Title: z0rbot IRC server</p>
 * <p>Description: z0rbot</p>
 * <p>Copyright: Copyright (c) 2004 - 2005</p>
 * <p>Company: z0r.nl</p>
 * @author mdvz0r
 * @version 2.2
 */


import java.lang.*;
import java.sql.*;


public class MysqlHandler {

    protected Connection connection;

    public void open(String address) {

	try {
	    System.out.println("Making database connection to: " + address);
	    Class.forName("com.mysql.jdbc.Driver").newInstance();
	    System.out.println("Class created, now making the connection...");
	    connection = DriverManager.getConnection(address);
	    System.out.println("Connection succesful");
	}
	catch(Exception e) {
          System.out.println("Connection failed: " + e);
	    ExceptionHandler exceptionHandler = new ExceptionHandler(e);
	}

    }


    public void close() {

	try {
	    connection.close();
	}
	catch(Exception e) {
	    ExceptionHandler exceptionHandler = new ExceptionHandler(e);
	}

    }
    
    public static long ipToLong(String ip) {
        try {
          java.util.StringTokenizer t = new java.util.StringTokenizer(ip, ".");
          String numero = "";
          int nr;
          while (t.hasMoreTokens()) {
            nr = Integer.parseInt(t.nextToken());
            numero = numero.concat((Integer.toHexString(nr).length()==1)?"0"+Integer.toHexString(nr):Integer.toHexString(nr));
          }
          return Long.parseLong(numero,16);
        } 
         catch (Exception e) {
          ExceptionHandler exceptionHandler = new ExceptionHandler(e);
          return 0;
        }
      }//ipToLong

    public static String longToIp(long ip) {
    	return String.valueOf((ip >> 24)+"."+((ip >> 16) & 255)+"."+((ip >> 8) & 255)+"."+(ip & 255));
    } //longToIp

}
