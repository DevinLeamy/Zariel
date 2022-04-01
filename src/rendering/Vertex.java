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
//        System.out.println(
//            position.v0 + " " + position.v1 + " " + position.v2 + " " +
//            uv.v0 + " " + uv.v1 + " " +
//            normal.v0 + " " + normal.v1 + " " + normal.v2
//        );
        return new float[] {
            position.v0, position.v1, position.v2,
            uv.v0, uv.v1,
            normal.v0, normal.v1, normal.v2,
            color.v0, color.v1, color.v2
        };
    }
}
