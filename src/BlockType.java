import math.Vector3;
import math.Vector3i;

public class BlockType {
    public static BlockType EMPTY = new BlockType(7, 7);
    public static BlockType BEDROCK = new BlockType(new Vector3i(40, 40, 230), 1, 1);
    public static BlockType LEAF = new BlockType(new Vector3i(140, 245, 140), 3, 4);
    public static BlockType WOOD = new BlockType(new Vector3i(117, 74, 21), 1, 4);
    public static BlockType STONE = new BlockType(new Vector3i(140, 140, 140), 0, 1);
    public static BlockType SNOW = new BlockType(4, 2);
    public static BlockType GRASS = new BlockType(new Vector3i(52, 140, 49), 2, 8);
    public static BlockType RED = new BlockType(8, 1);


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
