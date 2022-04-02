import math.Vector3;
import controller.Controller;

import static org.lwjgl.glfw.GLFW.*;

/**
 * TODO: Make this an abstract class??
 * Then we could abstract away the camera and controller initialization
 * and the controller reading for subclasses, perhaps
 * TODO: Separate camera from Model?? Make a _Renderable_??
 */
public class Player {
    private Controller controller;
    private Camera camera;
    final private float cameraMovementSpeed = 1; // 1u / second
    final private float cameraRotationSpeed = (float) Math.PI / 4; // 1u / second

    public Player(Camera camera) {
        controller = Controller.getInstance();
        this.camera = camera;
    }

    public void update(float dt) {
        if (controller.keyPressed(GLFW_KEY_W)) { camera.moveForward(dt * cameraMovementSpeed); }
        if (controller.keyPressed(GLFW_KEY_S)) { camera.moveForward(dt * -cameraMovementSpeed); }

        if (controller.keyPressed(GLFW_KEY_A)) { camera.moveRight(dt * -cameraMovementSpeed); }
        if (controller.keyPressed(GLFW_KEY_D)) { camera.moveRight(dt * cameraMovementSpeed); }

        if (controller.keyPressed(GLFW_KEY_LEFT_SHIFT)) { camera.moveUp(dt * -cameraMovementSpeed); }
        if (controller.keyPressed(GLFW_KEY_SPACE)) { camera.moveUp(dt * cameraMovementSpeed); }
    }
}
