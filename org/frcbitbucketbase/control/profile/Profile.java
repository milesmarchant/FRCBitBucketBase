package org.frcbitbucketbase.control.profile;


/**
 * An base class for motion profiles.
 * 
 * @author Miles Marchant
 * @version 0.9
 *
 */
public abstract class Profile<T> {	
	
	Spline[] splines;
	
	public Profile(){}
	
	public abstract T getOutput(long time);

	public abstract void generateSplines(double drive);
	
	public boolean finished(long time) {
		return false;
	}

}
