package bachelorthesis.methods.detection.bayesian;

import static bachelorthesis.methods.UtilityMethods.*;
import static bachelorthesis.utility.Configurations.*;
import static bachelorthesis.utility.Constants.EARLIEST_YEAR;

import java.util.Arrays;
import java.util.Random;
import java.util.function.DoubleFunction;
import java.util.zip.DataFormatException;

import org.apache.commons.lang3.ArrayUtils;

import bachelorthesis.data.DataPoint;
import bachelorthesis.data.Result;
import bachelorthesis.data.Value;
import bachelorthesis.methods.detection.DetectionMethod;
import bachelorthesis.utility.ExceptionHandler;

/**
 * Translated from a Python implementation with following license
 * 
 * The MIT License (MIT)

Copyright (c) 2014 Johannes Kulick

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 * 
 *
 */
public class BayesianDetection extends DetectionMethod {

	@Override
	protected Result detect() {
		double[] logLikelihoods = expSum(offlineCpd(series.getAllValues(0)));
		for (int i = 1; i < logLikelihoods.length - 1; i++) {
			if  (logLikelihoods[i] >= BAYESIAN_THRESHOLD) {
				result.addInterestingPoint(i + EARLIEST_YEAR);
			}
			if (logLikelihoods[i - 1] < logLikelihoods[i] && logLikelihoods[i] >= logLikelihoods[i + 1]) {
				result.addInterestingPoint(new DataPoint(i + EARLIEST_YEAR, 0.5f));
			}
		}
		return result;
	}

	/**
	 * Generates time series for code testing
	 * @param numberOfParts number of parts with constant mean
	 * @param samePointsMin	min number of points per part
	 * @param samePointsMax max number of points per part
	 * @param maxDeviation maximal deviation of mean (e.g. 0.1)
	 * @return generated time series with given specs
	 */
	private Value[] generate(int numberOfParts, int samePointsMin, int samePointsMax, double maxDeviation) {
		Value[] data = new Value[0];
		int sum = EARLIEST_YEAR;
		for (int i = 0; i < numberOfParts; i++) {
			Random rand = new Random();
			int numberOfPoints = rand.nextInt(samePointsMax - samePointsMin + 1) + samePointsMin;
			double mean = rand.nextDouble();
			double var = rand.nextDouble() * maxDeviation;
			System.out.println(sum + " - " + (sum + numberOfPoints) + "\t" + mean);
			sum += numberOfPoints;
			Value[] add = new Value[numberOfPoints];
			for (int j = 0; j < numberOfPoints; j++) {
				add[j] = new Value(mean + ((rand.nextDouble() * 2 - 1) * var));
			}
			data = ArrayUtils.addAll(data, add);
		}
		return data;
	}

	private double[][] offlineCpd(Value[] data) {
		int n = data.length;
		double[] Q = new double[n];
		double[] g = new double[n];
		double[] G = new double[n];
		double[][] P = new double[n][n];
		
		Arrays.fill(g, Math.log(1.d/(data.length + 1)));
		G[0] = g[0];
		for (int i = 1; i < G.length; i++) {
			G[i] = Math.log((Math.exp(G[i - 1]) + Math.exp(g[i])));
		}
		for (double[] array : P) {
			Arrays.fill(array, Double.NEGATIVE_INFINITY);
		}
		
		P[n - 1][n - 1] = gaussianObsLogLikelihood(data, n - 1,  n);
		Q[n - 1] = P[n - 1][n - 1];
		
		for (int t = n - 2; t >= 0; t--) {
			double p_next_cp = Double.NEGATIVE_INFINITY;
			for (int s = t; s < n - 1; s++) {
				P[t][s] = gaussianObsLogLikelihood(data, t, s + 1);
				double summand = P[t][s] + Q[s + 1] + g[s + 1 - t];
				p_next_cp = Math.log((Math.exp(p_next_cp) + Math.exp(summand)));
				if (summand - p_next_cp < BAYESIAN_TRUNCATE) {
					break;
				}
			}
			P[t][n - 1] = gaussianObsLogLikelihood(data, t, n);
			double antiG;
			if (G[n - 1 - t] < -1e-15) {
				antiG = Math.log(1.d - Math.exp(G[n - 1 - t]));
			} else {
				antiG = Math.log(-G[n - 1 - t]);
			}
			Q[t] = Math.log((Math.exp(p_next_cp) + Math.exp(P[t][n - 1] + antiG)));
		}
		
		double[][] Pcp = new double[n - 1][n - 1];
		for (double[] array : Pcp) {
			Arrays.fill(array, Double.NEGATIVE_INFINITY);
		}
		for (int t = 0; t < n - 1; t++) {
			Pcp[0][t] = P[0][t] + Q[t + 1] + g[t] - Q[0];
			if (Double.isNaN(Pcp[0][t])) {
				Pcp[0][t] = Double.NEGATIVE_INFINITY;
			}
		}
		for (int j = 1; j < n - 1; j++) {
			for (int t = j; t < n - 1; t++) {
				double[] tmp_cond = copyOfRange(Pcp[j - 1], j - 1, t);
				
				tmp_cond = add(tmp_cond, getSameEntryOfAllArrays(copyOfRange(P, j, t + 1), t));
				double summand = Q[t + 1];
				tmp_cond = forEach(tmp_cond, value -> value + summand);
				tmp_cond = add(tmp_cond, copyOfRange(g, 0, t - j + 1));
				double[] negativePart = forEach(copyOfRange(Q, j, t + 1), value -> -value);
				tmp_cond = add(tmp_cond, negativePart);
				
				double[] tempArray = forEach(tmp_cond, value -> Math.exp(value));
				Pcp[j][t] = Math.log(sum(tempArray));
				if (Double.isNaN(Pcp[j][t])) {
					Pcp[j][t] = Double.NEGATIVE_INFINITY;
				}
			}
		}
		return Pcp;
	}

	private double[] add(double[] array1, double[] array2) {
		if (array1.length != array2.length) {
			ExceptionHandler.handleOwnException(new DataFormatException("Arrays are of different length"));
		}
		double[] result = new double[array1.length];
		for (int i = 0; i < array1.length; i++) {
			result[i] = array1[i] + array2[i];
		}
		return result;
	}

	private double gaussianObsLogLikelihood(Value[] data, int t, int s) {
		s++;
		double n = s - t;
		double mean = calculateMean(data, t, s).doubleValue();
		
		double muT = mean * n / (1 + n);
		double nuT = n + 1;
		double alphaT = n / 2 + 1;
		double betaT = calculateQuadSum(data, t, s).doubleValue() / 2 + 1 + (n / (n + 1)) * Math.pow(mean, 2) / 2;
		double scale = (betaT * (nuT + 1)) / (alphaT * nuT);
		
		double prob = getProb(data, t, s, muT, nuT, scale);
		double lgA = getLgA(nuT, scale);
		return n * lgA - (nuT + 1) / 2 * prob;
	}

	private double getLgA(double nuT, double scale) {
		double result = Gamma.logGamma((nuT + 1) / 2);
		result -= Math.log(Math.sqrt(Math.PI * nuT * scale));
		result -= Gamma.logGamma(nuT / 2);
		return result;
	}

	private double getProb(Value[] data, int t, int s, double muT, double nuT, double scale) {
		double[] array = Value.getDoubleArray(copyOfRange(data, t, s));
		array = forEach(array, value -> Math.pow(value - muT, 2));
		array = forEach(array, value -> value / (nuT * scale) + 1);
		array = forEach(array, value -> Math.log(value));
		return sum(array);
	}

	private double[] expSum(double[][] likelihoods) {
		double[] result = new double[likelihoods.length];
		for (double[] subLikelihood : likelihoods) {
			for (int i = 0; i < subLikelihood.length; i++) {
				result[i] = result[i] + Math.exp(subLikelihood[i]);
			}
		}
		return result;
	}

	private double sum(double[] array) {
		double result = 0;
		for (int i = 0; i < array.length; i++) {
			result += array[i];
		}
		return result;
	}

	private double[] forEach(double[] values, DoubleFunction<Double> function) {
		double[] result = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			result[i] = function.apply(values[i]);
		}
		return result;
	}

	private double[] getSameEntryOfAllArrays(double[][] arrays, int t) {
		double[] result = new double[arrays.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = arrays[i][t];
		}
		return result;
	}
	
	private Value[] copyOfRange(Value[] array, int from, int to) {
		return Arrays.copyOfRange(array, from, Math.min(to, array.length));
	}
	
	private double[] copyOfRange(double[] array, int from, int to) {
		return Arrays.copyOfRange(array, from, Math.min(to, array.length));
	}
	
	private double[][] copyOfRange(double[][] array, int from, int to) {
		return Arrays.copyOfRange(array, from, Math.min(to, array.length));
	}
}
