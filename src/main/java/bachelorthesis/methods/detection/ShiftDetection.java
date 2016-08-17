package bachelorthesis.methods.detection;

import static bachelorthesis.utility.Constants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import bachelorthesis.data.Result;
import bachelorthesis.data.Value;

public abstract class ShiftDetection extends DetectionMethod {
	
	protected ArrayList<Value> shifts;

	@Override
	public Result detect() {
		shifts = new ArrayList<Value>(LATEST_YEAR - EARLIEST_YEAR);
		calculateShifts(series.getAllValues(getSmoothingLevel()));
		calculateResult();
		return result;
	}

	private void calculateShifts(Value[] series) {
		Value last;
		int index = 0;
		Iterator<Value> values = Arrays.asList(series).iterator();
		last = values.next();
		while (values.hasNext()) {
			Value temp = values.next();
			shifts.add(index++, temp.subtract(last));
			last = temp;
		}
	}

	protected abstract void calculateResult();
	protected abstract int getSmoothingLevel();
}
