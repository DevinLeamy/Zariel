package engine.renderers;

import engine.graphics.Mesh;
import engine.main.Camera;
import engine.shaders.TerrainShader;
import math.Vector3i;

import static org.lwjgl.opengl.GL41.*;

public class TerrainRenderer extends Renderer {
    Camera camera;
    Vector3i chunkLocation;

    public TerrainRenderer() {
        super(new TerrainShader());
    }

    public void setRenderContext(Camera camera, Vector3i chunkLocation) {
        this.camera = camera;
        this.chunkLocation = chunkLocation;
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
        shader.setUniform("location", chunkLocation);
        shader.setUniform("projectionM", camera.projectionMatrix());
    }
}
