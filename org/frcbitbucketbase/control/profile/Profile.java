package org.frcbitbucketbase.control.profile;

import org.frcbitbucketbase.control.KinematicController;
import org.frcbitbucketbase.control.MovementVector;


/**
 * An base class for motion profiles.
 * 
 * @author Miles Marchant
 * @version 0.9
 *
 */
public abstract class Profile<T> {	
	
	protected Spline[] splines;
	
	public Profile(){}
	
	public abstract T getOutput(long time);
	
	protected abstract Spline[] generateSplines(MovementVector values);
	
	public abstract Spline[] regenerateSplines(double drive);
	
	public abstract boolean finished(long time);
	
	public void setSplines(Spline[] splines){
		this.splines = splines;
	}

}
