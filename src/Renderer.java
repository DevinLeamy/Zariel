import static org.lwjgl.opengl.GL41.*;

class Renderer {
    public ShaderProgram shader;

    public Renderer(ShaderProgram shader) {
        this.shader = shader;
    }

    /**
     * NOTE: it is assumed that shader program uniforms have been set
     * via renderer.shader.setUniform(...) BEFORE this function is executed.
     */
    public void renderMesh(VoxelMesh mesh) {
        shader.link();
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glFrontFace(GL_CCW);

        mesh.link();

        glDrawArrays(GL_TRIANGLES, 0, mesh.vertices());

        mesh.unlink();
    }
}