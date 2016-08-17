package bachelorthesis.methods;

import static bachelorthesis.utility.Constants.*;

import bachelorthesis.data.TimeSeries;
import bachelorthesis.data.Value;

public class UtilityMethods {
	
	public static Value calculateMean(TimeSeries series, int smoothingLevel, boolean ignoreZeroes) {
		return calculateMean(series, 0, LATEST_YEAR - EARLIEST_YEAR + 1, smoothingLevel, ignoreZeroes);
	}
	
	public static Value calculateMean(TimeSeries series, int from, int to, int smoothingLevel, boolean ignoreZeroes) {
		return calculateMean(series.getAllValues(smoothingLevel), from, to, ignoreZeroes);
	}
	
	public static Value calculateMean(Value[] values, int from, int to) {
		return calculateMean(values, from, to, false);
	}
	
	public static Value calculateMean(Value[] values, int from, int to, boolean ignoreZeroes) {
		int count = 0;
		Value sum = Value.ZERO;
		for (int i = from; i < to && i < values.length; i++) {
			Value next = values[i];
			if(!ignoreZeroes ||!next.equals(Value.ZERO)) {
				count++;
				sum = sum.add(next);
			}
		}
		if (count == 0) count++;
		return sum.divide(new Value(count));
	}
	
	public static Value calculateQuadSum(Value[] values, int from, int to) {
		Value mean = calculateMean(values, from, to);
		Value result = Value.ZERO;
		for (int i = from; i < to && i < values.length; i++) {
			result = result.add(values[i].subtract(mean).pow(2));
		}
		return result;
	}
}
