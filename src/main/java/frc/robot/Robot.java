// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  private VictorSPX left1 = new VictorSPX(10);
  private VictorSPX left2 = new VictorSPX(9);
  private VictorSPX right1 = new VictorSPX(8);
  private VictorSPX right2 = new VictorSPX(7);

  private Joystick joystick = new Joystick(0);
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   * 
   */
  @Override
  public void robotPeriodic() {

    NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    NetworkTableEntry tx = table.getEntry("tx");
    NetworkTableEntry ty = table.getEntry("ty");
    NetworkTableEntry ta = table.getEntry("ta");

    //read values periodically
    double x = tx.getDouble(0.0);
    double y = ty.getDouble(0.0);
    double area = ta.getDouble(0.0);

    //post to smart dashboard periodically
    SmartDashboard.putNumber("LimelightX", x);
    SmartDashboard.putNumber("LimelightY", y);
    SmartDashboard.putNumber("LimelightArea", area);
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {

  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {

    NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    NetworkTableEntry tx = table.getEntry("tx");
    NetworkTableEntry tv = table.getEntry("tv");
    double x = tx.getDouble(0.0);
    double isTarget = tv.getDouble(0.0);
    if(isTarget == 0.0){
      left1.set(ControlMode.PercentOutput, 0);
      left2.set(ControlMode.PercentOutput, 0);
      right1.set(ControlMode.PercentOutput, 0);
      right2.set(ControlMode.PercentOutput, 0);
    }else {
      if (x > 6){
      left1.set(ControlMode.PercentOutput, 0.3);
      left2.set(ControlMode.PercentOutput, 0.3);
      right1.set(ControlMode.PercentOutput, 0);
      right2.set(ControlMode.PercentOutput, 0);
      }else if(x < -6){
      right1.set(ControlMode.PercentOutput, -0.3);
      right2.set(ControlMode.PercentOutput, -0.3);
      left1.set(ControlMode.PercentOutput, 0);
      left2.set(ControlMode.PercentOutput, 0);
      }else{
        right1.set(ControlMode.PercentOutput, 0);
        right2.set(ControlMode.PercentOutput, 0);
        left1.set(ControlMode.PercentOutput, 0);
        left2.set(ControlMode.PercentOutput, 0);

      }
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    double leftStick = joystick.getRawAxis(1);
    double rightStick = joystick.getRawAxis(5);
    leftStick = leftStick * -1;

  // squares the motor power; easier to use low speeds, but high speed is uneffected
    if(leftStick > 0 ){
    leftStick = leftStick * leftStick;
    }else if (leftStick < 0){
      leftStick = leftStick * leftStick * -1;
    }
    if(rightStick > 0 ){
      rightStick = rightStick * rightStick;
      }else if (rightStick < 0){
        rightStick = rightStick * rightStick * -1;
      }
    
    left1.set(ControlMode.PercentOutput, leftStick);
    left2.set(ControlMode.PercentOutput, leftStick);
    
    // if you press A, all wheels are controled by left stick (makes sure that you drive straight)
    if(joystick.getRawButton(1)){
      right1.set(ControlMode.PercentOutput, -leftStick);
      right2.set(ControlMode.PercentOutput, -leftStick);
    }else{
    right1.set(ControlMode.PercentOutput, rightStick);
    right2.set(ControlMode.PercentOutput, rightStick);
    }

    

    //  ORIGINAL DRIVING CODE:
    // double rawAxis0 = joystick.getRawAxis(0);
    // if (rawAxis0 < 0.1 && rawAxis0 > -0.1) {
    //   left1.set(ControlMode.PercentOutput, -rawAxis1);
    //   left2.set(ControlMode.PercentOutput, -rawAxis1);
    //   right1.set(ControlMode.PercentOutput, rawAxis1);
    //   right2.set(ControlMode.PercentOutput, rawAxis1);
    // } else if (rawAxis0 >= 0.1) {
    //   left1.set(ControlMode.PercentOutput, 0.5);
    //   left2.set(ControlMode.PercentOutput, 0.5);
    //   right1.set(ControlMode.PercentOutput, 0.5);
    //   right2.set(ControlMode.PercentOutput, 0.5);
    // } else {
    //   left1.set(ControlMode.PercentOutput, -0.5);
    //   left2.set(ControlMode.PercentOutput, -0.5);
    //   right1.set(ControlMode.PercentOutput, -0.5);
    //   right2.set(ControlMode.PercentOutput, -0.5);
    // }

  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}