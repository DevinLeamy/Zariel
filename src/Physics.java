import math.Vector3;

// ref: https://asawicki.info/Mirror/Car%20Physics%20for%20Games/Car%20Physics%20for%20Games.html
public class Physics {
    // drag
    public static Vector3 computeDrag(float cDrag, Vector3 velocity) {
        float speed = computeSpeed(velocity);

        return Vector3.scale(velocity, speed * -cDrag);
    }

    // rolling resistance
    public static Vector3 computeRR(float cRR, Vector3 velocity) {
        return Vector3.scale(velocity, -cRR);
    }

    public static float computeGravity(float mass) {
        return mass * PhysicsConfig.GRAVITY;
    }


    // traction
    public static Vector3 computeTraction(Vector3 direction, float engineForce) {
        return Vector3.scale(direction, engineForce);
    }
    // braking
    public static Vector3 computeBraking(Vector3 direction, float cBraking) {
        return Vector3.scale(direction, -cBraking);
    }

    // longitudinal
    public static Vector3 computeLongitudinal(Vector3 fTractionOrBraking, Vector3 fDrag, Vector3 fRR) {
        return Vector3.add(fTractionOrBraking, fDrag, fRR);
    }

    public static float computeSpeed(Vector3 velocity) {
        return velocity.len();
    }

    // acceleration
    public static Vector3 computeAcceleration(Vector3 force, float mass) {
        if (mass <= 0) {
            System.err.println("Error: mass must be positive");
            System.exit(1);
        }

        return Vector3.scale(force, 1.0f / mass);
    }

    public static void applyAcceleration(float dt, Vector3 velocity, Vector3 acceleration) {
        velocity.add(Vector3.scale(acceleration, dt));
    }

    public static void applyVelocity(float dt, Vector3 position, Vector3 velocity) {
        position.add(Vector3.scale(velocity, dt));
    }

    public static float computeFrontWeightRatio(float wheelBase, float cgToFrontAxle) {
        return wheelBase * (1 - cgToFrontAxle);
    }

    public static float computeRearWeightRatio(float wheelBase, float cgToRearAxle) {
        return wheelBase * (1 - cgToRearAxle);
    }

    public static float computeWeightFront(float wheelBase, float weightRatioFront, float cgHeight, float mass, float linearAcceleration) {
        float weight = computeGravity(mass);

        return weightRatioFront * weight - (cgHeight / wheelBase) * mass * linearAcceleration;
    }

    public static float computeWeightRear(float wheelBase, float weightRatioRear, float cgHeight, float mass, float linearAcceleration) {
        float weight = computeGravity(mass);

        return weightRatioRear * weight + (cgHeight / wheelBase) * mass * linearAcceleration;
    }

    public static float computeSlip(float angularVelocity, float wheelRadius, float longitudinalVelocity) {
        return (angularVelocity * wheelRadius - longitudinalVelocity) / Math.abs(longitudinalVelocity);
    }

    public static void applyAngularVelocity(float dt, float angularVelocity, Transform transform) {
        transform.rotate(new Vector3(0, angularVelocity * dt, 0));
    }

    public static float computeAngularVelocity(float radiusOfRotation, float tangentVelocity) {
        // (rotations per second) * (radians per rotation) = (radians per second)
        // (v / circumference) * (2PI / 1)
        return tangentVelocity / radiusOfRotation;
    }
}
