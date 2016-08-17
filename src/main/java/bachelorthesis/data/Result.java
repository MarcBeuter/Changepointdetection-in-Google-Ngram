package bachelorthesis.data;

import static bachelorthesis.utility.Configurations.SETTLE;

/**
 * Representation of detection results related to a term.
 *  In case of comparison of two terms contains related points of the terms and the reference term.
 * @author Marc
 *
 */
public class Result {
	
	private final String term;
	private DataPointMap interestingPoints;
	private DataPointMap relatedPoints;
	private String comparisonTerm;
	private float similarity;
	
	public Result(String term) {
		this.term = term;
		interestingPoints = new DataPointMap();
		relatedPoints = new DataPointMap();
	}

	public String getTerm() {
		return term;
	}

	public void setInterestingPoint(DataPoint dataPoint) {
		interestingPoints.put(dataPoint);
	}
	
	public void addInterestingPoint(int year) {
		addInterestingPoint(new DataPoint(year));
	}
	
	public void addInterestingPoint(DataPoint dataPoint) {
		interestingPoints.add(dataPoint);
	}
	
	public void removeInterestingPoint(DataPoint dataPoint) {
		removeInterestingPoint(dataPoint.getYear());
	}
	
	public void removeInterestingPoint(int year) {
		interestingPoints.remove(year);
	}
	
	public void addRelatedPoint(DataPoint dataPoint) {
		relatedPoints.add(dataPoint);
	}
	
	public String getRelatedTerm() {
		return comparisonTerm;
	}
	
	public void setRelatedTerm(String term) {
		comparisonTerm = term;
	}
	
	public DataPointMap getRelatedPoints() {
		return relatedPoints;
	}
	
	/**
	 * used for combination of two results, if more than one detection method is used
	 */
	public void combineAndAdd(Result result) {
		if (result.getTerm().equals(this.term)) {
			for (DataPoint point : result.getInterestingPoints().pointSet()) {
				addInterestingPoint(point);
			}
		}
	}

	/**
	 * reduces weigths in relation to number of interesting points (to prevent granted similarity to terms with a huge number of interesting points)
	 */
	public void settle() {
		if (SETTLE) {
			int count = interestingPoints.size();
			interestingPoints.forEach((year, weight) -> interestingPoints.put(year, (float) (weight / Math.sqrt(count))));
		}
	}

	public float getSimilarity() {
		return similarity;
	}

	public void setSimilarity(float similarity) {
		this.similarity = similarity;
	}

	public DataPointMap getInterestingPoints() {
		return interestingPoints;
	}
}
