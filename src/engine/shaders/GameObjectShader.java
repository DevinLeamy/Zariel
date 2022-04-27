package engine.shaders;

import engine.graphics.ShaderProgram;

public class GameObjectShader extends ShaderProgram {
    private static String VERTEX_SHADER_FILE = "res/shaders/voxel_renderable.vert";
    private static String FRAGMENT_SHADER_FILE = "res/shaders/voxel_renderable.frag";

    public GameObjectShader() {
        super(VERTEX_SHADER_FILE, FRAGMENT_SHADER_FILE);
    }
}
