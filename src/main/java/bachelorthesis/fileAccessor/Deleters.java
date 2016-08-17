package bachelorthesis.fileAccessor;

import static bachelorthesis.utility.ExceptionHandler.handleIOException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handles deletion of files
 */
public class Deleters {
	
	public static void deleteNewFile(String name) {
		try {
			Files.delete(PathGenerator.newFilesDirectory().resolve(name));
		} catch (IOException e) {handleIOException(e);}
	}
	
	public static boolean deleteNgramIfExistent(String name) {
		Path path = PathGenerator.ngramFilesDirectory().resolve(name);
		return deleteIfExists(path);
	}
	
	public static void deleteTotalIfExistent(String name) {
		Path path = PathGenerator.totalsFilesDirectory().resolve(name);
		deleteIfExists(path);
	}
	
	private static boolean deleteIfExists(Path path) {
		boolean fileExists = Files.exists(path);
		if (fileExists) {
			try {
				Files.delete(path);
			} catch (IOException e) {handleIOException(e);}
		}
		return fileExists;
	}
}
