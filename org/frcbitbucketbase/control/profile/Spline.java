package org.frcbitbucketbase.control.profile;

import java.util.List;
import java.util.function.LongFunction;

import org.frcbitbucketbase.control.MovementVector;

public class Spline{
	
	LongFunction<MovementVector> functions;
	private long startTime;
	private long endTime;
	
	public Spline(LongFunction<MovementVector> functions, long startTime, long endTime){
		this.functions = functions;
		this.endTime = endTime;
	}
	
	public MovementVector calculate(int order, long time){
		return functions.apply(time);
	}
	
	public long getStartTime(){
		return startTime;
	}
	
	public long getEndTime(){
		return endTime;
	}
}