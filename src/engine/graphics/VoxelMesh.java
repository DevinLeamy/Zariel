package engine.graphics;

import static org.lwjgl.opengl.GL41.*;

public class VoxelMesh {
    private int vertices;
    private int vao;
    private int vbo;
    private int ebo;

    public VoxelMesh(int vertices, int vao, int vbo, int ebo) {
        this.vertices = vertices;
        this.vao = vao;
        this.vbo = vbo;
        this.ebo = ebo;
    }
    public VoxelMesh(int vertices, int vao, int vbo) {
        this(vertices, vao, vbo, -1);
    }

    public int vertices() {
        return vertices;
    }

    // attach the mesh for rendering
    public void link() {
        glBindVertexArray(vao);
    }

    public void unlink() {
        glBindVertexArray(0);
    }

    public void dispose() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
    }
}
