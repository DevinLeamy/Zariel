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
    final private static int CHUNK_SIZE = 16;
    private static VertexShader vs = new VertexShader("res/shaders/chunk.vert");
    private static FragmentShader fs = new FragmentShader("res/shaders/chunk.frag");
    private static ShaderProgram shader = new ShaderProgram(vs, fs);
    private static Mesh voxelMesh = MeshLoader.loadMesh("res/cube.obj");

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

    public Chunk(Vector3 location) {
        this.updated = true;
        this.vao = glGenVertexArrays();
        this.vbo = glGenBuffers();
        this.chunkVerticesCount = 0;
        this.location = location;


        /**
         * Initialize the blocks in the chunk
         */
        blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for (int i = 0; i < CHUNK_SIZE; ++i) {
            for (int j = 0; j < CHUNK_SIZE; ++j) {
                for (int k = 0; k < CHUNK_SIZE; ++k) {
                    blocks[i][j][k] = new Block(Math.random() < 0.2, BlockType.GENERAL);
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
//        if (false) {
//            TODO: Check if the chunk has updated for the perspective has changed.
//            return;
//        }
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
        Vector3 v1 = new Vector3 (1.0f,  -1.0f, -1.0f);
        Vector3 v2 = new Vector3 (1.0f,  -1.0f, 1.0f);
        Vector3 v3 = new Vector3 (-1.0f, -1.0f, 1.0f);
        Vector3 v4 = new Vector3 (-1.0f, -1.0f, -1.0f);
        Vector3 v5 = new Vector3 (1.0f,  1.0f, -1.0f );
        Vector3 v6 = new Vector3 (1.0f,  1.0f, 1.0f);
        Vector3 v7 = new Vector3 (-1.0f, 1.0f, 1.0f);
        Vector3 v8 = new Vector3 (-1.0f, 1.0f, -1.0f);

        ArrayList<Vector3> vertices = new ArrayList<>(List.of(
            Vector3.zeros(), v1, v2, v3, v4, v5, v6, v7, v8
        ));

        // translate vertices
        Vector3 translation = new Vector3(
                x + location.x * CHUNK_SIZE,
                y + location.y * CHUNK_SIZE - CHUNK_SIZE * 2,
                z + location.z * CHUNK_SIZE
        );
        for (Vector3 vertex : vertices) {
            vertex.add(translation);
        }

        // faces - note: indices start at 1, not zero
        ArrayList<int[]> triangles = new ArrayList<>(List.of(
                new int[]{2, 3, 4},
                new int[]{8, 7, 6},
                new int[]{5, 6, 2},
                new int[]{6, 7, 3},
                new int[]{3, 7, 8},
                new int[]{1, 4, 8},
                new int[]{1, 2, 4},
                new int[]{5, 8, 6},
                new int[]{1, 5, 2},
                new int[]{2, 6, 3},
                new int[]{4, 3, 8},
                new int[]{5, 1, 8}
        ));

        ArrayList<Vertex> blockVertices = new ArrayList<>();

        for (int[] triangle : triangles) {
            blockVertices.add(new Vertex(vertices.get(triangle[0])));
            blockVertices.add(new Vertex(vertices.get(triangle[1])));
            blockVertices.add(new Vertex(vertices.get(triangle[2])));
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
