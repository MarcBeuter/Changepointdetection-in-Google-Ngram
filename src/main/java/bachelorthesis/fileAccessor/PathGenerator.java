package bachelorthesis.fileAccessor;

import static bachelorthesis.utility.Constants.*;
import static bachelorthesis.utility.ExceptionHandler.handleIOException;
import static bachelorthesis.utility.StringHandler.*;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

/**
 * Creates Paths or DirectoryStreams of Paths from the files in the framework directories (created from the Initializer)
 */
public class PathGenerator {
	
	static DirectoryStream<Path> newFiles() {
		return createDirectoryStream(basePath().resolve(SOURCE_FOLDER), (input -> true));
	}
	
	static DirectoryStream<Path> ngramFiles(String language) {
		Filter<Path> filter = (path -> {
			String regex = language + "-[\\d].*";
			return path.getFileName().toString().matches(regex);
		});
		return createDirectoryStream(basePath().resolve(NGRAM_FOLDER), filter);
	}
	
	static DirectoryStream<Path> ngramFilesStartingWith(String name) {
		Filter<Path> filter = (path -> {
			return path.getFileName().toString().startsWith(name);
		});
		return createDirectoryStream(basePath().resolve(NGRAM_FOLDER), filter);
	}
	
	static Path ngramFileByTerm(String term, String language) {
		int ngramNumber = getNgramNumber(term);
		String supposedName = getAffix(ngramNumber, language) + "-" + getSuffix(ngramNumber, term);
		Filter<Path> filter = (path -> {
			String fileName = path.getFileName().toString();
			return fileName.equals(supposedName);
		});
		DirectoryStream<Path> files = createDirectoryStream(basePath().resolve(NGRAM_FOLDER), filter);
		Iterator<Path> iterator = files.iterator();
		Path result = null;
		if (iterator.hasNext()) {
			result = iterator.next();
		} else {
			System.out.println("There is no file with name '" + supposedName + "' in directory '" + NGRAM_FOLDER + "'");
		}
		try {
			files.close();
		} catch (IOException e) {handleIOException(e);}
		return result;
	}

	static DirectoryStream<Path> totalsFiles() {
		return createDirectoryStream(basePath().resolve(NGRAM_TOTALS_FOLDER), (input -> true));
	}
	
	static Path newFilesDirectory() {
		return basePath().resolve(SOURCE_FOLDER);
	}
	
	static Path ngramFilesDirectory() {
		return basePath().resolve(NGRAM_FOLDER);
	}
	
	static Path totalsFilesDirectory() {
		return basePath().resolve(NGRAM_TOTALS_FOLDER);
	}
	
	static Path resultDirectory() {
		return basePath().resolve(RESULT_FOLDER);
	}
	
	private static DirectoryStream<Path> createDirectoryStream(Path path, Filter<? super Path> filter) {
		DirectoryStream<Path> result = null;
		try {
			result = Files.newDirectoryStream(path);
			if (!result.iterator().hasNext()) {
				System.out.println("No Files in directory: '" + path + "'");
			}
			result.close();
			result = Files.newDirectoryStream(path, filter);
		} catch (IOException e) {handleIOException(e);}
		return result;
	}

	private static Path basePath() {
		return FileSystems.getDefault().getPath(System.getProperty("user.dir"), new String[0]);
	}
}
