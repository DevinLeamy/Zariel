package engine.shaders;

import engine.graphics.ShaderProgram;

public class TerrainShader extends ShaderProgram {
    private static String VERTEX_SHADER_FILE = "res/shaders/chunk.vert";
    private static String FRAGMENT_SHADER_FILE = "res/shaders/chunk.frag";

    public TerrainShader() {
        super(VERTEX_SHADER_FILE, FRAGMENT_SHADER_FILE);
    }
}
