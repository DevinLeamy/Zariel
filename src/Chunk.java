import math.Vector3;
import org.lwjgl.BufferUtils;
import rendering.Mesh;
import rendering.MeshLoader;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL41.*;

public class Chunk {
    final private int CHUNK_SIZE = 16;
    private static VertexShader vs = new VertexShader("res/shaders/chunk.vert");
    private static FragmentShader fs = new FragmentShader("res/shaders/chunk.frag");
    private static ShaderProgram shader = new ShaderProgram(vs, fs);
    private static Mesh voxelMesh = MeshLoader.loadMesh("res/cube.obj");

    Block[][][] blocks;

    public Chunk() {
        blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for (int i = 0; i < CHUNK_SIZE; ++i) {
            for (int j = 0; j < CHUNK_SIZE; ++j) {
                for (int k = 0; k < CHUNK_SIZE; ++k) {
                    blocks[i][j][k] = new Block(Math.random() < 0.5, BlockType.GENERAL);
                }
            }
        }
    }

    public void render(Camera perspective) {
        ArrayList<Vector3> chunkVertices = new ArrayList<>();
        for (int i = 0; i < CHUNK_SIZE; ++i) {
            for (int j = 0; j < CHUNK_SIZE; ++j) {
                for (int k = 0; k < CHUNK_SIZE; ++k) {
                    if (!blocks[i][j][k].isActive()) {
                        continue;
                    }

                    ArrayList<Vector3> blockVertices = createBlockVertices(blocks[i][j][k].getBlockType(), i, j, k);
                    chunkVertices.addAll(blockVertices);
                }
            }
        }

        // buffer to hold vertex data
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(chunkVertices.size() * 3);

        for (Vector3 vertex : chunkVertices) {
            vertexBuffer.put(vertex.toArray());
        }

        // return buffer to the start
        vertexBuffer.flip();

        shader.link();

        // set shader uniforms
        int shaderHandle = shader.getProgramHandle();
        int viewMHander         = glGetUniformLocation(shaderHandle, "viewM");
        int projectionMHandler  = glGetUniformLocation(shaderHandle, "projectionM");

        glUniformMatrix4fv(viewMHander, true, perspective.viewMatrix().toFloatBuffer());
        glUniformMatrix4fv(projectionMHandler, true, perspective.projectionMatrix().toFloatBuffer());

        // create buffers
        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();

        glBindVertexArray(vao);

        // fill buffer
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // enable attributes
        glEnableVertexAttribArray(0); // position

        int stride = 4 * 3; // 3 = # of floats per vertex
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0L);

        glDrawArrays(GL_TRIANGLES, 0, chunkVertices.size());

        // clean up buffers
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
    }

    private ArrayList<Vector3> createBlockVertices(BlockType blockType, int x, int y, int z) {
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
        Vector3 translation = new Vector3(x, y, z);
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

        ArrayList<Vector3> blockVertices = new ArrayList<>();

        for (int[] triangle : triangles) {
            blockVertices.add(vertices.get(triangle[0]));
            blockVertices.add(vertices.get(triangle[1]));
            blockVertices.add(vertices.get(triangle[2]));
        }

        return blockVertices;
    }

    public void renderBlock(int x, int y, int z, Camera perspective) {
        shader.link();

        int shaderHandle = shader.getProgramHandle();

        int transformMHandler   = glGetUniformLocation(shaderHandle,  "transformM");
        int viewMHander         = glGetUniformLocation(shaderHandle, "viewM");
        int projectionMHandler  = glGetUniformLocation(shaderHandle, "projectionM");

        Transform voxelTrans = new Transform(
                new Vector3(x, y, z),
                Vector3.zeros(),
                new Vector3(0.5f, 0.5f, 0.5f)
        );

        // transpose: true! This implies that the matrix will be read row by row (not column by column!)
        glUniformMatrix4fv(transformMHandler, true, voxelTrans.toMatrix().toFloatBuffer());
        glUniformMatrix4fv(viewMHander, true, perspective.viewMatrix().toFloatBuffer());
        glUniformMatrix4fv(projectionMHandler, true, perspective.projectionMatrix().toFloatBuffer());

        // bind buffer array
        glBindVertexArray(voxelMesh.vao);

        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glFrontFace(GL_CW);

        // draw 'vertices' many vertices
        glDrawElements(GL_TRIANGLES, voxelMesh.indices.size(), GL_UNSIGNED_INT, 0);

        // unbind
        glBindVertexArray(0);
    }
}
