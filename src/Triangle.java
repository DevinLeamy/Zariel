import org.lwjgl.BufferUtils;

import java.nio.*;

import static org.lwjgl.opengl.GL41.*;

public class Triangle extends Model {

    public Triangle(VertexShader vs, FragmentShader fs, Transform transform) {
        super(vs, fs, transform);

        this.vertexCount = 3;
        this.coordinatesPerVertex = 3;
        this.channelsPerColor = 3;

        // initialize vertex buffer
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(
                this.vertexCount * this.coordinatesPerVertex
        );
        vertexBuffer.put(new float[] { 0f, 0f, 0f, });
        vertexBuffer.put(new float[] { 0f, 1f, 0f, });
        vertexBuffer.put(new float[] { 1f, 1f, 0f, });
        vertexBuffer.flip();

        // initialize color buffer
        FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(
                this.vertexCount * this.channelsPerColor
        );
        colorBuffer.put(new float[] { 1f, 0f, 0f, });
        colorBuffer.put(new float[] { 0f, 1f, 0f, });
        colorBuffer.put(new float[] { 0f, 0f, 1f, });
        colorBuffer.flip();

        // load vertex buffer data
        this.vboVertexHandle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.vboVertexHandle);
        // GL_STATIC_DRAW => Data is modified once and used many times
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);


        // load color buffer data
        this.vboColorHandle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.vboColorHandle);
        glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
    }
}
