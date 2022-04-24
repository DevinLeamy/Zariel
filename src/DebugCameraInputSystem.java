import ecs.ComponentRegistry;
import ecs.ComponentStore;
import ecs.Entity;
import math.Vector3;

import static org.lwjgl.glfw.GLFW.*;

public class DebugCameraInputSystem extends InstanceSystem {
    final private float cameraMovementSpeed = 30; // 1u / second
    final private float mouseSensitivity = 0.002f;

    World world = World.getInstance();
    Controller controller = Controller.getInstance();
    ComponentStore<DebugCameraConfig> debugCameraConfigStore = ComponentStore.of(DebugCameraConfig.class);

    private int[] mousePos;

    public DebugCameraInputSystem() {
        super(ComponentRegistry.getSignature(DebugCameraConfig.class), 0);

        this.mousePos = new int[] { 0, 0 };
    }

    @Override
    protected void update(float dt, Entity entity) {
        DebugCameraConfig config = debugCameraConfigStore.getComponent(entity).get();

        Camera camera = world.getPerspective();
        Vector3 cameraPosition = camera.transform.position;

        handleMouseUpdate(dt, controller.mousePosition(), config);

        Vector3 forward = Vector3.sub(camera.targetPosition, cameraPosition).normalize();
        Vector3 right = Vector3.cross(forward, Transform.up).normalize();

        handleKeyPresses(dt, camera.transform, forward, right);

        float pitch = config.pitch;
        float yaw = config.yaw;
        Vector3 target = calculateDirection(cameraPosition, yaw, pitch);
        camera.lookAt(target);
    }


    private Vector3 calculateDirection(Vector3 position, float yaw, float pitch) {
        Vector3 orientation = new Vector3(
                (float) Math.cos(yaw) * (float) Math.cos(pitch),
                (float) Math.sin(pitch),
                (float) Math.sin(yaw) * (float) Math.cos(pitch)
        ).normalize();
        return Vector3.add(position, orientation);
    }


    private void handleMouseUpdate(float dt, int[] newMousePos, DebugCameraConfig config) {
        if (mousePos[0] == 0 && newMousePos[0] != 0) {
            // initialize mouse position
            mousePos = newMousePos;
        }

        int dx = newMousePos[0] - mousePos[0];
        int dy = newMousePos[1] - mousePos[1];

        config.yaw += dx * mouseSensitivity;
        config.pitch += -dy * mouseSensitivity;
        config.pitch = Utils.clamp((float) -Math.PI / 2 + 0.01f, (float) Math.PI / 2 - 0.01f, config.pitch);

        mousePos = newMousePos;
    }

    private void handleKeyPresses(float dt, Transform transform, Vector3 forward, Vector3 right) {
        if (controller.keyPressed(GLFW_KEY_W)) { transform.translate(Vector3.scale(forward, dt * cameraMovementSpeed)); }
        if (controller.keyPressed(GLFW_KEY_S)) { transform.translate(Vector3.scale(forward, -dt * cameraMovementSpeed)); }
        // left and right
        if (controller.keyPressed(GLFW_KEY_A)) { transform.translate(Vector3.scale(right, -dt * cameraMovementSpeed)); }
        if (controller.keyPressed(GLFW_KEY_D)) { transform.translate(Vector3.scale(right, dt * cameraMovementSpeed)); }
        // up and down
        if (controller.keyPressed(GLFW_KEY_SPACE)) { transform.translate(Vector3.scale(Transform.up, dt * cameraMovementSpeed)); }
        if (controller.keyPressed(GLFW_KEY_LEFT_SHIFT)) { transform.translate(Vector3.scale(Transform.up, -dt * cameraMovementSpeed)); }
    }
}
