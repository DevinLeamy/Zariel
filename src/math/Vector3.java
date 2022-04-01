package math;

public class Vector3 {
    public float v0;
    public float v1;
    public float v2;

    public Vector3(float v0, float v1, float v2) {
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
    }

    public float[] toArray() {
        return new float[] { v0, v1, v2 };
    }

    public void normalize() {
        float len = len();
        v0 /= len;
        v1 /= len;
        v2 /= len;
    }

    public float len() {
        return (float) Math.sqrt(v0 * v0 + v1 * v1 + v2 * v2);
    }

    public static Vector3 cross(Vector3 v1, Vector3 v2) {
        return new Vector3(
            v1.v1 * v2.v2 - v1.v2 * v2.v1,
            v1.v2 * v2.v0 - v1.v0 * v2.v2,
            v1.v0 * v1.v1 - v1.v1 * v2.v0
        );
    }
}
