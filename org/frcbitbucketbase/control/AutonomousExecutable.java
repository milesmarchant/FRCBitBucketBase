package org.frcbitbucketbase.control;

/**
 * 
 * @author Miles Marchant
 * @version 0.9
 *
 */
public abstract class AutonomousExecutable extends Thread{
	
	public boolean sequential;
	
	public AutonomousExecutable(boolean sequential){
		this.sequential = sequential;
	}
	
	@Override
	public abstract void run();
	
	public abstract boolean isFinished();
	
	public abstract boolean verify();
	
	public abstract void cancel();

}