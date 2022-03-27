import java.nio.*;

import static org.lwjgl.opengl.GL46.*;

abstract public class Model {
    protected Transform transform;
    protected int vboVertexHandle;
    protected int vboColorHandle;
    protected int vertexCount;
    protected int coordinatesPerVertex;
    protected int channelsPerColor;
    protected Shader vertexShader;
    protected Shader fragmentShader;

    public Model() {

    }

    public void render() {
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
}
