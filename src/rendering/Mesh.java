package rendering;
import org.lwjgl.BufferUtils;

import java.nio.*;
import java.util.*;

import static org.lwjgl.opengl.GL41.*;
/**
 * A mesh is the 3D geometry
 */
public class Mesh {
    public ArrayList<Vertex> vertices;
    public ArrayList<Integer> indices;
    public int vao, vbo, ebo;

    public Mesh(ArrayList<Vertex> vertices, ArrayList<Integer> indices) {
        this.vertices = vertices;
        this.indices = indices;
        prepareMesh();
    }

    private void prepareMesh() {
        // create buffers
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ebo = glGenBuffers();

        // bind VAO and array buffer (order matters!)
        glBindVertexArray(vao);

        // fill buffers
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, getVertexBuffer(), GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, getIndexBuffer(), GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        int stride = 4 * 8; // 8 = 3 (coords) + 2 (uv) + 3 (normal)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0L);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, stride,  3 * 4);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, stride,  5 * 4);

        // unbind
        glBindVertexArray(0);
    }

    public FloatBuffer getVertexBuffer() {
        // 8 = 3 (coords) + 2 (uv) + 3 (normal)
        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.size() * 8);
        for (Vertex vertex : vertices) {
            buffer.put(vertex.toArray());
            for (float v : vertex.toArray()) {
                System.out.print(v + " ");
            }
            System.out.println("");
        }

        return buffer;
    }

    public IntBuffer getIndexBuffer() {
        IntBuffer buffer = BufferUtils.createIntBuffer(indices.size());

        for (int index : indices) {
            buffer.put(index);
        }

        return buffer;
    }

    public void dispose() {
        glDeleteBuffers(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
    }
}