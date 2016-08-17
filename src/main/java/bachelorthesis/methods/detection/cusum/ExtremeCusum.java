package bachelorthesis.methods.detection.cusum;

import static bachelorthesis.utility.Configurations.*;
import static bachelorthesis.utility.Constants.EARLIEST_YEAR;
import static bachelorthesis.utility.Constants.LATEST_YEAR;

import java.util.ArrayList;

import bachelorthesis.data.DataPoint;
import bachelorthesis.data.Result;
import bachelorthesis.data.Value;
import bachelorthesis.methods.UtilityMethods;

public class ExtremeCusum extends AbstractCusum {
	
	protected Value mean;
	protected Value last;

	@Override
	protected Value calculateNext(Value lastS, Value value, Value weight) {
		return lastS.multiply(EXTREME_CUSUM_ATTENUATION).add(value).add(last.negate()).add(weight.negate());
	}

	@Override
	protected Value getWeight(Value value) {
		return mean.multiply(EXTREME_CUSUM_WEIGHT_DAMPING);
	}

	@Override
	protected Result detect() {
		maxima = new ArrayList<Value>(LATEST_YEAR - EARLIEST_YEAR + 1);
		minima = new ArrayList<Value>(LATEST_YEAR - EARLIEST_YEAR + 1);
		index = 0;
		last = series.getAllValues(EXTREME_CUSUM_SMOOTHING_LEVEL)[0];
		mean = UtilityMethods.calculateMean(series, EXTREME_CUSUM_SMOOTHING_LEVEL, EXTREME_CUSUM_IGNORE_ZEROES_FOR_MEAN);
		for (Value value : series.getAllValues(EXTREME_CUSUM_SMOOTHING_LEVEL)) {
			next(value);
			last = value;
		}
		int year = EARLIEST_YEAR - 1;
		for (Value value : maxima) {
			year++;
			if (value.biggerThan(mean.multiply(EXTREME_CUSUM_THRESHOLD))) {
				result.addInterestingPoint(new DataPoint(year, EXTREME_CUSUM_WEIGHT));
			}
		}
		year = EARLIEST_YEAR - 1;
		for (Value value : minima) {
			year++;
			if (value.negate().biggerThan(mean.multiply(EXTREME_CUSUM_THRESHOLD))) {
				result.addInterestingPoint(new DataPoint(year, EXTREME_CUSUM_WEIGHT));
			}
		}
		result.settle();
		return result;
	}

}
