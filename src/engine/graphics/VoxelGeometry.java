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
