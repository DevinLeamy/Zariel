import math.Vector2;
import math.Vector3;
import math.Vector3i;
import org.lwjgl.BufferUtils;
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
    int vao, vbo;
    int chunkVerticesCount;
    Vector3i location;
    float[][] noiseMap;
    public boolean loaded;
    boolean updated;

    public Chunk(Vector3i location) {
        this.vao = glGenVertexArrays();
        this.vbo = glGenBuffers();
        this.chunkVerticesCount = 0;
        this.location = location;
        this.loaded = false;
        this.updated = false;


        NoiseMapGenerator noiseMapGenerator = NoiseMapGenerator.getInstance();
        Vector3 worldCoords = chunkCoordsToWorldCoords(location);

        this.noiseMap = noiseMapGenerator.generateNoiseMap((int) worldCoords.x, (int) worldCoords.z, CHUNK_SIZE, CHUNK_SIZE);

        /**
         * Initialize the blocks in the chunk
         */
        blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
    }

    public Vector3 chunkCoordsToWorldCoords(Vector3i chunkCoords) {
        return Vector3i.scale(chunkCoords, CHUNK_SIZE).toVector3();
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
        ArrayList<Vector3i> trees = new ArrayList<>();
        for (int x = 0; x < CHUNK_SIZE; ++x) {
            for (int z = 0; z < CHUNK_SIZE; ++z) {
                float noise = noiseMap[x][z];
                noise = (noise + 1.0f) / 2.0f;
//                noise = 1.0f - noise * noise;

                int maxHeight = Integer.min((int) (noise * CHUNK_SIZE), CHUNK_SIZE - 1);
                int maxWorldHeight = (int) Float.min(this.location.y * CHUNK_SIZE + maxHeight, Config.FLOOR_LEVEL);
                for (int y = 0; y < maxHeight; ++y) {
                    float worldY = y + this.location.y * CHUNK_SIZE;

                    // check if the block is within spawning range
                    if (worldY < Config.GROUND_LEVEL || worldY >= Config.FLOOR_LEVEL) {
                        blocks[x][y][z] = new Block(false, BlockType.EMPTY);
                        continue;
                    }

                    blocks[x][y][z] = new Block(true, worldY > maxWorldHeight - 2 ? BlockType.GRASS : BlockType.DIRT);
                }

                for (int y = maxHeight; y < CHUNK_SIZE; ++y) {
                    blocks[x][y][z] = new Block(false, BlockType.EMPTY);
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
                    blocks[i][j][k].setUpdateCallback(this::onBlockUpdate);
                }
            }
        }
    }

    public void spawnTree(Vector3i chunkSpawnPoint) {
        int lowerY = location.y * CHUNK_SIZE + chunkSpawnPoint.y;
        int upperY = lowerY + 5;

        for (int i = lowerY; i <= upperY; ++i) {
            if (i < Config.GROUND_LEVEL || i >= Config.FLOOR_LEVEL) {
                return;
            }
        }

        int x = chunkSpawnPoint.x;
        int y = chunkSpawnPoint.y;
        int z = chunkSpawnPoint.z;

        try {
            BlockType truck = BlockType.DIRT;
            BlockType leaf = BlockType.SAND;
            // truck
            blocks[x][y][z] = new Block(true, truck);
            blocks[x][y + 1][z] = new Block(true, truck);
            blocks[x][y + 2][z] = new Block(true, truck);
            blocks[x][y + 3][z] = new Block(true, truck);

            // foliage
            blocks[x][y + 3][z + 1] = new Block(true, leaf);
            blocks[x][y + 3][z - 1] = new Block(true, leaf);
            blocks[x + 1][y + 3][z] = new Block(true, leaf);
            blocks[x - 1][y + 3][z] = new Block(true, leaf);
            blocks[x][y + 4][z] = new Block(true, leaf);
            blocks[x + 1][y + 4][z + 1] = new Block(true, leaf);
            blocks[x - 1][y + 4][z + 1] = new Block(true, leaf);
            blocks[x - 1][y + 4][z - 1] = new Block(true, leaf);
            blocks[x + 1][y + 4][z - 1] = new Block(true, leaf);
            blocks[x][y + 5][z] = new Block(true, leaf);
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
        glEnableVertexAttribArray(4);

        int stride = 4 * Vertex.size; // 12 = 3 (coords) + 2 (uv) + 3 (normal) + 3 (colors) + 1 (AO)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0L);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, stride,  3 * 4);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, stride,  5 * 4);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, stride,  8 * 4);
        glVertexAttribPointer(4, 1, GL_FLOAT, false, stride,  11 * 4);

        // unbind vertex array
        glBindVertexArray(0);

        loaded = true;
        updated = false;
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

    private int calculateAmbientOcclusion(Vector3 vertexNormal, int x, int y, int z) {
        World world = World.getInstance();
        // Looking down (-z going up, +x going right)
        // TODO: calculate these based on the vertex normal
        boolean topLeft     = world.blockIsActive(x - 1, y, z);
        boolean topRight    = world.blockIsActive(x, y, z);
        boolean bottomLeft  = world.blockIsActive(x - 1, y, z - 1);
        boolean bottomRight = world.blockIsActive(x, y, z - 1);

        int topLeftI = topLeft ? 1 : 0;
        int topRightI = topRight ? 1 : 0;
        int bottomLeftI = bottomLeft ? 1 : 0;
        int bottomRightI = bottomRight ? 1 : 0;

        if (!topLeft) {
            return calculateAO(bottomLeftI, topRightI, bottomRightI);
        } else if (!topRight) {
            return calculateAO(bottomRightI, topLeftI, bottomLeftI);
        } else if (!bottomLeft) {
           return calculateAO(topLeftI, bottomRightI, topRightI);
        } else if (!bottomRight) {
            return calculateAO(topRightI, bottomLeftI, topLeftI);
        } else {
            // Vertex is completely occluded
            return 0;
        }
    }

    private int calculateAO(int sideOne, int sideTwo, int corner) {
        if (sideOne == 1 && sideTwo == 1) {
            return 0;
        }

        return 3 - (sideOne + sideTwo + corner);
    }

    private ArrayList<Vertex> createBlockVertices(BlockType blockType, int x, int y, int z) {
        // vertices
        // TODO: Turn these into enums (eg. FRONT_LEFT_BOTTOM)
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
        ArrayList<Vector3> normals = new ArrayList<>(List.of(
            Vector3.zeros(),
            Direction.DOWN.normal,
            Direction.UP.normal,
            Direction.RIGHT.normal,
            Direction.BACK.normal,
            Direction.LEFT.normal,
            Direction.FRONT.normal
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

        int worldX = (int) translation.x;
        int worldY = (int) translation.y;
        int worldZ = (int) translation.z;

        World world = World.getInstance();

        // left-face
        if (!world.blockIsActive(worldX - 1, worldY, worldZ)) {
            triangles.add(new int[][] {
                    {3, 5}, {7, 5}, {8, 5}
            });
            triangles.add(new int[][]{
                    {4, 5}, {3, 5}, {8, 5}
            });
        }

        // bottom-face
        if (!world.blockIsActive(worldX, worldY - 1, worldZ)) {
            triangles.add(new int[][]{
                    {2, 1}, {3, 1}, {4, 1}
            });
            triangles.add(new int[][]{
                    {1, 1}, {2, 1}, {4, 1}
            });
        }

        // front-face
        if (!world.blockIsActive(worldX, worldY, worldZ - 1)) {
            triangles.add(new int[][]{
                    {1, 6}, {4, 6}, {8, 6}
            });
            triangles.add(new int[][]{
                    {5, 6}, {1, 6}, {8, 6}
            });
        }

        // right-face
        if (!world.blockIsActive(worldX + 1, worldY, worldZ)) {
            triangles.add(new int[][]{
                    {5, 3}, {6, 3}, {2, 3}
            });
            triangles.add(new int[][]{
                    {1, 3}, {5, 3}, {2, 3}
            });
        }

        // top-face
        if (!world.blockIsActive(worldX, worldY + 1, worldZ)) {
            triangles.add(new int[][]{
                    {8, 2}, {7, 2}, {6, 2}
            });
            triangles.add(new int[][]{
                    {5, 2}, {8, 2}, {6, 2}
            });
        }

        // back-face
        if (!world.blockIsActive(worldX, worldY, worldZ + 1)) {
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
                Vector3 pos = vertices.get(vertex[0]);
                Vector3 normal = normals.get(vertex[1]);
                Vector3 color = blockType.color;
                int ambientOcclusion = calculateAmbientOcclusion(normal, (int) pos.x, (int) pos.y, (int) pos.z);

                Vertex v = new Vertex(pos, Vector2.zeros(), normal, color);
                v.setAmbientOcclusion(ambientOcclusion);

                blockVertices.add(v);
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
        loaded = false;
    }

    public void update() {
        load();
    }

    public Vector3i getLocation() {
        return location;
    }

    public Block getBlock(Vector3i pos) {
        return blocks[pos.x][pos.y][pos.z];
    }

    public static Vector3i getChunkLocalCoords(Vector3i worldCoords) {
        int x = worldCoords.x, y = worldCoords.y, z = worldCoords.z;

        int innerX = Utils.mod(x, CHUNK_SIZE);
        int innerY = Utils.mod(y, CHUNK_SIZE);
        int innerZ = Utils.mod(z, CHUNK_SIZE);

        return new Vector3i(innerX, innerY, innerZ);
    }
}
