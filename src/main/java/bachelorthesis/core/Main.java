package bachelorthesis.core;

import static bachelorthesis.fileAccessor.Initializer.initializeDirectories;
import static bachelorthesis.fileAccessor.Writers.closeWriter;
import static bachelorthesis.fileAccessor.Writers.getResultWriter;
import static bachelorthesis.utility.Configurations.RESULT_FILE_LINE_LIMIT;
import static bachelorthesis.utility.Constants.*;
import static bachelorthesis.utility.ExceptionHandler.handleReflectiveException;
import static bachelorthesis.utility.StringHandler.*;

import java.io.BufferedWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Stream;

import bachelorthesis.data.DataPoint;
import bachelorthesis.data.Result;
import bachelorthesis.fileAccessor.Writers;
import bachelorthesis.fileTransformer.NewFilesImporter;
import bachelorthesis.methods.detection.DetectionMethods;
import bachelorthesis.methods.smoothing.SmoothingMethods;

/**
 * Handles command line arguments and sets the operation mode
 */
public class Main {
	
	private static Method mode;
	private static DetectionMethods detectionMode;
	private static SmoothingMethods smoothingMode;
	private static String language;
	private static Calculator calculator;
	private static Consumer<String> printer;
	private static BufferedWriter resultWriter = null;
	private static int outputLineCount = 0;
	private static int outputFileCount = 1;

	public static void main(String[] args) {
		initializeDirectories();
		if (handlePrintMode(args)) {
			args = Arrays.copyOfRange(args, 1, args.length);
		}
		if (handleMode(args)) {
			print(args[0]);
			args = Arrays.copyOfRange(args, 1, args.length);
		}
		if (handleDetectionMode(args)) {
			print(args[0]);
			args = Arrays.copyOfRange(args, 1, args.length);
		}
		if (handleSmoothingMode(args)) {
			print(args[0]);
			args = Arrays.copyOfRange(args, 1, args.length);
		}
		if (handleLanguage(args)) {
			args = Arrays.copyOfRange(args, 1, args.length);
		}
		try {
			if (args.length == 0) {
				if (mode == null) {
					print(NO_TERMS);
					help();
				} else if (mode.getName().equals("help") || mode.getName().equals("importNewFiles")) {
						mode.invoke(null, new Object[0]);
				}
			} else {
				calculator = new Calculator(args);
				calculator.setDetectionMethod(detectionMode);
				calculator.setSmoothing(smoothingMode);
				calculator.setLanguage(language);
				mode.invoke(null, new Object[0]);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			handleReflectiveException(e);
		}
		if (resultWriter != null) {
			closeWriter(resultWriter);
		}
	}
	
	public static void help() {
		printHelp();
	}

	private static boolean handleMode(String[] args) {
		if (args.length != 0) {
			try {
				switch (args[0]) {
				case IMPORT_COMMAND: mode = Main.class.getMethod("importNewFiles", new Class[0]); return true;
				case CALCULATE_POINTS: mode = Main.class.getMethod("calculatePoints", new Class[0]);return true;
				case CALCULATE_SIMILAR: mode = Main.class.getMethod("calculateSimilar", new Class[0]); return true;
				case CALCULATE_YEARS: mode = Main.class.getMethod("calculateYears", new Class[0]); return true;
				case HELP: mode = Main.class.getMethod("help", new Class[0]); return true;
				default: print(standardMode(CALCULATE_POINTS));
					mode = Main.class.getMethod("calculatePoints", new Class[0]);
				}
			} catch (NoSuchMethodException | SecurityException e) {
				handleReflectiveException(e);;
			}
		}
		return false;
	}
	
	private static boolean handlePrintMode(String[] args) {
		if (args.length != 0) {
			switch (args[0]) {
			case SHELL: setShellOutput(); return true;
			case FILE: setFileOutput(false);return true;
			case FILE_AND_CONSOLE: setFileOutput(true);return true;
			default: ;
			}
		}
		setShellOutput();
		return false;
	}

	private static void setShellOutput() {
		printer = line -> System.out.println(line);
	}
	
	private static void setFileOutput(boolean withShell) {
		Date now = new Date();
		resultWriter = getResultWriter(getResultFileName(outputFileCount++, now));
		printer = line -> {
			Writers.write(resultWriter, line.toString() + "\n");
			if (outputLineCount++ > RESULT_FILE_LINE_LIMIT) {
				closeWriter(resultWriter);
				resultWriter = getResultWriter(getResultFileName(outputFileCount++, now));
				outputLineCount = 0;
			}
			if (withShell) {
				System.out.println(line);
			}
		};
	}

	private static boolean handleDetectionMode(String[] args) {
		if (args.length != 0) {
			switch (args[0]) {
			case COMBINED_DETECTION: detectionMode = DetectionMethods.CombinedDetection; return true;
			case EXTREME_CUSUM: detectionMode = DetectionMethods.Cusum;return true;
			case SUDDEN_SHIFT: detectionMode = DetectionMethods.SuddenShift; return true;
			case FIRST_OCCURENCE: detectionMode = DetectionMethods.FirstOccurence; return true;
			case BAYESIAN_DETECTION: detectionMode = DetectionMethods.BayesianDetection; return true;
			default: print(standardDetectionMode(COMBINED_DETECTION));
			detectionMode = DetectionMethods.CombinedDetection;
			}
		}
		return false;
	}

	private static boolean handleSmoothingMode(String[] args) {
		if (args.length != 0) {
			switch (args[0]) {
			case MOVING_AVERAGE: smoothingMode = SmoothingMethods.SimpleMovingAverage; return true;
			default: smoothingMode = SmoothingMethods.SimpleMovingAverage;
			}
		}
		return false;
	}
	
	private static boolean handleLanguage(String[] args) {
		if (args.length != 0) {
			if (isLanguageCommand(args[0])) {
				language = args[0].substring(1);
				print(language);
				return true;
			} else {
				language = ENGLISH;
				print(standardLanguage(language));
			}
		}
		return false;
	}

	public static void calculateYears() {
		Stream<Result> results = calculator.calculateYears();
		print("");
		results.forEach(result -> {
			print(result.getTerm());
		});
		results.close();
	}

	public static void calculatePoints() {
		Stream<Result> results = calculator.calculate();
		results.forEach(result -> {
			print("");
			print(result.getTerm());
			for (DataPoint point : result.getInterestingPoints().pointSet()) {
				print(point);
			}
		});	
		results.close();
	}

	public static void calculateSimilar() {
		Stream<Result> results = calculator.calculateAndCompare();
		Map<String, Float> summary = new HashMap<String, Float>();
		results.forEach(result -> {
			printformOfSimilarityResult(result).forEach(line -> print(line));
			summary.put(result.getTerm(), result.getSimilarity());
		});
		print("");
		for (Entry<String, Float> entry : summary.entrySet()) {
			print(entry.getKey() + "\t" + entry.getValue());
		}
		results.close();
	}

	public static void importNewFiles() {
		NewFilesImporter stripper = new NewFilesImporter();
		stripper.readInNewFiles();
	}
	
	public static void print(Object o) {
		printer.accept(o.toString());
	}
}
