package engine.renderers;

import engine.graphics.Mesh;
import engine.shaders.TexturedQuadShader;
import math.Matrix4;

import static org.lwjgl.opengl.GL41.*;

public class TexturedQuadRenderer extends Renderer {
    int textureHandle;
    Matrix4 scalingMatrix;
    Matrix4 screenCoordMatrix;

    public TexturedQuadRenderer() {
        super(new TexturedQuadShader());
    }

    public void setRenderContext(int textureHandle, Matrix4 screenCoordMatrix, Matrix4 scalingMatrix) {
        this.textureHandle = textureHandle;
        this.screenCoordMatrix = screenCoordMatrix;
        this.scalingMatrix = scalingMatrix;
    }

    @Override
    protected void renderHook(Mesh mesh) {
        glDepthMask(false);
        glDrawArrays(GL_TRIANGLES, 0, mesh.vertices());
        glDepthMask(true);
    }

    @Override
    protected void setUniforms() {
        glBindTexture(GL_TEXTURE_2D, textureHandle);
        shader.setUniform("scalingM", scalingMatrix);
        shader.setUniform("screenCoordM", screenCoordMatrix);
    }
}
