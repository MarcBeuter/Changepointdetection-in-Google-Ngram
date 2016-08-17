package bachelorthesis.data;

import static bachelorthesis.utility.Constants.*;
import static bachelorthesis.utility.ExceptionHandler.handleReflectiveException;

import java.util.ArrayList;

import bachelorthesis.methods.UtilityMethods;
import bachelorthesis.methods.smoothing.SmoothingMethod;

/**
 * representation of the values connected to a term
 */
public class TimeSeries {
	
	private String term;
	private SmoothingMethod smoothingMethod;
	private ArrayList<Value[]> values = new ArrayList<Value[]>();
	private ArrayList<Value> means = new ArrayList<Value>();
	private ArrayList<Value> meansWithoutZeroes = new ArrayList<Value>();
	
	/**
	 * Creates an empty time series
	 */
	public TimeSeries(String term) {
		this.term = term;
		values.add(new Value[LATEST_YEAR - EARLIEST_YEAR + 1]);
	}
	
	public String getTerm() {
		return term;
	}
	
	public void addValue(int year, Value value) {
		values.get(0)[year - EARLIEST_YEAR] = value;
	}
	
	public Value getValue(int year, int smoothingLevel) {
		return getAllValues(smoothingLevel)[year - EARLIEST_YEAR];
	}

	public TimeSeries fillUp() {
		Value[] unsmoothed = values.get(0);
		for (int i = 0; i < unsmoothed.length; i++) {
			if (unsmoothed[i] == null) { 
				unsmoothed[i] = new Value(0);
			}
		}
		return this;
	}

	public Value[] getAllValues(int smoothingLevel) {
		for (int i = values.size(); i <= smoothingLevel; i++) {
			values.add(i, smoothingMethod.smooth(this, i));
		}
		return values.get(smoothingLevel);
	}

	public Value getMean(int smoothingLevel, boolean ignoreZeroes) {
		ArrayList<Value> list;
		if (ignoreZeroes) {
			list = meansWithoutZeroes;
		} else {
			list = means;
		}
		for (int i = list.size(); i <= smoothingLevel; i++) {
			list.add(i, UtilityMethods.calculateMean(this, i, ignoreZeroes));
		}
		return list.get(smoothingLevel);
	}

	public void setSmoothingMethod(SmoothingMethod method) {
		smoothingMethod = method;
	}
	
	public SmoothingMethod getSmoothingMethod() {
		SmoothingMethod result = null;
		try {
			result = smoothingMethod.getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			handleReflectiveException(e);
		}
		return result;
	}
}
