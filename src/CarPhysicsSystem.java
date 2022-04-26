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
        float linearAcceleration = cd.linearAcceleration;
        float steeringAngle = cd.steerAngle;
        boolean accelerating = cd.throttle > 0;

        float sideSlipAngle = Vector3.angleBetween(velocity, direction);
        float speed = velocity.len();

        // local to the car
        float vForward = (float) (Math.cos(sideSlipAngle) * speed);
        float vRight = (float) (Math.sin(sideSlipAngle) * speed);


        float wheelBase = cd.cgToFrontAxle + cd.cgToRearAxle;
        float frontWeightRatio = Physics.computeFrontWeightRatio(wheelBase, cd.cgToFrontAxle);
        float rearWeightRatio = Physics.computeRearWeightRatio(wheelBase, cd.cgToRearAxle);

        float weightFront = Physics.computeWeightFront(wheelBase, frontWeightRatio, cd.cgHeight, cd.mass, linearAcceleration);
        float weightRear = Physics.computeWeightRear(wheelBase, rearWeightRatio, cd.cgHeight, cd.mass, linearAcceleration);


        System.out.println("Steering angle: " + Utils.radiansToDegrees(cd.steerAngle));
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

//        float angleAccDir = Vector3.angleBetween(acceleration, direction);
//        float accelMag = acceleration.len();
//
//        float accelForward = (float) (Math.cos(angleAccDir) * accelMag);
//        float accelRight = (float) (Math.sin(angleAccDir) * accelMag);
//
//        float angleVeloDir = Vector3.angleBetween(velocity, direction);
//        float speed = velocity.len();
//
//        float veloForward = (float) (Math.cos(angleVeloDir) * speed);
//        float veloRight = (float) (Math.sin(angleVeloDir) * speed);
//
//        System.out.println(veloForward + " velo " + veloRight + " " + angleVeloDir);
//
//        // Weight on axles based on centre of gravity and weight shift due to forward/reverse acceleration
//        float axleWeightFront = mass * (cd.axleWeightRatioFront * PhysicsConfig.GRAVITY - cd.weightTransfer * accelForward * cd.cgHeight / cd.wheelBase);
//        float axleWeightRear =  mass * (cd.axleWeightRatioRear * PhysicsConfig.GRAVITY + cd.weightTransfer * accelForward * cd.cgHeight / cd.wheelBase);
//
//        // Resulting velocity of the wheels as result of the yaw rate of the car body.
//        // v = yawrate * r where r is distance from axle to CG and yawRate (angular velocity) in rad/s.
//        float yawSpeedFront = cd.cgToFrontAxle * cd.yawRate;
//        float yawSpeedRear = -cd.cgToRearAxle * cd.yawRate;
//
//        // Calculate slip angles for front and rear wheels (a.k.a. alpha)
//        var slipAngleFront = (float) Math.atan2(veloRight + yawSpeedFront, Math.abs(veloForward)) - Math.copySign(cd.steerAngle, veloForward);
//        var slipAngleRear  = (float) Math.atan2(veloRight + yawSpeedRear,  Math.abs(veloForward));
//
//        float tireGripFront = cd.tireGrip;
//        float tireGripRear = cd.tireGrip * (1 - cd.eBrake * (1 - cd.lockGrip)); // reduce rear grip when ebrake is on
//
//        var frictionForceFrontRight = Utils.clamp(-tireGripFront, tireGripFront, -cd.cornerStiffnessFront * slipAngleFront) * axleWeightFront;
//        var frictionForceRearRight = Utils.clamp(-tireGripRear, tireGripRear, -cd.cornerStiffnessRear * slipAngleRear) * axleWeightRear;
//
//        //  Get amount of brake/throttle from our inputs
//        var brake = Float.min(cd.brake * cd.brakeForce + cd.eBrake * cd.eBrakeForce, cd.brakeForce);
//        var throttle = cd.throttle * cd.engineForce;
//
//
//        //  Resulting force in local car coordinates.
//        //  This is implemented as a RWD car only.
//        var tractionForceForward = throttle - brake * Math.copySign(brake, veloForward);
//        var tractionForceRight = 0;
//
//        var dragForceForward = -cd.rollResist * veloForward - cd.airResist * veloForward * Math.abs(veloForward);
//        var dragForceRight = -cd.rollResist * veloForward - cd.airResist * veloRight * Math.abs(veloRight);
//
//        // total force in car coordinates
//        var totalForceForward = dragForceForward + tractionForceForward;
//        var totalForceRight = dragForceRight + tractionForceRight + (float) Math.cos(cd.steerAngle) * frictionForceFrontRight + frictionForceRearRight;
//
//        // acceleration along car axes
//        var newAccelForward = totalForceForward / cd.mass;  // forward/reverse accel
//        var newAccelRight = totalForceRight / cd.mass;  // sideways accel
//
//        // acceleration in world coordinates
//        Vector3 newAccel = Vector3.add(
////            Vector3.scale(transform.right(), newAccelRight),
//            new Vector3(0, acceleration.y, 0),
//            Vector3.scale(transform.direction(), newAccelForward)
//        );
//
//        System.out.println(newAccelRight + " " + newAccelForward);
//
//        dynamics.acceleration = newAccel;

        // calculate rotational forces
//        var angularTorque = (frictionForceFrontRight + tractionForceRight) * cd.cgToFrontAxle - frictionForceRearRight * cd.cgToRearAxle;
//
//        //  Sim gets unstable at very slow speeds, so just stop the car
//        if( Math.abs(speed) < 0.5 && cd.throttle == 0){
//            velocity.x = 0;
//            velocity.y = 0;
//            cd.yawRate = 0;
//            angularTorque = 0;
//        }
//
//        var angularAccel = angularTorque / cd.inertia;
//
//        cd.yawRate = angularAccel * dt;
//        transform.rotate(new Vector3(0, cd.yawRate, 0));

//        cd.throttle = 0;
//        cd.brake = 0;
//        Vector3 velocity = dynamics.velocity;
//        float fGravity = Physics.computeGravity(carDynamics.mass);
//
//        Vector3 fTraction = Physics.computeTraction(transform.direction(), carDynamics.engineForce);
////        Vector3 fDrag = Physics.computeDrag(PhysicsConfig.C_DRAG, velocity);
//        float fDrag = PhysicsConfig.C_DRAG * velocity.len() * velocity.len();
//        float fRR = PhysicsConfig.C_RR * fGravity; // Physics.computeRR(PhysicsConfig.C_RR, fGravity);
//
//
//        float resistance = Math.min(dynamics.acceleration.len() * carDynamics.mass, fDrag + fRR);
//        System.out.println(resistance);
//
//        if (velocity.len() != 0) {
//            fTraction.add(Vector3.scale(velocity.clone().normalize(), -resistance));
//        }
//
//        Vector3 fLongitudinal = fTraction.clone();
//
////        Vector3 fLongitudinal = Physics.computeLongitudinal(fTraction, fDrag, fRR);
//
////        System.out.println(dynamics.acceleration.z + " vel: " + dynamics.velocity.z + " long: " + fLongitudinal.z);
//
//        Vector3 acceleration = Physics.computeAcceleration(fLongitudinal, carDynamics.mass);
//        dynamics.acceleration.add(Vector3.scale(acceleration, dt));
////        System.out.println(acceleration + " " + carDynamics.engineForce);
//
//        // reset
//        carDynamics.engineForce = 0;
    }
}
