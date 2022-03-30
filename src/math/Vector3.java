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
}
