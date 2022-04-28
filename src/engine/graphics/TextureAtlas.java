package engine.graphics;

import math.Vector2;
import static org.lwjgl.opengl.GL41.*;

public class TextureAtlas {
    private int atlasHandle;
    final private float tileWidth;
    final private float tileHeight;
    final private float margin = 0.0000f;

    /**
     * @param path: path the texture atlas image
     * @param rows: rows in the texture atlas
     * @param cols: columns in the texture atlas
     */
    public TextureAtlas(String path, int rows, int cols) {
        this.atlasHandle = TextureLoader.loadTextureAtlasTexture(path);
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
}
