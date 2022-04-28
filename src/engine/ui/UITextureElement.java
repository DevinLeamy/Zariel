package engine.ui;

import engine.graphics.TextureLoader;
import engine.main.BoundingBox2D;
import math.Vector2;

import static org.lwjgl.opengl.GL41.*;

public class UITextureElement extends UIElement {
      int textureHandle;
     public UITextureElement(Vector2 size, Vector2 position, String texturePath) {
         super(size, position);

        textureHandle = TextureLoader.load2DTexture(texturePath);
    }

    public int textureHandle() {
         return textureHandle;
    }

    @Override
    public void dispose() {
        glDeleteTextures(textureHandle);
    }
}
