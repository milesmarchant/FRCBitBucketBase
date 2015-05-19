package org.frcbitbucketbase.control;

/**
 * A descriptor of movement in 2 dimensional vector format. Instances of this object allow classes such as <code>KinematicControllers</code> and <code>ValueControllers</code>
 * to communicate. This class is designed to be vague, so contextual meaning of the fields must be handled by the user.
 * <p>
 * For example, an action controlling a single motor requires a single vector, wherein the direction field indicates forward or backwards. A translational movement with
 * rotation may require use of several vectors, to indicate target translation and rotation, as well as instantaneous values such as target velocity.
 * 
 * @author Miles Marchant
 *
 */
public class MovementVector {
	
	/**
	 * Value of this vector. This may refer to any value; for example, position, velocity, or acceleration.
	 */
	public double value;
	/**
	 * Translational direction of this vector
	 */
	public double direction;
	
	/**
	 * Rotation of the vector
	 */
	public double rotation;
	
	/**
	 * Constructor for the MovementVector class. Simply sets the object's fields as the matching parameters.
	 * 
	 * @param value      is a value given to this MovementVector
	 * @param direction  is a direction given to this MovementVector
	 */
	public MovementVector(double value, double direction, double rotation){
		this.value = value;
		this.direction = direction;
		this.rotation = rotation;
	}

}
