import math.Vector2;
import math.Vector3;
import math.Vector3i;
import org.lwjgl.BufferUtils;
import rendering.Vertex;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL41.*;

public class MeshGenerator {

    public static VoxelMesh generateVoxelMesh(VoxelGeometry voxels, Vector3i location) {
        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();

        ArrayList<Vertex> vertices = new ArrayList<>();
        Vector3i dimensions = voxels.dimensions();
        for (int i = 0; i < dimensions.x; ++i) {
            for (int j = 0; j < dimensions.y; ++j) {
                for (int k = 0; k < dimensions.z; ++k) {
                    Block block = voxels.getBlock(i, j, k).get();
                    if (!block.isActive()) {
                        continue;
                    }
                    ArrayList<Vertex> blockVertices = createBlockVertices(block.getBlockType(), i, j, k,
                        location);
                    vertices.addAll(blockVertices);
                }
            }
        }

        if (vertices.size() == 0) {
            return new VoxelMesh(0, vao, vbo);
        }

        // buffer to hold vertex data
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.size() * Vertex.size);
        vertices.forEach(vertex -> vertexBuffer.put(vertex.toArray()));
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

        return new VoxelMesh(vertices.size(), vao, vbo);
    }

    private static ArrayList<Vertex> createBlockVertices(BlockType blockType, int x, int y, int z,
                                                         Vector3i location) {
        Vector3 v1 = new Vector3 (1.0f,  0.0f, 0.0f);
        Vector3 v2 = new Vector3 (1.0f,  0.0f, 1.0f);
        Vector3 v3 = new Vector3 (0.0f, 0.0f, 1.0f);
        Vector3 v4 = new Vector3 (0.0f, 0.0f, 0.0f);
        Vector3 v5 = new Vector3 (1.0f,  1.0f, 0.0f );
        Vector3 v6 = new Vector3 (1.0f,  1.0f, 1.0f);
        Vector3 v7 = new Vector3 (0.0f, 1.0f, 1.0f);
        Vector3 v8 = new Vector3 (0.0f, 1.0f, 0.0f);

        Vector2[] uvs = World.atlas.getUVs(blockType.textureRow, blockType.textureCol);

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
        Vector3 translation = new Vector3(x, y, z);
        ArrayList<Vector3> vertices = new ArrayList<>(List.of(
                Vector3.zeros(), v1, v2, v3, v4, v5, v6, v7, v8
        ));
        vertices.forEach(vertex -> vertex.add(translation));

        // faces - note: indices start at 1, not zero
        // { { vertex, normal } }
        ArrayList<int[][]> triangles = new ArrayList<>();

        int worldX = (int) translation.x + location.x;
        int worldY = (int) translation.y + location.y;
        int worldZ = (int) translation.z + location.z;

        World world = World.getInstance();

        // left-face
        if (!world.blockIsActive(worldX - 1, worldY, worldZ)) {
            triangles.add(new int[][] {
                    {3, 5, 3}, {7, 5, 0}, {8, 5, 1}
            });
            triangles.add(new int[][]{
                    {4, 5, 2}, {3, 5, 3}, {8, 5, 1}
            });
        }

        // bottom-face
        if (!world.blockIsActive(worldX, worldY - 1, worldZ)) {
            triangles.add(new int[][]{
                    {2, 1, 3}, {3, 1, 0}, {4, 1, 1}
            });
            triangles.add(new int[][]{
                    {1, 1, 2}, {2, 1, 3}, {4, 1, 1}
            });
        }

        // front-face
        if (!world.blockIsActive(worldX, worldY, worldZ - 1)) {
            triangles.add(new int[][]{
                    {1, 6, 3}, {4, 6, 0}, {8, 6, 1}
            });
            triangles.add(new int[][]{
                    {5, 6, 2}, {1, 6, 3}, {8, 6, 1}
            });
        }

        // right-face
        if (!world.blockIsActive(worldX + 1, worldY, worldZ)) {
            triangles.add(new int[][]{
                    {5, 3, 3}, {6, 3, 0}, {2, 3, 1}
            });
            triangles.add(new int[][]{
                    {1, 3, 2}, {5, 3, 3}, {2, 3, 1}
            });
        }

        // top-face
        if (!world.blockIsActive(worldX, worldY + 1, worldZ)) {
            triangles.add(new int[][]{
                    {8, 2, 3}, {7, 2, 0}, {6, 2, 1}
            });
            triangles.add(new int[][]{
                    {5, 2, 2}, {8, 2, 3}, {6, 2, 1}
            });
        }

        // back-face
        if (!world.blockIsActive(worldX, worldY, worldZ + 1)) {
            triangles.add(new int[][]{
                    {6, 4, 3}, {7, 4, 0}, {3, 4, 1}
            });
            triangles.add(new int[][]{
                    {2, 4, 2}, {6, 4, 3}, {3, 4, 1}
            });
        }

        ArrayList<Vertex> blockVertices = new ArrayList<>();

        for (int[][] triangle : triangles) {
            for (int[] vertex : triangle) {
                Vector3 pos = vertices.get(vertex[0]);
                Vector3 normal = normals.get(vertex[1]);
                Vector2 uv = uvs[vertex[2]];
                Vector3 color = Vector3.zeros(); // blockType.color;
                int ambientOcclusion = calculateAmbientOcclusion(
                        (int) pos.x + location.x,
                        (int) pos.y + location.y,
                        (int) pos.z + location.z
                );

                Vertex v = new Vertex(pos, uv, normal, color);
                v.setAmbientOcclusion(ambientOcclusion);

                blockVertices.add(v);
            }
        }

        return blockVertices;
    }

    private static int calculateAmbientOcclusion(int x, int y, int z) {
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

    private static int calculateAO(int sideOne, int sideTwo, int corner) {
        if (sideOne == 1 && sideTwo == 1) {
            return 0;
        }

        return 3 - (sideOne + sideTwo + corner);
    }
}
