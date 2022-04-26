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
        CarDynamics cd = entity.getComponent(CarDynamics.class).get();

        Vector3 velocity = dynamics.velocity;
        Vector3 acceleration = dynamics.acceleration;
        Vector3 direction = transform.direction();
//        float linearAcceleration = cd.linearAcceleration;
        float steeringAngle = cd.steerAngle;
        boolean accelerating = cd.throttle > 0;

//        float sideSlipAngle = Vector3.angleBetween(velocity, direction);
//        float speed = velocity.len();

        // local to the car
//        float vForward = (float) (Math.cos(sideSlipAngle) * speed);
//        float vRight = (float) (Math.sin(sideSlipAngle) * speed);


        float wheelBase = cd.cgToFrontAxle + cd.cgToRearAxle;
//        float frontWeightRatio = Physics.computeFrontWeightRatio(wheelBase, cd.cgToFrontAxle);
//        float rearWeightRatio = Physics.computeRearWeightRatio(wheelBase, cd.cgToRearAxle);

//        float weightFront = Physics.computeWeightFront(wheelBase, frontWeightRatio, cd.cgHeight, cd.mass, linearAcceleration);
//        float weightRear = Physics.computeWeightRear(wheelBase, rearWeightRatio, cd.cgHeight, cd.mass, linearAcceleration);


//        System.out.println("Steering angle: " + Utils.radiansToDegrees(cd.steerAngle));
        float radiusOfRotation = wheelBase / (float) Math.sin(steeringAngle);
        dynamics.angularVelocity = Physics.computeAngularVelocity(radiusOfRotation, Math.abs(velocity.len()));
        // bias towards the center
        cd.steerAngle += Math.copySign(cd.steerAngle * 0.025f, cd.steerAngle) * -1;

//        System.out.println(vRight + " " + speed);


        float resistanceForward = (20) * velocity.len() * Math.abs(velocity.len()) + 600 * velocity.len();
        float tractionForward = cd.engineForce * cd.throttle;

//        float resistanceRight = 0.42f * vRight * Math.abs(vRight) + 300 * vRight;

//        System.out.println(resistanceRight + " " + resistanceForward);


        Vector3 fLongitudinal = Vector3.scale(direction, tractionForward);
//        System.out.println(tractionForward + " " + )
        if (velocity.len() > 0) {

            fLongitudinal.add(Vector3.scale(velocity.clone().normalize(), -resistanceForward));
        }
//        fLongitudinal.add(Vector3.scale(transform.right(), -resistanceRight));
//        Vector3 fLongitudinal = Physics.computeLongitudinal(
//                Physics.computeTraction(direction, cd.engineForce * cd.throttle),
//                Physics.computeDrag(0.42f, velocity),
//                Physics.computeRR(12, velocity)
//        );
        dynamics.acceleration = Physics.computeAcceleration(fLongitudinal, cd.mass);
        dynamics.acceleration.y = acceleration.y;

        cd.linearAcceleration = accelerating ? acceleration.len() : -acceleration.len();

        cd.throttle = 0;
        cd.brake = 0;
    }
}
