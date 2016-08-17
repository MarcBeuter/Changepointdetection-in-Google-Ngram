package bachelorthesis.data;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

/**
 * Modified version of TreeMap to handle dataPoints with year as key and weight as value
 *
 */
public class DataPointMap extends TreeMap<Integer, Float> {

	private static final long serialVersionUID = 8529591230005434463L;
	
	public void add(DataPoint point) {
		if (this.contains(point)) {
			put(getPoint(point).addWeight(point));
		} else {
			put(point);
		}
	}
	
	public void put(DataPoint point) {
		put(point.getKey(), point.getValue());
	}

	public DataPoint getPoint(int key) {
		return new DataPoint(key, this.get(key));
	}
	
	private DataPoint getPoint(DataPoint point) {
		return getPoint(point.getKey());
	}
	
	public boolean contains(DataPoint point) {
		return this.containsKey(point.getKey());
	}
	
	public TreeSet<DataPoint> pointSet() {
		Set<Entry<Integer, Float>> set = super.entrySet();
		TreeSet<DataPoint> result = new TreeSet<DataPoint>();
		set.forEach(entry -> result.add(new DataPoint(entry.getKey(), entry.getValue())));
		return result;
	}
}
