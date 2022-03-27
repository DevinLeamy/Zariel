import org.lwjgl.BufferUtils;

import java.nio.*;

import static org.lwjgl.opengl.GL41.*;

abstract public class Model {
    protected Transform transform;
    protected int vboVertexHandle;
    protected int vboColorHandle;
    protected int vertexCount;
    protected int coordinatesPerVertex;
    protected int channelsPerColor;
    protected ShaderProgram shaderProgram;

    public Model(VertexShader vs, FragmentShader fs, Transform transform) {
        this.transform = transform;
        this.shaderProgram = new ShaderProgram(vs, fs);
    }

    public void render() {
        // bind shader program
        shaderProgram.link();

        // TEMP
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(
                this.vertexCount * this.coordinatesPerVertex
        );
        vertexBuffer.put(new float[] { 0f, 0f, 0f, });
        vertexBuffer.put(new float[] { 0f, 1f, 0f, });
        vertexBuffer.put(new float[] { 1f, 1f, 0f, });
        vertexBuffer.flip();
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        // GL_STATIC_DRAW => Data is modified once and used many times
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        // ------



        // bind position vbo
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glVertexPointer(coordinatesPerVertex, GL_FLOAT, 0, 0L);

        // bind color vbo
        glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
        glColorPointer(channelsPerColor, GL_FLOAT, 0, 0L);

        // enable the attributes
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);

        // draw 'vertices' many vertices
        glDrawArrays(GL_TRIANGLES, 0, vertexCount);

        // disable the attributes (reset state)
        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_COLOR_ARRAY);
    }

    public void update(float dt) {

    }

    public void cleanUp() {
        // delete buffers
        glDeleteBuffers(vboVertexHandle);
        glDeleteBuffers(vboColorHandle);
    }
}
