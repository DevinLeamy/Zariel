package engine.controller;

import engine.main.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Handles controller input
 */

public class Controller {
    static private Map<Integer, Integer> keyPressed = new HashMap<>();
    static private Map<Integer, Integer> mouseButtonState = new HashMap<>();
    static private ArrayList<Listener> listeners = new ArrayList<>();
    static private int mouseX;
    static private int mouseY;
    static private float scrollDelta;

    public static void addListener(Listener listener) {
        listeners.add(listener);
    }

    public static boolean keyPressed(int key) {
        return keyPressed.getOrDefault(key, GLFW_RELEASE) == GLFW_PRESS || keyPressed.getOrDefault(key, GLFW_RELEASE) == GLFW_REPEAT;
    }

    // TODO: swap keyPressed and keyDown
    public static boolean keyDown(int key) {
        return keyPressed.getOrDefault(key, -1) == GLFW_PRESS;
    }

    public static boolean mouseButtonPressed(int key) {
        return mouseButtonState.getOrDefault(key, -1) == GLFW_PRESS;
    }

    public static float pollScrollDelta() {
        float prevScrollDelta = scrollDelta;
        // reset delta
        scrollDelta = 0.0f;

        return prevScrollDelta;
    }

    public static int takeKeyPressState(int key) {
        int state = keyPressed.getOrDefault(key, -1);
        keyPressed.remove(key);

        return state;
    }

    public static int takeMouseButtonState(int key) {
        int state = mouseButtonState.getOrDefault(key, -1);
        mouseButtonState.remove(key);

        return state;
    }

    public static int[] mousePosition() {
        return new int[] { mouseX, mouseY };
    }

    private static void setKeyState(int key, int action) {
        keyPressed.put(key, action);
    }

    private static void setMouseButtonState(int key, int action) {
        mouseButtonState.put(key, action);
    }

    public static void onKeyPressedCallback(long window, int key, int scancode, int action, int mods) {
        switch (action) {
            case GLFW_PRESS  ->  setKeyState(key, GLFW_PRESS);
            case GLFW_REPEAT ->  setKeyState(key, GLFW_REPEAT);
            case GLFW_RELEASE -> setKeyState(key, GLFW_RELEASE);
        }
    }

    public static void onMouseButtonCallback(long window, int button, int action, int mods) {
        switch (action) {
            case GLFW_PRESS   -> setMouseButtonState(button, GLFW_PRESS);
            case GLFW_RELEASE -> setMouseButtonState(button, GLFW_RELEASE);
        }
        for (Listener listener : listeners) {
            listener.update();
        }
    }

    public static void onMousePositionCallback(long window, double mouseX, double mouseY) {
        Controller.mouseX = (int) mouseX;
        Controller.mouseY = (int) mouseY;
    }

    public static void onScrollCallback(long window, double scrollXDelta, double scrollYDelta) {
        scrollDelta = (float) scrollYDelta;
    }
}
