package engine.renderers;

import engine.components.Transform;
import engine.graphics.Mesh;
import engine.main.Camera;
import engine.shaders.GameObjectShader;

import static org.lwjgl.opengl.GL41.*;

public class GameObjectRenderer extends Renderer {
    Camera camera;
    Transform gameObjectTransform;

    public GameObjectRenderer() {
        super(new GameObjectShader());
    }

    public void setRenderContext(Camera camera, Transform gameObjectTransform) {
        this.camera = camera;
        this.gameObjectTransform = gameObjectTransform;
    }

    @Override
    protected void renderHook(Mesh mesh) {
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glFrontFace(GL_CCW);
        glDrawArrays(GL_TRIANGLES, 0, mesh.vertices());
    }

    @Override
    protected void setUniforms() {
        shader.setUniform("viewM", camera.viewMatrix());
        shader.setUniform("modelM", gameObjectTransform.modelMatrix());
        shader.setUniform("projectionM", camera.projectionMatrix());
    }
}
