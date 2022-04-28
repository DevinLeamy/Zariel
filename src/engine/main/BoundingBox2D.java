package engine.main;

import math.Vector2;

public class BoundingBox2D {
    public Vector2 size;
    public Vector2 position;

    public BoundingBox2D(Vector2 size, Vector2 position) {
        this.size = size;
        this.position = position;
    }
}
