import math.Matrix4;
import math.Vector3;
import math.Vector3i;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL41.*;

// TODO: implement the iterator interface for the blocks so we don't have to
//       next for loops all the time.

public class Chunk {
    final private static int CHUNK_SIZE = Config.CHUNK_SIZE;
    private static VertexShader vs = new VertexShader("res/shaders/chunk.vert", new Uniform[] {
        new Uniform("viewM", Uniform.UniformT.MATRIX_4F),
        new Uniform("projectionM", Uniform.UniformT.MATRIX_4F),
        new Uniform("location", Uniform.UniformT.VECTOR_3F)
    });
    private static FragmentShader fs = new FragmentShader("res/shaders/chunk.frag", new Uniform[] {});
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

        TerrainGenerator terrainGenerator = new TerrainGenerator();
        NoiseMapGenerator noiseMap = NoiseMapGenerator.getInstance();
        voxels = new VoxelGeometry(new Vector3i(CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE));

        for (int x = 0; x < CHUNK_SIZE; ++x) {
            for (int z = 0; z < CHUNK_SIZE; ++z) {
                int nx = location.x * CHUNK_SIZE + x;
                int ny = location.z * CHUNK_SIZE + z;
//                float noise = noiseMap.noise(1 * nx, 1 * ny);
////                                +  Config.debug2 * noiseMap.noise(2 * nx + 65, 2 * ny + 97)
//                            +  0.72f * noiseMap.noise(2 * nx + 65, 2 * ny + 97)
//                        + 0.25f * noiseMap.noise(4 * nx + 30, 4 * ny + 20);
//                noise /= 1.07f;
//                noise = (float) Math.pow(noise, 1.2f);
                int height = (int) Math.floor(0.5 * Config.MAX_LEVEL);
                height -= height % 2;
                int chunkBase = this.location.y * CHUNK_SIZE;

                for (int y = 0; y < CHUNK_SIZE; ++y) {
                    int worldHeight = chunkBase + y;
                    if (worldHeight > height) {
                        break;
                    }

                    voxels.setBlock(x, y, z, terrainGenerator.getBlock(height));
                }

                if (0.001f > Math.random() && height >= chunkBase && height < chunkBase + CHUNK_SIZE) {
                    trees.add(new Vector3i(x, height, z));
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
            BlockType truck = BlockType.WOOD;
            BlockType leaf = BlockType.LEAF;
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

        Vector3 cameraPos = perspective.transform.position;
        Vector3 playerPos = World.getInstance().player.transform.position;

        Matrix4 viewMatrix = Camera.lookAt(cameraPos, playerPos, new Vector3(0, 1, 0));
        Matrix4 projectionMatrix = perspective.projectionMatrix();
        Vector3i location = Vector3i.scale(this.location, CHUNK_SIZE);

        renderer.shader.setUniform("viewM", viewMatrix);
        renderer.shader.setUniform("projectionM", projectionMatrix);
        renderer.shader.setUniform("location", location);

        renderer.renderMesh(mesh);
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
