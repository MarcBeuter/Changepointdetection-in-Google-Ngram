package bachelorthesis.data;

import static bachelorthesis.utility.Constants.SCALE;

import java.math.BigDecimal;

/**
 * Modified version of BigDecimal with set scale, and a view added features
 *	to round, methods like biggerThan, smallerThan, specific toString and equals
 */
public class Value extends BigDecimal {

	public static Value ZERO = new Value(0);
	public static Value ONE = new Value(1);
	public static Value MINUS = new Value(-1);
	
	
	BigDecimal value;

	private static final long serialVersionUID = -3564911828500987492L;

	public Value(int val) {
		super(val);
		super.setScale(SCALE.getPrecision(), SCALE.getRoundingMode());
	}

	public Value(String val) {
		super(val);
		super.setScale(SCALE.getPrecision(), SCALE.getRoundingMode());
	}

	public Value(BigDecimal val) {
		super(val.toPlainString());
		super.setScale(SCALE.getPrecision(), SCALE.getRoundingMode());
	}
	
	public Value(double val) {
		super(val);
		super.setScale(SCALE.getPrecision(), SCALE.getRoundingMode());
	}

	public Value round(int decimalPlace) {
		return new Value(this.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP));
	}

	public String toString() {
		return super.stripTrailingZeros().toPlainString();
	}
	
	public Value multiply(Value value) {
		return new Value(super.multiply(value, SCALE));
	}
	
	public Value divide(Value value) {
		return new Value(super.divide(value, SCALE));
	}
	
	public Value add(Value value) {
		return new Value(super.add(value));
	}
	
	public Value subtract(Value value) {
		return new Value(super.subtract(value));
	}
	
	public Value pow(int value) {
		return new Value(super.pow(value));
	}
	
	public Value min(Value value) {
		return new Value(super.min(value));
	}
	
	public Value max(Value value) {
		return new Value(super.max(value));
	}

	public Value abs() {
		return new Value(super.abs());
	}

	public boolean biggerEqualThan(Value value) {
		return this.compareTo(value) >= 0;
	}

	public boolean biggerThan(Value value) {
		return this.compareTo(value) > 0;
	}
	
	public static double[] getDoubleArray(Value[] array) {
		double[] result = new double[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = array[i].doubleValue();
		}
		return result;
	}
	
	public Value negate() {
		return multiply(MINUS);
	}
	
	@Override
	/**
	 * Returns true if the values are equal (compareTo(..) == 0)
	 */
	public boolean equals(Object o) {
		if (!(o instanceof Value)) {
            return false;
		} else {
			return this.compareTo((Value)o) == 0;
		}
	}
}
