package engine.graphics;

import static org.lwjgl.opengl.GL41.GL_VERTEX_SHADER;

public class VertexShader extends Shader {
    public VertexShader(String shaderPath) {
        super(shaderPath, GL_VERTEX_SHADER);
    }
}
