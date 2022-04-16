import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBImageResize.*;

/**
 * Implemented using a cube map as described here: https://learnopengl.com/Advanced-OpenGL/Cubemaps
 */
public class SkyBox {
    private static VertexShader vs = new VertexShader("res/shaders/skybox.vs");
    private static FragmentShader fs = new FragmentShader("res/shaders/skybox.fs");
    private static ShaderProgram shader = new ShaderProgram(vs, fs);

    private int textureHandle;
    private int width, height, channels;

    /**
     * @param images: [top, right, front, left, back, bottom]
     */
    public SkyBox(String[] images) {
//       initialize();
    }

//    private static loadSkyBoxTexture(String[] images) {
//        textureHandle = glGenTextures();
//        // TODO: skybox
//
//    }

//    private void initialize() {
//        glBindTexture(GL_TEXTURE_2D, textureHandle);
//
//
//
//        IntBuffer width = IntBuffer.allocate(1);
//        IntBuffer height = IntBuffer.allocate(1);
//        IntBuffer channels = IntBuffer.allocate(1);
//
//        ByteBuffer imageData = stbi_load(imageSrc, width, height, channels, 3);
//
//        if (imageData == null) {
//            System.out.println("Error: Image failed to load");
//            return;
//        }
//
//        this.width = width.get(0);
//        this.height = height.get(0);
//        this.channels = channels.get(0);
//
//        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, this.width, this.height, 0, GL_RGB, GL_UNSIGNED_BYTE, imageData);
//
//        stbi_image_free(imageData);
//    }

    public void render() {
        glBindTexture(GL_TEXTURE_2D, textureHandle);

    }
}
