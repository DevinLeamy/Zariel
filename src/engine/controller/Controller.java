package engine.controller;

import static org.lwjgl.glfw.GLFW.*;

import java.util.*;

/**
 * Handles controller input
 * TODO: Make this a singleton??
 */

public class Controller {
    private Map<Integer, Integer> keyPressed;
    private Map<Integer, Integer> mouseButtonState;
    private int mouseX;
    private int mouseY;
    private float scrollDelta;
    private static Controller instance;

    private Controller() {
        keyPressed = new HashMap<>();
        mouseButtonState = new HashMap<>();
        instance = this;
    }

    public boolean keyPressed(int key) {
        return keyPressed.getOrDefault(key, GLFW_RELEASE) == GLFW_PRESS || keyPressed.getOrDefault(key, GLFW_RELEASE) == GLFW_REPEAT;
    }
    // TODO: swap keyPressed and keyDown

    public boolean keyDown(int key) {
        return keyPressed.getOrDefault(key, -1) == GLFW_PRESS;
    }

    public boolean mouseButtonPressed(int key) {
        return mouseButtonState.getOrDefault(key, -1) == GLFW_PRESS;
    }

    public float pollScrollDelta() {
        float scrollDelta = this.scrollDelta;
        // reset delta
        this.scrollDelta = 0.0f;

        return scrollDelta;
    }

    public int takeKeyPressState(int key) {
        int state = keyPressed.getOrDefault(key, -1);
        keyPressed.remove(key);

        return state;
    }

    public int takeMouseButtonState(int key) {
        int state = mouseButtonState.getOrDefault(key, -1);
        mouseButtonState.remove(key);

        return state;
    }

    public int[] mousePosition() {
        return new int[] { mouseX, mouseY };
    }

    public static Controller getInstance() {
        if (Controller.instance == null) {
            Controller.instance = new Controller();
        }

        return Controller.instance;
    }

    private void setKeyState(int key, int action) {
        keyPressed.put(key, action);
    }

    private void setMouseButtonState(int key, int action) {
        mouseButtonState.put(key, action);
    }

    public static void onKeyPressedCallback(long window, int key, int scancode, int action, int mods) {
        Controller controller = Controller.getInstance();

        switch (action) {
            case GLFW_PRESS  -> controller.setKeyState(key, GLFW_PRESS);
            case GLFW_REPEAT -> controller.setKeyState(key, GLFW_REPEAT);
            case GLFW_RELEASE -> controller.setKeyState(key, GLFW_RELEASE);
        }
    }

    public static void onMouseButtonCallback(long window, int button, int action, int mods) {
        Controller controller = Controller.getInstance();

        switch (action) {
            case GLFW_PRESS   -> controller.setMouseButtonState(button, GLFW_PRESS);
            case GLFW_RELEASE -> controller.setMouseButtonState(button, GLFW_RELEASE);
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
