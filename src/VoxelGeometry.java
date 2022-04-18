import math.Vector3i;

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
                    activeBlocks += voxels[i][j][k].isActive() ? 1 : 0;
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
                    if (voxels[i][j][k].isActive()) {
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
            System.out.println("Cannot set");
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
}
