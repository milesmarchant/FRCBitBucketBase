package org.frcbitbucketbase.control.profile;

import org.frcbitbucketbase.control.MovementVector;

public class TrapezoidalProfile extends Profile<MovementVector> {
	
	MovementVector values;
	Spline[] transSplines;
	Spline[] rotSplines;
	
	public TrapezoidalProfile(double translationAngle, double translationTarget, double maxVel, double maxAccel,
			double angularTarget, double maxAngularVel, double maxAngularAccel){
		values = new MovementVector.VectorBuilder()
			.translationAngle(translationAngle).position(translationTarget).velocity(maxVel).acceleration(maxAccel)
			.angle(angularTarget).angularVel(maxAngularVel).angularAccel(maxAngularAccel)
			.createVector();
		
		generateSplines(values);
		
	}

	@Override
	public MovementVector getOutput(long time) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void generateSplines(MovementVector values) {
		
//		/*
//		 * Indexes:
//		 * 0 - start
//		 * 1 - end of acceleration / start of flat
//		 * 2 - end of flat / start of deceleration
//		 * 3 - end of deceleration
//		 */
//		long[] transTimes = new long[4];
//		long[] rotTimes = new long[4];
				
		double acceleration = values.getValue("acceleration");
		double velocity = values.getValue("velocity");
		double transAccelDist = Math.pow(velocity, 2) / (2 * acceleration);
		if(transAccelDist <= values.getValue("position")/2){
			
			long accelEndTime = (long) (velocity / acceleration);
			double flatDist = values.getValue("position") - 2 * transAccelDist;
			long flatEndTime = accelEndTime + (long)(flatDist / velocity);
			long decelEndTime = flatEndTime + accelEndTime;
			
			Spline accelSpline = accelSpline(acceleration, accelEndTime);
			Spline flatSpline = flatSpline(transAccelDist, velocity, accelEndTime, flatEndTime);
			Spline decelSpline = decelSpline(transAccelDist + flatDist, velocity, acceleration, flatEndTime, decelEndTime);
			
			transSplines = new Spline[]{accelSpline, flatSpline, decelSpline};
			
		} else if(transAccelDist > values.getValue("position")/2){
			
			double newVelocity = Math.sqrt(acceleration * values.getValue("position"));
			double newTransAccelDist = values.getValue("position");
			long accelEndTime = (long) (newVelocity / acceleration);
			long decelEndTime = accelEndTime * 2;

			Spline accelSpline = accelSpline(acceleration, accelEndTime);
			Spline decelSpline = decelSpline(newTransAccelDist, newVelocity, acceleration, accelEndTime, decelEndTime);
			
			transSplines = new Spline[]{accelSpline, decelSpline};
			
		}
		
		double rotAcceleration = values.getValue("angularAcceleration");
		double rotVelocity = values.getValue("angularVel");
		double rotTransAccelDist = Math.pow(rotVelocity, 2) / (2 * rotAcceleration);
		if(rotTransAccelDist <= values.getValue("angle")/2){
			
			long rotAccelEndTime = (long) (rotVelocity / rotAcceleration);
			double rotFlatDist = values.getValue("angle") - 2 * transAccelDist;
			long rotFlatEndTime = rotAccelEndTime + (long)(rotFlatDist / rotVelocity);
			long rotDecelEndTime = rotFlatEndTime + rotAccelEndTime;
			
			Spline rotAccelSpline = accelSpline(rotAcceleration, rotAccelEndTime);
			Spline rotFlatSpline = flatSpline(rotTransAccelDist, rotVelocity, rotAccelEndTime, rotFlatEndTime);
			Spline rotDecelSpline = decelSpline(rotTransAccelDist + rotFlatDist, rotVelocity, rotAcceleration, rotFlatEndTime, rotDecelEndTime);
			
			transSplines = new Spline[]{rotAccelSpline, rotFlatSpline, rotDecelSpline};
			
		} else if(rotTransAccelDist > values.getValue("angle")/2){
			
			double newRotVelocity = Math.sqrt(rotAcceleration * values.getValue("angle"));
			double newRotTransAccelDist = values.getValue("angle");
			long rotAccelEndTime = (long) (newRotVelocity / rotAcceleration);
			long rotDecelEndTime = rotAccelEndTime * 2;

			Spline rotAccelSpline = rotAccelSpline(rotAcceleration, rotAccelEndTime);
			Spline rotDecelSpline = rotDecelSpline(newRotTransAccelDist, newRotVelocity, rotAcceleration, rotAccelEndTime, rotDecelEndTime);
			
			transSplines = new Spline[]{rotAccelSpline, rotDecelSpline};
			
		}
		
		
		
		
		
	}
	
	private Spline accelSpline(double acceleration, long endTime){
		return new Spline(time -> new MovementVector.VectorBuilder()
		.position(acceleration * Math.pow(time, 2) / 2)
		.velocity(acceleration * time)
		.acceleration(acceleration).createVector(),
		0,
		endTime);
	}
	
	private Spline flatSpline(double startDistance, double velocity, long startTime, long endTime){
		return new Spline(time -> new MovementVector.VectorBuilder()
		.position(startDistance + velocity * (time-startTime))
		.velocity(velocity)
		.acceleration(0).createVector(),
		startTime,
		endTime);
	}
	
	private Spline decelSpline(double startDistance, double velocity, double deceleration, long startTime, long endTime){
		return new Spline(time -> new MovementVector.VectorBuilder()
		.position(startDistance + (velocity * time-startTime) - deceleration * Math.pow(time-startTime, 2) / 2)
		.velocity(velocity - deceleration * (time-startTime))
		.acceleration(-deceleration).createVector(),
		startTime,
		endTime);
	}
	
	//uses non-rot var names
	private Spline rotAccelSpline(double acceleration, long endTime){
		return new Spline(time -> new MovementVector.VectorBuilder()
		.position(acceleration * Math.pow(time, 2) / 2)
		.velocity(acceleration * time)
		.acceleration(acceleration).createVector(),
		0,
		endTime);
	}
	
	//uses non-rot var names
	private Spline rotFlatSpline(double startDistance, double velocity, long startTime, long endTime){
		return new Spline(time -> new MovementVector.VectorBuilder()
		.position(startDistance + velocity * (time-startTime))
		.velocity(velocity)
		.acceleration(0).createVector(),
		startTime,
		endTime);
	}
	
	//uses non-rot var names
	private Spline rotDecelSpline(double startDistance, double velocity, double deceleration, long startTime, long endTime){
		return new Spline(time -> new MovementVector.VectorBuilder()
		.position(startDistance + (velocity * time-startTime) - deceleration * Math.pow(time-startTime, 2) / 2)
		.velocity(velocity - deceleration * (time-startTime))
		.acceleration(-deceleration).createVector(),
		startTime,
		endTime);
	}

	@Override
	public void regenerateSplines(double drive) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean finished(long time) {
		// TODO Auto-generated method stub
		return false;
	}

}
