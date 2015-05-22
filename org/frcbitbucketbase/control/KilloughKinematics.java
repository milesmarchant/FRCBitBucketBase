package org.frcbitbucketbase.control;


public class KilloughKinematics extends KinematicController {
	
	double sqrt2 = Math.sqrt(2);
	
	enum Wheel{
		
		FL(Math.PI/4, 1.0, -1.0, 1),
		FR(3*Math.PI/4, 1.0, 1.0, 1),
		RL(5*Math.PI/4, -1.0, -1.0, 1),
		RR(7*Math.PI/4, -1.0, 1.0, 1);
		
		private double wheelAngle;
		private double rotationAngle;
		private double radius;
		private double direction;
		Wheel(double wheelAngle, double yOffset, double xOffset, int direction){
			this.wheelAngle = wheelAngle;
			this.rotationAngle = Math.atan2(yOffset, xOffset)-Math.PI/2;
			this.radius = Math.hypot(xOffset, yOffset);
			this.direction = direction;
		}
		
	}

	/**
	 * FL, FR, RR, RL
	 */
	@Override
	public Double[] getOutputs(MovementVector input) {
		return new Double[]{
			getWheelValue(input.getValue("velocity"), input.getValue("translationAngle"), input.getValue("angularVel"), Wheel.FL),
			getWheelValue(input.getValue("velocity"), input.getValue("translationAngle"), input.getValue("angularVel"), Wheel.FR),
			getWheelValue(input.getValue("velocity"), input.getValue("translationAngle"), input.getValue("angularVel"), Wheel.RL),
			getWheelValue(input.getValue("velocity"), input.getValue("translationAngle"), input.getValue("angularVel"), Wheel.RR),
		};
	}
	
	public double getWheelValueXY(double y, double x, double rot, Wheel wheel){
		return y*Math.cos(wheel.wheelAngle) + x*Math.sin(wheel.wheelAngle) - rot*Math.sin(wheel.rotationAngle-wheel.wheelAngle);
	}
	
	public double getWheelValue(double vel, double translationAngle, double angularVel, Wheel wheel){
		//     dir             * ( vt * cos(theta-wheelAngle)                          - r * w * sin(rtheta - wheelAngle))
		return wheel.direction * ( vel * Math.cos(translationAngle - wheel.wheelAngle) - wheel.radius * angularVel * Math.sin(wheel.rotationAngle - wheel.wheelAngle));   
	}

}
