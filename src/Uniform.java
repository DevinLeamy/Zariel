import static org.lwjgl.opengl.GL41.*;

import java.nio.FloatBuffer;

public class Uniform {
    public enum UniformT {
        MATRIX_4F, VECTOR_3F, FLOAT
    }
    private String name;
    private UniformT type;

    public Uniform(String name, UniformT type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setUniform(int shaderHandle, FloatBuffer value, UniformT type) {
        if (this.type != type) {
            System.err.println("Error: invalid type for uniform");
            return;
        }
        glUseProgram(shaderHandle);

        int uniformHandle = glGetUniformLocation(shaderHandle, name);

        switch(type) {
            case FLOAT -> {}
            case MATRIX_4F -> glUniformMatrix4fv(uniformHandle, true, value);
            case VECTOR_3F -> glUniform3fv(uniformHandle, value);
        }

        glUseProgram(0);
    }
}
