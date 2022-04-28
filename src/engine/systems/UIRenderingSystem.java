package engine.systems;

import engine.World;
import engine.ecs.System;
import engine.renderers.UIRenderer;
import engine.ui.UITextureElement;
import engine.ui.UI;
import math.Vector2;

public class UIRenderingSystem extends System {
    UIRenderer renderer;
    public UIRenderingSystem() {
        super(0);
        renderer = new UIRenderer();

        World world = World.getInstance();
        UI ui = world.ui;
        UITextureElement textureElement = new UITextureElement(
                new Vector2(0.5f, 0.5f),
                new Vector2(0.5f, 0),
                "res/images/menu.jpg"
        );

        for (int i = 0; i < 4; ++i) {
            UITextureElement child = new UITextureElement(
                    new Vector2(0.25f, 0.25f),
                    new Vector2(0.25f * i, 0.25f * i),
                    "res/images/menu.jpg"
            );
            textureElement.addChild(child);
        }

        ui.addElement(textureElement);
    }

    @Override
    public void update(float dt) {
        World world = World.getInstance();
        UI ui = world.ui;
        renderer.render(ui.main);
    }
}
