package engine.renderers;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;

abstract public class Renderer {
    final protected ShaderProgram shader;

    protected Renderer(ShaderProgram shader) {
        this.shader = shader;
    }

    final public void render(Mesh mesh) {
        beforeRender(mesh);
        renderHook(mesh);
        afterRender(mesh);
    }

    abstract protected void renderHook(Mesh mesh);
    abstract protected void setUniforms();

    private void beforeRender(Mesh mesh) {
        shader.link();
        mesh.link();
        setUniforms();
    }

    private void afterRender(Mesh mesh) {
        shader.unlink();
        mesh.unlink();
    }
}
