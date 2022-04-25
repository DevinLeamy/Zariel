import ecs.ComponentRegistry;
import ecs.Entity;
import math.Vector3;

public class CarPhysicsSystem extends InstanceSystem {
    public CarPhysicsSystem() {
        super(ComponentRegistry.getSignature(Transform.class, Dynamics.class, CarDynamics.class), 0);
    }

    @Override
    protected void update(float dt, Entity entity) {
        Transform transform = entity.getComponent(Transform.class).get();
        Dynamics dynamics = entity.getComponent(Dynamics.class).get();
        CarDynamics carDynamics = entity.getComponent(CarDynamics.class).get();

        Vector3 velocity = dynamics.velocity;

        Vector3 fTraction = Physics.computeTraction(transform.direction(), carDynamics.engineForce);
        Vector3 fDrag = Physics.computeDrag(PhysicsConfig.C_DRAG, velocity);
        Vector3 fRR = Physics.computeRR(PhysicsConfig.C_RR, velocity);
        Vector3 fLongitudinal = Physics.computeLongitudinal(fTraction, fDrag, fRR);

        Vector3 acceleration = Physics.computeAcceleration(fLongitudinal, carDynamics.mass);

//        System.out.println(acceleration + " " + carDynamics.engineForce);

        dynamics.acceleration.add(Vector3.scale(acceleration, dt));

        // reset
        carDynamics.engineForce = 0;
    }
}
