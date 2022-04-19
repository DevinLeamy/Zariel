import math.Vector3;
import math.Vector3i;

public class BlockType {
    public static BlockType EMPTY = new BlockType(Vector3i.zeros());
    public static BlockType SAND = new BlockType(new Vector3i(252, 232, 177));
    public static BlockType DIRT = new BlockType(new Vector3i(117, 74, 21));
    public static BlockType BEDROCK = new BlockType(new Vector3i(117, 74, 21));
    public static BlockType GRASS = new BlockType(new Vector3i(52, 140, 49));
    public static BlockType STONE = new BlockType(new Vector3i(140, 140, 140));
    public static BlockType WATER = new BlockType(new Vector3i(40, 40, 230));
    public static BlockType SNOW = new BlockType(new Vector3i(245, 245, 245));
    public static BlockType RED = new BlockType(new Vector3i(255, 40, 0));

    public final Vector3 color;

    public BlockType(Vector3i rgb) {
        this.color = Vector3.scale(rgb.toVector3(), 1.0f / 255f);
    }
}
