package bachelorthesis.fileAccessor;

import static bachelorthesis.utility.ExceptionHandler.handleIOException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Grants possibility to write to the directories of the framework
 */
public class Writers {

	/**
	 * @return a buffered writer to a file in the Ngrams-directory
	 */
	public static BufferedWriter getNgramWriter(String fileName) {
		return getWriter(PathGenerator.ngramFilesDirectory().resolve(fileName));
	}

	/**
	 * @return a buffered writer to a file in the NgramsTotals-directory
	 */
	public static BufferedWriter getTotalWriter(String fileName) {
		return getWriter(PathGenerator.totalsFilesDirectory().resolve(fileName));
	}

	public static void write(BufferedReader reader, Path file) {
		BufferedWriter writer = getWriter(file);
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				writer.write(line + "\n");
			}
			writer.close();
		} catch (IOException e) {handleIOException(e);}
	}

	public static void write(BufferedWriter writer, Stream<String> stream) {
		stream.forEach(line -> write(writer, line + "\n"));
		stream.close();
		try {
			writer.close();
		} catch (IOException e) {handleIOException(e);}
	}

	public static void write(BufferedWriter writer, String line) {
		try {
			writer.write(line);
		} catch (IOException e) {handleIOException(e);}
	}
	
	private static BufferedWriter getWriter(Path path) {
		BufferedWriter writer = null;
		try {
			writer = Files.newBufferedWriter(PathGenerator.ngramFilesDirectory().resolve(path));
		} catch (IOException e) {handleIOException(e);}
		return writer;
	}

	public static BufferedWriter getResultWriter(String resultFileName) {
		BufferedWriter writer = null;
		try {
			writer = Files.newBufferedWriter(PathGenerator.resultDirectory().resolve(resultFileName));
		} catch (IOException e) {handleIOException(e);}
		return writer;
	}
	
	public static void closeWriter(BufferedWriter writer) {
		try {
			writer.close();
		} catch (IOException e) {
			handleIOException(e);
		}
	}
}
