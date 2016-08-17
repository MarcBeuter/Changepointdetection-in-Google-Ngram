package bachelorthesis.methods;

import static bachelorthesis.utility.Configurations.YEARS_THRESHOLD;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import bachelorthesis.data.DataPointMap;
import bachelorthesis.data.Result;

public class YearMethod {
	
	List<Integer> years = new LinkedList<Integer>();

	public YearMethod(List<String> years) {
		years.forEach(year -> {
			if (StringUtils.isNumeric(year)) {
				this.years.add(Integer.valueOf(year));
			}
		});
	}

	public Result calculateHits(Result series) {
		Iterator<Integer> iterator = years.iterator();
		DataPointMap interestingPoints = series.getInterestingPoints();
		while (iterator.hasNext()) {
			Integer next = iterator.next();
			if (!interestingPoints.containsKey(next) || interestingPoints.get(next) < YEARS_THRESHOLD) {
				return null;
			}
		}
		return series;
	}

}
