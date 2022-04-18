import math.Matrix4;
import math.Vector3;
import math.Vector3i;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL41.*;

public class ShaderProgram {
    private int programHandle;
    private Map<String, Uniform> uniforms;

    public ShaderProgram(Shader... shaders) {
        this.programHandle = glCreateProgram();
        this.uniforms = new HashMap<>();

        // attach shaders and collect uniforms
        for (Shader shader : shaders) {
            glAttachShader(programHandle, shader.getShaderHandle());
            for (Uniform uniform : shader.getUniforms()) {
                uniforms.put(uniform.getName(), uniform);
            }
        }

        glLinkProgram(programHandle);
    }

    public void setUniform(String name, float value) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(1);
        buffer.put(value);
        buffer.flip();

        uniforms.get(name).setUniform(programHandle, buffer, Uniform.UniformT.FLOAT);
    }

    public void setUniform(String name, Matrix4 value) {
        uniforms.get(name).setUniform(programHandle, value.toFloatBuffer(), Uniform.UniformT.MATRIX_4F);
    }

    public void setUniform(String name, Vector3i value) {
        setUniform(name, value.toVector3());
    }

    public void setUniform(String name, Vector3 value) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
        buffer.put(value.toArray());
        buffer.flip();

        uniforms.get(name).setUniform(programHandle, buffer, Uniform.UniformT.VECTOR_3F);
    }

    // when the program is about to be used
    public void link() {
        glUseProgram(programHandle);

        if (glGetProgrami(programHandle, GL_LINK_STATUS) == 0) {
            System.err.println("Error linking Shader code: " + glGetProgramInfoLog(programHandle, 1024));
            System.exit(1);
        }
    }

    public int getProgramHandle() {
        return programHandle;
    }

    // when the program is no longer going to be used
    public void cleanUp() {
        glDeleteProgram(programHandle);
    }
}
