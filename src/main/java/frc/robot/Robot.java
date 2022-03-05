// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  // private WPI_VictorSPX left1 = new WPI_VictorSPX(10);
  // private WPI_VictorSPX left2 = new WPI_VictorSPX(7);
  // private VictorSPX right1 = new VictorSPX(8);
  // private VictorSPX right2 = new VictorSPX(9);
  private MotorControllerGroup right = new MotorControllerGroup( new WPI_VictorSPX(8), new WPI_VictorSPX(9));
  private MotorControllerGroup left = new MotorControllerGroup( new WPI_VictorSPX(7), new WPI_VictorSPX(10));
  private VictorSPX intake = new VictorSPX(0);
  private VictorSPX transfer = new VictorSPX(2);
  private TalonFX shooterPower = new TalonFX(11);
  private TalonSRX shooterCam = new TalonSRX(0);

  private Joystick joystickButtons = new Joystick(1);
  private Joystick joystickDriver = new Joystick(0);

  boolean CamVariable = false;
  boolean CamVariableTwo = false;
  boolean CamIsOn = false;
  double CamRemainder = 0;
  boolean CamRemainderIsGood = false;
  boolean CamHasRotated= false;
  boolean CamFixer = false;

  // public Robot() {
  //   super(0.02);
  // }

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
    SmartDashboard.putNumber("Limelight_X", x);
    SmartDashboard.putNumber("Limelight_Y", y);
    SmartDashboard.putNumber("Limelight_Area", area);
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

  int counter = 0;
  
  @Override
  public void autonomousPeriodic() {
    // if(counter < 150){
    //   left.set(0.25);
    //   right.set(-0.25);
    // }else if(counter < 250){
    //   left.set(-0.25);
    //   right.set(0.25); 
    // }else{
    //   left.set(0);
    //   right.set(0);
    // }


    counter++;

    // NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    // NetworkTableEntry tx = table.getEntry("tx");
    // NetworkTableEntry tv = table.getEntry("tv");
    // double x = tx.getDouble(0.0);
    // double isTarget = tv.getDouble(0.0);
    // if(isTarget == 0.0){
    //   left.set(0);
    //   right.set(0);
    // }else {
    //   if (x > 6){
    //     left.set(0.3);
    //     right.set(0);
    //   }else if(x < -6){
    //     right.set(-0.3);
    //     left.set(0);
    //   }else{
    //     right.set(0);
    //     left.set(0);
    //
    //}
    //}
  }

  // This function is called once when teleop is enabled.
  @Override
  public void teleopInit() {}

  // This function is called periodically during operator control.
  @Override
  public void teleopPeriodic() {

    // Drive code
    double leftStick = joystickDriver.getRawAxis(1);
    double rightStick = joystickDriver.getRawAxis(5);
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
    
    left.set(leftStick);
    
    // if you press A, all wheels are controled by left stick (makes sure that you drive straight)
    if(joystickDriver.getRawButton(4) || joystickDriver.getRawButton(1)){
      right.set(-leftStick);
    }else{
    right.set(rightStick);
    }


    // Intake
    if(joystickButtons.getRawButton(3)){
      intake.set(ControlMode.PercentOutput, -0.5);
    }else{
      intake.set(ControlMode.PercentOutput, 0);
    }

    // Transfer code
    if(joystickButtons.getRawButton(4)){
      transfer.set(ControlMode.PercentOutput, -1);
    }else{
      transfer.set(ControlMode.PercentOutput, 0);
    }

    // Shooter code
    if(joystickButtons.getRawButton(2)){
    shooterPower.set(ControlMode.PercentOutput, -0.65);
    }else{
    shooterPower.set(ControlMode.PercentOutput, 0);
    }

    // Cam code
// Every time the cam completes a rotation, the positionreturns to 0
// (It doesn't really set the current position to 0, it just tells the code to consider it to be 0)
    CamRemainder = shooterCam.getSelectedSensorPosition() % -40960;

    // CamRemainderIsGood = true when th ecam is in starting position
    if(CamRemainder <= 0 && CamRemainder >= -2000){
      CamRemainderIsGood = true;
    }else{
      CamRemainderIsGood = false;
    }

    // CamHasRotated = true when the cam is part way through a rotation
    // CamHasRotated = false when the cam completes a rotation
    if(CamRemainder <= -19000 && CamRemainder >= -21000){
     CamHasRotated = true;
    }
    // When you press button 5, th ecam rotates 360*
    if(joystickButtons.getRawButton(5) && !CamIsOn && CamRemainderIsGood){
      shooterCam.set(ControlMode.PercentOutput, -0.4);
      CamIsOn = true;
    }
  
    // Cam stops when it completes a rotation
      if(CamRemainderIsGood && CamIsOn && CamHasRotated){
        shooterCam.set(ControlMode.PercentOutput, 0);
        CamIsOn = false;
        CamHasRotated = false;
      }

        // System.out.println(CamRemainder);

      // OLD CAM CODE
      // if(joystickButtons.getRawButton(5) && shooterCam.getSelectedSensorPosition() >= 0 && CamIsOn == false){
      //   shooterCam.set(ControlMode.PercentOutput, -0.4);
      //   System.out.println("Button 6 is pressed!!!");
      //   CamIsOn = true;
      //   CamVariable = true;
      // }
      // if(CamVariable == true){
      //   if(shooterCam.getSelectedSensorPosition() < -20000){
      //     CamVariable = false;
      //     shooterCam.set(ControlMode.PercentOutput, 0.4);
      //     CamVariableTwo = true;
      //   }
      // }
      // if(shooterCam.getSelectedSensorPosition() > 0 && CamVariableTwo == true){
      //   shooterCam.set(ControlMode.PercentOutput, 0);
      //   CamVariableTwo = false;
      //   CamIsOn = false;
      // }

      SmartDashboard.putNumber("Encoder_Position", shooterCam.getSelectedSensorPosition());

      // Cam reset code
      // Use this if the Cam gets into the wrong position
      if(joystickButtons.getRawButton(6) && joystickButtons.getRawButton(1)){

        CamFixer = true;

        if(joystickButtons.getRawAxis(1) == 1){
          shooterCam.set(ControlMode.PercentOutput, 0.2);
        }else if(joystickButtons.getRawAxis(1) == -1){
          shooterCam.set(ControlMode.PercentOutput, -0.2);
        }else{
          shooterCam.set(ControlMode.PercentOutput, 0);
        }
      }

      if(!joystickButtons.getRawButton(6) && !joystickButtons.getRawButton(1) && CamFixer == true){
        shooterCam.setSelectedSensorPosition(0);
        CamFixer = false;
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

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {
  
  }

  @Override
  public void testPeriodic() {

    if(joystickButtons.getRawButton(6)){
      shooterCam.set(ControlMode.PercentOutput, 0.2);
    }else{
      shooterCam.set(ControlMode.PercentOutput, 0);
    }

  }
}