import math.Vector3;

public enum Direction {
    DOWN(new Vector3( 0.0f, -1.0f,  0.0f)),
    UP(new Vector3( 0.0f,  1.0f,  0.0f)),
    RIGHT(new Vector3( 1.0f,  0.0f,  0.0f)),
    BACK(new Vector3( 0.0f,  0.0f,  1.0f)),
    LEFT(new Vector3(-1.0f,  0.0f,  0.0f)),
    FRONT(new Vector3( 0.0f,  0.0f, -1.0f));

    public final Vector3 normal;

    private Direction(Vector3 normal) {
        this.normal = normal;
    }
}
