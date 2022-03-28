import static org.lwjgl.opengl.GL41.*;

abstract public class Model {
    protected Transform transform;
    protected int vbo;
    protected int vao;
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
        // bind buffer array
        glBindVertexArray(this.vao);
        // draw 'vertices' many vertices
        glDrawArrays(GL_TRIANGLES, 0, vertexCount);

        glBindVertexArray(0);
    }

    public void update(float dt) {

    }

    public void cleanUp() {
        // delete buffers
        glDeleteBuffers(vao);
//        glDeleteBuffers(vboColorHandle);
    }
}
