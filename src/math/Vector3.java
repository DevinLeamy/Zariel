package math;

import java.util.Arrays;

public class Vector3 {
    public float x;
    public float y;
    public float z;

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float[] toArray() {
        return new float[] { x, y, z };
    }

    /**
     * Normalizes a vector. Updates "this"
     * @return "this"
     */
    public Vector3 normalize() {
        float len = len();

        if (len == 0) {
            System.err.println("Error: cannot normalize 0 vector");
            return null;
        }
        x /= len;
        y /= len;
        z /= len;

        return this;
    }

    public Vector3 scale(float mag) {
        x *= mag;
        y *= mag;
        z *= mag;

        return this;
    }

    @Override
    public boolean equals(Object _other) {
        if (!(_other instanceof Vector3 other)) {
            return false;
        }

        return x == other.x && y == other.y && z == other.z;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(toArray());
    }

    public static Vector3 sub(Vector3 u, Vector3 v) {
        return new Vector3(
               u.x - v.x,
               u.y - v.y,
               u.z - v.z
        );
    }

    public static Vector3 add(Vector3 u, Vector3 v) {
        return new Vector3(
                u.x + v.x,
                u.y + v.y,
                u.z + v.z
        );
    }

    public static Vector3 mult(Vector3 u, Vector3 v) {
        return new Vector3(
                u.x * v.x,
                u.y * v.y,
                u.z * v.z
        );
    }

    public Vector3 add(Vector3 other) {
        x += other.x;
        y += other.y;
        z += other.z;

        return this;
    }

    public Vector3 sub(Vector3 other) {
        x -= other.x;
        y -= other.y;
        z -= other.z;

        return this;
    }

    public Vector3 negate() {
        x *= -1;
        y *= -1;
        z *= -1;

        return this;
    }

    public float len() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public static Vector3 cross(Vector3 u, Vector3 v) {
        return new Vector3(
            u.y * v.z - u.z * v.y,
            u.z * v.x - u.x * v.z,
            u.x * v.y - u.y * v.x
        );
    }

    public static Vector3 scale(Vector3 v, float mag) {
        return new Vector3(
                v.x * mag,
                v.y * mag,
                v.z * mag
        );
    }

    public Vector3i toVector3i() {
        return new Vector3i((int) x, (int) y, (int) z);
    }

    public Vector3i toVector3i(boolean floored) {
        if (floored) {
            return new Vector3i((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
        }
        return toVector3i();
    }

    public Vector3 clone() {
        return new Vector3(x, y, z);
    }

    public static Vector3 zeros() {
        return new Vector3(0, 0, 0);
    }

    public String toString() {
        return String.format("x: %.2f y: %.2f, z: %.2f", x, y, z);
    }

    public static float dot(Vector3 u, Vector3 v) {
        return u.x * v.x + u.y * v.y + u.z * v.z;
    }

    public static float angleBetween(Vector3 u, Vector3 v) {
        if (u.len() == 0 || v.len() == 0) {
            System.err.println("Error: cannot calculate angle between zero vector");
            return 0;
        }

        return (float) Math.acos(Vector3.dot(u, v) / (u.len() * v.len()));
    }

    public static Vector3 anglesBetween(Vector3 u, Vector3 v) {
        return new Vector3(
            angleBetween(new Vector3(0, u.y, u.z), new Vector3(0, v.y, v.z)),
            angleBetween(new Vector3(u.x, 0, u.z), new Vector3(v.x, 0, v.z)),
            angleBetween(new Vector3(u.x, u.y, 0), new Vector3(v.x, v.y, 0))
        );
    }
}
