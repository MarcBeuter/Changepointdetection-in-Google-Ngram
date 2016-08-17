package bachelorthesis.fileTransformer;

import static bachelorthesis.utility.Constants.*;
import static bachelorthesis.utility.StringHandler.seperateNgramLine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Contains filter to remove unused data from ngramFiles 
 */
public class NgramFilter {
	
	public static boolean filter(String line) {
		String[] seperatedLine = seperateNgramLine(line);
		if (seperatedLine == null) {
			return false;
		} else {
			boolean result = true;
			List<Method> methods = new LinkedList<Method>(Arrays.asList(NgramFilter.class.getMethods()));
			methods.removeIf(method -> !method.isAnnotationPresent(LineFilter.class));
			Iterator<Method> method = methods.iterator();
			while (method.hasNext() && result) {
				try {
					result = (boolean)method.next().invoke(null, (Object)seperatedLine);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			return result;
		}
	}

	@LineFilter
	public static boolean filterEarlyYears(String[] line) {
		return (Integer.valueOf(line[YEAR_INDEX]) >= EARLIEST_YEAR);
	}
	
	@LineFilter
	public static boolean filterWordClasses(String[] line) {
		String term = line[TERM_INDEX];
		boolean result = true;
		result = result && !term.contains("_NOUN");
		result = result && !term.contains("_VERB");
		result = result && !term.contains("_ADJ");
		result = result && !term.contains("_ADV");
		result = result && !term.contains("_PRON");
		result = result && !term.contains("_DET");
		result = result && !term.contains("_ADP");
		result = result && !term.contains("_NUM");
		result = result && !term.contains("_CONJ");
		result = result && !term.contains("_PRT");
		result = result && !term.contains("_ROOT_");
		result = result && !term.contains("_START_");
		result = result && !term.contains("_END_");
		return result;
	}
	
	@LineFilter
	public static boolean filterX(String[] line) {
		return !line[TERM_INDEX].contains("_X");
	}
	
	@LineFilter
	public static boolean filterWordPoint(String[] line) {
		String term = line[TERM_INDEX];
		boolean result = true;
		result = result && !term.matches(".*[^\\d]\\.[\\d]+"); // e.g. "word.23"
		result = result && !term.matches(".*\\._"); // e.g. "word._"
		result = result && !term.matches(".*_\\."); // e.g. "word_."
		return result;
	}
}

