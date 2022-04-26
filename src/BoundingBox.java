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
            new Vector3(1, 1, 1)
    };

    private Vector3 dimensions;

    public BoundingBox(float width, float height, float depth) {
        this.dimensions = new Vector3(width, height, depth);
    }

    public Vector3[] vertices() {
        Vector3[] vertices = new Vector3[VERTEX_OFFSETS.length];

        for (int i = 0; i < 8; ++i) {
            vertices[i] = Vector3.mult(dimensions, VERTEX_OFFSETS[i]);
        }

        return vertices;
    }

    public int getWidth() {
        return (int) dimensions.x;
    }
    public int getHeight() {
        return (int) dimensions.y;
    }
    public int getDepth() {
        return (int) dimensions.z;
    }
}
