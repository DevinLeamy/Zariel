import math.Vector3;
import math.Vector3i;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL41.*;

// TODO: implement the iterator interface for the blocks so we don't have to
//       next for loops all the time.

public class Chunk {
    final private static int CHUNK_SIZE = Config.CHUNK_SIZE;
    private static VertexShader vs = new VertexShader("res/shaders/chunk.vert");
    private static FragmentShader fs = new FragmentShader("res/shaders/chunk.frag");
    private static ShaderProgram shader = new ShaderProgram(vs, fs);

    /**
     * @field blocks: Blocks in the chunk
     * @field updated: Chunk mesh has been updated since the last render
     * @field chunkVerticesCount: Number of vertices in the chunk mesh
     * @field location: World coordinates (in chunks) of the chunk
     */

    VoxelGeometry voxels;
    VoxelMesh mesh;
    Renderer renderer;
    Vector3i location;
    float[][] noiseMap;
    public boolean loaded;
    boolean updated;

    public Chunk(Vector3i location) {
        this.location = location;
        this.loaded = false;
        this.updated = false;
        this.renderer = new Renderer(shader);


        NoiseMapGenerator noiseMapGenerator = NoiseMapGenerator.getInstance();
        Vector3 worldCoords = chunkCoordsToWorldCoords(location);

        this.noiseMap = noiseMapGenerator.generateNoiseMap((int) worldCoords.x, (int) worldCoords.z, CHUNK_SIZE, CHUNK_SIZE);
    }

    public Vector3 chunkCoordsToWorldCoords(Vector3i chunkCoords) {
        return Vector3i.scale(chunkCoords, CHUNK_SIZE).toVector3();
    }

    /**
     * Check if chunk contains at least one active block.
     */
    public boolean isActive() {
        return voxels.isActive();
    }

    public void initializeGeometry() {
        ArrayList<Vector3i> trees = new ArrayList<>();

        voxels = new VoxelGeometry(new Vector3i(CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE));

        for (int x = 0; x < CHUNK_SIZE; ++x) {
            for (int z = 0; z < CHUNK_SIZE; ++z) {
                float noise = noiseMap[x][z];
                noise = (noise + 1.0f) / 2.0f;
//                noise = 1.0f - noise * noise;

                int maxHeight = Integer.min((int) (noise * CHUNK_SIZE), CHUNK_SIZE - 1);
                int maxWorldHeight = (int) Float.min(this.location.y * CHUNK_SIZE + maxHeight, 17);
                for (int y = 0; y < maxHeight; ++y) {
                    float worldY = y + this.location.y * CHUNK_SIZE;

                    // check if the block is within spawning range
                    if (worldY > 17) {
                        voxels.setBlock(x, y, z, new Block(true, BlockType.SNOW));
                    } else {
                        voxels.setBlock(x, y, z, new Block(true, worldY > maxWorldHeight - 2 ? BlockType.GRASS : BlockType.DIRT));
                    }

                }

                if (0.001f > Math.random()) {
                    trees.add(new Vector3i(x, maxHeight, z));
                }
            }
        }

        trees.forEach(this::spawnTree);

        for (int i = 0; i < CHUNK_SIZE; ++i) {
            for (int j = 0; j < CHUNK_SIZE; ++j) {
                for (int k = 0; k < CHUNK_SIZE; ++k) {
                    voxels.getBlock(i, j, k).get().setUpdateCallback(this::onBlockUpdate);
                }
            }
        }
    }

    public void spawnTree(Vector3i chunkSpawnPoint) {
        int lowerY = chunkSpawnPoint.y;
        int upperY = lowerY + 5;

        if (upperY >= CHUNK_SIZE) {
            return;
        }

        int x = chunkSpawnPoint.x;
        int y = chunkSpawnPoint.y;
        int z = chunkSpawnPoint.z;

        try {
            BlockType truck = BlockType.DIRT;
            BlockType leaf = BlockType.SAND;
            // truck
            voxels.setBlock(x, y, z, new Block(true, truck));
            voxels.setBlock(x, y + 1, z, new Block(true, truck));
            voxels.setBlock(x, y + 2, z, new Block(true, truck));
            voxels.setBlock(x, y + 3, z, new Block(true, truck));

            // foliage
            voxels.setBlock(x, y + 3, z + 1, new Block(true, leaf));
            voxels.setBlock(x, y + 3,z - 1, new Block(true, leaf));
            voxels.setBlock(x + 1,y + 3,z, new Block(true, leaf));
            voxels.setBlock(x - 1,y + 3,z, new Block(true, leaf));
            voxels.setBlock(x,y + 4,z, new Block(true, leaf));
            voxels.setBlock(x + 1,y + 4,z + 1, new Block(true, leaf));
            voxels.setBlock(x - 1,y + 4,z + 1, new Block(true, leaf));
            voxels.setBlock(x - 1,y + 4,z - 1, new Block(true, leaf));
            voxels.setBlock(x + 1,y + 4,z - 1, new Block(true, leaf));
            voxels.setBlock(x, y + 5,z, new Block(true, leaf));
        } catch (ArrayIndexOutOfBoundsException e) {}
    }

    public void onBlockUpdate() {
        updated = true;
    }

    public static Vector3 worldCoordToChunkLocation(Vector3 coord) {
        return new Vector3 (
                coord.x / CHUNK_SIZE,
                coord.y / CHUNK_SIZE,
                coord.z / CHUNK_SIZE
        );
    }

    /**
     * Generate the mesh for a chunk and update the chunk's
     * vao and vbo.
     */
    public void load() {
        this.mesh = MeshGenerator.generateVoxelMesh(voxels, Vector3i.scale(location, CHUNK_SIZE));
        updated = false;
        loaded = true;
    }

    /**
     * You could store a uniform for the chunk coords and block size and then
     * only store the local vertex coordinates in the buffer. Then you won't need to
     * know the actual coordinates until you render so the mesh generation and rendering
     * steps can be separated.
     */
    public void render(Camera perspective) {
        if (mesh.vertices() == 0) {
            return;
        }

//        System.out.println(mesh.vertices());
        renderer.renderMesh(perspective, mesh, location.toVector3().scale(CHUNK_SIZE));
    }

    public Block getBlock(Vector3i pos) {
        return voxels.voxels[pos.x][pos.y][pos.z];// getBlock(pos);
    }

    public void unload() {
        mesh.dispose();
        loaded = false;
    }

    public static Vector3i getChunkLocalCoords(Vector3i worldCoords) {
        int x = worldCoords.x, y = worldCoords.y, z = worldCoords.z;

        int innerX = Utils.mod(x, CHUNK_SIZE);
        int innerY = Utils.mod(y, CHUNK_SIZE);
        int innerZ = Utils.mod(z, CHUNK_SIZE);

        return new Vector3i(innerX, innerY, innerZ);
    }

    public BoundingBox getBoundingBox() {
        return new BoundingBox(Vector3.scale(location.toVector3(), CHUNK_SIZE), CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE);
    }
}
