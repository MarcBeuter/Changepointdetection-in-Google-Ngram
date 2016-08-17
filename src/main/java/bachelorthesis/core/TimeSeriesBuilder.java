package bachelorthesis.core;

import static bachelorthesis.utility.Configurations.TIME_SERIES_MIN_COUNT;
import static bachelorthesis.utility.StringHandler.*;
import java.util.function.Function;

import bachelorthesis.data.TimeSeries;
import bachelorthesis.data.Totals;
import bachelorthesis.data.Value;

/**
 * Builds TimeSeries' or Streams of TimeSeries' from the ngram-data
 */
public class TimeSeriesBuilder {
	
	TimeSeries series = new TimeSeries("");
	
	Totals[] allTotals = new Totals[6];
	
	Function<String, TimeSeries> collector;

	private Value countSum  = Value.ZERO; //for removing Time Series with low counts
	
	public TimeSeriesBuilder(String term, String language) {
		collector = line -> {
			TimeSeries result = null;
			if (getTermFromNgram(line).equals(term)) {
				if (!series.getTerm().equals(term)) {
					series = new TimeSeries(term);
				}
				addValue(line);
			} else if (series.getTerm().equals(term)) {
				series.fillUp();
				result = series;
			}
			return result;
		};
		fillTotals(language);
	}

	public TimeSeriesBuilder(String language) {
		collector = line -> {
			TimeSeries result = null;
			String term = getTermFromNgram(line);
			if (!term.equals(series.getTerm())) {
				if (!series.getTerm().equals("")) {
					series.fillUp();
					result = series;
				}
				series = new TimeSeries(term);
			}
			addValue(line);
			if (result != null) {
				if (!countSum.biggerEqualThan(TIME_SERIES_MIN_COUNT)) {
					result = null;
				}
				countSum = Value.ZERO;
			}
			return result;
		};
		fillTotals(language);
	}

	public TimeSeries collect(String line) {
		return collector.apply(line);
	}
	
	private void addValue(String line) {
		int year = getYearFromNgram(line);
		String term = getTermFromNgram(line);
		Value total = allTotals[getNgramNumber(term)].getCount(year);
		Value value = getCountFromNgram(line);
		countSum = countSum.add(value);
		Value relativeValue = value.divide(total);
		series.addValue(year, relativeValue);
	}
	
	private void fillTotals(String language) {
		for (int i = 1; i <= 5; i++) {
			Totals totals = new Totals(getTotalsFileName(i, language));
			totals.read();
			allTotals[i] = totals;
		}
	}
	
	public TimeSeries getTimeSeries() {
		series.fillUp();
		return series;
	}
}
