package math;

import java.util.Arrays;

public class Vector4 {
    public float x, y, z, w;

    public Vector4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public float[] toArray() {
        return new float[] { x, y, z, w };
    }

    /**
     * Normalizes a vector. Updates "this"
     * @return "this"
     */
    public Vector4 normalize() {
        float len = len();

        if (len == 0) {
            System.err.println("Error: cannot normalize 0 vector");
            return null;
        }
        x /= len;
        y /= len;
        z /= len;
        w /= len;

        return this;
    }

    public Vector4 ceil() {
        x = (float) Math.ceil(x);
        y = (float) Math.ceil(y);
        z = (float) Math.ceil(z);
        w = (float) Math.ceil(w);

        return this;
    }

    public Vector4 scale(Vector4 dim) {
        x *= dim.x;
        y *= dim.y;
        z *= dim.z;
        w *= dim.w;

        return this;
    }

    public Vector4 scale(float mag) {
        x *= mag;
        y *= mag;
        z *= mag;
        w *= mag;

        return this;
    }

    @Override
    public boolean equals(Object _other) {
        if (!(_other instanceof Vector4 other)) {
            return false;
        }

        return x == other.x && y == other.y && z == other.z && w == other.w;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(toArray());
    }

    public static Vector4 sub(Vector4 u, Vector4 v) {
        return new Vector4(
                u.x - v.x,
                u.y - v.y,
                u.z - v.z,
                u.w - v.w
        );
    }

    public static Vector4 add(Vector4... vs) {
        Vector4 res = Vector4.zeros();
        for (Vector4 v : vs) {
            res.add(v);
        }

        return res;
    }

    public static Vector4 mult(Vector4 u, Vector4 v) {
        return new Vector4(
                u.x * v.x,
                u.y * v.y,
                u.z * v.z,
                u.w * v.w
        );
    }

    public Vector4 add(Vector4 other) {
        x += other.x;
        y += other.y;
        z += other.z;
        w += other.w;

        return this;
    }

    public Vector4 sub(Vector4 other) {
        x -= other.x;
        y -= other.y;
        z -= other.z;
        w -= other.w;

        return this;
    }

    public Vector4 negate() {
        x *= -1;
        y *= -1;
        z *= -1;
        w *= -1;

        return this;
    }

    public float len() {
        return (float) Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public static Vector4 scale(Vector4 v, float mag) {
        return new Vector4(
                v.x * mag,
                v.y * mag,
                v.z * mag,
                v.w * mag
        );
    }

    @Override
    public Vector4 clone() {
        return new Vector4(x, y, z, w);
    }

    public static Vector4 zeros() {
        return new Vector4(0, 0, 0, 0);
    }

    public String toString() {
        return String.format("vec4[x: %.2f y: %.2f z: %.2f w: %.2f]", x, y, z, w);
    }

    public static float dot(Vector4 u, Vector4 v) {
        return u.x * v.x + u.y * v.y + u.z * v.z + u.w * v.w;
    }
}
