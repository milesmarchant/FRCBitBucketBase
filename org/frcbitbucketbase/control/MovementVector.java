package org.frcbitbucketbase.control;

import java.util.Hashtable;

/**
 * 
 * @author Miles Marchant
 *
 */
public class MovementVector extends Hashtable<String, Double> {
	
	public MovementVector(){}
	
	public double getValue(String key){
		return this.get(key);
	}
	
	public MovementVector merge(MovementVector other){
		this.putAll(other);
		return this;
	}

	public static class VectorBuilder{
		
		MovementVector vector = new MovementVector();
		
		public VectorBuilder(){
		}
		
		/**
		 * This will merge two vectors together. The argument will overwrite values that have already been set in the builder.
		 * 
		 * @param other another vector to be merged with this. Overwrites keys.
		 * @return
		 */
		public VectorBuilder merge(MovementVector other){
			vector.merge(other);
			return this;
		}
		
		public VectorBuilder translationAngle(double translationAngle){
			vector.put("translationAngle", translationAngle);
			return this;
		}
		
		public VectorBuilder position(double position){
			vector.put("position", position);
			return this;
		}
		
		public VectorBuilder velocity(double velocity){
			vector.put("velocity", velocity);
			return this;
		}
		
		public VectorBuilder acceleration(double acceleration){
			vector.put("acceleration", acceleration);
			return this;
		}
		
		public VectorBuilder jerk(double jerk){
			vector.put("jerk", jerk);
			return this;
		}
		
		public VectorBuilder angle(double angle){
			vector.put("angle", angle);
			return this;
		}
		
		public VectorBuilder angularVel(double angularVel){
			vector.put("angularVel", angularVel);
			return this;
		}
		
		public VectorBuilder angularAccel(double angularAccel){
			vector.put("angularAccel", angularAccel);
			return this;
		}
		
		public VectorBuilder angularJerk(double angularJerk){
			vector.put("angularJerk", angularJerk);
			return this;
		}
		
		public MovementVector createVector(){
			return this.vector;
		}
		
	}
	
	
}