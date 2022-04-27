package engine.main;

import engine.controller.Controller;
import engine.ecs.ComponentRegistry;
import engine.ecs.ComponentStore;
import engine.ecs.Entity;
import engine.ecs.InstanceSystem;
import math.Vector3;
import math.Vector3i;
import util.Utils;

import java.util.Optional;

import static org.lwjgl.glfw.GLFW.*;

/**
 * TODO: Remove the "state" from this system.
 * Systems should be loosely "stateless".
 */
public class DebugCameraInputSystem extends InstanceSystem {
    final private float cameraMovementSpeed = 15; // 1u / second
    final private float mouseSensitivity = 0.002f;

    World world = World.getInstance();
    Controller controller = Controller.getInstance();
    ComponentStore<DebugCameraConfig> debugCameraConfigStore = ComponentStore.of(DebugCameraConfig.class);

    private int[] mousePos;
    private Block previouslySelectedBlock;
    private Runnable undoPreviousSelection;

    public DebugCameraInputSystem() {
        super(ComponentRegistry.getSignature(DebugCameraConfig.class), 0);

        this.mousePos = new int[] { 0, 0 };
        this.undoPreviousSelection = () -> {};
    }

    @Override
    protected void update(float dt, Entity entity) {
        DebugCameraConfig config = debugCameraConfigStore.getComponent(entity).get();

        Camera camera = world.getPerspective();
        Vector3 cameraPosition = camera.transform.position;

        Vector3 forward = Vector3.sub(camera.targetPosition, cameraPosition).normalize();
        Vector3 right = Vector3.cross(forward, Transform.up).normalize();

        handleKeyPresses(dt, camera.transform, forward, right);
        handleMouseUpdate(dt, controller.mousePosition(), config);

        Vector3 newTarget = calculateDirection(cameraPosition, config.yaw, config.pitch);
        camera.lookAt(newTarget);


        // -z axis
        forward = Vector3.sub(newTarget, cameraPosition).normalize();
        Optional<Block> maybeBlock = getSelectedBlock(cameraPosition, forward);

        maybeBlock.ifPresent(selectedBlock -> {
            if (previouslySelectedBlock != selectedBlock) {
                this.undoPreviousSelection.run();
                this.undoPreviousSelection = new Runnable() {
                    // undo selectedBlock selection, because it will become the previous selection
                    private Block previouslySelectedBlock = selectedBlock;
                    private BlockType oldBlockType = selectedBlock.getBlockType();

                    @Override
                    public void run() {
                        previouslySelectedBlock.setBlockType(oldBlockType);
                    }
                };
            }

            selectedBlock.setBlockType(BlockType.SNOW);
            handleMouseButtons(selectedBlock);

            previouslySelectedBlock = selectedBlock;
        });

    }

    private void handleMouseButtons(Block selectedBlock) {
        if (controller.takeMouseButtonState(GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
            selectedBlock.setActive(false);
        }
    }


    private Vector3 calculateDirection(Vector3 position, float yaw, float pitch) {
        Vector3 orientation = new Vector3(
                (float) Math.cos(yaw) * (float) Math.cos(pitch),
                (float) Math.sin(pitch),
                (float) Math.sin(yaw) * (float) Math.cos(pitch)
        ).normalize();
        return Vector3.add(position, orientation);
    }

    /**
     * Highlight the block under your cursor.
     */
    private Optional<Block> getSelectedBlock(Vector3 source, Vector3 forward) {
        float MAX_SELECT_DISTANCE = 15.0f;

        float dist = 0.0f;

        while (dist < MAX_SELECT_DISTANCE) {
            Vector3i pos = Vector3.add(source, Vector3.scale(forward, dist)).toVector3i();

            if (world.blockIsActive(pos)) {
                System.out.println("Selected block: " + pos);
                return world.getBlock(pos);
            }

            // increment by half of a block width
            dist += 0.5f;
        }

        return Optional.empty();
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
