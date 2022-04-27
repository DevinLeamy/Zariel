package engine.graphics;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL41.*;

public class Shader {
    protected int type;
    protected int shaderHandle;
    protected Uniform[] uniforms;

    public Shader(String shaderPath, int type, Uniform[] uniforms) {
        this.type = type;
        this.uniforms = uniforms;

        compileShader(shaderPath, type);
    }

    public Uniform[] getUniforms() {
        return uniforms;
    }

    private String loadSource(String path) {
        String source = "";
        try {
            source = Files.readString(Path.of(path), StandardCharsets.US_ASCII);
        } catch (IOException e) {
            System.err.println("Error: loading shader (" + path + ")");
            e.printStackTrace();
        }

        return source;
    }

    private void compileShader(String sourcePath, int type) {
        // create shader
        shaderHandle = glCreateShader(type);
        if (shaderHandle == 0) {
            System.err.println("could not create shader object; check engine.graphics.ShaderProgram.isSupported()");
            System.exit(1);
        }

        // bind the shader source
        glShaderSource(shaderHandle, loadSource(sourcePath));

        // compile the shader
        glCompileShader(shaderHandle);

        if (glGetShaderi(shaderHandle, GL_COMPILE_STATUS) == 0) {
            System.err.println("Error compiling engine.graphics.Shader code: (" + sourcePath + ") " + glGetShaderInfoLog(shaderHandle, 1024));
            System.exit(1);
        }
    }

    public int getShaderHandle() {
        return shaderHandle;
    }

    public void cleanUp() {
        glDeleteShader(shaderHandle);
    }
}