package bachelorthesis.fileTransformer;

import static bachelorthesis.utility.StringHandler.getTotalsFileName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import bachelorthesis.data.Totals;
import bachelorthesis.fileAccessor.Readers;

/**
 * Generates and modifies TotalsFiles
 */
public class TotalGenerator {
	
	ArrayList<LinkedHashSet<String>> corruptedTotals = new ArrayList<LinkedHashSet<String>>();
	ArrayList<HashMap<String, List<String>>> newFiles = new ArrayList<HashMap<String, List<String>>>();
	
	public TotalGenerator() {
		//add null elements at index 0, so that index = ngramNumber
		corruptedTotals.add(null);
		newFiles.add(null);
		
		for (int i = 1; i <= 5; i++) {
			corruptedTotals.add(new LinkedHashSet<String>());
			newFiles.add(new HashMap<String, List<String>>());
		}
	}

	public void corruptNgram(String name) {
		int ngramNumber = getNgramNumber(name);
		String language = getLanguage(name);
		corruptedTotals.get(ngramNumber).add(language);
	}

	public void addNgram(String name) {
		int ngramNumber = getNgramNumber(name);
		String language = getLanguage(name);
		if (!newFiles.get(ngramNumber).containsKey(language)) {
			newFiles.get(ngramNumber).put(language, new LinkedList<String>());
		}
		newFiles.get(ngramNumber).get(language).add(name);
	}

	public void writeTotalFromNewFile(String name, Stream<String> file) {
		Totals total = new Totals(name);
		total.readNew(file);
	}

	public void updateTotals() {
		for (int i = 2; i <= 5; i++) {
			generateNewTotals(i);
			updateTotals(i);
		}
	}

	private void generateNewTotals(int ngramNumber) {
		LinkedHashSet<String> languages = corruptedTotals.get(ngramNumber);
		for (String language : languages) {
			generateNewTotal(ngramNumber, language);
			newFiles.get(ngramNumber).remove(language);
		}
	}

	private void updateTotals(int ngramNumber) {
		Set<Entry<String, List<String>>> languages = newFiles.get(ngramNumber).entrySet();
		for (Entry<String, List<String>> language : languages) {
			for (String fileName : language.getValue()) {
				addToTotals(ngramNumber, language.getKey(), fileName);
			}
		}
	}

	private int getNgramNumber(String name) {
		int indexOfNumber = name.indexOf("gram") - 1;
		return Integer.valueOf(name.substring(indexOfNumber, indexOfNumber + 1));
	}

	private String getLanguage(String name) {
		return name.substring(0, name.indexOf("-"));
	}
	
	private void addToTotals(int ngramNumber, String language, String newNgramFileName) {
		Totals total = new Totals(getTotalsFileName(ngramNumber, language));
		total.read();
		Stream<String> lines = Readers.readNgramFilesStartingWith(newNgramFileName);
		lines.forEach(line -> total.addCount(line));
		total.write();
	}
	
	private void generateNewTotal(int ngramNumber, String language) {
		Totals.generateTotals(getTotalsFileName(ngramNumber, language));
	}
}
