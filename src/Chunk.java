import ecs.Component;
import ecs.ComponentRegistry;
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
        voxels = new VoxelGeometry(new Vector3i(CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE));

        Block[][][] trackBlocks = VoxelGeometry.loadFromFile("res/voxels/track2.vox").voxels;

        for (int i = 0; i < CHUNK_SIZE; ++i) {
            for (int j = 0; j < CHUNK_SIZE; ++j) {
                for (int k = 0; k < CHUNK_SIZE; ++k) {
                    try {
                       voxels.setBlock(i, j, k, trackBlocks[i][j][k]);
                    } catch (Exception e) {
                        if (voxels.voxels[i][j][k] == null) {
                            voxels.setBlock(i, j, k, new Block());
                        }
                    }

                    voxels.getBlock(i, j, k).get().setUpdateCallback(this::onBlockUpdate);
                }
            }
        }
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
    public void render(Camera prospective) {
        if (mesh.vertices() == 0) {
            return;
        }

        Vector3i location = Vector3i.scale(this.location, CHUNK_SIZE);

        renderer.shader.setUniform("viewM", prospective.viewMatrix());
        renderer.shader.setUniform("projectionM", prospective.projectionMatrix());
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
        return new BoundingBox(CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE);
    }
}
