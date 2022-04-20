import math.Vector3;

import java.util.ArrayList;

public class BoundingBox {
    final private static Vector3[] VERTEX_OFFSETS = new Vector3[] {
            new Vector3(0, 0, 0),
            new Vector3(1, 0, 0),
            new Vector3(0, 1, 0),
            new Vector3(0, 0, 1),
            new Vector3(1, 1, 0),
            new Vector3(0, 1, 1),
            new Vector3(1, 0, 1),
            new Vector3(1, 0, 1)
    };

    private Vector3 origin;
    private Vector3 dimensions;

    public BoundingBox(Vector3 origin, float width, float height, float depth) {
        this.origin = origin;
        this.dimensions = new Vector3(width, height, depth);
    }

    public void setOrigin(Vector3 origin) {
        this.origin = origin;
    }

    public Vector3[] vertices() {
        Vector3[] vertices = new Vector3[6];

        for (int i = 0; i < 6; ++i) {
            Vector3 originOffset = Vector3.mult(dimensions, VERTEX_OFFSETS[i]);
            vertices[i] = origin.clone().add(originOffset);
        }

        return vertices;
    }
}
