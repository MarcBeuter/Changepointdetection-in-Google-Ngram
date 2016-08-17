package bachelorthesis.fileAccessor;

import static bachelorthesis.fileAccessor.PathGenerator.*;
import static bachelorthesis.utility.ExceptionHandler.handleIOException;
import static bachelorthesis.utility.StringHandler.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import bachelorthesis.core.TimeSeriesBuilder;
import bachelorthesis.data.TimeSeries;

/**
 * Reads files and produces TimeSeries, Streams of TimeSeries or Streams of file-lines
 */
public class Readers {
	
	public static Stream<TimeSeries> readNgramsOfLanguage(String language) {
		Stream<String> lines = createStreamFromFiles(ngramFiles(language));
		TimeSeriesBuilder builder = new TimeSeriesBuilder(language);
 		return lines.map(line -> builder.collect(line)).filter(series -> series != null);
	}

	public static TimeSeries readNgram(String term, String language) {
		Stream<String> stream = null;
		TimeSeriesBuilder builder = new TimeSeriesBuilder(term, language);
		TimeSeries series = null;
		Path path = PathGenerator.ngramFileByTerm(term, language);
		if (path != null) {
			try {
				stream = Files.lines(path);
			} catch (IOException e) {handleIOException(e);}
			Iterator<String> lines = stream.iterator();
			while (lines.hasNext() && (series = builder.collect(lines.next())) == null) {}
			if (!lines.hasNext()) {series = builder.getTimeSeries();}
		}
		return (series != null) ? series : new TimeSeries(term).fillUp();
	}

	public static Stream<String> readNgramFilesStartingWith(String affix) {
		DirectoryStream<Path> files = ngramFilesStartingWith(affix);
		return createStreamFromFiles(files);
	}

	public static Stream<String> readTotals(String name) {
		Path path = totalsFilesDirectory().resolve(name);
		Stream<String> result = null;
		if (Files.exists(path)) {
			try {
				result = Files.lines(path);
			} catch (IOException e) {handleIOException(e);}
		}
		return result;
	}
	
	public static Map<String, Stream<String>> readNewFiles() {
		DirectoryStream<Path> files = PathGenerator.newFiles();
		Map<String, Stream<String>> result = new HashMap<String, Stream<String>>();
		for (Path file: files) {
			Stream<String> lines = null;
			file = unzipIfZipped(file);
			try {
				lines = Files.lines(file);
			} catch (IOException e) {handleIOException(e);}
			result.put(file.getFileName().toString(), lines);
		}
		try {
			files.close();
		} catch (IOException e) {handleIOException(e);}
		return result;
	}
	
	private static Stream<String> createStreamFromFiles(DirectoryStream<Path> files) {
		Stream<String> result = null;
		for (Path file : files) {
			Stream<String> part = null;
			try {
				part = Files.lines(file);
			} catch (IOException e) {handleIOException(e);}
			if (result == null) {
				result = part;
			} else {
				result = Stream.concat(result, part);
			}
		}
		try {
			files.close();
		} catch (IOException e) {handleIOException(e);}
		return Stream.concat(result, Stream.of("dummy234\t2008\t1\t1"));
	}

	private static Path unzipIfZipped(Path file) {
		String name = file.getFileName().toString();
		if (hasArchiveEnding(name)){
			try {
				GZIPInputStream inputStream = new GZIPInputStream(new FileInputStream(file.toFile()));
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
				file = file.getParent().resolve(removeArchiveEnding(name));
				Writers.write(reader, file);
				inputStream.close();
				reader.close();
				Deleters.deleteNewFile(name);
			} catch (IOException e) {
				handleIOException(e);
			}
		}
		return file;
	}
}
