package engine.graphics;

import math.Matrix4;
import math.Vector3;
import math.Vector3i;
import math.Vector4;

import static org.lwjgl.opengl.GL41.*;
import static engine.util.Utils.*;

public class ShaderProgram {
    private int programHandle;

    public ShaderProgram(String vertexShaderFile, String fragmentShaderFile) {
        this.programHandle = glCreateProgram();
        VertexShader vs = new VertexShader(vertexShaderFile);
        FragmentShader fs = new FragmentShader(fragmentShaderFile);
        glAttachShader(programHandle, vs.handle());
        glAttachShader(programHandle, fs.handle());
        glLinkProgram(programHandle);
    }

    public int getUniformLocation(String uniform) {
        return Uniform.getUniformLocation(programHandle, uniform);
    }

    public void setUniform(String name, float value) {
        Uniform.setUniform(getUniformLocation(name), createFloatBuffer(value), Uniform.UniformT.FLOAT);
    }

    public void setUniform(String name, Matrix4 value) {
        Uniform.setUniform(getUniformLocation(name), createFloatBuffer(value), Uniform.UniformT.MATRIX_4F);
    }

    public void setUniform(String name, Vector3i value) {
        Uniform.setUniform(getUniformLocation(name), createFloatBuffer(value), Uniform.UniformT.VECTOR_3F);
    }

    public void setUniform(String name, Vector3 value) {
        Uniform.setUniform(getUniformLocation(name), createFloatBuffer(value), Uniform.UniformT.VECTOR_3F);
    }

    public void setUniform(String name, Vector4 value) {
        Uniform.setUniform(getUniformLocation(name), createFloatBuffer(value), Uniform.UniformT.VECTOR_4F);
    }

    public void link() {
        glUseProgram(programHandle);

        if (glGetProgrami(programHandle, GL_LINK_STATUS) == 0) {
            System.err.println("Error linking engine.graphics.Shader code: " + glGetProgramInfoLog(programHandle, 1024));
            System.exit(1);
        }
    }

    public void unlink() {
        glUseProgram(0);
    }

    final public void dispose() {
        glDeleteProgram(programHandle);
    }
}
