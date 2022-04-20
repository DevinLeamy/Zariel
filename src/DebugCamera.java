import math.Vector3;
import math.Vector3i;

import java.util.ArrayList;
import java.util.Optional;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_O;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_FILL;

public class DebugCamera {
    final private float cameraMovementSpeed = 10; // 1u / second
    final private float cameraRotationSpeed = (float) Math.PI / 4; // 1u / second
    final private float mouseSensitivity = 0.002f;
    final private float scrollSensitivity = 0.03f;

    private Controller controller;
    private Camera camera;
    private int[] mousePos;
    private boolean wireframe;

    public DebugCamera() {
        this.controller = Controller.getInstance();
        this.camera = new Camera(
                (float) Math.PI - (float) Math.PI / 2,
                World.getInstance().window.getAspectRatio(),
                new Vector3(0, 17 + 6.0f, 0)
        );
        mousePos = new int[] { 0, 0 };
        wireframe = false;
    }

    private void handleScrollUpdate(float scrollDelta) {
        camera.fov += scrollDelta * scrollSensitivity;
        camera.fov = Utils.clamp(0.1f * (float) Math.PI, 0.7f * (float) Math.PI, camera.fov);
    }

    public void update(float dt) {
        handleMouseUpdate(dt, controller.mousePosition());
        handleScrollUpdate(controller.pollScrollDelta());
        handleKeyPresses(dt);
    }

    public Camera getPerspective() {
        return camera;
    }

    private void handleMouseUpdate(float dt, int[] newMousePos) {
        if (mousePos[0] == 0 && newMousePos[0] != 0) {
            // initialize mouse position
            mousePos = newMousePos;
        }

        int dx = newMousePos[0] - mousePos[0];
        int dy = newMousePos[1] - mousePos[1];

        camera.transform.rotate(new Vector3(0, dx * mouseSensitivity, 0));
//        camera.transform.rotate(new Vector3(-dy * mouseSensitivity);

        mousePos = newMousePos;
    }

    private ArrayList<Action> handleKeyPresses(float dt) {
        ArrayList<Action> updates = new ArrayList<>();
        Transform transform = camera.transform;

        if (controller.keyPressed(GLFW_KEY_W)) { transform.translate(Vector3.scale(transform.direction(), dt * cameraMovementSpeed)); }
        if (controller.keyPressed(GLFW_KEY_S)) { transform.translate(Vector3.scale(transform.direction(), -dt * cameraMovementSpeed)); }
        // left and right
        if (controller.keyPressed(GLFW_KEY_A)) { transform.translate(Vector3.scale(transform.right(), -dt * cameraMovementSpeed)); }
        if (controller.keyPressed(GLFW_KEY_D)) { transform.translate(Vector3.scale(transform.right(), dt * cameraMovementSpeed)); }
        // up and down
        if (controller.keyPressed(GLFW_KEY_SPACE)) { transform.translate(Vector3.scale(Transform.up, dt * cameraMovementSpeed)); }
        if (controller.keyPressed(GLFW_KEY_LEFT_SHIFT)) { transform.translate(Vector3.scale(Transform.up, -dt * cameraMovementSpeed)); }

        // TODO: move this elsewhere
        // wireframe
        if (controller.keyPressed(GLFW_KEY_P)) { wireframe = false; }
        if (controller.keyPressed(GLFW_KEY_O)) { wireframe = true; }
        glPolygonMode(GL_FRONT_AND_BACK, wireframe ? GL_LINE : GL_FILL);

        return updates;
    }
}
