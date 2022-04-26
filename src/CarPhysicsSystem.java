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
        float fGravity = Physics.computeGravity(carDynamics.mass);

        Vector3 fTraction = Physics.computeTraction(transform.direction(), carDynamics.engineForce);
//        Vector3 fDrag = Physics.computeDrag(PhysicsConfig.C_DRAG, velocity);
        float fDrag = PhysicsConfig.C_DRAG * velocity.len() * velocity.len();
        float fRR = PhysicsConfig.C_RR * fGravity; // Physics.computeRR(PhysicsConfig.C_RR, fGravity);


        float resistance = Math.min(dynamics.acceleration.len() * carDynamics.mass, fDrag + fRR);
        System.out.println(resistance);

        if (velocity.len() != 0) {
            fTraction.add(Vector3.scale(velocity.clone().normalize(), -resistance));
        }

        Vector3 fLongitudinal = fTraction.clone();

//        Vector3 fLongitudinal = Physics.computeLongitudinal(fTraction, fDrag, fRR);

//        System.out.println(dynamics.acceleration.z + " vel: " + dynamics.velocity.z + " long: " + fLongitudinal.z);

        Vector3 acceleration = Physics.computeAcceleration(fLongitudinal, carDynamics.mass);
        dynamics.acceleration.add(Vector3.scale(acceleration, dt));
//        System.out.println(acceleration + " " + carDynamics.engineForce);

        // reset
        carDynamics.engineForce = 0;
    }
}
