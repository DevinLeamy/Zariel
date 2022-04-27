package math;

import java.util.Arrays;

public class Vector3i {
    public int x;
    public int y;
    public int z;

    public Vector3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int[] toArray() {
        return new int[] { x, y, z };
    }

    /**
     * Normalizes a vector. Updates "this"
     * @return "this"
     */
    public Vector3i normalize() {
        int len = len();

        if (len == 0) {
            System.err.println("Error: cannot normalize 0 vector");
            return null;
        }
        x /= len;
        y /= len;
        z /= len;

        return this;
    }

    public Vector3i scale(float mag) {
        x *= mag;
        y *= mag;
        z *= mag;

        return this;
    }

    @Override
    public boolean equals(Object _other) {
        if (!(_other instanceof Vector3i other)) {
            return false;
        }

        return x == other.x && y == other.y && z == other.z;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(toArray());
    }

    public static Vector3i sub(Vector3i u, Vector3i v) {
        return new Vector3i(
                u.x - v.x,
                u.y - v.y,
                u.z - v.z
        );
    }

    public static Vector3i add(Vector3i u, Vector3i v) {
        return new Vector3i(
                u.x + v.x,
                u.y + v.y,
                u.z + v.z
        );
    }

    public Vector3i add(Vector3i other) {
        x += other.x;
        y += other.y;
        z += other.z;

        return this;
    }

    public int len() {
        return (int) Math.sqrt(x * x + y * y + z * z);
    }

    public static Vector3i cross(Vector3i u, Vector3i v) {
        return new Vector3i(
                u.y * v.z - u.z * v.y,
                u.z * v.x - u.x * v.z,
                u.x * v.y - u.y * v.x
        );
    }

    public static Vector3i scale(Vector3i v, float mag) {
        return new Vector3i(
                (int) (v.x * mag),
                (int) (v.y * mag),
                (int) (v.z * mag)
        );
    }

    public Vector3 toVector3() {
        return new Vector3(x, y, z);
    }

    public static Vector3i zeros() {
        return new Vector3i(0, 0, 0);
    }

    public String toString() {
        return "x: " + x + " y: " + y + " z: " + z + "\n";
    }

    public static int dot(Vector3i u, Vector3i v) {
        return u.x * v.x + u.y * v.y + u.z * v.z;
    }

    public Vector3i clone() {
        return new Vector3i(x, y, z);
    }
}
