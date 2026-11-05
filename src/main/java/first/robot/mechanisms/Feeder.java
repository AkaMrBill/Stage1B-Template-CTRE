package first.robot.mechanisms;

import org.wpilib.command3.Command;
import org.wpilib.command3.Mechanism;
import org.wpilib.framework.RobotBase;
import org.wpilib.hardware.bus.CANPort;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.hardware.TalonFX;

import first.robot.simulation.SingleFlywheelSim;

public class Feeder implements Mechanism{

    
    private final TalonFX motor = new TalonFX(5, new CANBus(CANPort.CAN_S0));


    public void setDefaultCommand (Command idle){
        Mechanism.super.setDefaultCommand(idle());
    }

    public Command feed() {
    return run(coroutine -> {
          System.out.println("3/4 Speed!");
          while (true) {
            motor.setThrottle(.75);
            coroutine.yield();
          }
        })
        .named("Set 3/4 Throttle");
    }


    public Command intake() {
    return run(coroutine -> {
          System.out.println("Full intake Speed!");
          while (true) {
            motor.setThrottle(-1.0);
            coroutine.yield();
          }
        })
        .named("Full intake Speed");
    }

    public Command outtake() {
    return run(coroutine -> {
          System.out.println("Spit it out!!");
          while (true) {
            motor.setThrottle(1.0);
            coroutine.yield();
          }
        })
        .named("Unload the haul");
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
