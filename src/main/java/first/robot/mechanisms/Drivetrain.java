package first.robot.mechanisms;

import org.wpilib.command3.Mechanism;

import java.util.function.DoubleSupplier;

import org.wpilib.command3.Command;
import org.wpilib.drive.DifferentialDrive;
import org.wpilib.framework.RobotBase;
import org.wpilib.hardware.bus.CANPort;
import org.wpilib.hardware.imu.OnboardIMU;
import org.wpilib.hardware.imu.OnboardIMU.MountOrientation;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.MotorAlignmentValue;

import first.robot.simulation.DrivetrainSim;

public class Drivetrain implements Mechanism {

     // Drive Train Motors
  public final TalonFX leftLeader = new TalonFX(0, new CANBus(CANPort.CAN_S0));
  public final TalonFX leftFollower = new TalonFX(1, new CANBus(CANPort.CAN_S0));
  public final TalonFX rightLeader = new TalonFX(2, new CANBus(CANPort.CAN_S0));
  public final TalonFX rightFollower = new TalonFX(3, new CANBus(CANPort.CAN_S0));

  public final DifferentialDrive differentialDrive =
      new DifferentialDrive(leftLeader::setThrottle, rightLeader::setThrottle);

  public final OnboardIMU imu = new OnboardIMU(MountOrientation.FLAT);

  public final DrivetrainSim drivetrainSim = new DrivetrainSim(leftLeader, rightLeader);


    public Drivetrain(){
        TalonFXConfiguration leftConfig = new TalonFXConfiguration();
                leftConfig.MotorOutput.withInverted(InvertedValue.Clockwise_Positive);

        leftLeader.getConfigurator().apply(leftConfig);
        leftFollower.getConfigurator().apply(leftConfig);

        leftFollower.setControl(new Follower(leftLeader.getDeviceID(), MotorAlignmentValue.Aligned));

        TalonFXConfiguration rightConfig = new TalonFXConfiguration();
                rightConfig.MotorOutput.withInverted(InvertedValue.CounterClockwise_Positive);

        rightLeader.getConfigurator().apply(rightConfig);
        rightFollower.getConfigurator().apply(rightConfig);

        rightFollower.setControl(new Follower(rightLeader.getDeviceID(), MotorAlignmentValue.Aligned));

        setDefaultCommand(idle());
    }

        public Command idle() {
            return run(coroutine -> {
                while (true) {
                    differentialDrive.arcadeDrive(0.0, 0.0);
                    coroutine.yield();
                }
            })
            .named("Idle");
        }

        public Command arcadeDrive(DoubleSupplier forwardThrottle, DoubleSupplier rotationThrottle) {
            return run(coroutine -> {
                while (true) {
                    differentialDrive.arcadeDrive(
                    forwardThrottle.getAsDouble(), rotationThrottle.getAsDouble());
                    coroutine.yield();
                }
            })
            .named("Drive");
    }

        public void periodic() {
        if (RobotBase.isSimulation()) {
        drivetrainSim.periodic();
        }
    }
}
