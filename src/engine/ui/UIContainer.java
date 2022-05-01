package engine.ui;

import math.Vector2;

public class UIContainer extends UIElement {
    public UIContainer(Vector2 size, Vector2 position) {
        super(size, position);
        alpha = 0;
    }

    @Override
    public void dispose() {
        for (UIElement element : children) {
            element.dispose();
        }
    }
}
