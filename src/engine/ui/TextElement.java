package engine.ui;

public class TextElement extends UIElement {
    String text;

    public TextElement(String text, float width, float height, float x, float y) {
        super(width, height, x, y);
        this.text = text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void init() {

    }

    @Override
    public void render() {

    }

    @Override
    public void dispose() {

    }
}
