import static org.lwjgl.opengl.GL41.*;

import rendering.Mesh;

abstract public class Model {
    protected Transform transform;
    protected Mesh mesh;
    protected ShaderProgram shaderProgram;

    public Model(VertexShader vs, FragmentShader fs, Transform transform, Mesh mesh) {
        this.mesh = mesh;
        this.transform = transform;
        this.shaderProgram = new ShaderProgram(vs, fs);
    }

    final public void render() {
        // bind shader program
        shaderProgram.link();

        setUniforms();

        // bind buffer array
        glBindVertexArray(mesh.vao);

        // draw 'vertices' many vertices
        glDrawElements(GL_TRIANGLES, mesh.indices.size(), GL_UNSIGNED_INT, 0);

        // unbind
        glBindVertexArray(0);
    }

    protected void setUniforms() {}

    public void update(float dt) {}

    public void cleanUp() {
        mesh.dispose();
    }
}
