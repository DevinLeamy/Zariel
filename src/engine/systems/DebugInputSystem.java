package engine.systems;

import engine.controller.Controller;
import engine.ecs.System;
import engine.main.Debug;
import engine.World;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.glPolygonMode;

public class DebugInputSystem extends System {
    public DebugInputSystem() {
        super(0);
    }

    @Override
    public void update(float dt) {
        if (Controller.takeKeyPressState(GLFW_KEY_Q) == GLFW_PRESS) {
            World.getInstance().reset();
        }
        if (Controller.takeKeyPressState(GLFW_KEY_0) == GLFW_PRESS) { Debug.DEBUG = true; }
        else if (Controller.takeKeyPressState(GLFW_KEY_9) == GLFW_PRESS) { Debug.DEBUG = false; }

        if (Controller.takeKeyPressState(GLFW_KEY_Z) == GLFW_PRESS) { Debug.cursorLocked = true; }
        else if (Controller.takeKeyPressState(GLFW_KEY_X) == GLFW_PRESS) { Debug.cursorLocked = false; }

        if (Controller.keyPressed(GLFW_KEY_P)) { Debug.wireframe = false; }
        if (Controller.keyPressed(GLFW_KEY_O)) { Debug.wireframe = true; }

        glPolygonMode(GL_FRONT_AND_BACK, Debug.wireframe ? GL_LINE : GL_FILL);
        World.getInstance().window.setCursorLocked(Debug.cursorLocked);
    }
}
