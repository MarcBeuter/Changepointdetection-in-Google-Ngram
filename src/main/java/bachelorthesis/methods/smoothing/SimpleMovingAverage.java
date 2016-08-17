package bachelorthesis.methods.smoothing;

import static bachelorthesis.utility.Constants.*;

import java.util.ArrayList;
import java.util.Arrays;

import bachelorthesis.data.TimeSeries;
import bachelorthesis.data.Value;

public class SimpleMovingAverage extends SmoothingMethod {
	private ArrayList<Value[]> divs = new ArrayList<Value[]>();
	
	public SimpleMovingAverage() {
		divs.add(new Value[LATEST_YEAR - EARLIEST_YEAR + 1]);
		Arrays.setAll(divs.get(0), (number -> Value.ONE));
	}

	@Override
	public Value[] smooth(TimeSeries series, int level) {
		Value[] divisor = getDivisor(level);
		Value[] summedValues = getSum(series, level);
		return merge(summedValues, divisor, ((first, second) -> first.divide(second)));
	}
	
	private Value[] getSum(TimeSeries series, int level) {
		Value[] base = merge(series.getAllValues(level - 1), getDivisor(level - 1), ((first, second) -> first.multiply(second)));
		return sumShifts(base, series.getAllValues(0), level);
	}
	
	private Value[] getDivisor(int level) {
		for (int i = divs.size(); i <= level + 1; i++) {
			divs.add(i, sumShifts(divs.get(i - 1), divs.get(0), i));
		}
		return divs.get(level);
	}

	private Value[] sumShifts(Value[] sumBase, Value[] shiftBase, int level) {
		Value[] left = shift(shiftBase, -level);
		Value[] right = shift(shiftBase, level);
		Value[] result = sumBase.clone();
		for (int i = 0; i < result.length; i++) {
			result[i] = result[i].add(left[i].add(right[i]));
		}
		return result;
	}

	private Value[] shift(Value[] array, int range) {
		Value[] result = new Value[array.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = (i + range >= 0 && i + range < result.length) ? array[i + range] : Value.ZERO;
		}
		return result;
	}
	
	

}
