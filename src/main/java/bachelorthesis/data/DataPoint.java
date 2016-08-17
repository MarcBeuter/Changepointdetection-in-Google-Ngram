package bachelorthesis.data;

import java.util.Map.Entry;

/**
 * Format to save a interesting point
 */
public class DataPoint implements Entry<Integer, Float>, Comparable<DataPoint> {
	private int year;
	private float weight;
	
	/**
	 * 	Sets weight to 1
	 */
	public DataPoint(int year) {
		this.year = year;
		weight = 1;
	}
	
	public DataPoint(int year, float weight) {
		this.year = year;
		this.weight = weight;
	}
	
	public int getYear() {
		return year;
	}
	
	public float getWeight() {
		return weight;
	}
	
	public DataPoint addWeight(DataPoint point) {
		return addWeight(point.getWeight());
	}
	
	public DataPoint addWeight(Float weight) {
		this.weight = this.weight + weight;
		return this;
	}
	
	public void multiplyWeight(DataPoint point) {
		multiplyWeight(point.getWeight());
	}
	
	public void multiplyWeight(Float weight) {
		this.weight *= weight;
	}
	
	@Override
	public boolean equals(Object o) {
		return (o instanceof DataPoint && ((DataPoint)o).getYear() == year);
	}
	
	@Override
	public String toString() {
		return year + "\t" + weight;
	}

	@Override
	public Integer getKey() {
		return getYear();
	}

	@Override
	public Float getValue() {
		return getWeight();
	}

	@Override
	public Float setValue(Float value) {
		weight = value;
		return weight;
	}

	public int compareTo(DataPoint value) {
		return this.getKey().compareTo(value.getKey());
	}
}
