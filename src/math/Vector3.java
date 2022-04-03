package math;

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

    public Vector3 add(Vector3 other) {
        x += other.x;
        y += other.y;
        z += other.z;

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

    public static Vector3 zeros() {
        return new Vector3(0, 0, 0);
    }

    public String toString() {
        return "x: " + x + " y: " + y + " z: " + z + "\n";
    }

    public static float dot(Vector3 u, Vector3 v) {
        return u.x * v.x + u.y * v.y + u.z * v.z;
    }
}
