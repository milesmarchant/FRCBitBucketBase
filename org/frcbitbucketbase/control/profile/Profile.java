package org.frcbitbucketbase.control.profile;

import java.util.function.LongFunction;

/**
 * An base class for motion profiles.
 * 
 * @author Miles Marchant
 * @version 0.9
 *
 */
public abstract class Profile<T> {	
	
	public Profile(){}
	
	public abstract T getOutput(long time);

	public abstract void generateSplines(double drive);
	
	public boolean finished(long time) {
		return false;
	}
	
	class Spline{
		
		LongFunction<Double> calculator;
		long startTime;
		long endTime;
		
		public Spline(LongFunction<Double> calculator, long startTime, long endTime){
			this.calculator = calculator;
			this.endTime = endTime;
		}
		
		public double calculate(long time){
			return calculator.apply(time);
		}
		
		public long getStartTime(){
			return startTime;
		}
		
		public long getEndTime(){
			return endTime;
		}
		
	}

}
