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
        UI ui = World.ui;
        renderer.render(ui.getMain());
    }
}
