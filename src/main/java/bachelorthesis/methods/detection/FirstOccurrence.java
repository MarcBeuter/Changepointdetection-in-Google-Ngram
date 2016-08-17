package bachelorthesis.methods.detection;

import static bachelorthesis.utility.Configurations.*;
import static bachelorthesis.utility.Constants.EARLIEST_YEAR;

import java.util.Arrays;
import java.util.Iterator;

import bachelorthesis.data.DataPoint;
import bachelorthesis.data.Result;
import bachelorthesis.data.Value;

public class FirstOccurrence extends DetectionMethod{

	@Override
	protected Result detect() {
		Value[] values = series.getAllValues(FIRST_OCCURENCE_SMOOTHING_LEVEL_DETECTION);
		Value max = Value.ZERO;
		for (Value value : values) {
			if (value.biggerEqualThan(max)) max = value;
		}
		Value threshold = max.multiply(FIRST_OCCURENCE_THRESHOLD);
		if (!series.getAllValues(FIRST_OCCURENCE_SMOOTHING_LEVEL_CONFIRMATION)[0].biggerEqualThan(threshold)) {
			Iterator<Value> iterator = Arrays.asList(values).iterator();
			int firstYear = 0;
			int yearCount = EARLIEST_YEAR;
			while (iterator.hasNext() && firstYear == 0) {
				if (iterator.next().biggerEqualThan(threshold)) {
					firstYear = yearCount;
					result.addInterestingPoint(new DataPoint(firstYear, FIRST_OCCURENCE_WEIGHT));
				}
				yearCount++;
			}
		}
		return result;
	}

}
