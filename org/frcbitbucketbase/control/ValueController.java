package org.frcbitbucketbase.control;

/**
 * 
 * @author Miles Marchant
 * @version 0.9
 *
 */
public abstract class ValueController<T, R> {
	
	public ValueController(){
	}
	
	public abstract R compute(T input, Object[] state);

}
