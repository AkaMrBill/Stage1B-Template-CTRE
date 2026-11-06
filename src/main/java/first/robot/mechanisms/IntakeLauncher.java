package first.robot.mechanisms;

import static org.wpilib.units.Units.Seconds;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.hardware.TalonFX;
import first.robot.simulation.SingleFlywheelSim;
import org.wpilib.command3.Command;
import org.wpilib.command3.Mechanism;
import org.wpilib.framework.RobotBase;
import org.wpilib.hardware.bus.CANPort;

public class IntakeLauncher implements Mechanism {

  private final TalonFX motor = new TalonFX(4, new CANBus(CANPort.CAN_S0));

  public void setDefaultCommand(Command idle) {
    Mechanism.super.setDefaultCommand(idle());
  }

  public Command shoot() {
    return run(coroutine -> {
          coroutine.wait(Seconds.of(2));
          System.out.println("2 sec pause then go .9");
          while (true) {
            motor.setThrottle(0.9);
            coroutine.yield();
          }
        })
        .named("2 sec pause then go .9");
  }

  public Command intake() {
    return run(coroutine -> {
          System.out.println("80% Speed!");
          while (true) {
            motor.setThrottle(.8);
            coroutine.yield();
          }
        })
        .named("Set 0.8 Throttle");
  }

  public Command outtake() {
    return run(coroutine -> {
          System.out.println("reverse 80% Speed!");
          while (true) {
            motor.setThrottle(-0.8);
            coroutine.yield();
          }
        })
        .named("Set -0.8 Throttle");
  }

  public Command idle() {
    return run(coroutine -> {
          System.out.println("Motors Stop");
          while (true) {
            motor.setThrottle(0.0);
            coroutine.yield();
          }
        })
        .named("Motor Stop");
  }

  private final SingleFlywheelSim sim = SingleFlywheelSim.forFeeder(motor);

  public void periodic() { // Update the simulation
    if (RobotBase.isSimulation()) {
      sim.periodic();
    }
  }
}
