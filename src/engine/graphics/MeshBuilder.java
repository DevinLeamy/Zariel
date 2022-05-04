package engine.graphics;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static java.lang.System.exit;
import static org.lwjgl.opengl.GL41.*;

public class MeshBuilder {
    private static class AttributeArray {
        final public float[] data;
        final public int attributeSize;
        final public int attributeCount;
        public AttributeArray(float[] data, int attributeSize) {
            this.data = data;
            this.attributeSize = attributeSize;
            this.attributeCount = data.length / attributeSize;

            if (data.length % attributeSize != 0) {
                System.err.println("Error: invalid attribute array format");
                exit(0);
            }
        }

        public float[] getAttribute(int index) {
            float[] attribute = new float[attributeSize];

            System.arraycopy(data, attributeSize * index, attribute, 0, attributeSize);

            return attribute;
        }
    }
    private ArrayList<AttributeArray> attributeArrays;

    public MeshBuilder() {
        attributeArrays = new ArrayList<>();
    }

    public void loadAttributeData(float[] data, int attributeSize) {
        attributeArrays.add(new AttributeArray(data, attributeSize));
    }

    private int calculateFloatsPerVertex() {
        int floats = 0;
        for (AttributeArray attributeArray : attributeArrays) {
            floats += attributeArray.attributeSize;
        }

        return floats;
    }

    private int countVertices() {
        if (attributeArrays.isEmpty()) {
            System.err.println("Error: no data in vertex buffer");
            return 0;
        }

        int vertices = attributeArrays.get(0).attributeCount;

        for (AttributeArray attributeArray : attributeArrays) {
            if (attributeArray.attributeCount != vertices) {
                System.err.println("Error: all attribute arrays must contains an equal number of attributes");
                exit(0);
            }
        }

        return vertices;
    }


    public Mesh build() {
        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        int floatsPerVertex = calculateFloatsPerVertex();
        int stride = 4 * floatsPerVertex;
        int vertices = countVertices();

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(floatsPerVertex * vertices);

        for (int i = 0; i < vertices; ++i) {
            for (AttributeArray attributeArray : attributeArrays) {
                vertexBuffer.put(attributeArray.getAttribute(i));
            }
        }
        vertexBuffer.flip();

        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        int pointer = 0;
        for (int i = 0; i < attributeArrays.size(); ++i) {
            int attributeSize = attributeArrays.get(i).attributeSize;

            glEnableVertexAttribArray(i);
            glVertexAttribPointer(i, attributeSize, GL_FLOAT, false, stride, pointer);

            pointer += attributeSize * 4;
        }

        // unbind vertex array
        glBindVertexArray(0);

        return new Mesh(vertices, vao, vbo);
    }
}
