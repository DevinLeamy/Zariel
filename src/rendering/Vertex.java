package rendering;

import math.*;

public class Vertex {
    public static int size = 12; // # of floats

    public Vector3 position;
    public Vector2 uv;
    public Vector3 normal;
    public Vector3 color;
    public int ambientOcclusion;

    public Vertex(Vector3 position, Vector2 uv, Vector3 normal, Vector3 color) {
        this.position = position;
        this.uv = uv;
        this.normal = normal;
        this.color = color;
        this.ambientOcclusion = 3;
    }

    public void setAmbientOcclusion(int ao) {
        this.ambientOcclusion = ao;
    }

    public Vertex(Vector3 position, Vector2 uv, Vector3 normal) {
        this(position, uv, normal, new Vector3(0.5f, 0.5f, 0.5f));
    }

    public Vertex(Vector3 position) {
        this(position, new Vector2(0, 0), new Vector3(0, 0, 0));
    }

    public Vertex(Vector3 position, Vector3 color) {
        this(position, new Vector2(0, 0), new Vector3(0, 0, 0), color);
    }

    public float[] toArray() {
        return new float[] {
            position.x, position.y, position.z,
            uv.x, uv.y,
            normal.x, normal.y, normal.z,
            color.x, color.y, color.z,
            ambientOcclusion
        };
    }
}
