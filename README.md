# FRCBitBucketBase
Base project for FRC Team 4183, the Bit Buckets, to use for each year's code.



## Table of contents

- [Table of contents](#table-of-contents)
- [Intro](#intro)
- [Framework documentation](#frameworkdocumentation)
	- [Basic robot classes](#basic-robot-classes)
	- [AutonomousController](#autonomouscontroller)
	- [AutonomousExecutable](#autonomousexecutable)
	- [Profiles](#profiles)
- [Examples](#examples)


## Intro

Programming a robot is easy, programming a robot well is hard. The purpose of this repository is to help the team implement more complex software features, and to avoid re-implementing the same tasks over and over. Some tasks which would be applicable to all years and might eventually be part of this repository:
- Powerful, easy-to-implement autonomous programming
- Motion profiling
- Use of complex, external sensors such as gyros
- Logging

Currently, the first two features are the focus of development. Ideally, programming a highly accurate, multistep autonomous should take minutes, assuming the needed profiles and actions have already been written.

While this project is designed for use on FRC robots running the 2015-current WPILIBJ control system, many of the classes are completely decoupled from a physical implementation. The following classes could be used on any system:
- Everything the control package except for:
	- [CommandExecutor](#commandexecutor)

## Framework

### Basic robot classes

The following classes reside directly in the /org/ directory and control or represent the robot as a whole.

#### `Robot`

`Robot` is entry point for your program and must be present to run on the roboRIO.

<a name="robot-robotinit"></a>
`Robot.robotInit()` is the first method called by the VM after the OS starts. Here you should initialize things such as [`OI`](#oi) and [`AutonomousController`](#autonomouscontroller), or anything else that needs to start as soon as the robot has power.

<a name="robot-disabledinit"></a>
`Robot.disabledInit()`

<a name="robot-disabledperiodic"></a>
`Robot.disabledPeriodic()`

<a name="robot-autonomousinit"></a>
`Robot.autonomousInit()`

<a name="robot-autonomousperiodic"></a>
`Robot.autonomousPeriodic()`

<a name="robot-teleopinit"></a>
`Robot.teleopInit()`

<a name="robot-teleopperiodic"></a>
`Robot.teleopPeriodic()`

<a name="robot-testinit"></a>
`Robot.testInit()`

<a name="robot-testperiodic"></a>
`Robot.testPeriodic()`

#### `OI`

#### `RobotMap`

#### `RobotConstants`

### Control


#### `AutonomousController`

<a name="autonomouscontroller"></a>
`AutonomousController` is the "brain" which queues and activates autonomous actions. It is implemented as a singleton, although this is bad software-design; it will later be refactored.

<a name="autonomouscontroller-runner"></a>
`AutonomousController.runner` is a statically defined thread which takes new actions off the queue and deals with them, as well as holding and stopping currently running actions. It is initialized in the [`generateThread(boolean paused)`](#autonomouscontroller-generatethread) method so that it may be restarted if the thread dies.

<a name="autonomouscontroller-generatethread"></a>
`AutonomousController.generateThread(boolean paused)` instantiates [`runner`](#autonomouscontroller-runner) as a new `Thread`, using [`runloop`](#autonomouscontroller-runloop) as a `run()` method. It then invokes `.start()` on [`runner`](#autonomouscontroller-runner) The paused parameter indicates whether the thread should start reading from the action queue immediately or not.

<a name="autonomouscontroller-runloop"></a>
`AutonomousController.runLoop(boolean startPaused)` runs a time-controlled while loop on the condition `!Thread.interrupted()`. TODO

<a name="autonomouscontroller-cancelactiveactions"></a>
`AutonomousController.cancelActiveActions()` iterates through the list of active actions, invoking the `.cancel()` method of each and removing them from the list.

<a name="autonomouscontroller-canceltype"></a>
`AutonomousController.cancelType(AutonomousExecutable type)` is not yet implemented.

<a name="autonomouscontroller-clearfinishedactions"></a>
`AutonomousController.clearFinishedActions()` iterates through the list of active actions, and if they are finished (as per their `.isFinished()` method), it removes them. It does not invoke `.cancel()` on the finished actions, so any cleanup code should happen in both `.isFinished()` and `.cancel()`.

<a name="autonomouscontroller-clearqueue"></a>
`AutonomousController.clearQueue()` clears the queue of actions.

<a name="autonomouscontroller-cleanup"></a>
`AutonomousController.cleanUp()` cleans up the queue of actions and all active actions. If invokes [`pause`](#autonomouscontroller-pause) and waits for one second before attempting to clear out the controller. This is to prevent an action from dodging the clean up by being shifted from the queue to the active list. If confirmation that the method has stopped shifting actions is not found in 1 second, this method returns false and exits. This method currently blocks the main thread for at most one second, so it is recommended to only use it in situations where hangs are allowed, such as [`disabledPeriodic`](#robot-disabledperiodic).

<a name="autonomouscontroller-initialize"></a>
`AutonomousController.initialize()` invokes [`generateThread(true)`](#autonomouscontroller-generatethread), assuming the following condition is true:
```java
runner == null || runner.isInterrupted() == true
```

<a name="autonomouscontroller-start()"></a>
`AutonomousController.start()` invokes [`generateThread(false)`](#autonomouscontroller-generatethread), assuming the following condition is true:
```java
runner == null || runner.isInterrupted() == true
````
Otherwise it will unpause the thread.

<a name="autonomouscontroller-pause"></a>
`AutonomousController.pause()` sets a boolean flag to true, so as to pause the thread. 

<a name="autonomouscontroller-add"></a>
`AutonomousController.add(AutonomousExecutable ae)` adds an action to the queue.


#### AutonomousExecutable

<a name="autonomousexecutable"></a>
`AutonomousExecutable` is an abstract class which extends `Thread`, and is the base class from which all autonomous actions are derived. It declares four abstract classes, a constructor, and a public field.

<a name="autonomousexecutable-sequential"></a>
`AutonomousExecutable.sequential` is a public boolean declaring whether a given action should be run in sequence or in parallel.

<a name="autonomousexecutable-run"></a>
`AutonomousExecutable.run()` overrides `Thread.run()` in order to pass the burden of implementation to subclasses. This is called by [`AutonomousController`](#autonomouscontroller) when actions are started.

<a name="autonomousexecutable-isFinished"></a>
`AutonomousExecutable.isFinished()` returns a boolean indicating whether or not the action is finished running.

<a name="autonomousexecutable-verify"></a>
`AutonomousExecutable.verify()` returns a boolean indicating whether or not the action is valid. For many implementations, this may simply return true.

<a name="autonomousexecutable-cancel"></a>
`AutonomousExecutable.cancel()` allows the [`AutonomousController`](#autonomouscontroller) to interrupt and stop an action. This must stop any loops present in `run()`, or the action may continue past its cancellation.

#### Built-in Implementations of AutonomousExecutable

<a name="CommandExecutor"></a>
`CommandExecutor` runs a WPILIB command, allowing for compatibility with other FRC.

<a name="ProfileExecutor"></a>
`ProfileExecutor` is a complex implementation which runs a motion profile. Which and what kind of profile is run is highly configurable at runtime, by virtue of several extendable subclasses (see: [`DataRetriever`](#dataretriever), [`DataSender`](#datasender), [`KinematicController`](#kinematiccontroller), [`MovementVector`](#movementvector), [`ValueController`](#valuecontroller))

### Profiles



## Examples
