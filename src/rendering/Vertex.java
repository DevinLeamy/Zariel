package rendering;

import math.*;

public class Vertex {
    public static int size = 11; // # of floats

    public Vector3 position;
    public Vector2 uv;
    public Vector3 normal;
    public Vector3 color;

    public Vertex(Vector3 position, Vector2 uv, Vector3 normal) {
        this.position = position;
        this.uv = uv;
        this.normal = normal;
        this.color = new Vector3(
                (float) Math.random(),
                (float) Math.random(),
                (float) Math.random()
        );
    }

    public float[] toArray() {
        return new float[] {
            position.x, position.y, position.z,
            uv.x, uv.y,
            normal.x, normal.y, normal.z,
            color.x, color.y, color.z
        };
    }
}
