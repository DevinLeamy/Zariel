package engine.main;

import static org.lwjgl.opengl.GL41.*;

public class VertexShader extends Shader {
    public VertexShader(String shaderPath, Uniform[] uniforms) {
        super(shaderPath, GL_VERTEX_SHADER, uniforms);
    }
}
