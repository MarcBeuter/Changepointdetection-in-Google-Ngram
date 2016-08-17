package bachelorthesis.fileTransformer;

import static bachelorthesis.utility.StringHandler.*;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import bachelorthesis.fileAccessor.Deleters;
import bachelorthesis.fileAccessor.Readers;
import bachelorthesis.fileAccessor.Writers;

/**
 * Imports new files from the NewFileInput-directory
 */
public class NewFilesImporter {
	
	TotalGenerator totals;
	
	public void readInNewFiles() {
		
		totals = new TotalGenerator();
		
		Map<String, Stream<String>> files = Readers.readNewFiles();
		for (Entry<String, Stream<String>> file: files.entrySet()) {
			String name = createNameFromInputFileName(file.getKey());
			if (hasTextFileEnding(name)) {
				totals.writeTotalFromNewFile(name, file.getValue());
			} else {
				writeNgram(name, file.getValue());
			}
			Deleters.deleteNewFile(file.getKey());
		}
		
		totals.updateTotals();
	}

	private void writeNgram(String name, Stream<String> value) {
		if (Deleters.deleteNgramIfExistent(name)) {
			totals.corruptNgram(name);
		} else {
			totals.addNgram(name);
		}
		Stream<String> stream = value.filter(line -> NgramFilter.filter(line));
		Writers.write(Writers.getNgramWriter(name), stream);
		value.close();
	}
}
