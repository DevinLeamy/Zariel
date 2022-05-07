package engine.main;

import math.Vector3;
import math.Vector3i;

public class BlockType {
    public static BlockType EMPTY = new BlockType(7, 7);
    public static BlockType SNOW = new BlockType(new Vector3i(245, 245, 245), 4, 2);

    public int textureRow;
    public int textureCol;
    public final boolean textured;
    public final boolean colored;
    public Vector3i color;
    public BlockType(int textureRow, int textureCol) {
        this.textureRow = textureRow;
        this.textureCol = textureCol;
        this.textured = true;
        this.colored = false;
    }

    public BlockType(Vector3i color) {
        this.color = color;
        this.textured = false;
        this.colored = true;
    }

    public BlockType(Vector3i color, int textureRow, int textureCol) {
        this.color = color;
        this.textureRow = textureRow;
        this.textureCol = textureCol;
        this.colored = true;
        this.textured = true;
    }

    public Vector3 getGLColor() {
        return Vector3.scale(color.toVector3(), 1 / 256.0f);
    }
}
