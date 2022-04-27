package engine.ui;

abstract public class UIElement {
    private static int NEXT_ID = 0;

    final public int id;
    float width, height;
    float x, y;

    /**
     * @param width as a percentage of the total screen (0-1)
     * @param height as a percentage of the total screen (0-1)
     * @param x coordinate on the screen (0-1)
     * @param y coordinate on the screen (0-1)
     */
    public UIElement(float width, float height, float x, float y) {
        this.id = NEXT_ID++;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }

    abstract public void render();
    abstract public void dispose();
}
