package bachelorthesis.utility;

import bachelorthesis.data.Value;

public final class Configurations {
	
	//Sudden Shift
	public static final int SUDDEN_SHIFT_SMOOTHING_LEVEL = 0;
	public static final Value SUDDEN_SHIFT_THRESHOLD = new Value(0.7); //percentage of mean, which the change have to reach to count as interesting 
	public static final float SUDDEN_SHIFT_WEIGHT = 2f; //base weight for changes, will be doubled for strong change in one step and reduced for huge quantities of interesting points
	public static final boolean SUDDEN_SHIFT_IGNORE_ZEROES_FOR_MEAN = true; //zeros will be ignored for the mean, used to calculate the final threshold
	
	//First Occurence
	public static final int FIRST_OCCURENCE_SMOOTHING_LEVEL_DETECTION = 0;
	public static final int FIRST_OCCURENCE_SMOOTHING_LEVEL_CONFIRMATION = 3; //used for confirmation, that a zero or small value in the first year is not just a break
	public static final Value FIRST_OCCURENCE_THRESHOLD = new Value(0.02); //percentage of the max value of the series
	public static final float FIRST_OCCURENCE_WEIGHT = 1;
	
	//Extreme Cusum
	public static final int EXTREME_CUSUM_SMOOTHING_LEVEL = 0;
	public static final Value EXTREME_CUSUM_THRESHOLD = new Value(0.2); //percentage of mean to exceed, to be counted as interesting
	public static final Value EXTREME_CUSUM_WEIGHT_DAMPING  = new Value(0.05); //percentage of mean, which will be subtracted in each step
	public static final Value EXTREME_CUSUM_ATTENUATION = new Value(0.5); //multiplier for past values before counting them to the actual value
	public static final float EXTREME_CUSUM_WEIGHT = 4f; //base weight for changes, will be reduced for huge quantities of interesting points
	public static final boolean EXTREME_CUSUM_IGNORE_ZEROES_FOR_MEAN = true; //zeros will be ignored for the mean, used to calculate the final threshold and the damping
	
	//Comparison
	public static final float SIGNIFICANCE_THRESHOLD = 0.2f; //after multiplying a pair of weights this threshold has to be reached for the match to count
	public static final float SIMILARITY_THRESHOLD = 0.6f; //percentage of self-similarity-weight to be reached, to count as similar
	public static final float AMOUNT_WEIGHT  = 0f; //weight to be added additional for each match (results in much higher similarity for matches in huge quantity)
	
	//Bayesian
	public static final int BAYESIAN_TRUNCATE = -60;
	public static final float BAYESIAN_THRESHOLD = 0.2f; //interesting point will be recognized, if likelihood is higher as both neighbors or if greater than threshold
	
	//Years
	public static final float YEARS_THRESHOLD = 1.0f; //minimal weight to have in all defined years to be returned as result

	public static final boolean SETTLE = true; //if weights should be reduced in cases of huge quantities of interesting points
	
	//Time Series Filter
	public static final Value TIME_SERIES_MIN_COUNT = new Value(1000000); //filters Time Series with a lower summed count while searching for similar Time Series or Time Series with year hits
	
	//Files
	public static final int RESULT_FILE_LINE_LIMIT = 1000000; //new result-file will be started when reaching this line-count
}
