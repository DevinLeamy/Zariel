package engine.ui;

import engine.main.BoundingBox2D;
import math.Vector2;

import static org.lwjgl.opengl.GL41.*;

public class TextElement extends UIElement {
    String text;

    public TextElement(Vector2 size, Vector2 position, String text) {
        super(size, position);
        this.text = text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void init() {

    }

    @Override
    public void dispose() {
    }
}
