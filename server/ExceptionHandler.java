/**
 * <p>Title: z0rbot IRC server</p>
 * <p>Description: z0rbot</p>
 * <p>Copyright: Copyright (c) 2004 - 2005</p>
 * <p>Company: z0r.nl</p>
 * @author mdvz0r & Elvis
 * @version 2.2
 */



import java.util.*;
import java.io.*;


public class ExceptionHandler {
/*
    public ExceptionHandler(Exception e) {
	System.out.println(e);
    }

    public ExceptionHandler(String e) {
	System.out.println(e);
    }

*/

    public ExceptionHandler(Exception e) {


	try{
	    PrintWriter printWriter = new PrintWriter(new FileWriter("Error/ErrorLog.txt", true));
	    Calendar calendar = new GregorianCalendar();
	    Date date = calendar.getTime();
	    StackTraceElement stackTraceElement[] = e.getStackTrace();

	    printWriter.println(date.toString() + " Error found in " + stackTraceElement[0].getFileName() + " " + stackTraceElement[0].getMethodName()  + " " + e.getMessage());
	    printWriter.flush();
	    printWriter.close();
	}
	catch(Exception eh) {
	    System.out.println(eh.getMessage());
	}

    }
    
    public ExceptionHandler(String e) {

	try{
	    PrintWriter printWriter = new PrintWriter(new FileWriter("Error/ErrorLog.txt", true));
	    Calendar calendar = new GregorianCalendar();
	    Date date = calendar.getTime();

	    printWriter.println(date.toString() + " Message: " + e);
	    printWriter.flush();
	    printWriter.close();
	}
	catch(Exception eh) {
	    System.out.println(eh.getMessage());
	}

    }
}

