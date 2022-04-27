package engine.graphics;

import static org.lwjgl.opengl.GL41.GL_FRAGMENT_SHADER;

public class FragmentShader extends Shader {
    public FragmentShader(String shaderPath) {
        super(shaderPath, GL_FRAGMENT_SHADER);
    }
}
