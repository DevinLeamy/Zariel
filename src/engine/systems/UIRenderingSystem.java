package engine.systems;

import engine.World;
import engine.ecs.System;
import engine.renderers.UIRenderer;
import engine.ui.UI;
import engine.ui.UIButton;
import engine.ui.UIContainer;
import math.Vector2;
import math.Vector3;

public class UIRenderingSystem extends System {
    UIRenderer renderer;
    public UIRenderingSystem() {
        super(0);
        renderer = new UIRenderer();

        World world = World.getInstance();
        UI ui = world.ui;
        UIContainer container = new UIContainer(
                new Vector2(1, 1f),
                new Vector2(0, 0)
        );
        ui.addElement(container);

        UIButton leftButton = new UIButton(
                new Vector2(0.1f, 0.1f),
                new Vector2(0, 0)
        );
        leftButton.setOnClick(() -> {
            World.getInstance().reset();
        });
        leftButton.setColor(new Vector3(1, 0, 0));

        UIButton rightButton = new UIButton(
                new Vector2(0.1f, 0.1f),
                new Vector2(0.9f, 0)
        );
        rightButton.setOnClick(() -> {
            java.lang.System.out.println("Right button clicked");
        });

        container.addChild(leftButton);
        container.addChild(rightButton);
    }

    @Override
    public void update(float dt) {
        World world = World.getInstance();
        UI ui = world.ui;
        renderer.render(ui.main);
    }
}
