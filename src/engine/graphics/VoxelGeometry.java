package engine.graphics;

import engine.main.Block;
import engine.main.BlockType;
import lib.voxlib.*;
import math.Vector3i;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Optional;

public class VoxelGeometry {
    public Block[][][] voxels;
    private Vector3i dimensions;
    private int activeBlocks;
    public VoxelGeometry(Block[][][] voxels) {
        this.activeBlocks = 0;
        this.voxels = voxels;
        this.dimensions = new Vector3i(voxels.length, voxels[0].length, voxels[0][0].length);

        for (int i = 0; i < dimensions.x; ++i) {
            for (int j = 0; j < dimensions.y; ++j) {
                for (int k = 0; k < dimensions.z; ++k) {
                    if (voxels[i][j][k] != null && voxels[i][j][k].isActive()) {
                        ++activeBlocks;
                    }
                }
            }
        }
    }

    public VoxelGeometry(Vector3i dimensions) {
        this.dimensions = dimensions;
        this.voxels = new Block[dimensions.x][dimensions.y][dimensions.z];
        this.activeBlocks = 0;
    }

    public boolean isActive() {
        return activeBlocks > 0;
    }

    public ArrayList<Vector3i> activeOffsets() {
        ArrayList<Vector3i> offsets = new ArrayList<>();
        for (int i = 0; i < dimensions.x; ++i) {
            for (int j = 0; j < dimensions.y; ++j) {
                for (int k = 0; k < dimensions.z; ++k) {
                    if (voxels[i][j][k] != null && voxels[i][j][k].isActive()) {
                        offsets.add(new Vector3i(i, j, k));
                    }
                }
            }
        }

        return offsets;
    }

    public Vector3i dimensions() {
        return dimensions;
    }

    public Optional<Block> getBlock(Vector3i index) {
        return getBlock(index.x, index.y, index.z);
    }

    public boolean blockIsActive(int x, int y, int z) {
        Vector3i index = new Vector3i(x, y, z);
        Optional<Block> block = getBlock(index);
        if (block.isPresent() && block.get().isActive()) {
            return true;
        }

        return false;
    }

    private boolean validIndex(int x, int y, int z) {
        return 0 <= x && x < dimensions.x && 0 <= y && y < dimensions.y && 0 <= z && z < dimensions.z;
    }
    private boolean validIndex(Vector3i index) {
        return validIndex(index.x, index.y, index.z);
    }

    public Optional<Block> getBlock(int i, int j, int k) {
        if (!validIndex(i, j, k)) {
            return Optional.empty();
        }

        if (voxels[i][j][k] == null) {
            voxels[i][j][k] = new Block();
        }
        return Optional.of(voxels[i][j][k]);
    }


    public void setBlock(int i, int j, int k, Block newBlock) {
        if (!validIndex(i, j, k)) {
//            System.out.println("Cannot set");
            return;
        }
        if (getBlock(i, j, k).get().isActive()) {
            activeBlocks -= 1;
        }
        if (newBlock.isActive()) {
            activeBlocks += 1;
        }
        voxels[i][j][k] = newBlock;
    }

    public static int[] readRGBA(int raw) {
        return new int[] {
                // TODO: this should be leftward bit shifts
                ((raw >> 16) & 0xFF),
                ((raw >> 8) & 0xFF),
                ((raw >> 0) & 0xFF),
                ((raw >> 24) & 0xFF)
        };
    }

    private static int[] DEFAULT_PALETTE = {
        0x00000000, 0xffffffff, 0xffccffff, 0xff99ffff, 0xff66ffff, 0xff33ffff, 0xff00ffff, 0xffffccff, 0xffccccff, 0xff99ccff, 0xff66ccff, 0xff33ccff, 0xff00ccff, 0xffff99ff, 0xffcc99ff, 0xff9999ff,
        0xff6699ff, 0xff3399ff, 0xff0099ff, 0xffff66ff, 0xffcc66ff, 0xff9966ff, 0xff6666ff, 0xff3366ff, 0xff0066ff, 0xffff33ff, 0xffcc33ff, 0xff9933ff, 0xff6633ff, 0xff3333ff, 0xff0033ff, 0xffff00ff,
        0xffcc00ff, 0xff9900ff, 0xff6600ff, 0xff3300ff, 0xff0000ff, 0xffffffcc, 0xffccffcc, 0xff99ffcc, 0xff66ffcc, 0xff33ffcc, 0xff00ffcc, 0xffffcccc, 0xffcccccc, 0xff99cccc, 0xff66cccc, 0xff33cccc,
        0xff00cccc, 0xffff99cc, 0xffcc99cc, 0xff9999cc, 0xff6699cc, 0xff3399cc, 0xff0099cc, 0xffff66cc, 0xffcc66cc, 0xff9966cc, 0xff6666cc, 0xff3366cc, 0xff0066cc, 0xffff33cc, 0xffcc33cc, 0xff9933cc,
        0xff6633cc, 0xff3333cc, 0xff0033cc, 0xffff00cc, 0xffcc00cc, 0xff9900cc, 0xff6600cc, 0xff3300cc, 0xff0000cc, 0xffffff99, 0xffccff99, 0xff99ff99, 0xff66ff99, 0xff33ff99, 0xff00ff99, 0xffffcc99,
        0xffcccc99, 0xff99cc99, 0xff66cc99, 0xff33cc99, 0xff00cc99, 0xffff9999, 0xffcc9999, 0xff999999, 0xff669999, 0xff339999, 0xff009999, 0xffff6699, 0xffcc6699, 0xff996699, 0xff666699, 0xff336699,
        0xff006699, 0xffff3399, 0xffcc3399, 0xff993399, 0xff663399, 0xff333399, 0xff003399, 0xffff0099, 0xffcc0099, 0xff990099, 0xff660099, 0xff330099, 0xff000099, 0xffffff66, 0xffccff66, 0xff99ff66,
        0xff66ff66, 0xff33ff66, 0xff00ff66, 0xffffcc66, 0xffcccc66, 0xff99cc66, 0xff66cc66, 0xff33cc66, 0xff00cc66, 0xffff9966, 0xffcc9966, 0xff999966, 0xff669966, 0xff339966, 0xff009966, 0xffff6666,
        0xffcc6666, 0xff996666, 0xff666666, 0xff336666, 0xff006666, 0xffff3366, 0xffcc3366, 0xff993366, 0xff663366, 0xff333366, 0xff003366, 0xffff0066, 0xffcc0066, 0xff990066, 0xff660066, 0xff330066,
        0xff000066, 0xffffff33, 0xffccff33, 0xff99ff33, 0xff66ff33, 0xff33ff33, 0xff00ff33, 0xffffcc33, 0xffcccc33, 0xff99cc33, 0xff66cc33, 0xff33cc33, 0xff00cc33, 0xffff9933, 0xffcc9933, 0xff999933,
        0xff669933, 0xff339933, 0xff009933, 0xffff6633, 0xffcc6633, 0xff996633, 0xff666633, 0xff336633, 0xff006633, 0xffff3333, 0xffcc3333, 0xff993333, 0xff663333, 0xff333333, 0xff003333, 0xffff0033,
        0xffcc0033, 0xff990033, 0xff660033, 0xff330033, 0xff000033, 0xffffff00, 0xffccff00, 0xff99ff00, 0xff66ff00, 0xff33ff00, 0xff00ff00, 0xffffcc00, 0xffcccc00, 0xff99cc00, 0xff66cc00, 0xff33cc00,
        0xff00cc00, 0xffff9900, 0xffcc9900, 0xff999900, 0xff669900, 0xff339900, 0xff009900, 0xffff6600, 0xffcc6600, 0xff996600, 0xff666600, 0xff336600, 0xff006600, 0xffff3300, 0xffcc3300, 0xff993300,
        0xff663300, 0xff333300, 0xff003300, 0xffff0000, 0xffcc0000, 0xff990000, 0xff660000, 0xff330000, 0xff0000ee, 0xff0000dd, 0xff0000bb, 0xff0000aa, 0xff000088, 0xff000077, 0xff000055, 0xff000044,
        0xff000022, 0xff000011, 0xff00ee00, 0xff00dd00, 0xff00bb00, 0xff00aa00, 0xff008800, 0xff007700, 0xff005500, 0xff004400, 0xff002200, 0xff001100, 0xffee0000, 0xffdd0000, 0xffbb0000, 0xffaa0000,
        0xff880000, 0xff770000, 0xff550000, 0xff440000, 0xff220000, 0xff110000, 0xffeeeeee, 0xffdddddd, 0xffbbbbbb, 0xffaaaaaa, 0xff888888, 0xff777777, 0xff555555, 0xff444444, 0xff222222, 0xff111111
    };

    public static VoxelGeometry loadFromFile(String path) {
        try (VoxReader reader = new VoxReader(new FileInputStream(path))) {
            VoxFile voxFile = reader.read();
            int[] rawPalette = voxFile.getPalette();
            Vector3i[] palette = new Vector3i[rawPalette.length];


            for (int i = 0; i <= 255; ++i) {
                int[] rgba = readRGBA(rawPalette[i]);
                palette[i] = new Vector3i(rgba[0], rgba[1], rgba[2]);
            }


            for (VoxModelInstance model_instance : voxFile.getModelInstances()) {
                VoxModelBlueprint model = model_instance.model;
                int maxX = 0, maxY = 0, maxZ = 0;

                for (Voxel voxel : model.getVoxels()) {
                    int x = voxel.getPosition().x;
                    int y = voxel.getPosition().z;
                    int z = voxel.getPosition().y;

                    maxX = Integer.max(maxX, x);
                    maxY = Integer.max(maxY, y);
                    maxZ = Integer.max(maxZ, z);
                }

                System.out.println(maxX + " " + maxY + " " + maxZ);

                VoxelGeometry geometry = new VoxelGeometry(new Vector3i(maxX + 1, maxY + 1, maxZ + 1));

                for (Voxel voxel : model.getVoxels()) {
                    int x = voxel.getPosition().x;
                    int z = voxel.getPosition().y;
                    int y = voxel.getPosition().z;
                    int index = voxel.getColourIndex() & 0xFF;

                    geometry.setBlock(x, y, z, new Block(true, new BlockType(palette[index])));
                }

                return geometry;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.err.println("Error reading voxel file");
        return new VoxelGeometry(new Vector3i(1, 1, 1));
    }
}
