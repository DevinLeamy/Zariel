import math.Vector3i;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class VoxelGeometry {
//    private Block[] voxels;
    public Block[][][] voxels;
    private Vector3i dimensions;
    public VoxelGeometry(Block[][][] voxels) {
//        this.voxels = flatten(voxels);
        this.voxels = voxels;
        this.dimensions = new Vector3i(voxels.length, voxels[0].length, voxels[0][0].length);
    }
    public VoxelGeometry(Vector3i dimensions) {
        this.dimensions = dimensions;
        this.voxels = new Block[dimensions.x][dimensions.y][dimensions.z];
//        this.voxels = new Block[dimensions.x * dimensions.y * dimensions.z];
    }

    public boolean isActive() {
        boolean active = false;
        for (int i = 0; i < dimensions.x; ++i) {
            for (int j = 0; j < dimensions.y; ++j) {
                for (int k = 0; k < dimensions.z; ++k) {
                    active |= voxels[i][j][k].isActive();
                }
            }
        }
//        for (Block voxel : voxels) {
//            if (voxel != null) {
//                active |= voxel.isActive();
//            }
//        }

        return active;
    }

    private Block[] flatten(Block[][][] voxels) {
        Vector3i dims = new Vector3i(voxels.length, voxels[0].length, voxels[0][0].length);
        Block[] flattened = Arrays.stream(voxels)
                .flatMap(Arrays::stream)
                .flatMap(Arrays::stream).toList()
                .toArray(new Block[dims.x * dims.y * dims.z]);
        return flattened;
    }

    public Vector3i dimensions() {
        return dimensions;
    }

    public Block getBlock(Vector3i index) {
        if (voxels[index.x][index.y][index.z] == null) {
            voxels[index.x][index.y][index.z] = new Block();
        }
        return voxels[index.x][index.y][index.z];
//        if (voxels[index(index)] == null) {
//            voxels[index(index)] = new Block();
//        }
//        return voxels[index(index)];
    }

    public boolean blockIsActive(int i, int j, int k) {
        if (0 <= i && i < dimensions.x && 0 <= j && j < dimensions.y && 0 <= k && k < dimensions.z) {
            return voxels[i][j][k].isActive();
        }
        return false;
    }

    public Block getBlock(int i, int j, int k) {
        if (voxels[i][j][k] == null) {
            voxels[i][j][k] = new Block();
        }
        return voxels[i][j][k];
//        if (voxels[index(i, j, k)] == null) {
//            voxels[index(i, j, k)] = new Block();
//        }
//        return voxels[index(i, j, k)];
    }

    private int index(int i, int j, int k) {
        return i * dimensions.x + j * dimensions.y + k * dimensions.z;
    }

    private int index(Vector3i i) {
        return Vector3i.dot(dimensions, i);
    }

    public void setBlock(int i, int j, int k, Block newBlock) {
//        voxels[index(i, j, k)] = newBlock;
        voxels[i][j][k] = newBlock;
    }
}
