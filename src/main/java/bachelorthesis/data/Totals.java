package bachelorthesis.data;

import static bachelorthesis.utility.Constants.*;
import static bachelorthesis.utility.StringHandler.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import bachelorthesis.fileAccessor.Deleters;
import bachelorthesis.fileAccessor.Readers;
import bachelorthesis.fileAccessor.Writers;
import bachelorthesis.fileTransformer.NgramFilter;

/**
 * Representation of a totals file of the ngram-data
 */
public class Totals {
	
	private String fileName;
	private Value[] count = new Value[LATEST_YEAR - EARLIEST_YEAR + 1];
	private Value[] bookCount = new Value[LATEST_YEAR - EARLIEST_YEAR + 1];
	
	/**
	 * Creates a empty Totals connected to a (maybe nonexistent) file in the Totals-Directory
	 * @param fileName the name of the file
	 */
	public Totals(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * Translates an original totals file to the intern totals-file-notation
	 */
	public void readNew(Stream<String> file) {
		file.forEach(line -> seperateAndAdd(line));
		file.close();
		write();
	}
	
	/**
	 * fills the totals instance with data from the connected file
	 */
	public void read() {
		Stream<String> lines = Readers.readTotals(fileName);
		if (lines != null) {
			lines.forEach(line -> setCount(line));
			lines.close();
		}
	}
	
	/**
	 * writes the new or modified data to the connected file
	 */
	public void write() {
		Deleters.deleteTotalIfExistent(fileName);
		List<String> lines = new LinkedList<String>();
		for (int i = EARLIEST_YEAR; i <= LATEST_YEAR; i++) {
			Value count = this.count[i - EARLIEST_YEAR];
			Value bookCount = this.bookCount[i - EARLIEST_YEAR];
			if (count != null) {
				lines.add(getTotalsLine(i, count, bookCount));
			}
		}
		Writers.write(Writers.getTotalWriter(fileName), lines.stream());
	}
	
	/**
	 * Generates a new totals file, at the time the first ngram file is added with the same word count (e.g. a new totals-file for 3-grams).
	 * Own ngram files have to be generated, because only 1-gram-totals-files exist to download
	 */
	public static Totals generateTotals(String fileName) {
		Totals totals = new Totals(fileName);
		Stream<String> lines = Readers.readNgramFilesStartingWith(removeTextFileEnding(fileName));
		lines.forEach(line -> totals.addCount(line));
		lines.close();
		totals.write();
		return totals;
	}
	
	public Value getCount(int year) {
		return count[year - EARLIEST_YEAR];
	}

	/**
	 * Used when adding new n-gram files to update totals-file data
	 */
	public void addCount(String line) {
		String[] seperatedLine = seperateNgramLine(line);
		if (seperatedLine != null) {
			int index = Integer.valueOf(seperatedLine[YEAR_INDEX]) - EARLIEST_YEAR;
			Value newCount = new Value(seperatedLine[COUNT_INDEX]);
			Value newBookCount = new Value(seperatedLine[BOOKCOUNT_INDEX]);
			count[index] = count[index] == null ? newCount : count[index].add(newCount);
			bookCount[index] = bookCount[index] == null ? newBookCount : bookCount[index].add(newBookCount);
		}
	}

	/**
	 * Splits the lines of original totals-files and changes format to own notation
	 */
	private void seperateAndAdd(String line) {
		List<String> newLines = Arrays.asList(line.split("\\s"));
		for (String newLine : newLines) {
			if (!newLine.equals("")) {
				newLine = convertTotalsNotation(newLine);
				String filterDummy = "dummy " + newLine.substring(0, 4) + " dummy dummy";
				if (NgramFilter.filter(filterDummy)) {
					setCount(newLine);
				} else {
				}
			}
		}
	}

	private void setCount(String newLine) {
		int index = getYearFromTotals(newLine) - EARLIEST_YEAR;
		count[index] = getCountFromTotals(newLine);
		bookCount[index] = getBookcountFromTotals(newLine);
	}
}
