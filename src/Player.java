import math.Vector3;
import controller.Controller;

import java.util.ArrayList;
import java.util.Optional;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL41.*;

public class Player {
    private Controller controller;
    private Camera camera;
    private int[] mousePos;
    private boolean wireframe;
    final private float cameraMovementSpeed = 6; // 1u / second
    final private float cameraRotationSpeed = (float) Math.PI / 4; // 1u / second
    final private float mouseSensitivity = 0.002f;

    public Player(Camera camera) {
        controller = Controller.getInstance();
        mousePos = new int[] { 0, 0 };
        wireframe = false;
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

    /**
     * Change the color of the currently selected block
     */
    private Optional<Action> pick() {
        float MAX_PICK_DISTANCE = 15.0f;
        World world = World.getInstance();

        Vector3 source = camera.position;
        Vector3 direction = camera.getForwardAxis();

        float dist = 0.0f;

        while (dist < MAX_PICK_DISTANCE) {
            Vector3 pos = Vector3.add(source, Vector3.scale(direction, dist));
            int x = (int) pos.x, y = (int) pos.y, z = (int) pos.z;

            if (world.blockIsActive(x, y, z)) {
                return Optional.of(new SelectAction(x, y, z));
            }

            // increment by half of a block width
            dist += 0.5f;
        }

        return Optional.empty();
    }

    private void handleKeyPresses(float dt) {
        // forwards and backwards
        if (controller.keyPressed(GLFW_KEY_W)) { camera.moveForward(dt * -cameraMovementSpeed); }
        if (controller.keyPressed(GLFW_KEY_S)) { camera.moveForward(dt * cameraMovementSpeed); }
        // left and right
        if (controller.keyPressed(GLFW_KEY_A)) { camera.moveLeft(dt * cameraMovementSpeed); }
        if (controller.keyPressed(GLFW_KEY_D)) { camera.moveLeft(dt * -cameraMovementSpeed); }
        // up and down
        if (controller.keyPressed(GLFW_KEY_SPACE)) { camera.moveUp(dt * cameraMovementSpeed); }
        if (controller.keyPressed(GLFW_KEY_LEFT_SHIFT)) { camera.moveUp(dt * -cameraMovementSpeed); }

        // wireframe
        if (controller.keyPressed(GLFW_KEY_P)) { wireframe = false; }
        if (controller.keyPressed(GLFW_KEY_O)) { wireframe = true; }
        glPolygonMode(GL_FRONT_AND_BACK, wireframe ? GL_LINE : GL_FILL);
    }

    public ArrayList<Action> update(float dt) {
        ArrayList<Action> updates = new ArrayList<>();
        handleMouseUpdate(dt, controller.mousePosition());
        handleKeyPresses(dt);

        pick().ifPresent(updates::add);

        return updates;
    }
}
