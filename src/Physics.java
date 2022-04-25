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

    // traction
    public static Vector3 computeTraction(Vector3 direction, float engineForce) {
        Vector3 normalized = direction.clone().normalize();
        return Vector3.scale(normalized, engineForce);
    }
    public static Vector3 computeTraction(Vector3 direction, Vector3 engineForce) {
        Vector3 normalized = direction.clone().normalize();
        return Vector3.mult(normalized, engineForce);
    }

    // braking
    public static Vector3 computeBraking(Vector3 direction, float cBraking) {
        assert(direction.len() == 1);
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
}
