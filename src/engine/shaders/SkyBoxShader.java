package engine.shaders;

import engine.graphics.ShaderProgram;

public class SkyBoxShader extends ShaderProgram {
    private static String VERTEX_SHADER_FILE = "res/shaders/skybox.vert";
    private static String FRAGMENT_SHADER_FILE = "res/shaders/skybox.frag";

    public SkyBoxShader() {
        super(VERTEX_SHADER_FILE, FRAGMENT_SHADER_FILE);
    }
}
