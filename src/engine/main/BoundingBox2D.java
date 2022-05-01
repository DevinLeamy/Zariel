package engine.main;

import math.Vector2;

public class BoundingBox2D {
    public Vector2 size;
    public Vector2 position;

    public BoundingBox2D(Vector2 size, Vector2 position) {
        this.size = size;
        this.position = position;
    }

    public boolean contains(float mouseX, float mouseY) {
        if (mouseX < position.x || mouseX >= position.x + size.x) return false;
        if (mouseY < position.y || mouseY >= position.y + size.y) return false;
        return true;
    }
}
