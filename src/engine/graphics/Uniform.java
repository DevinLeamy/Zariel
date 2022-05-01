package engine.graphics;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL41.*;

public class Uniform {
    public enum UniformT {
        MATRIX_4F, VECTOR_3F, VECTOR_4F, FLOAT
    }

    // assumes the relevant shader has been linked
    public static void setUniform(int location, FloatBuffer value, UniformT type) {
        switch(type) {
            case FLOAT -> {}
            case MATRIX_4F -> glUniformMatrix4fv(location, true, value);
            case VECTOR_3F -> glUniform3fv(location, value);
            case VECTOR_4F -> glUniform4fv(location, value);
        }
    }

    public static int getUniformLocation(int shaderHandle, String uniformName) {
        return glGetUniformLocation(shaderHandle, uniformName);
    }
}
