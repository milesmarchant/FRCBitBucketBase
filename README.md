# FRCBitBucketBase
Base project for FRC Team 4183, the Bit Buckets, to use for each year's code.



## Table of contents

- [Table of contents](#table-of-contents)
- [Intro](#intro)
- [Framework](#framework)
	- [Basic robot classes](#basic-robot-classes)
	- [AutonomousController](#autonomouscontroller)
	- [AutonomousExecutables](#autonomousexecutables)
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
[`Robot.robotInit()`](#robot-robotinit) is the first method called by the VM after the OS starts. Here you should initialize things such as [`OI`](#oi) and [`AutonomousController`](#autonomouscontroller), or anything else that needs to start as soon as the robot has power.

<a name="robot-disabledinit"></a>
[`Robot.disabledInit()`](#robot-disabledinit)

<a name="robot-disabledperiodic"></a>
[`Robot.disabledPeriodic()`](#robot-disabledperiodic)

<a name="robot-autonomousinit"></a>
[`Robot.autonomousInit()`](#robot-autonomousinit)

<a name="robot-autonomousperiodic"></a>
[`Robot.autonomousPeriodic()`](#robot-autonomousperiodic)

<a name="robot-teleopinit"></a>
[`Robot.teleopInit()`](#robot-teleopinit)

<a name="robot-teleopperiodic"></a>
[`Robot.teleopPeriodic()`](#robot-teleopperiodic)

<a name="robot-testinit"></a>
[`Robot.testInit()`](#robot-testinit)

<a name="robot-testperiodic"></a>
[`Robot.testPeriodic()`](#robot-testperiodic)

#### `OI`

#### `RobotMap`

#### `RobotConstants`

### Control

#### `AutonomousController`

<a name="autonomouscontroller></a>
[`AutonomousController`](#autonomouscontroller) is the "brain" which queues and activates autonomous actions. It is implemented as a singleton, although this is bad software-design; it will later be refactored.

<a name="autonomouscontroller-runner>
[`AutonomousController.runner`](#autononomouscontroller-runner) is a statically defined thread which takes new actions off the queue and deals with them, as well as holding and stopping currently running actions. It is initialized in the [`generateThread(boolean paused)`](#autonomouscontroller-generatethread) method so that it may be restarted if the thread dies.

<a name="autonomouscontroller-generatethread></a>
['AutonomousController.generateThread(boolean paused)`](#autonomouscontroller-generatethread)


### AutonomousExecutables



### Profiles



## Examples
