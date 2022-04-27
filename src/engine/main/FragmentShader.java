package engine.main;

import static org.lwjgl.opengl.GL41.*;

public class FragmentShader extends Shader {
    public FragmentShader(String shaderPath, Uniform[] uniforms) {
        super(shaderPath, GL_FRAGMENT_SHADER, uniforms);
    }
}
