package controller;

import static org.lwjgl.glfw.GLFW.*;

import java.util.*;

/**
 * Handles controller input
 * TODO: Make this a singleton??
 */

public class Controller {
    private Map<Integer, Boolean> keyPressed;
    private static Controller instance;

    private Controller() {
        keyPressed = new HashMap<>();
        instance = this;
    }

    public boolean keyPressed(int key) {
        return keyPressed.getOrDefault(key, false);
    }

    private void setKeyPressed(int key, boolean pressed) {
        keyPressed.put(key, pressed);
    }

    public static Controller getInstance() {
        if (Controller.instance == null) {
            Controller.instance = new Controller();
        }

        return Controller.instance;
    }

    public static void onKeyPressedCallback(long window, int key, int scancode, int action, int mods) {
        Controller controller = Controller.instance;

        if (controller == null) {
            System.err.println("Error: Controller has not been initialized");
            return;
        }

        switch (action) {
            case GLFW_PRESS   -> controller.setKeyPressed(key, true);
            case GLFW_RELEASE -> controller.setKeyPressed(key, false);
        }
    }
}
