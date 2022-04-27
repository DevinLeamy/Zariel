package engine.graphics;

import engine.World;
import engine.main.*;
import math.Vector2;
import math.Vector3;
import math.Vector3i;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static engine.graphics.Lighting.calculateAO;
import static engine.main.SkyBox.SKYBOX_VERTICES;
import static org.lwjgl.opengl.GL41.*;

public class MeshGenerator {

    private static Mesh generateMesh(VoxelGeometry voxels, Optional<Vector3i> location) {
        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();

        ArrayList<Vertex> vertices = new ArrayList<>();
        ArrayList<Vector3i> activeOffsets = voxels.activeOffsets();

        for (Vector3i offset : activeOffsets) {
            Block block = voxels.getBlock(offset.x, offset.y, offset.z).get();
            if (location.isPresent()) {
                ArrayList<Vertex> blockVertices = createWorldBlockVertices(block.getBlockType(), offset.toVector3(), location.get());
                vertices.addAll(blockVertices);
            } else {
                ArrayList<Vertex> blockVertices = createLocalBlockVertices(block.getBlockType(), offset.toVector3(), voxels);
                vertices.addAll(blockVertices);
            }
        }

        if (vertices.size() == 0) {
            return new Mesh(0, vao, vbo);
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

        return new Mesh(vertices.size(), vao, vbo);
    }

    public static Mesh generateMesh(VoxelGeometry voxels, Vector3i location) {
        return generateMesh(voxels, Optional.of(location));
    }

    public static Mesh generateLocalMesh(VoxelGeometry voxels) {
        return generateMesh(voxels, Optional.empty());
    }

    public static Mesh generateSkyBoxMesh() {
        MeshBuilder builder = new MeshBuilder();
        builder.loadAttributeData(SKYBOX_VERTICES, 3);

        return builder.build();
    }

    private static float[] flatten(ArrayList<Vector3> data) {
        float[] flattened = new float[data.size() * 3];
        for (int i = 0; i < data.size(); ++i) {
            float[] v = data.get(i).toArray();
            flattened[i * 3] = v[0];
            flattened[i * 3 + 1] = v[1];
            flattened[i * 3 + 2] = v[2];
        }

        return flattened;
    }

    public static Mesh generateCubeWireMesh() {
        MeshBuilder builder = new MeshBuilder();

        ArrayList<Vector3> lines = new ArrayList<>(List.of(
                // far face
                Cube.FAR_TOP_LEFT, Cube.FAR_TOP_RIGHT,
                Cube.FAR_TOP_LEFT, Cube.FAR_BOTTOM_LEFT,
                Cube.FAR_TOP_RIGHT, Cube.FAR_BOTTOM_RIGHT,
                Cube.FAR_BOTTOM_LEFT, Cube.FAR_BOTTOM_RIGHT,

                // close face
                Cube.NEAR_TOP_LEFT, Cube.NEAR_TOP_RIGHT,
                Cube.NEAR_TOP_LEFT, Cube.NEAR_BOTTOM_LEFT,
                Cube.NEAR_TOP_RIGHT, Cube.NEAR_BOTTOM_RIGHT,
                Cube.NEAR_BOTTOM_LEFT, Cube.NEAR_BOTTOM_RIGHT,

                // attach faces
                Cube.NEAR_TOP_LEFT, Cube.FAR_TOP_LEFT,
                Cube.NEAR_TOP_RIGHT, Cube.FAR_TOP_RIGHT,
                Cube.NEAR_BOTTOM_RIGHT, Cube.FAR_BOTTOM_RIGHT,
                Cube.NEAR_BOTTOM_LEFT, Cube.FAR_BOTTOM_LEFT
        ));

        builder.loadAttributeData(flatten(lines), 3);
        return builder.build();
    }

    private static ArrayList<Vertex> createLocalBlockVertices(BlockType blockType, Vector3 localOffset, VoxelGeometry voxels) {
        Vector3 v1 = new Vector3 (1.0f,  0.0f, 0.0f); // bottom near right
        Vector3 v2 = new Vector3 (1.0f,  0.0f, 1.0f); // bottom far right
        Vector3 v3 = new Vector3 (0.0f, 0.0f, 1.0f); // bottom far left
        Vector3 v4 = new Vector3 (0.0f, 0.0f, 0.0f); // bottom near left
        Vector3 v5 = new Vector3 (1.0f,  1.0f, 0.0f ); // top near right
        Vector3 v6 = new Vector3 (1.0f,  1.0f, 1.0f);  // top far right
        Vector3 v7 = new Vector3 (0.0f, 1.0f, 1.0f);   // top far left
        Vector3 v8 = new Vector3 (0.0f, 1.0f, 0.0f);  // top near left

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
        Vector3 translation = localOffset.clone();
        ArrayList<Vector3> vertices = new ArrayList<>(List.of(
                Vector3.zeros(), v1, v2, v3, v4, v5, v6, v7, v8
        ));
        vertices.forEach(vertex -> vertex.add(translation));

        // faces - note: indices start at 1, not zero
        // { { vertex, normal } }
        ArrayList<int[][]> triangles = new ArrayList<>();

        int worldX = (int) translation.x;
        int worldY = (int) translation.y;
        int worldZ = (int) translation.z;

        // left-face
        if (!voxels.blockIsActive(worldX - 1, worldY, worldZ)) {
            triangles.add(new int[][] {
                    {3, 5, 3}, {7, 5, 0}, {8, 5, 1}
            });
            triangles.add(new int[][]{
                    {4, 5, 2}, {3, 5, 3}, {8, 5, 1}
            });
        }

        // bottom-face
        if (!voxels.blockIsActive(worldX, worldY - 1, worldZ)) {
            triangles.add(new int[][]{
                    {2, 1, 3}, {3, 1, 0}, {4, 1, 1}
            });
            triangles.add(new int[][]{
                    {1, 1, 2}, {2, 1, 3}, {4, 1, 1}
            });
        }

        // front-face
        if (!voxels.blockIsActive(worldX, worldY, worldZ - 1)) {
            triangles.add(new int[][]{
                    {1, 6, 3}, {4, 6, 0}, {8, 6, 1}
            });
            triangles.add(new int[][]{
                    {5, 6, 2}, {1, 6, 3}, {8, 6, 1}
            });
        }

        // right-face
        if (!voxels.blockIsActive(worldX + 1, worldY, worldZ)) {
            triangles.add(new int[][]{
                    {5, 3, 3}, {6, 3, 0}, {2, 3, 1}
            });
            triangles.add(new int[][]{
                    {1, 3, 2}, {5, 3, 3}, {2, 3, 1}
            });
        }

        // top-face
        if (!voxels.blockIsActive(worldX, worldY + 1, worldZ)) {
            triangles.add(new int[][]{
                    {8, 2, 3}, {7, 2, 0}, {6, 2, 1}
            });
            triangles.add(new int[][]{
                    {5, 2, 2}, {8, 2, 3}, {6, 2, 1}
            });
        }

        // back-face
        if (!voxels.blockIsActive(worldX, worldY, worldZ + 1)) {
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

                Vector3 color = blockType.colored ? blockType.getGLColor() : Vector3.zeros();
                int ambientOcclusion = calculateLocalAmbientOcclusion(pos.toVector3i(), voxels);

                Vertex v = new Vertex(pos, uv, normal, color);
                v.setAmbientOcclusion(ambientOcclusion);

                blockVertices.add(v);
            }
        }

        return blockVertices;
    }

    private static int calculateLocalAmbientOcclusion(Vector3i localOffset, VoxelGeometry voxels) {
        return 3;
//        int x = localOffset.x, y = localOffset.y, z = localOffset.z;
//
//        boolean topLeft     = voxels.blockIsActive(x - 1, y, z);
//        boolean topRight    = voxels.blockIsActive(x, y, z);
//        boolean bottomLeft  = voxels.blockIsActive(x - 1, y, z - 1);
//        boolean bottomRight = voxels.blockIsActive(x, y, z - 1);
//
//        int topLeftI = topLeft ? 1 : 0;
//        int topRightI = topRight ? 1 : 0;
//        int bottomLeftI = bottomLeft ? 1 : 0;
//        int bottomRightI = bottomRight ? 1 : 0;
//
//        if (!topLeft) {
//            return calculateAO(bottomLeftI, topRightI, bottomRightI);
//        } else if (!topRight) {
//            return calculateAO(bottomRightI, topLeftI, bottomLeftI);
//        } else if (!bottomLeft) {
//            return calculateAO(topLeftI, bottomRightI, topRightI);
//        } else if (!bottomRight) {
//            return calculateAO(topRightI, bottomLeftI, topLeftI);
//        } else {
//            // Vertex is completely occluded
//            return 0;
//        }
    }

    private static ArrayList<Vertex> createWorldBlockVertices(BlockType blockType, Vector3 localOffset, Vector3i worldLocation) {
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
        Vector3 translation = localOffset.clone();
        ArrayList<Vector3> vertices = new ArrayList<>(List.of(
                Vector3.zeros(), v1, v2, v3, v4, v5, v6, v7, v8
        ));
        vertices.forEach(vertex -> vertex.add(translation));

        // faces - note: indices start at 1, not zero
        // { { vertex, normal } }
        ArrayList<int[][]> triangles = new ArrayList<>();

        int worldX = (int) (translation.x + worldLocation.x);
        int worldY = (int) (translation.y + worldLocation.y);
        int worldZ = (int) (translation.z + worldLocation.z);

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

                Vector3 color = blockType.colored ? blockType.getGLColor() : Vector3.zeros();
                int ambientOcclusion = calculateAmbientOcclusion(
                        (int) (pos.x + worldLocation.x),
                        (int) (pos.y + worldLocation.y),
                        (int) (pos.z + worldLocation.z)
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
}
