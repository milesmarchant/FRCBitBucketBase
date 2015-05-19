package org.frcbitbucketbase.control;

import org.frcbitbucketbase.control.profile.Profile;

/**
 * A base class for Kinematic Controllers. A kinematic controller provides translation from absolute coordinates to motor or output specific values. For example,
 * a robot driven by four mecanum wheels would use a particular subclass of <code>KinematicController</code> which translates two movement vectors (translation and
 * rotation) into four motor output values.
 * <p>
 * Subclasses of <code>KinematicController</code> can be used to ensure modular control over motors and outputs. Scaling between real world speeds and motor output values
 * should occur here.
 * <p>
 * The user must ensure the order of values is consistent with other objects. It is recommended to document the order of associated motors.
 * 
 * @author Miles Marchant
 * @version 0.9
 *
 */
public abstract class KinematicController {

	public abstract double verify(Profile<?> profile, long time);
	
	public abstract Double[] getOutputs(MovementVector input);
	
	
	
	
	
	
	
}
