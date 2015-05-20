package org.frcbitbucketbase.control.profile;

import org.frcbitbucketbase.control.MovementVector;

public class TrapezoidalProfile<T> extends Profile<T> {
	
	MovementVector values;
	
	public TrapezoidalProfile(double translationAngle, double translationTarget, double maxVel, double maxAccel,
			double angularTarget, double maxAngularVel, double maxAngularAccel){
		values = new MovementVector.VectorBuilder()
			.translationAngle(translationAngle).position(translationTarget).velocity(maxVel).acceleration(maxAccel)
			.angle(angularTarget).angularVel(maxAngularVel).angularAccel(maxAngularAccel)
			.createVector();
		
		generateSplines(values);
		
	}

	@Override
	public T getOutput(long time) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Spline[] generateSplines(MovementVector values) {
		return null;
		// TODO Auto-generated method stub
		
	}

	@Override
	public Spline[] regenerateSplines(double drive) {
		return null;
		// TODO Auto-generated method stub
	}

	@Override
	public boolean finished(long time) {
		// TODO Auto-generated method stub
		return false;
	}

}
