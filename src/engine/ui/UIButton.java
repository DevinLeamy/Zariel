package engine.ui;

import engine.World;
import engine.controller.Controller;
import engine.main.BoundingBox2D;
import engine.main.Listener;
import engine.view.Window;
import math.Vector2;

public class UIButton extends UIElement implements Listener {
    Runnable onClick;
    Runnable onHover;

    public UIButton(Vector2 size, Vector2 position) {
        super(size, position);
        this.onClick = () -> {};
        this.onHover = () -> {};

        Controller.addListener(this);
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }

    public void setOnHover(Runnable onHover) {
        this.onHover = onHover;
    }

    /**
     * Callback that is triggered by a controller
     * when the mouse is clicked.
     * @param mouseX: mouse x position in window coordinates
     * @param mouseY: mouse y position in window coordinates
     */
    public void onClickCallback(int mouseX, int mouseY) {
        float normalizedX = mouseX / (float) Window.WIDTH;
        float normalizedY = 1 - (mouseY / (float) Window.HEIGHT);

        normalizedX = (2 * normalizedX) - 1;
        normalizedY = (2 * normalizedY) - 1;

        System.out.printf("CALLBACK: %f, %f\n", normalizedX, normalizedY);

        if (isWithinBounds(normalizedX, normalizedY)) {
            onClick.run();
        }
    }

    /**
     * Callback that is triggered by a controller
     * when the cursor is moved.
     * @param mouseX: mouse x position in window coordinates
     * @param mouseY: mouse y position in window coordinates
     */
    public void onHoverCallback(int mouseX, int mouseY) {
        if (isWithinBounds(mouseX, mouseY)) {
            onHover.run();
        }
    }

    private boolean isWithinBounds(float mouseX, float mouseY) {
        // TODO: should be the parent's bounding container
        BoundingBox2D bounds = boundingContainer();

        return bounds.contains(mouseX, mouseY);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void update() {
        int[] mousePosition = Controller.mousePosition();
        onClickCallback(mousePosition[0], mousePosition[1]);
    }
}
