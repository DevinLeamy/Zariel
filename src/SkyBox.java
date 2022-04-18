import math.Matrix4;
import math.Vector3;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBImageResize.*;

/**
 * Implemented using a cube map as described here: https://learnopengl.com/Advanced-OpenGL/Cubemaps
 */
public class SkyBox {
    private static VertexShader vs = new VertexShader("res/shaders/skybox.vert", new Uniform[] {
            new Uniform("viewM", Uniform.UniformT.MATRIX_4F),
            new Uniform("projectionM", Uniform.UniformT.MATRIX_4F),
    });
    private static FragmentShader fs = new FragmentShader("res/shaders/skybox.frag", new Uniform[] {});
    private static ShaderProgram shader = new ShaderProgram(vs, fs);

    private int textureHandle;
    private int vao, vbo;

    /**
     * @param faces: [right, left, top, bottom, front, back]
     */
    public SkyBox(String[] faces) {
       textureHandle = SkyBox.loadSkyBoxTexture(faces);
       initialize();
    }

    private static int loadSkyBoxTexture(String[] faces) {
        int textureHandle = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureHandle);
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        for (int i = 0; i < 6; ++i) {
            ByteBuffer image = stbi_load(faces[i], width, height, channels, 3);
            /**
             * We can dynamically generate the target with GL_TEXTURE_CUBE_MAP_POSITIVE_X + i
             * because the GL targets for the different faces of the cube increment linearly.
             * This order must be followed: [right, left, top, bottom, front, back]
             */
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image);

            if (image != null) {
                stbi_image_free(image);
            }
        }

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

        return textureHandle;
    }

    private void initialize() {
        vao = glGenVertexArrays();
        vbo = glGenBuffers();

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        FloatBuffer vertexData = BufferUtils.createFloatBuffer(SKYBOX_VERTICES.length);
        vertexData.put(SKYBOX_VERTICES);
        vertexData.flip();

        glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        /**
         * stride = 0 because values are tightly packed
         */
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0L);

        glBindVertexArray(0);
    }

    public void render(Camera perspective) {
        // The skybox is always at the back of the scene
        glDepthMask(false);

        shader.link();
        int shaderHandle = shader.getProgramHandle();
        int viewMHander = glGetUniformLocation(shaderHandle, "viewM");
        int projectionMHandler = glGetUniformLocation(shaderHandle, "projectionM");

        Matrix4 viewMatrix = Camera.lookAt(perspective.transform.position, World.getInstance().player.transform.position, new Vector3(0, 1, 0));
        // remove translation
        // https://stackoverflow.com/questions/47225368/simplest-way-to-convert-mat3-to-mat4
        viewMatrix.m[0][3] = 0;
        viewMatrix.m[1][3] = 0;
        viewMatrix.m[2][3] = 0;
        viewMatrix.m[3][3] = 1;
        viewMatrix.m[3][2] = 0;
        viewMatrix.m[3][1] = 0;
        viewMatrix.m[3][0] = 0;

        glUniformMatrix4fv(viewMHander, true, viewMatrix.toFloatBuffer());
        glUniformMatrix4fv(projectionMHandler, true, perspective.projectionMatrix().toFloatBuffer());
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureHandle);

        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, SKYBOX_VERTICES.length);

        // -- restore
        glBindVertexArray(0);
        glDepthMask(true);
    }

    private static float[] SKYBOX_VERTICES = new float[] {
            // positions
            -1.0f,  1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f,  1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f,

            -1.0f, -1.0f,  1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f,
            -1.0f,  1.0f,  1.0f,
            -1.0f, -1.0f,  1.0f,

            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,

            -1.0f, -1.0f,  1.0f,
            -1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f, -1.0f,  1.0f,
            -1.0f, -1.0f,  1.0f,

            -1.0f,  1.0f, -1.0f,
            1.0f,  1.0f, -1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            -1.0f,  1.0f,  1.0f,
            -1.0f,  1.0f, -1.0f,

            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f,  1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f,  1.0f,
            1.0f, -1.0f,  1.0f
    };
}
