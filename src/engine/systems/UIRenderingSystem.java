package engine.systems;

import engine.World;
import engine.ecs.System;
import engine.renderers.UIRenderer;
import engine.ui.UI;

public class UIRenderingSystem extends System {
    UIRenderer renderer;
    public UIRenderingSystem() {
        super(0);
        renderer = new UIRenderer();
    }

    @Override
    public void update(float dt) {
        World world = World.getInstance();
        UI ui = world.ui;
        renderer.render(ui.main);
    }
}
