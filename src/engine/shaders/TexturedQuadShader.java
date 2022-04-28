package engine.shaders;

import engine.graphics.ShaderProgram;

public class TexturedQuadShader extends ShaderProgram {
    private static String VERTEX_SHADER_FILE = "res/shaders/quad/textured_quad.vert";
    private static String FRAGMENT_SHADER_FILE = "res/shaders/quad/textured_quad.frag";

    public TexturedQuadShader() {
        super(VERTEX_SHADER_FILE, FRAGMENT_SHADER_FILE);
    }
}
