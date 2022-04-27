package engine.systems;

import engine.controller.Controller;
import engine.ecs.System;
import engine.main.Debug;
import engine.World;

import static org.lwjgl.glfw.GLFW.*;

public class DebugInputSystem extends System {
    Controller controller = Controller.getInstance();


    public DebugInputSystem() {
        super(0);
    }

    @Override
    public void update(float dt) {
        if (controller.takeKeyPressState(GLFW_KEY_Q) == GLFW_PRESS) {
            World.getInstance().reset();
        }
        if (controller.takeKeyPressState(GLFW_KEY_0) == GLFW_PRESS) {
            Debug.DEBUG = true;
        } else if (controller.takeKeyPressState(GLFW_KEY_9) == GLFW_PRESS) {
            Debug.DEBUG = false;
        }
    }
}
