import math.Vector2;
import math.Vector3;
import org.lwjgl.BufferUtils;
import rendering.Mesh;
import rendering.MeshLoader;
import rendering.Vertex;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

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

    Block[][][] blocks;
    boolean updated;
    int vao, vbo;
    int chunkVerticesCount;
    Vector3 location;
    float[][] noiseMap;

    public Chunk(Vector3 location) {
        this.updated = true;
        this.vao = glGenVertexArrays();
        this.vbo = glGenBuffers();
        this.chunkVerticesCount = 0;
        this.location = location;

        NoiseMapGenerator noiseMapGenerator = NoiseMapGenerator.getInstance();
        Vector3 worldCoords = chunkCoordsToWorldCoords(location);

        this.noiseMap = noiseMapGenerator.generateNoiseMap((int) worldCoords.x, (int) worldCoords.z, CHUNK_SIZE, CHUNK_SIZE);

        /**
         * Initialize the blocks in the chunk
         */
        blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
    }

    public Vector3 chunkCoordsToWorldCoords(Vector3 chunkCoords) {
        return Vector3.scale(chunkCoords, CHUNK_SIZE);
    }

    /**
     * Check if chunk contains at least one active block.
     * TODO: this might be able to be improved using this.chunkVerticesCount
     */
    public boolean isActive() {
        // TODO: refactor when Iterator interface is implemented
        for (int i = 0; i < CHUNK_SIZE; ++i) {
            for (int j = 0; j < CHUNK_SIZE; ++j) {
                for (int k = 0; k < CHUNK_SIZE; ++k) {
                    if (blocks[i][j][k].isActive()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void initialize() {
        for (int x = 0; x < CHUNK_SIZE; ++x) {
            for (int z = 0; z < CHUNK_SIZE; ++z) {
                float noise = noiseMap[x][z];
                noise = (noise + 1.0f) / 2.0f;

                int maxHeight = (int) (noise * CHUNK_SIZE);
                int maxWorldHeight = (int) Float.min(this.location.y * CHUNK_SIZE + maxHeight, Config.FLOOR_LEVEL);
                for (int y = 0; y < maxHeight; ++y) {
                    float worldY = y + this.location.y * CHUNK_SIZE;

                    // check if the block is within spawning range
                    if (worldY < Config.GROUND_LEVEL || worldY >= Config.FLOOR_LEVEL) {
                        blocks[x][y][z] = new Block(false, BlockType.EMPTY);
                        continue;
                    }

                    blocks[x][y][z] = new Block(true, worldY > maxWorldHeight - 2 && Math.random() > 0.7 ? BlockType.SAND : BlockType.DIRT);
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

        // vertex normals
        Vector3 n1 = new Vector3( 0.0f, -1.0f,  0.0f);
        Vector3 n2 = new Vector3( 0.0f,  1.0f,  0.0f);
        Vector3 n3 = new Vector3( 1.0f,  0.0f,  0.0f);
        Vector3 n4 = new Vector3( 0.0f,  0.0f,  1.0f);
        Vector3 n5 = new Vector3(-1.0f,  0.0f,  0.0f);
        Vector3 n6 = new Vector3( 0.0f,  0.0f, -1.0f);

        ArrayList<Vector3> normals = new ArrayList<>(List.of(
            Vector3.zeros(), n1, n2, n3, n4, n5, n6
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
        // { { vertex, normal } }
        ArrayList<int[][]> triangles = new ArrayList<>();

        // left-face
        if (x == 0 || !blocks[x - 1][y][z].isActive()) {
            triangles.add(new int[][] {
                    {3, 5}, {7, 5}, {8, 5}
            });
            triangles.add(new int[][]{
                    {4, 5}, {3, 5}, {8, 5}
            });
        }

        // bottom-face
        if (y == 0 || !blocks[x][y - 1][z].isActive()) {
            triangles.add(new int[][]{
                    {2, 1}, {3, 1}, {4, 1}
            });
            triangles.add(new int[][]{
                    {1, 1}, {2, 1}, {4, 1}
            });
        }

        // front-face
        if (z == 0 || !blocks[x][y][z - 1].isActive()) {
            triangles.add(new int[][]{
                    {1, 6}, {4, 6}, {8, 6}
            });
            triangles.add(new int[][]{
                    {5, 6}, {1, 6}, {8, 6}
            });
        }

        // right-face
        if (x == CHUNK_SIZE - 1 || !blocks[x + 1][y][z].isActive()) {
            triangles.add(new int[][]{
                    {5, 3}, {6, 3}, {2, 3}
            });
            triangles.add(new int[][]{
                    {1, 3}, {5, 3}, {2, 3}
            });
        }

        // top-face
        if (y == CHUNK_SIZE - 1 || !blocks[x][y + 1][z].isActive()) {
            triangles.add(new int[][]{
                    {8, 2}, {7, 2}, {6, 2}
            });
            triangles.add(new int[][]{
                    {5, 2}, {8, 2}, {6, 2}
            });
        }

        // back-face
        if (z == CHUNK_SIZE - 1 || !blocks[x][y][z + 1].isActive()) {
            triangles.add(new int[][]{
                    {6, 4}, {7, 4}, {3, 4}
            });
            triangles.add(new int[][]{
                    {2, 4}, {6, 4}, {3, 4}
            });
        }

        ArrayList<Vertex> blockVertices = new ArrayList<>();

        for (int[][] triangle : triangles) {
            for (int[] vertex : triangle) {
               blockVertices.add(new Vertex(
                       vertices.get(vertex[0]),
                       Vector2.zeros(),
                       normals.get(vertex[1]),
                       blockType.color
               ));
            }
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
