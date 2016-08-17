package bachelorthesis.core;

import static bachelorthesis.core.Main.print;
import static bachelorthesis.fileAccessor.Readers.readNgramsOfLanguage;
import static bachelorthesis.utility.StringHandler.NO_PIVOT;
import static bachelorthesis.utility.StringHandler.getTermsFromArgs;
import java.util.List;
import java.util.stream.Stream;

import bachelorthesis.data.Result;
import bachelorthesis.data.TimeSeries;
import bachelorthesis.fileAccessor.Readers;
import bachelorthesis.methods.ComparisonMethod;
import bachelorthesis.methods.MethodsTable;
import bachelorthesis.methods.YearMethod;
import bachelorthesis.methods.detection.DetectionMethod;
import bachelorthesis.methods.detection.DetectionMethods;
import bachelorthesis.methods.detection.SuddenShift;
import bachelorthesis.methods.smoothing.SimpleMovingAverage;
import bachelorthesis.methods.smoothing.SmoothingMethod;
import bachelorthesis.methods.smoothing.SmoothingMethods;

/**
 * Handles terms input and calculation of results
 */
public class Calculator {
	
	private List<String> terms;
	private String language;
	private SmoothingMethod smoothingMethod = new SimpleMovingAverage();
	private DetectionMethod detectionMethod = new SuddenShift();
	private Result result = null;
	private ComparisonMethod comparer;
	
	public Calculator(String[] input) {
		terms = getTermsFromArgs(input);
		terms.forEach(term -> print(term));
	}

	public Stream<Result> calculate() {
		return terms.stream().map(term -> calculate(Readers.readNgram(term, language)));
	}

	public Stream<Result> calculateAndCompare() {
		if (terms == null || terms.size() == 0) {
			print(NO_PIVOT);			
		} else {
			initializeComparison();
		}
		if (terms.size() == 1) {
			return calculateAndCompareToAll();
		} else {
			return calculateAndCompareToList();
		}
	}

	public Stream<Result> calculateYears() {
		YearMethod years = new YearMethod(terms);
		Stream<Result> results = readNgramsOfLanguage(language).map(series -> {
			series.setSmoothingMethod(smoothingMethod);
			return years.calculateHits(detectionMethod.detect(series));
		});
		return results.filter(result -> result != null);
	}

	public Stream<Result> calculateAndCompareToAll() {
		Stream<Result> results = readNgramsOfLanguage(language).map(tSeries -> {
			tSeries.setSmoothingMethod(smoothingMethod);
			return comparer.compare(detectionMethod.detect(tSeries), false);
		});
		return results.filter(result -> result != null);
	}

	public Stream<Result> calculateAndCompareToList() {
		return terms.stream().map(term -> {
			return comparer.compare(calculate(Readers.readNgram(term, language)), true);
		});
	}

	private void initializeComparison() {
		if (result == null) {
			result = calculate(Readers.readNgram(getPivotTerm(), language));
		}
		if (comparer == null) {
			comparer = new ComparisonMethod(result);
		}
	}

	public Result calculate(TimeSeries series) {
		series.setSmoothingMethod(smoothingMethod);
		result = detectionMethod.detect(series);
		return result;
	}

	public void setSmoothing(SmoothingMethods method) {
		smoothingMethod = MethodsTable.getInstance().getSmoothingMethod(method);
	}

	public void setDetectionMethod(DetectionMethods method) {
		detectionMethod = MethodsTable.getInstance().getDetectionMethod(method);
	}

	private String getPivotTerm() {
		return terms.get(0);
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
}
