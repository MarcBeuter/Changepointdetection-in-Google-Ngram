package bachelorthesis.utility;

import static bachelorthesis.core.Main.print;
import static bachelorthesis.utility.Constants.*;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import bachelorthesis.data.DataPoint;
import bachelorthesis.data.Result;
import bachelorthesis.data.Value;

/**
 * Handles String modification, processing and creation
 */
public class StringHandler {
	
	public static final String NO_PIVOT = "No Pivotterm set";
	public static final String NO_TERMS = "No Term in arguments";

	public static String[] seperateNgramLine(String line) {
		String[] result = line.split("\\s");
		if (result.length < 4) {
			result = null;
		} else {
			while(result.length > 4) {
				String temp = result[0];
				result = Arrays.copyOfRange(result, 1, result.length);
				result[0] = temp + " " + result[0];
			}
		}
		return result;
	}
	
	public static String getSuffix(int ngramNumber, String term) {
		return term.substring(0, Math.min(ngramNumber, 2)).toLowerCase();
	}
	
	public static String getAffix(int ngramNumber, String language) {
		return language + "-" + ngramNumber + "gram";
	}

	public static String getTotalsFileName(int ngramNumber, String language) {
		return getAffix(ngramNumber, language) + ".txt";
	}

	public static String getTotalsLine(int year, Value count, Value bookCount) {
		String line = "";
		line += year;
		line += "\t" + count;
		line += "\t" + bookCount;
		return line;
	}

	public static boolean hasArchiveEnding(String name) {
		return name.endsWith(".gz");
	}

	public static String removeArchiveEnding(String name) {
		return name.substring(0, name.indexOf(".gz"));
	}

	public static boolean hasTextFileEnding(String name) {
		return name.endsWith(".txt");
	}

	public static String removeTextFileEnding(String name) {
		return name.substring(0, name.indexOf(".txt"));
	}

	public static String createNameFromInputFileName(String oldName) {
		String result = oldName.replace("googlebooks-", "");
		result = result.replace("all-", "");
		result = result.replace("-20120701", "");
		result = result.replace("totalcounts", "1gram");
		return result;
	}

	public static String convertTotalsNotation(String newLine) {
		//remove first and last number (year and book-count)
		String booksPerSide = newLine.substring(newLine.indexOf(",") + 1 , newLine.lastIndexOf(","));
		//remove second number (total-count)
		booksPerSide = booksPerSide.substring(booksPerSide.indexOf(","));
		newLine = newLine.replace(booksPerSide, "");
		newLine = newLine.replace(",", "\t");
		return newLine;
	}

	public static int getYearFromTotals(String newLine) {
		return Integer.valueOf(newLine.split("\\s")[0]);
	}

	public static Value getBookcountFromTotals(String newLine) {
		return new Value(newLine.split("\\s")[1]);
	}

	public static Value getCountFromTotals(String newLine) {
		return new Value(newLine.split("\\s")[2]);
	}

	public static String getTermFromNgram(String line) {
		return seperateNgramLine(line)[TERM_INDEX];
	}

	public static int getYearFromNgram(String line) {
		return Integer.valueOf(seperateNgramLine(line)[YEAR_INDEX]);
	}

	public static Value getCountFromNgram(String line) {
		return new Value(seperateNgramLine(line)[COUNT_INDEX]);
	}

	public static Value getBookcountFromNgram(String line) {
		return new Value(seperateNgramLine(line)[BOOKCOUNT_INDEX]);
	}

	public static int getNgramNumber(String term) {
		return term.split("\\s").length;
	}

	public static void printHelp() {
		print("args is read as");
		print("[result mode] [mode] [detection mode] [smoothing mode] [language] term[,term]*");
		print("The first of each category is the default");
		print("");
		print("result print modes:");
		Arrays.asList(PRINT_MODES).forEach(mode -> print(mode));
		print("");
		print("modes:");
		Arrays.asList(MODES).forEach(command -> print(command));
		print("");
		print("detection modes:");
		Arrays.asList(DETECTION_MODES).forEach(mode -> print(mode));
		print("");
		print("smoothing modes:");
		Arrays.asList(SMOOTHING_MODES).forEach(mode -> print(mode));
		print("");
		print("languages:");
		Arrays.asList(LANGUAGES).forEach(language -> print("-" + language));
		print("The test language is for own file-compilations." + 
		" Just substitute the language abbrevation in your file name with 'test'");
	}

	public static boolean isLanguageCommand(String string) {
		return string.startsWith("-") && Arrays.asList(LANGUAGES).contains(string.substring(1));
	}

	public static List<String> getTermsFromArgs(String[] input) {
		String complete = "";
		for (int i = 0; i < input.length; i++) {
			complete += input[i] + " ";
		}
		complete = complete.substring(0, complete.length() - 1);
		return Arrays.asList(complete.split(",[\\s]?"));
	}

	public static List<String> printformOfSimilarityResult(Result result) {
		List<String> list = new LinkedList<String>();
		list.add("");
		list.add("'" + result.getRelatedTerm() + "' similar to '" + result.getTerm() + "'");
		list.add(String.valueOf(result.getSimilarity()));
		for (DataPoint point : result.getRelatedPoints().pointSet()) {
			list.add(point.toString());
		}
		return list;
	}
	
	public static String standardMode(String command) {
		return "Standard Mode (" + command + ")";
	}
	public static String standardDetectionMode(String command) {
		return "Standard Detection Mode (" + command + ")";
	}
	public static String standardLanguage(String language) {
		return "Standard Language (-" + language + ")";
	}

	public static String getResultFileName(int fileNumber, Date now) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("_yyyy-MM-dd_HH-mm-ss_");
		return "Result" + dateFormatter.format(now) + fileNumber + ".txt";
	}
}
