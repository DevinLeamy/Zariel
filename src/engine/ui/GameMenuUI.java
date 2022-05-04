package engine.ui;

import engine.World;
import math.Vector2;
import math.Vector3;

public class GameMenuUI {
    public static void init() {
        UI ui = World.ui;

        UIElement container = new UIElement(
                new Vector2(1, 1f),
                new Vector2(0, 0)
        );
        container.setTexture("res/images/ui/flag.jpg");
        ui.addElement(container);

        UIButton leftButton = new UIButton(
                new Vector2(0.1f, 0.1f),
                new Vector2(0, 0)
        );
        leftButton.setOnClick(() -> {
            World.reset();
        });
        leftButton.setColor(new Vector3(1, 0, 0));

        UIButton rightButton = new UIButton(
                new Vector2(0.1f, 0.1f),
                new Vector2(0.9f, 0)
        );
        rightButton.setOnClick(() -> {
            java.lang.System.out.println("Playing Game");
            World.onPlayingGame();
        });
        rightButton.setTexture("res/images/ui/play.png");

        container.addChild(leftButton);
        container.addChild(rightButton);
    }
}
