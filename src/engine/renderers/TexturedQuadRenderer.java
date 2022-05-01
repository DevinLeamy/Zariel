package engine.renderers;

import engine.graphics.Mesh;
import engine.shaders.TexturedQuadShader;
import math.Matrix4;
import math.Vector3;
import math.Vector4;

import static org.lwjgl.opengl.GL41.*;

public class TexturedQuadRenderer extends Renderer {
    int textureHandle;
    Matrix4 scalingMatrix;
    Matrix4 screenCoordMatrix;
    Vector4 color;

    public TexturedQuadRenderer() {
        super(new TexturedQuadShader());
    }

    public void setRenderContext(int textureHandle, Vector4 color, Matrix4 screenCoordMatrix, Matrix4 scalingMatrix) {
        this.textureHandle = textureHandle;
        this.screenCoordMatrix = screenCoordMatrix;
        this.scalingMatrix = scalingMatrix;
        this.color = color;
    }

    @Override
    protected void renderHook(Mesh mesh) {
        glDepthMask(false);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDrawArrays(GL_TRIANGLES, 0, mesh.vertices());
        glDisable(GL_BLEND);
        glDepthMask(true);
    }

    @Override
    protected void setUniforms() {
        glBindTexture(GL_TEXTURE_2D, textureHandle);
        shader.setUniform("color", color);
        shader.setUniform("scalingM", scalingMatrix);
        shader.setUniform("screenCoordM", screenCoordMatrix);
    }
}
