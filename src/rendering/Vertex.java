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

//        float color = (Math.abs(position.x)) / (Math.abs(position.x) + Math.abs(position.y) + Math.abs(position.z) + 0.01f);
        float color = Double.toString(Math.PI).charAt(Math.abs(Math.round(position.x + position.y + position.z)) % 17) / 10.0f;
        this.color = new Vector3(
                color, color, color
//        (Math.abs(position.x)) / (Math.abs(position.x) + Math.abs(position.y) + Math.abs(position.z) + 0.01f),
//        (Math.abs(position.y)) / (Math.abs(position.x) + Math.abs(position.y) + Math.abs(position.z) + 0.01f),
//        (Math.abs(position.z)) / (Math.abs(position.x) + Math.abs(position.y) + Math.abs(position.z) + 0.01f)
//                (float) Math.random(),
//                (float) Math.random(),
//                (float) Math.random()
        );
    }

    public Vertex(Vector3 position) {
        this(position, new Vector2(0, 0), new Vector3(0, 0, 0));
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
