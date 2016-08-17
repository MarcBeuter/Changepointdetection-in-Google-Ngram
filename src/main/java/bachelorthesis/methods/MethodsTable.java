package bachelorthesis.methods;

import static bachelorthesis.utility.ExceptionHandler.handleReflectiveException;

import java.util.EnumMap;

import bachelorthesis.methods.detection.CombinedDetection;
import bachelorthesis.methods.detection.DetectionMethod;
import bachelorthesis.methods.detection.DetectionMethods;
import bachelorthesis.methods.detection.FirstOccurrence;
import bachelorthesis.methods.detection.SuddenShift;
import bachelorthesis.methods.detection.bayesian.BayesianDetection;
import bachelorthesis.methods.detection.cusum.ExtremeCusum;
import bachelorthesis.methods.smoothing.SimpleMovingAverage;
import bachelorthesis.methods.smoothing.SmoothingMethod;
import bachelorthesis.methods.smoothing.SmoothingMethods;

/**
 * Table of detection- and smoothing-methods
 * @author Marc
 *
 */
public class MethodsTable {
	
	private EnumMap<SmoothingMethods, Class<? extends SmoothingMethod>> smoothingMethods;
	private EnumMap<DetectionMethods, Class<? extends DetectionMethod>> detectionMethods;
	
	private static MethodsTable table = null;
	
	public static MethodsTable getInstance() {
		if (table == null) {
			table = new MethodsTable();
		}
		return table;
	}

	public SmoothingMethod getSmoothingMethod(SmoothingMethods method) {
		SmoothingMethod result = null;
		if (method != null) {
			try {
				result = smoothingMethods.get(method).newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				handleReflectiveException(e);;
			}
		}
		return result;
	}

	public DetectionMethod getDetectionMethod(DetectionMethods method) {
		DetectionMethod result = null;
		if (method != null) {
			try {
				result = detectionMethods.get(method).newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				handleReflectiveException(e);;
			}
		}
		return result;
	}
	
	private MethodsTable() {
		detectionMethods = new EnumMap<DetectionMethods, Class<? extends DetectionMethod>>(DetectionMethods.class);
		smoothingMethods = new EnumMap<SmoothingMethods, Class<? extends SmoothingMethod>>(SmoothingMethods.class);
		
		detectionMethods.put(DetectionMethods.SuddenShift, SuddenShift.class);
		detectionMethods.put(DetectionMethods.FirstOccurence, FirstOccurrence.class);
		detectionMethods.put(DetectionMethods.Cusum, ExtremeCusum.class);
		detectionMethods.put(DetectionMethods.CombinedDetection, CombinedDetection.class);
		detectionMethods.put(DetectionMethods.BayesianDetection, BayesianDetection.class);
		
		smoothingMethods.put(SmoothingMethods.SimpleMovingAverage, SimpleMovingAverage.class);
	}
}
