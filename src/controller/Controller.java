package controller;

import math.Vector2;

import static org.lwjgl.glfw.GLFW.*;

import java.util.*;

/**
 * Handles controller input
 * TODO: Make this a singleton??
 */

public class Controller {
    private Map<Integer, Boolean> keyPressed;
    private int mouseX;
    private int mouseY;
    private static Controller instance;

    private Controller() {
        keyPressed = new HashMap<>();
        instance = this;
    }

    public boolean keyPressed(int key) {
        return keyPressed.getOrDefault(key, false);
    }

    public int[] mousePosition() {
        return new int[] { mouseX, mouseY };
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
        Controller controller = Controller.getInstance();

        switch (action) {
            case GLFW_PRESS   -> controller.setKeyPressed(key, true);
            case GLFW_RELEASE -> controller.setKeyPressed(key, false);
        }
    }

    public static void onMousePositionCallback(long window, double mouseX, double mouseY) {
        Controller controller = Controller.getInstance();

        controller.mouseX = (int) mouseX;
        controller.mouseY = (int) mouseY;
    }
}
