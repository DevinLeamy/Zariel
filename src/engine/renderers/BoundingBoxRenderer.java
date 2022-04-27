package engine.renderers;

import engine.components.Transform;
import engine.graphics.Mesh;
import engine.main.Camera;
import engine.shaders.GameObjectShader;

import static org.lwjgl.opengl.GL41.*;

public class BoundingBoxRenderer extends Renderer {
    Camera camera;
    Transform boundingBoxTransform;

    public BoundingBoxRenderer() {
        super(new GameObjectShader());
    }

    public void setRenderContext(Camera camera, Transform boundingBoxTransform) {
        this.camera = camera;
        this.boundingBoxTransform = boundingBoxTransform;
    }

    @Override
    protected void renderHook(Mesh mesh) {
        glDrawArrays(GL_LINES, 0, mesh.vertices());
    }

    @Override
    public void setUniforms() {
        shader.setUniform("viewM", camera.viewMatrix());
        shader.setUniform("modelM", boundingBoxTransform.modelMatrix());
        shader.setUniform("projectionM", camera.projectionMatrix());
    }
}
