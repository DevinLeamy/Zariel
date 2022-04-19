import math.Vector2;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.stb.STBImage.*;

public class TextureAtlas {
    private int atlasHandle;
    final private float tileWidth;
    final private float tileHeight;
    final private float margin = 0.0001f;

    /**
     * @param path: path the texture atlas image
     * @param rows: rows in the texture atlas
     * @param cols: columns in the texture atlas
     */
    public TextureAtlas(String path, int rows, int cols) {
        this.atlasHandle = TextureAtlas.loadTexture(path);
        this.tileWidth = 1.0f / rows;
        this.tileHeight = 1.0f / cols;
    }

    /**
     * @return UVs for the texture located at (row, col) in the texture atlas
     */
    public Vector2[] getUVs(int row, int col) {
        Vector2 topLeft = new Vector2(col * tileWidth + margin, row * tileHeight + margin);
        Vector2 topRight = new Vector2(col * tileWidth - margin, (row + 1) * tileHeight + margin);
        Vector2 bottomLeft = new Vector2((col + 1) * tileWidth + margin, row * tileHeight - margin);
        Vector2 bottomRight = new Vector2((col + 1) * tileWidth - margin, (row + 1) * tileHeight - margin);

        return new Vector2[] {
                topLeft,
                topRight,
                bottomRight,
                bottomLeft
        };
    }

    public void link() {
        glBindTexture(GL_TEXTURE_2D, atlasHandle);
    }

    public static int loadTexture(String path) {
        int textureHandle = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureHandle);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        ByteBuffer data = stbi_load(path, width, height, channels, 3);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, data);
        glGenerateMipmap(GL_TEXTURE_2D);

        if (data != null) {
            stbi_image_free(data);
        }

//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

        return textureHandle;
    }


}
