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
	protected Spline[] generateSplines(MovementVector values) {
		
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
			Spline accelSpline = accelSpline(acceleration, accelEndTime);
//					new Spline(time -> new MovementVector.VectorBuilder()
//					.position(acceleration * Math.pow(time, 2) / 2)
//					.velocity(acceleration * time)
//					.acceleration(acceleration).createVector(),
//					0,
//					accelEndTime);
			
			double flatDist = values.getValue("position") - 2 * transAccelDist;
			long flatEndTime = accelEndTime + (long)(flatDist / velocity);
			Spline flatSpline = flatSpline(transAccelDist, velocity, accelEndTime, flatEndTime);
//					new Spline(time -> new MovementVector.VectorBuilder()
//					.position(transAccelDist + velocity * (time-accelEndTime))
//					.velocity(velocity)
//					.acceleration(0).createVector(),
//					accelEndTime,
//					flatEndTime);
			
			long decelEndTime = flatEndTime + accelEndTime;
			Spline decelSpline = decelSpline(transAccelDist + flatDist, velocity, acceleration, flatEndTime, decelEndTime);
//					new Spline(time -> new MovementVector.VectorBuilder()
//					.position(transAccelDist + flatDist + (velocity * time-flatEndTime) - acceleration * Math.pow(time-flatEndTime, 2) / 2)
//					.velocity(velocity - acceleration * (time-flatEndTime))
//					.acceleration(-acceleration).createVector(),
//					flatEndTime,
//					decelEndTime);
			
			transSplines = new Spline[]{accelSpline, flatSpline, decelSpline};
			
		} else if(transAccelDist > values.getValue("position")/2){
			
			double newVelocity = Math.sqrt(acceleration * values.getValue("position"));
			double newTransAccelDist = values.getValue("position");
			
			long accelEndTime = (long) (newVelocity / acceleration);
			Spline accelSpline = new Spline(time -> new MovementVector.VectorBuilder()
					.position(acceleration * Math.pow(time, 2) / 2)
					.velocity(acceleration * time)
					.acceleration(acceleration).createVector(),
					0,
					accelEndTime);
			
			long decelEndTime = accelEndTime * 2;
			Spline decelSpline = new Spline(time -> new MovementVector.VectorBuilder()
					.position(newTransAccelDist + (newVelocity * time-accelEndTime) - acceleration * Math.pow(time-accelEndTime, 2) / 2)
					.velocity(newVelocity - acceleration * (time-accelEndTime))
					.acceleration(-acceleration).createVector(),
					accelEndTime,
					decelEndTime);
			
			transSplines = new Spline[]{accelSpline, decelSpline};
			
		}
		
		
		
		
		
		
		
		return splines;
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
