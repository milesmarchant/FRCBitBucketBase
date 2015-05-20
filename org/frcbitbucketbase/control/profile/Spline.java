package org.frcbitbucketbase.control.profile;

import java.util.function.LongFunction;

public abstract class Spline{
	
	LongFunction<Double>[] functions;
	long startTime;
	long endTime;
	
	public Spline(LongFunction<Double>[] functions, long startTime, long endTime){
		this.functions = functions;
		this.endTime = endTime;
	}
	
	public double calculate(int order, long time){
		return functions[order-1].apply(time);
	}
	
	public long getStartTime(){
		return startTime;
	}
	
	public long getEndTime(){
		return endTime;
	}
	
}