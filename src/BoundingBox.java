import math.Vector3;

public class BoundingBox extends Box {
    public BoundingBox(float width, float height, float depth) {
        super(new Vector3(width, height, depth));
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
