package rendering;

import math.*;

public class Vertex {
    public Vector3 position;
    public Vector2 uv;
    public Vector3 normal;

    public Vertex(Vector3 position, Vector2 uv, Vector3 normal) {
        this.position = position;
        this.uv = uv;
        this.normal = normal;
    }

    public float[] toArray() {
        return new float[] {
            position.v0, position.v1, position.v2,
            uv.v0, uv.v1,
            normal.v0, normal.v1, normal.v2
        };
    }
}
