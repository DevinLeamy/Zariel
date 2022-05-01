package engine.systems;

import engine.World;
import engine.ecs.System;
import engine.renderers.UIRenderer;
import engine.ui.UIContainer;
import engine.ui.UI;
import engine.ui.UIElement;
import math.Vector2;

public class UIRenderingSystem extends System {
    UIRenderer renderer;
    public UIRenderingSystem() {
        super(0);
        renderer = new UIRenderer();

        World world = World.getInstance();
        UI ui = world.ui;
        UIElement container = new UIElement(
                new Vector2(0.25f, 0.5f),
                new Vector2(0, 0)
        );

//        for (int i = 0; i < 1; ++i) {
//            UIElement child = new UIElement(
//                    new Vector2(0.1f, 0.1f),
//                    new Vector2(0.1f * i, 0)
//            );
//            child.setTexture(String.format("res/images/numbers/%d.png", i));
//            container.addChild(child);
//        }

        ui.addElement(container);
    }

    @Override
    public void update(float dt) {
        World world = World.getInstance();
        UI ui = world.ui;
        renderer.render(ui.main);
    }
}
