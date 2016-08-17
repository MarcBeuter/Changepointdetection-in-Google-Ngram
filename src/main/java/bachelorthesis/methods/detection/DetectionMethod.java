package bachelorthesis.methods.detection;

import bachelorthesis.data.Result;
import bachelorthesis.data.TimeSeries;

public abstract class DetectionMethod {
	
	protected TimeSeries series;
	protected Result result;

	public Result detect(TimeSeries series) {
		this.series = series;
		result = new Result(series.getTerm());
		return detect();
	}

	protected abstract Result detect();
}
