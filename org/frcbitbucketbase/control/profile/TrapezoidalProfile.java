package org.frcbitbucketbase.control.profile;

import org.frcbitbucketbase.control.MovementVector;

public class TrapezoidalProfile extends Profile<MovementVector> {
	
	MovementVector values;
	Spline[] transSplines;
	Spline[] rotSplines;
	long profileStartTime = 0;
	int transIndex = 0;
	int rotIndex = 0;
	
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
		if(profileStartTime == 0){
			profileStartTime = System.currentTimeMillis();
		}
		
		MovementVector transVector = null;
		if(transIndex > transSplines.length){
			
		} else if(transSplines[transIndex].getEndTime() < time-profileStartTime){
			transIndex++;
			transVector = new MovementVector.VectorBuilder().translationAngle(values.getValue("translationAngle")).merge(transSplines[transIndex].calculate(time)).createVector();
		}
		
		MovementVector rotVector = null;
		if(rotIndex > rotSplines.length){
		
		} else if(rotSplines[rotIndex].getEndTime() < time-profileStartTime){
			rotIndex++;
			rotVector = rotSplines[rotIndex].calculate(time);
		}
		
		if(transVector != null && rotVector != null){
			return transVector.merge(rotVector);
		} else if(transVector == null && rotVector == null){
			return null;
		} else{
			return (transVector == null) ? rotVector : transVector;
		}
	}

	@Override
	protected void generateSplines(MovementVector values) {		
		if(values.getValue("position")>0 && values.getValue("velocity")>0 && values.getValue("acceleration")>0){
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
		}
		if(values.getValue("angle")>0 && values.getValue("angularVel")>0 && values.getValue("angularAccel")>0){
			double rotAcceleration = values.getValue("angularAcceleration");
			double rotVelocity = values.getValue("angularVel");
			double rotTransAccelDist = Math.pow(rotVelocity, 2) / (2 * rotAcceleration);
			if(rotTransAccelDist <= values.getValue("angle")/2){
				
				long rotAccelEndTime = (long) (rotVelocity / rotAcceleration);
				double rotFlatDist = values.getValue("angle") - 2 * rotTransAccelDist;
				long rotFlatEndTime = rotAccelEndTime + (long)(rotFlatDist / rotVelocity);
				long rotDecelEndTime = rotFlatEndTime + rotAccelEndTime;
				
				Spline rotAccelSpline = rotAccelSpline(rotAcceleration, rotAccelEndTime);
				Spline rotFlatSpline = rotFlatSpline(rotTransAccelDist, rotVelocity, rotAccelEndTime, rotFlatEndTime);
				Spline rotDecelSpline = rotDecelSpline(rotTransAccelDist + rotFlatDist, rotVelocity, rotAcceleration, rotFlatEndTime, rotDecelEndTime);
				
				rotSplines = new Spline[]{rotAccelSpline, rotFlatSpline, rotDecelSpline};
				
			} else if(rotTransAccelDist > values.getValue("angle")/2){
				
				double newRotVelocity = Math.sqrt(rotAcceleration * values.getValue("angle"));
				double newRotTransAccelDist = values.getValue("angle");
				long rotAccelEndTime = (long) (newRotVelocity / rotAcceleration);
				long rotDecelEndTime = rotAccelEndTime * 2;
	
				Spline rotAccelSpline = rotAccelSpline(rotAcceleration, rotAccelEndTime);
				Spline rotDecelSpline = rotDecelSpline(newRotTransAccelDist, newRotVelocity, rotAcceleration, rotAccelEndTime, rotDecelEndTime);
				
				rotSplines = new Spline[]{rotAccelSpline, rotDecelSpline};
			}
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
	
	private Spline rotAccelSpline(double acceleration, long endTime){
		return new Spline(time -> new MovementVector.VectorBuilder()
		.angle(acceleration * Math.pow(time, 2) / 2)
		.angularVel(acceleration * time)
		.angularAccel(acceleration).createVector(),
		0,
		endTime);
	}
	
	private Spline rotFlatSpline(double startDistance, double velocity, long startTime, long endTime){
		return new Spline(time -> new MovementVector.VectorBuilder()
		.angle(startDistance + velocity * (time-startTime))
		.angularVel(velocity)
		.angularAccel(0).createVector(),
		startTime,
		endTime);
	}
	
	private Spline rotDecelSpline(double startDistance, double velocity, double deceleration, long startTime, long endTime){
		return new Spline(time -> new MovementVector.VectorBuilder()
		.angle(startDistance + (velocity * time-startTime) - deceleration * Math.pow(time-startTime, 2) / 2)
		.angularVel(velocity - deceleration * (time-startTime))
		.angularAccel(-deceleration).createVector(),
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
