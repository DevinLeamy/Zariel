package engine.ui;

import math.Vector2;

public class UIButton extends UIElement {
    Runnable onClick;
    Runnable onHover;

    public UIButton(Vector2 size, Vector2 position, Runnable onClick, Runnable onHover) {
        super(size, position);

        this.onClick = onClick;
        this.onHover = onHover;
    }

    public UIButton(Vector2 size, Vector2 position, Runnable onClick) {
        this(size, position, onClick, () -> {});
    }

    @Override
    public void dispose() {

    }
}
