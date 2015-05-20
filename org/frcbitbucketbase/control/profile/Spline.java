package org.frcbitbucketbase.control.profile;

import java.util.function.LongFunction;

import org.frcbitbucketbase.control.MovementVector;

public abstract class Spline{
	
	LongFunction<MovementVector>[] functions;
	long startTime;
	long endTime;
	
	public Spline(LongFunction<MovementVector>[] functions, long startTime, long endTime){
		this.functions = functions;
		this.endTime = endTime;
	}
	
	public MovementVector calculate(int order, long time){
		return functions[order-1].apply(time);
	}
	
	public long getStartTime(){
		return startTime;
	}
	
	public long getEndTime(){
		return endTime;
	}
}