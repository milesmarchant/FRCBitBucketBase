package org.frcbitbucketbase.control;

import edu.wpi.first.wpilibj.command.Command;

public class CommandExecutor extends AutonomousExecutable{
	
	Command command;
	
	public CommandExecutor(Command command, boolean sequential){
		super(sequential);
		this.command = command;
	}
	
	@Override
	public void run(){
		command.start();
	}

	@Override
	public boolean isFinished() {
		if(command.timeSinceInitialized() > 0){
			return !command.isRunning();
		} else{
			return false;
		}
	}

	@Override
	public boolean verify() {
		return true;
	}

	@Override
	public void cancel() {
		command.cancel();
	}

}
