import math.Vector3;
import org.lwjgl.BufferUtils;
import rendering.Mesh;
import rendering.MeshLoader;
import rendering.Vertex;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL41.*;

public class Chunk {
    final private static int CHUNK_SIZE = 64;
    private static VertexShader vs = new VertexShader("res/shaders/chunk.vert");
    private static FragmentShader fs = new FragmentShader("res/shaders/chunk.frag");
    private static ShaderProgram shader = new ShaderProgram(vs, fs);

    /**
     * @field blocks: Blocks in the chunk
     * @field updated: Chunk mesh has been updated since the last render
     * @field chunkVerticesCount: Number of vertices in the chunk mesh
     * @field location: World coordinates (in chunks) of the chunk
     */

    Block[][][] blocks;
    boolean updated;
    int vao, vbo;
    int chunkVerticesCount;
    Vector3 location;
    NoiseMap noiseMap;

    public Chunk(Vector3 location) {
        this.updated = true;
        this.vao = glGenVertexArrays();
        this.vbo = glGenBuffers();
        this.chunkVerticesCount = 0;
        this.location = location;
        this.noiseMap = new NoiseMap(CHUNK_SIZE, CHUNK_SIZE);

        /**
         * Initialize the blocks in the chunk
         */
        blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        initialize();
    }

    public void initialize() {
        for (int x = 0; x < CHUNK_SIZE; ++x) {
            for (int z = 0; z < CHUNK_SIZE; ++z) {
                float noise = noiseMap.noise(x, z);
                noise = (noise + 1.0f) / 2.0f;

                int maxHeight = (int) (noise * CHUNK_SIZE);
                for (int y = 0; y < maxHeight; ++y) {
                    float worldY = y + this.location.y * CHUNK_SIZE;

                    // check if the block is within spawning range
                    if (worldY < Config.GROUND_LEVEL || worldY >= Config.FLOOR_LEVEL) {
                        blocks[x][y][z] = new Block(false, BlockType.EMPTY);
                        continue;
                    }

                    if (Math.random() < 0.2) {
                        blocks[x][y][z] = new Block(true, BlockType.DIRT);
                    } else {
                        blocks[x][y][z] = new Block(true, BlockType.SAND);
                    }
                }

                for (int y = maxHeight; y < CHUNK_SIZE; ++y) {
                    blocks[x][y][z] = new Block(false, BlockType.EMPTY);
                }
            }
        }
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
        ArrayList<Vertex> chunkVertices = new ArrayList<>();
        for (int i = 0; i < CHUNK_SIZE; ++i) {
            for (int j = 0; j < CHUNK_SIZE; ++j) {
                for (int k = 0; k < CHUNK_SIZE; ++k) {
                    if (!blocks[i][j][k].isActive()) {
                        continue;
                    }

                    ArrayList<Vertex> blockVertices = createBlockVertices(blocks[i][j][k].getBlockType(), i, j, k);
                    chunkVertices.addAll(blockVertices);
                }
            }
        }

        chunkVerticesCount = chunkVertices.size();

        if (chunkVerticesCount == 0) {
            return;
        }

        // buffer to hold vertex data
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(chunkVerticesCount * Vertex.size);

        for (Vertex vertex : chunkVertices) {
            vertexBuffer.put(vertex.toArray());
        }

        // return buffer to the start
        vertexBuffer.flip();

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        // store vertex data in GL_ARRAY_BUFFER
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // enable attributes
        glEnableVertexAttribArray(0); // position
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);

        int stride = 4 * Vertex.size; // 11 = 3 (coords) + 2 (uv) + 3 (normal) + 3 (colors)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0L);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, stride,  3 * 4);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, stride,  5 * 4);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, stride,  8 * 4);

        // unbind vertex array
        glBindVertexArray(0);
    }

    public void render(Camera perspective) {
        if (chunkVerticesCount == 0) {
            return;
        }

        shader.link();

        // set shader uniforms
        int shaderHandle = shader.getProgramHandle();
        int viewMHander         = glGetUniformLocation(shaderHandle, "viewM");
        int projectionMHandler  = glGetUniformLocation(shaderHandle, "projectionM");

        glUniformMatrix4fv(viewMHander, true, perspective.viewMatrix().toFloatBuffer());
        glUniformMatrix4fv(projectionMHandler, true, perspective.projectionMatrix().toFloatBuffer());

        glBindVertexArray(vao);

        glDrawArrays(GL_TRIANGLES, 0, this.chunkVerticesCount);

        // clean up buffers
        glBindVertexArray(0);
    }

    private ArrayList<Vertex> createBlockVertices(BlockType blockType, int x, int y, int z) {
        // vertices
        Vector3 v1 = new Vector3 (1.0f,  0.0f, 0.0f);
        Vector3 v2 = new Vector3 (1.0f,  0.0f, 1.0f);
        Vector3 v3 = new Vector3 (0.0f, 0.0f, 1.0f);
        Vector3 v4 = new Vector3 (0.0f, 0.0f, 0.0f);
        Vector3 v5 = new Vector3 (1.0f,  1.0f, 0.0f );
        Vector3 v6 = new Vector3 (1.0f,  1.0f, 1.0f);
        Vector3 v7 = new Vector3 (0.0f, 1.0f, 1.0f);
        Vector3 v8 = new Vector3 (0.0f, 1.0f, 0.0f);

        ArrayList<Vector3> vertices = new ArrayList<>(List.of(
            Vector3.zeros(), v1, v2, v3, v4, v5, v6, v7, v8
        ));

        // translate vertices
        Vector3 translation = new Vector3(
                x + location.x * CHUNK_SIZE,
                y + location.y * CHUNK_SIZE,
                z + location.z * CHUNK_SIZE
        );
        for (Vector3 vertex : vertices) {
            vertex.add(translation);
        }

        // faces - note: indices start at 1, not zero
        ArrayList<int[]> triangles = new ArrayList<>();

        // left-face
        if (x == 0 || !blocks[x - 1][y][z].isActive()) {
            triangles.add(new int[]{3, 7, 8});
            triangles.add(new int[]{4, 3, 8});
        }

        // bottom-face
        if (y == 0 || !blocks[x][y - 1][z].isActive()) {
            triangles.add(new int[]{2, 3, 4});
            triangles.add(new int[]{1, 2, 4});
        }

        // front-face
        if (z == 0 || !blocks[x][y][z - 1].isActive()) {
            triangles.add(new int[]{1, 4, 8});
            triangles.add(new int[]{5, 1, 8});
        }

        // right-face
        if (x == CHUNK_SIZE - 1 || !blocks[x + 1][y][z].isActive()) {
            triangles.add(new int[]{5, 6, 2});
            triangles.add(new int[]{1, 5, 2});
        }

        // top-face
        if (y == CHUNK_SIZE - 1 || !blocks[x][y + 1][z].isActive()) {
            triangles.add(new int[]{8, 7, 6});
            triangles.add(new int[]{5, 8, 6});
        }

        // back-face
        if (z == CHUNK_SIZE - 1 || !blocks[x][y][z + 1].isActive()) {
            triangles.add(new int[]{6, 7, 3});
            triangles.add(new int[]{2, 6, 3});
        }

        ArrayList<Vertex> blockVertices = new ArrayList<>();

        for (int[] triangle : triangles) {
            blockVertices.add(new Vertex(vertices.get(triangle[0]), blockType.color));
            blockVertices.add(new Vertex(vertices.get(triangle[1]), blockType.color));
            blockVertices.add(new Vertex(vertices.get(triangle[2]), blockType.color));
        }

        return blockVertices;
    }

    /**
     * Free all the data that has been allocated for the chunk.
     */
    public void unload() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
    }

    public Vector3 getLocation() {
        return location;
    }
}
