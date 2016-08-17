package bachelorthesis.methods.detection.cusum;

import java.util.ArrayList;

import bachelorthesis.data.Value;
import bachelorthesis.methods.detection.DetectionMethod;

public abstract class AbstractCusum extends DetectionMethod {
	
	protected ArrayList<Value> maxima;
	protected ArrayList<Value> minima;
	protected int index;

	protected void next(Value value) {
		Value lastMax;
		Value lastMin;
		if (index == 0) {
			lastMax = Value.ZERO;
			lastMin = Value.ZERO;
		} else {
			lastMax = maxima.get(index - 1);
			lastMin = minima.get(index - 1);
		}
		Value weight = getWeight(value);
		nextMaximum(lastMax, value, weight);
		nextMinimum(lastMin, value, weight);
		index++;
	}
	
	protected void nextMaximum(Value last, Value value, Value weight) {
		maxima.add(index, Value.ZERO.max(calculateNext(last, value, weight)));
	}
	
	protected void nextMinimum(Value last, Value value, Value weight) {
		minima.add(index, Value.ZERO.min(calculateNext(last, value, weight.negate())));
	}

	protected abstract Value calculateNext(Value last, Value value, Value weight);

	protected abstract Value getWeight(Value value);
}
