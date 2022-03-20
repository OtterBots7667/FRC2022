// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

// import com.ctre.phoenix.led.CANdle.VBatOutputMode;
import com.ctre.phoenix.motorcontrol.ControlMode;
// import com.ctre.phoenix.motorcontrol.FeedbackDevice;
// import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
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

  // Cam variables
  boolean CamVariable = false;
  boolean CamVariableTwo = false;
  boolean CamIsOn = false;
  double  CamRemainder = 0;
  boolean CamRemainderIsGood = false;
  boolean CamHasRotated= false;
  boolean CamFixer = false;

  // Auto-orientation variables
  boolean xIsGood = false;
  boolean orientation = false;
  boolean orientation2 = false;

  // Autonomous variables
  boolean autoCam1 = false;
  boolean autoCam2 = false;
  double  autoCamRemainder = 0;
  int     counter = 0;

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
  autoCam1 = false;
  autoCam2 = false;
  counter = 0;
  }

  @Override
  public void autonomousPeriodic() {

    autoCamRemainder = shooterCam.getSelectedSensorPosition() % -40960;

    // Robot moves backwards slightly, shooter powers on
  if (counter < 100){

      left.set(-0.25);
      right.set(0.25);
      shooterPower.set(ControlMode.PercentOutput, -0.65);    

    // Robot stops moving, cam spins and shoots the ball
    }else if(counter < 200){

      left.set(0);
      right.set(0);

      if(autoCamRemainder <= -19000 && autoCamRemainder >= -21000){
        autoCam1 = true;
      }

      if(autoCamRemainder <= 100 && autoCamRemainder >= -2500 && autoCam1){
        shooterCam.set(ControlMode.PercentOutput, 0);
        autoCam1 = false;
        autoCam2 = true;
      }else if(!autoCam2){
        shooterCam.set(ControlMode.PercentOutput, -0.4);
      }

      // shooter turns off, robot moves backwards a bit more
    }else if(counter < 300){

      shooterPower.set(ControlMode.PercentOutput, 0);

      left.set(-0.25);
      right.set(0.25);

      // robot stops
    }else{
      left.set(0);
      right.set(0);
    }

    counter++;

  }

  // This function is called once when teleop is enabled.
  @Override
  public void teleopInit() {}

  // This function is called periodically during operator control.
  @Override
  public void teleopPeriodic() {

    NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    NetworkTableEntry tx = table.getEntry("tx");
    NetworkTableEntry ty = table.getEntry("ty");
    NetworkTableEntry tv = table.getEntry("tv");

    //read values periodically
    double x = tx.getDouble(0.0);
    double y = ty.getDouble(0.0);
    boolean v = tv.getDouble(0) == 1;


    // DRIVE CODE
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
      intake.set(ControlMode.PercentOutput, -0.7);
    }else{
      intake.set(ControlMode.PercentOutput, 0);
    }

    // Transfer
    if(joystickButtons.getRawButton(4)){
      transfer.set(ControlMode.PercentOutput, -1);
    }else{
      transfer.set(ControlMode.PercentOutput, 0);
    }

    // Shooter
    if(joystickButtons.getRawButton(2)){
    shooterPower.set(ControlMode.PercentOutput, -0.65);
    }else{
    shooterPower.set(ControlMode.PercentOutput, 0);
    }

    // Cam
// Every time the cam completes a rotation, the position resets to 0
// (It doesn't really set the current position to 0, it just tells the code to consider it to be 0)
    CamRemainder = shooterCam.getSelectedSensorPosition() % -40960;

    // CamRemainderIsGood = true when the cam is in starting position
    if(CamRemainder <= 100 && CamRemainder >= -2500){
      CamRemainderIsGood = true;
    }else{
      CamRemainderIsGood = false;
    }

    // CamHasRotated = true when the cam is part way through a rotation
    // CamHasRotated = false when the cam completes a rotation
    if(CamRemainder <= -19000 && CamRemainder >= -21000){
     CamHasRotated = true;
    }
    // When you press button 5, the cam rotates 360*
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


      // OLD CAM CODE
      //
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
          shooterCam.set(ControlMode.PercentOutput, -0.2);
        }else if(joystickButtons.getRawAxis(1) == -1){
          shooterCam.set(ControlMode.PercentOutput, 0.2);
        }else{
          shooterCam.set(ControlMode.PercentOutput, 0);
        }
      }


      // Auto-orientation
      // Press these buttons to make the robot automaticly move to shooting position
      if(!joystickButtons.getRawButton(6) && !joystickButtons.getRawButton(1) && CamFixer == true){
        shooterCam.setSelectedSensorPosition(0);
        CamFixer = false;
      }


      if(joystickDriver.getRawButton(5) && joystickDriver.getRawButton(6)){
        orientation = true;
        orientation2 = true;
      }

        // Horizontal orientation
        if(orientation){
          if((x >= -3 && x <= 3) || !v){
            xIsGood = true;
            right.set(0);
            left.set(0);
            orientation = false;
            System.out.println("and stop");
          }else if(x < -3){
            right.set(-0.4);
            left.set(-0.4);
            System.out.println("to the left");
          }else if(x > 3){
            right.set(0.4);
            left.set(0.4);
            System.out.println("to the right");
          }
        }
  
        // Vertical orientation
        if(xIsGood && orientation2){
  
        if((y <= 20 && y >= 14) || !v){
          right.set(0);
          left.set(0);
          xIsGood = false;
          if((x >= -3 && x <= 3) || !v){
          }else{
            orientation = true;
            orientation2 = true;
          }
          }else if(y > 20){
            right.set(0.3);
            left.set(-0.3);
          }else if(y < 14){
            right.set(-0.3);
            left.set(0.3);
          }
  
        }

        // Press the menu and window buttons to stop auto-orientation (If it locks on to the wrong thing)
if(joystickDriver.getRawButton(7) && joystickDriver.getRawButton(8)){
  orientation = false;
  orientation2 = false;
  right.set(0);
  left.set(0);
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
  LiveWindow.setEnabled(false);
  }

  @Override
  public void testPeriodic() {

// AUTO-ORIENTATION TESTING:

    NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    NetworkTableEntry tx = table.getEntry("tx");
    NetworkTableEntry ty = table.getEntry("ty");

    //read values periodically
    double x = tx.getDouble(0.0);
    double y = ty.getDouble(0.0);


    // System.out.println(x);

    if(joystickDriver.getRawButton(5) && joystickDriver.getRawButton(6)){
      // System.out.println("Buttons are pressed");
      orientation = true;
    }

      if(orientation){
        if(x >= -3 && x <= 3){
          xIsGood = true;
          right.set(0);
          left.set(0);
          orientation = false;
          System.out.println("It's working");
        }else if(x < -3){
          right.set(-0.4);
          left.set(-0.4);
          System.out.println("hmmm");
        }else if(x > 3){
          right.set(0.4);
          left.set(0.4);
          System.out.println("huh");
        }
      }

      if(xIsGood){

      if(y <= 20 && y >= 12){
        right.set(0);
        left.set(0);
        xIsGood = false;
        }else if(y > 20){
          right.set(0.2);
          left.set(-0.2);
        }else if(y < 12){
          right.set(-0.2);
          left.set(0.2);
        }

      }
    
  }
}