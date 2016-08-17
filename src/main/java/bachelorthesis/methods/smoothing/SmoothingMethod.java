package bachelorthesis.methods.smoothing;

import java.util.function.BinaryOperator;

import bachelorthesis.data.TimeSeries;
import bachelorthesis.data.Value;

public abstract class SmoothingMethod {
	
	public abstract Value[] smooth(TimeSeries series, int level);
	
	protected Value[] merge(Value[] first, Value[] second, BinaryOperator<Value> function) {
		Value[] result = new Value[Math.max(first.length, second.length)];
		for (int i = 0; i < result.length; i++) {
			result[i] = (first.length <= i) ? second[i] : ((second.length <= i) ? first[i] : function.apply(first[i], second[i]));
		}
		return result;
	}
}
