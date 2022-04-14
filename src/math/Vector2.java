package math;

import java.util.Arrays;

public class Vector2 {
    public float x;
    public float y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 scale(float mag) {
        x *= mag;
        y *= mag;

        return this;
    }

    public static Vector2 scale(Vector2 v, float mag) {
        return new Vector2(
                v.x * mag,
                v.y * mag
        );
    }

    @Override
    public boolean equals(Object _other) {
        if (!(_other instanceof Vector2 other)) {
            return false;
        }

        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(toArray());
    }

    public float len() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public Vector2 normalize() {
        float mag = len();

        if (mag == 0) {
            System.err.println("Cannot normalize 0 vector");
            return this;
        }

        x /= mag;
        y /= mag;

        return this;
    }

    public static Vector2 zeros() {
        return new Vector2(0.0f, 0.0f);
    }

    public static Vector2 sub(Vector2 u, Vector2 v) {
        return new Vector2(
                u.x - v.x,
                u.y - v.y
        );
    }

    public static float dot(Vector2 u, Vector2 v) {
        return u.x * v.x + u.y * v.y;
    }

    public String toString() {
        return "x: " + x + " y: " + y;
    }


    public float[] toArray() {
        return new float[] { x, y };
    }
}
