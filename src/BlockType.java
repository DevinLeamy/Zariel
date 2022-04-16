import math.Vector3;

public enum BlockType {
    GENERAL(new Vector3(0.0f, 0.0f, 1.0f)),
    SAND(new Vector3(252 / 255f, 232 / 255f, 177 / 255f)),
    DIRT(new Vector3(117 / 255f, 74 / 255f, 21 / 255f)),
    GRASS(new Vector3(52 / 255f, 140 / 255f, 49 / 255f)),
    EMPTY(new Vector3(0.0f, 0.0f, 0.0f));

    public final Vector3 color;

    BlockType(Vector3 color) {
        this.color = color;
    }
}
