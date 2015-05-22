package org.frcbitbucketbase.control;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * A class designed as an alternative method of running autonomous scripts, as opposed to WPILIB's <code>CommandGroup</code>. It does not start
 * itself, and it is necessary to call the <code>start</code> method.
 * <p>
 * It can also be used to control 'automagical' actions, wherein user action is passed to a small automatic script that (usually) does not block
 * user actions. FRC specific examples of automagical actions would be aligning and firing a frisbee in 2013 or lifting a tote one level in 2015.
 * <p>
 * <code>AutonomousController</code> holds a <code>LinkedList</code> (for First-In-First-Out behavior, or FIFO) which contains the actions to be executed.
 * The terms 'actions' and 'executors' are used interchangeably. Objects implementing the <code>AutonomousExecutable</code> interface are
 * added and packaged into <code>Executors</code>, which are wrapper objects that allow the user to dictate whether or not a certain action should
 * block further autonomous actions (sequential) or allow the controller to start the next action (parallel). Using parallel actions is discouraged
 * unless conflict checking has been implemented by the user.
 * <p>
 * All aspects of this class are static and should be accessed as such; it is final and the constructor is private.
 * <p>
 * This class contains a static thread which deals with Executors in a FIFO priority. It can be paused and resumed safely, as pausing
 * simply prevents further action from being taken, as opposed to interrupting the thread. Pausing the thread will not pause the current action. There is
 * not currently an implemented method for interrupting a running action.
 * <p>
 * Currently, all AutonomousExecutors must be created in code. Later versions may include reflectively creating new objects from JSON or 
 * reading serialized object data.
 * 
 * @author Miles Marchant
 * @version 0.9
 *
 */
public final class AutonomousController{
	
	/**
	 * This private constructor replaces the default, public, no-args constructor and disallows instantiation of this class. This, along with the 
	 * final modifier in the class declaration, simulates the concept of "static" or "singleton" classes, which are not natively supported in java.
	 */
	private AutonomousController(){}
	
	static LinkedList<AutonomousExecutable> executorQueue = new LinkedList<AutonomousExecutable>();
	static ArrayList<AutonomousExecutable> activeExecutors = new ArrayList<AutonomousExecutable>();
	
	private volatile static boolean paused;
	private volatile static boolean confirmPaused;
	
	protected static long iterTime = 10;
	
	/**
	 * The core logic of the AutonomousController is run via this thread. This thread reads Executor objects from the queue and starts them.
	 */
	static Thread runner;
	
	/**
	 * Initializes the controller's static thread as a new thread with an anonymous Runnable parameter, which gives the desired operation.
	 */
	private static void generateThread(boolean startPaused){
		runner = new Thread(new Runnable(){
			@Override
			public void run() {
				runLoop(startPaused);
			}
		});
		runner.start();
	}
	
	/**
	 * Runs the main logic loop which deals with actions in the queue.
	 */
	private static void runLoop(boolean startPaused){
		boolean sequentialRunning = false;
		long loopStartTime = 0;
		paused = startPaused;
		while(!Thread.interrupted()){
			sequentialRunning = false;
			loopStartTime = System.currentTimeMillis();
			if(paused == false && executorQueue.isEmpty() == false){
				confirmPaused = false;
				clearFinishedActions();
				for(AutonomousExecutable e: activeExecutors){
					if(e.sequential == true){
						sequentialRunning = true;
						break;
					}
				}
				if(sequentialRunning == false){
					AutonomousExecutable next;
					do{
						next = executorQueue.poll();
						next.start();
						activeExecutors.add(next);
						System.out.println("Executor queue size:\t" + executorQueue.size());
						System.out.println("Active executors:\t" + activeExecutors.size());
					} while(executorQueue.isEmpty() == false && next.sequential == false);
				}
			} else if(paused == true){
				confirmPaused = true;
			}
			try{
				Thread.sleep(iterTime-(System.currentTimeMillis()-loopStartTime));
			} catch(InterruptedException e){
				System.out.println("AutonomousController thread interrupted");
				Thread.currentThread().interrupt();
			} catch(IllegalArgumentException e){
			}
		}
	}
	
	/**
	 * Cancels all currently running actions by calling their cancel() method and removing them from the List
	 */
	public static void cancelActiveActions(){
		activeExecutors.removeIf(e -> {
			e.cancel();
			return true;
		});
	}
	
	/**
	 * NYI
	 * 
	 * @param type
	 */
	public static void cancelType(AutonomousExecutable type){
		
	}
	
	/**
	 * Clears out any actions which finished, as per the action's implementation of isFinished()
	 */
	public static void clearFinishedActions(){
		activeExecutors.removeIf(e -> {
			if(e.isFinished() == true){
				return true;
			}
			return false;
		});
	}
	
	/**
	 * Clears all actions in the queue
	 */
	public static void clearQueue(){
		executorQueue.clear();
	}
	
	//TODO ensure nothing gets added while cleaning
	/**
	 * Cleans up and stops this Controller. Pauses, clears the queue, and removes all active actions.
	 * 
	 * @return
	 */
	public static boolean cleanUp(){
		pause();
		int timeout = 0;
		while(confirmPaused == false && timeout < 1000){
			timeout++;
			try {
				Thread.sleep(1);
			} catch (InterruptedException e1) {}
		}
		if(timeout >= 1000){
			System.out.println("Pausing took >1second");
			return false;
		}
		clearQueue();
		cancelActiveActions();
		return true;
	}
	
	/**
	 * Initializes the thread, and sets it paused.
	 */
	public static void initialize(){
		if(runner == null || runner.isInterrupted() == true){
			generateThread(true);
		}
	}

	/**
	 * Starts the static thread. If <code>Thread.start()</code> has not yet been invoked, this method will do so. If the thread is not paused, this method
	 * does nothing. If the thread is paused, this method will unpause it.
	 */
	public static void start(){
		if(runner == null || runner.isInterrupted() == true){
			generateThread(false);
		} else{
			paused = false;
		}
	}
	
	/**
	 * Pauses the static thread. If the thread is not running, this method will do nothing. If the thread is running and not paused, this method
	 * will pause it. If the thread is paused, this method will do nothing.
	 */
	public static void pause(){
		paused = true;
	}
	
	/**
	 * Adds an <code>AutonomousExecutable</code> to the queue.
	 * 
	 * @param ae is the AutonomousExecutable object to be added
	 */
	public static void add(AutonomousExecutable ae){
		executorQueue.offer(ae);
	}
}
