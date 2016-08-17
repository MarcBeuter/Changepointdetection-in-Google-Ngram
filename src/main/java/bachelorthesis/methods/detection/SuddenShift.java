package bachelorthesis.methods.detection;

import static bachelorthesis.utility.Configurations.*;
import static bachelorthesis.utility.Constants.EARLIEST_YEAR;

import bachelorthesis.data.DataPoint;
import bachelorthesis.data.Value;
public class SuddenShift extends ShiftDetection {

	@Override
	protected void calculateResult() {
		Value threshold = series.getMean(getSmoothingLevel(), SUDDEN_SHIFT_IGNORE_ZEROES_FOR_MEAN).multiply(SUDDEN_SHIFT_THRESHOLD);
		Value lastChange = Value.ZERO;
		int year = EARLIEST_YEAR;
		for (Value shift : shifts) {
			year++;
			if (!threshold.equals(Value.ZERO)) {
				if (shift.abs().biggerEqualThan(threshold)) {
					result.addInterestingPoint(new DataPoint(year, 2 * SUDDEN_SHIFT_WEIGHT));
					lastChange = Value.ZERO;
				} else {
					if (shift.add(lastChange).abs().biggerEqualThan(threshold)) {
						result.addInterestingPoint(new DataPoint(year - 1, SUDDEN_SHIFT_WEIGHT));
						result.addInterestingPoint(new DataPoint(year, SUDDEN_SHIFT_WEIGHT));
					}
					lastChange = shift;
				}
			}
		}
		result.settle();
		
	}

	@Override
	protected int getSmoothingLevel() {
		return SUDDEN_SHIFT_SMOOTHING_LEVEL;
	}
}