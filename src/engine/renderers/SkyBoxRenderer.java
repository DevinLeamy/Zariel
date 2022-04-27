package engine.renderers;

import engine.graphics.Mesh;
import engine.main.Camera;
import engine.shaders.SkyBoxShader;
import math.Matrix4;

import static org.lwjgl.opengl.GL41.*;

public class SkyBoxRenderer extends Renderer {
    Camera camera;
    int skyBoxTextureHandle;

    public SkyBoxRenderer() {
        super(new SkyBoxShader());
    }

    public void setRenderContext(Camera camera, int skyBoxTextureHandle) {
        this.camera = camera;
        this.skyBoxTextureHandle = skyBoxTextureHandle;
    }

    @Override
    protected void renderHook(Mesh mesh) {
        glDepthMask(false);
        glDrawArrays(GL_TRIANGLES, 0, mesh.vertices());
        glDepthMask(true);
    }

    @Override
    protected void setUniforms() {
        Matrix4 viewMatrix = camera.viewMatrix();
        // remove translation
        // https://stackoverflow.com/questions/47225368/simplest-way-to-convert-mat3-to-mat4
        viewMatrix.m[0][3] = 0;
        viewMatrix.m[1][3] = 0;
        viewMatrix.m[2][3] = 0;
        viewMatrix.m[3][3] = 1;
        viewMatrix.m[3][2] = 0;
        viewMatrix.m[3][1] = 0;
        viewMatrix.m[3][0] = 0;


        shader.setUniform("viewM", viewMatrix);
        shader.setUniform("projectionM", camera.projectionMatrix());
        glBindTexture(GL_TEXTURE_CUBE_MAP, skyBoxTextureHandle);
    }
}
