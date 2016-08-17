package bachelorthesis.fileAccessor;

import static bachelorthesis.fileAccessor.PathGenerator.*;

import java.io.IOException;
import java.nio.file.Files;

/**
 * Creates directory structure if it is not present
 * Insert files downloaded from http://storage.googleapis.com/books/ngrams/books/datasetsv2.html into the directroy NewFileInput
 * Alter the files in the other directories at own risk.
 */
public class Initializer {
	
	public static void initializeDirectories() {
		try {
			Files.createDirectories(newFilesDirectory());
			Files.createDirectories(ngramFilesDirectory());
			Files.createDirectories(totalsFilesDirectory());
			Files.createDirectories(resultDirectory());
		} catch (IOException e) {e.printStackTrace();}
	}
}
