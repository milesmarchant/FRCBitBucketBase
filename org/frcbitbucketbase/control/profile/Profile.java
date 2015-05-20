package org.frcbitbucketbase.control.profile;

import org.frcbitbucketbase.control.KinematicController;


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
	
	public abstract void regenerateSplines(double drive);
	
	public abstract boolean finished(long time);

}
