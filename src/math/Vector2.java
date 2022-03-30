package math;

public class Vector2 {
    public float v0;
    public float v1;

    public Vector2(float v0, float v1) {
        this.v0 = v0;
        this.v1 = v1;
    }

    public float[] toArray() {
        return new float[] { v0, v1 };
    }
}
