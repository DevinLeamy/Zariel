import math.Vector3;
import controller.Controller;

import static org.lwjgl.glfw.GLFW.*;

public class Player {
    private Controller controller;
    private Camera camera;
    private int[] mousePos;
    final private float cameraMovementSpeed = 2; // 1u / second
    final private float cameraRotationSpeed = (float) Math.PI / 4; // 1u / second
    final private float mouseSensitivity = 0.002f;

    public Player(Camera camera) {
        controller = Controller.getInstance();
        mousePos = new int[] { 0, 0 };
        this.camera = camera;
    }

    private void handleMouseUpdate(float dt, int[] newMousePos) {
        if (mousePos[0] == 0 && newMousePos[0] != 0) {
            // initialize mouse position
            mousePos = newMousePos;
        }

        int dx = newMousePos[0] - mousePos[0];
        int dy = newMousePos[1] - mousePos[1];

        camera.updateYaw(dx * mouseSensitivity);
        camera.updatePitch(-dy * mouseSensitivity);

        mousePos = newMousePos;
    }

    private void handleKeyPresses(float dt) {
        // forwards and backwards
        if (controller.keyPressed(GLFW_KEY_W)) { camera.moveForward(dt * -cameraMovementSpeed); }
        if (controller.keyPressed(GLFW_KEY_S)) { camera.moveForward(dt * cameraMovementSpeed); }
        // left and right
        if (controller.keyPressed(GLFW_KEY_A)) { camera.moveLeft(dt * cameraMovementSpeed); }
        if (controller.keyPressed(GLFW_KEY_D)) { camera.moveLeft(dt * -cameraMovementSpeed); }
    }

    public void update(float dt) {
        handleMouseUpdate(dt, controller.mousePosition());
        handleKeyPresses(dt);
    }
}
