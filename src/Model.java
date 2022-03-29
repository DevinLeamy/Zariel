import static org.lwjgl.opengl.GL41.*;
import rendering.Mesh;

abstract public class Model {
    protected Transform transform;
    protected Mesh mesh;
    protected int vbo, vao, ebo;
    protected int vertexCount, coordinatesPerVertex, channelsPerColor;
    protected ShaderProgram shaderProgram;

    public Model(VertexShader vs, FragmentShader fs, Transform transform) {
//        this.mesh = new Mesh();
        this.transform = transform;
        this.shaderProgram = new ShaderProgram(vs, fs);
    }

    final public void render() {
        // bind shader program
        shaderProgram.link();

        setUniforms();

        // bind buffer array
        glBindVertexArray(this.vao);
        // draw 'vertices' many vertices
        glDrawArrays(GL_TRIANGLES, 0, this.vertexCount);

        // unbind
        glBindVertexArray(0);
    }

    protected void setUniforms() {}

    public void update(float dt) {}

    public void cleanUp() {
        // delete buffers
        glDeleteBuffers(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
    }
}
