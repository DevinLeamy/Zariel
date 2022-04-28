package engine.ui;

import engine.main.BoundingBox2D;
import math.Vector2;

public class UI {
    final public static float MAIN_WINDOW_WIDTH = 2;
    final public static float MAIN_WINDOW_HEIGHT = 2;
    final public static BoundingBox2D MAIN_WINDOW_CONTAINER = new BoundingBox2D(new Vector2(MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT), new Vector2(-1, -1));

    final public UIContainer main;

    public UI() {
        main = new UIContainer(new Vector2(1, 1), new Vector2(0, 0));
    }

    public void addElement(UIElement element) {
        main.addChild(element);
    }

    public void removeElement(int elementId) {
        // .dispose() removed UI element
        main.removeChild(elementId);
    }

    public void clear() {
        main.dispose();
    }
}
