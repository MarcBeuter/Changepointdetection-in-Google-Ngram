package bachelorthesis.methods.detection;

import bachelorthesis.data.Result;
import bachelorthesis.methods.MethodsTable;

public class CombinedDetection extends DetectionMethod{

	@Override
	protected Result detect() {
		Result firstOcc = MethodsTable.getInstance().getDetectionMethod(DetectionMethods.FirstOccurence).detect(series);
		Result suddenShifts = MethodsTable.getInstance().getDetectionMethod(DetectionMethods.SuddenShift).detect(series);
		Result extremeCusum = MethodsTable.getInstance().getDetectionMethod(DetectionMethods.Cusum).detect(series);
		result = firstOcc;
		result.combineAndAdd(suddenShifts);
		result.combineAndAdd(extremeCusum);
		return result;
	}
}
