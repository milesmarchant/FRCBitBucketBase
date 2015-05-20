package org.frcbitbucketbase.control;

import java.util.Arrays;
import java.util.function.DoubleBinaryOperator;

import org.frcbitbucketbase.control.profile.Profile;

/**
 * 
 * 
 * @author Miles Marchant
 * @version 0.9
 * 
 */
public class ProfileExecutor extends AutonomousExecutable {
	
	Profile<MovementVector> profile;
	long iterTime;
	long startTime;
	long currTime;
	volatile boolean stop = false;
	KinematicController kc;
	ValueController<MovementVector, MovementVector> vc;
	DataSender ds;
	DataRetriever dr;
	
	/**
	 * 
	 * @param profile
	 * @param kc
	 * @param vc
	 * @param ds
	 * @param dr
	 * @param iterTime
	 */
	public ProfileExecutor(Profile<MovementVector> profile, KinematicController kc, ValueController<MovementVector, MovementVector> vc, DataSender ds, DataRetriever dr, long iterTime, boolean sequential){
		super(sequential);
		this.profile = profile;
		this.kc = kc;
		this.vc = vc;
		this.ds = ds;
		this.dr = dr;
		this.iterTime = iterTime;
	}
	
	/**
	 * 
	 */
	@Override
	public boolean verify(){
		Double[] drive;
		Double[] outputs;
		for(long time = 1; time <= 15000; time++){
			outputs = kc.getOutputs(profile.getOutput(time));
			drive = new Double[outputs.length];
			for(int i = 0; i < outputs.length; i++){
				drive[i] = Math.abs(outputs[i]);
			}
			Double maxWheelV = 0.0;
			for(Double wheelV: drive){
				if(wheelV > maxWheelV){
					maxWheelV = wheelV;
				}
			}
			if(maxWheelV > 1){
				profile.setSplines(profile.regenerateSplines(maxWheelV));
				time = 0;
			}
		}
		return true;
	}
	
	/**
	 * 
	 */
	@Override
	public void run() {
		
		startTime = System.currentTimeMillis();
		long calcTime = 0;
		
		while(!Thread.interrupted()){
			
			//execute the profile at the current time
			currTime = System.currentTimeMillis();
			ds.sendData(
					kc.getOutputs(
							vc.compute(
									profile.getOutput(currTime - startTime), (Double[]) dr.retrieveData())
							)
				);
			if(profile.finished(currTime - startTime)){
				this.interrupt();
				continue;
			}
			
			//wait for the next loop around; this is controlled to ensure a consistent iteration time
			try{
				calcTime = System.currentTimeMillis() - currTime;
				Thread.sleep(iterTime-calcTime);
			} catch(InterruptedException e){
				System.out.println("Thread interrupted");
				Thread.currentThread().interrupt();
			} catch(IllegalArgumentException e){
				System.out.println("Calculation took longer than the iteration time");
			}
		}
	}
	
	/**
	 * 
	 */
	@Override
	public boolean isFinished() {
		if(Thread.interrupted()){
			return true;
		} else if(startTime == 0){
			return false;
		} else {
			return false;
		}
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}
	
}
