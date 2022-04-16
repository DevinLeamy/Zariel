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
    private float scrollDelta;
    private static Controller instance;

    private Controller() {
        keyPressed = new HashMap<>();
        instance = this;
    }

    public boolean keyPressed(int key) {
        return keyPressed.getOrDefault(key, false);
    }

    public float pollScrollDelta() {
        float scrollDelta = this.scrollDelta;
        // reset delta
        this.scrollDelta = 0.0f;

        return scrollDelta;
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
            case GLFW_PRESS   -> controller.setKeyPressed(key, glfwGetKey(window, key) == GLFW_PRESS);
            case GLFW_RELEASE -> controller.setKeyPressed(key, glfwGetKey(window, key) != GLFW_RELEASE);
        }
    }

    public static void onMousePositionCallback(long window, double mouseX, double mouseY) {
        Controller controller = Controller.getInstance();

        controller.mouseX = (int) mouseX;
        controller.mouseY = (int) mouseY;
    }

    public static void onScrollCallback(long window, double scrollXDelta, double scrollYDelta) {
        Controller controller = Controller.getInstance();

        controller.scrollDelta = (float) scrollYDelta;
    }
}
