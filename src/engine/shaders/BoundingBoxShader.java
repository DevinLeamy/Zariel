package engine.shaders;

import engine.graphics.ShaderProgram;


public class BoundingBoxShader extends ShaderProgram {
    private static String VERTEX_SHADER_FILE = "res/shaders/boundingbox/vs.vert";
    private static String FRAGMENT_SHADER_FILE = "res/shaders/boundingbox/fs.frag";

    public BoundingBoxShader() {
       super(VERTEX_SHADER_FILE, FRAGMENT_SHADER_FILE);
    }
}
