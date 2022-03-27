import static org.lwjgl.opengl.GL41.*;

public class ShaderProgram {
    private int programHandle;

    public ShaderProgram(Shader... shaders) {
        // create program
        programHandle = glCreateProgram();

        // attach shaders
        for (Shader shader : shaders) {
            glAttachShader(programHandle, shader.getShaderHandle());
        }

        glLinkProgram(programHandle);
    }

    // when the program is about to be used
    public void link() {
        glUseProgram(programHandle);

        if (glGetProgrami(programHandle, GL_LINK_STATUS) == 0) {
            System.err.println("Error linking Shader code: " + glGetProgramInfoLog(programHandle, 1024));
            System.exit(1);
        }
    }

    // when the program is no longer going to be used
    public void cleanUp() {
        glDeleteProgram(programHandle);
    }
}
