package bachelorthesis.utility;

import java.io.IOException;
import java.util.zip.DataFormatException;

/**
 * Handles all caught exceptions
 */
public class ExceptionHandler {
	
	public static void handleIOException(IOException e) {
		e.printStackTrace();
	}

	public static void handleReflectiveException(Exception e) {
		e.printStackTrace();
	}

	public static void handleOwnException(DataFormatException e) {
		System.out.println(e.getMessage());
		e.printStackTrace();
	}
}
