package bachelorthesis.utility;

import java.math.MathContext;
import java.math.RoundingMode;

public final class Constants {
	
	//ngram line indexes
	public final static int TERM_INDEX = 0;
	public final static int YEAR_INDEX = 1;
	public final static int COUNT_INDEX = 2;
	public final static int BOOKCOUNT_INDEX = 3;
	
	//year boundaries
	public final static int EARLIEST_YEAR = 1800;
	public final static int LATEST_YEAR = 2008;
	
	public final static MathContext SCALE = new MathContext(10, RoundingMode.HALF_UP);
	
	//folderNames
	public final static String SOURCE_FOLDER = "NewFileInput";
	public final static String NGRAM_FOLDER = "NGrams";
	public final static String NGRAM_TOTALS_FOLDER = "NGramsTotals";
	public final static String RESULT_FOLDER = "Results";
	
	//modes
	public static final String IMPORT_COMMAND = "-import";
	public static final String CALCULATE_POINTS = "-points";
	public static final String CALCULATE_SIMILAR = "-similar";
	public static final String CALCULATE_YEARS = "-years";
	public static final String HELP = "-help";
	public static final String[] MODES = {CALCULATE_POINTS,
			CALCULATE_SIMILAR, CALCULATE_YEARS, IMPORT_COMMAND, HELP};

	//detection modes
	public static final String COMBINED_DETECTION = "-combinedDetection";
	public static final String BAYESIAN_DETECTION = "-bayesian";
	public static final String EXTREME_CUSUM = "-cusum";
	public static final String SUDDEN_SHIFT = "-shift";
	public static final String FIRST_OCCURENCE = "-first";
	public static final String[] DETECTION_MODES = {COMBINED_DETECTION, BAYESIAN_DETECTION,
			EXTREME_CUSUM, SUDDEN_SHIFT, FIRST_OCCURENCE};
	
	//smoothing modes
	public static final String MOVING_AVERAGE = "-movingAverage";
	public static final String[] SMOOTHING_MODES = {MOVING_AVERAGE};
	
	//print modes
	public static final String SHELL = "-shell";
	public static final String FILE = "-file";
	public static final String FILE_AND_CONSOLE = "-bothOutputs";
	public static final String[] PRINT_MODES = {SHELL, FILE, FILE_AND_CONSOLE};
	
	//languages
	public static final String ENGLISH = "eng";
	public static final String AMERICAN_ENGLISH = "eng-us";
	public static final String BRITISH_ENGLISH = "eng-gb";
	public static final String CHINESE_SIMPLIFIED = "chi-sim";
	public static final String FRENCH = "fre";
	public static final String GERMAN = "ger";
	public static final String HEBREW = "heb";
	public static final String ITALIAN = "ita";
	public static final String RUSSIAN = "rus";
	public static final String SPANISH = "spa";
	public static final String TEST_LANGUAGE = "test";
	
	public static final String[] LANGUAGES = {ENGLISH, AMERICAN_ENGLISH, BRITISH_ENGLISH,
			CHINESE_SIMPLIFIED, FRENCH, GERMAN, HEBREW, ITALIAN, RUSSIAN, SPANISH, TEST_LANGUAGE
	};
}
