import math.Vector3;
import controller.Controller;

import static org.lwjgl.glfw.GLFW.*;

public class Player {
    private Controller controller;
    private Camera camera;
    private int[] mousePos;
    final private float cameraMovementSpeed = 1; // 1u / second
    final private float cameraRotationSpeed = (float) Math.PI / 4; // 1u / second
    final private float mouseSensitivity = 0.01f;

    public Player(Camera camera) {
        controller = Controller.getInstance();
        mousePos = new int[] { -1, -1 };
        this.camera = camera;
    }

    private void handleMouseUpdate(float dt, int[] newMousePos) {
        if (mousePos[0] == -1) {
            // initialize mouse position
            mousePos = newMousePos;
            return;
        }

        int dx = newMousePos[0] - mousePos[0];
        int dy = newMousePos[1] - mousePos[1];

//        camera.updateYaw(dx * mouseSensitivity);
//        camera.updatePitch(dy * mouseSensitivity);

        mousePos = newMousePos;

//
        if (controller.keyPressed(GLFW_KEY_I)) { camera.updateYaw(cameraRotationSpeed * dt); }
        if (controller.keyPressed(GLFW_KEY_K)) { camera.updateYaw(-cameraRotationSpeed * dt); }

        if (controller.keyPressed(GLFW_KEY_O)) { camera.updatePitch(cameraRotationSpeed * dt); }
        if (controller.keyPressed(GLFW_KEY_L)) { camera.updatePitch(-cameraRotationSpeed * dt); }
    }

    private void handleKeyPresses(float dt) {
        // forwards and backwards
        if (controller.keyPressed(GLFW_KEY_W)) { camera.moveForward(dt * -cameraMovementSpeed); }
        if (controller.keyPressed(GLFW_KEY_S)) { camera.moveForward(dt * cameraMovementSpeed); }
        // left and right
        if (controller.keyPressed(GLFW_KEY_A)) { camera.moveRight(dt * -cameraMovementSpeed); }
        if (controller.keyPressed(GLFW_KEY_D)) { camera.moveRight(dt * cameraMovementSpeed); }
    }

    public void update(float dt) {
        handleMouseUpdate(dt, controller.mousePosition());
        handleKeyPresses(dt);
    }
}
