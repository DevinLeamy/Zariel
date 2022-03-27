import org.lwjgl.BufferUtils;

import java.nio.*;

import static org.lwjgl.opengl.GL46.*;

public class Triangle extends Model {

    public Triangle() {
        super();

        this.vertexCount = 3;
        this.coordinatesPerVertex = 3;
        this.channelsPerColor = 3;

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(
                this.vertexCount * this.coordinatesPerVertex
        );
        vertexBuffer.put(new float[] { -1f, -1f, 0f, });
        vertexBuffer.put(new float[] { 1f, -1f, 0f, });
        vertexBuffer.put(new float[] { 1f, 1f, 0f, });

        this.vboVertexHandle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.vboVertexHandle);
        // GL_STATIC_DRAW => Data is modified once and used many times
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(
            this.vertexCount * this.channelsPerColor
        );

        this.vboColorHandle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.vboColorHandle);
        glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
    }
}
