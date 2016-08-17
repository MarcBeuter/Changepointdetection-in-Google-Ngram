package bachelorthesis.methods;

import static bachelorthesis.utility.Configurations.*;

import bachelorthesis.data.DataPoint;
import bachelorthesis.data.DataPointMap;
import bachelorthesis.data.Result;

public class ComparisonMethod {
	
	Result reference;
	float threshold;
	
	public ComparisonMethod(Result reference) {
		this.reference = reference;
		threshold = compare(reference, true).getSimilarity() * SIMILARITY_THRESHOLD;
	}

	public Result compare(Result target, boolean returnAllResults) {
		calculateSimilarPoints(target);
		calculateSimilarity(target);
		return (returnAllResults || target.getSimilarity() >= threshold) ? target : null;
	}

	private void calculateSimilarPoints(Result target) {
		DataPointMap targetMap = target.getInterestingPoints();
		target.setRelatedTerm(reference.getTerm());
		for (DataPoint point : reference.getInterestingPoints().pointSet()) {
			if (targetMap.contains(point)) {
				DataPoint targetPoint = targetMap.getPoint(point.getKey());
				targetPoint.multiplyWeight(point);
				if (targetPoint.getWeight() >= SIGNIFICANCE_THRESHOLD) {
					target.addRelatedPoint(targetPoint);
				}
			}
		}
	}

	private void calculateSimilarity(Result target) {
		float sum = 0;
		for (DataPoint point : target.getRelatedPoints().pointSet()) {
			sum += point.getWeight() + AMOUNT_WEIGHT;
		}
		target.setSimilarity(sum);
	}
}
