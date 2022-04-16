import math.Vector3;
import controller.Controller;
import math.Vector3i;

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
    private Vector3i previousSelection;

    public Player(Camera camera) {
        controller = Controller.getInstance();
        mousePos = new int[] { 0, 0 };
        wireframe = false;
        this.camera = camera;
        previousSelection = new Vector3i(0, 0, 0);
    }

    private void handleMouseUpdate(float dt, int[] newMousePos) {
        if (mousePos[0] == 0 && newMousePos[0] != 0) {
            // initialize mouse position
            mousePos = newMousePos;
        }

        int dx = newMousePos[0] - mousePos[0];
        int dy = newMousePos[1] - mousePos[1];

        camera.updateYaw(-dx * mouseSensitivity);
        camera.updatePitch(-dy * mouseSensitivity);

        mousePos = newMousePos;
    }


    /**
     * Highlight the block under your cursor.
     */
    private Optional<Vector3i> getSelectedBlock() {
        float MAX_SELECT_DISTANCE = 15.0f;
        World world = World.getInstance();

        Vector3 source = camera.position;
        Vector3 direction = camera.getForwardAxis();

        float dist = 0.0f;

        while (dist < MAX_SELECT_DISTANCE) {
            Vector3i pos = Vector3.add(source, Vector3.scale(direction, -dist)).toVector3i();

            if (world.blockIsActive(pos)) {
                return Optional.of(pos);
            }

            // increment by half of a block width
            dist += 0.5f;
        }

        return Optional.empty();
    }

    private ArrayList<Action> handleKeyPresses(float dt) {
        ArrayList<Action> updates = new ArrayList<>();

        // forwards and backwards
        if (controller.keyPressed(GLFW_KEY_W)) { camera.moveForward(dt * cameraMovementSpeed); }
        if (controller.keyPressed(GLFW_KEY_S)) { camera.moveForward(dt * -cameraMovementSpeed); }
        // left and right
        if (controller.keyPressed(GLFW_KEY_A)) { camera.moveRight(-dt * cameraMovementSpeed); }
        if (controller.keyPressed(GLFW_KEY_D)) { camera.moveRight(dt * cameraMovementSpeed); }
        // up and down
        if (controller.keyPressed(GLFW_KEY_SPACE)) { camera.moveUp(dt * cameraMovementSpeed); }
        if (controller.keyPressed(GLFW_KEY_LEFT_SHIFT)) { camera.moveUp(dt * -cameraMovementSpeed); }

        if (controller.keyPressed(GLFW_KEY_M)) {
            Optional<Vector3i> selected = getSelectedBlock();
            Block newBlock = new Block(false, BlockType.SAND);
            selected.ifPresent((Vector3i location) -> {
                System.out.println("HER");
                updates.add(new BlockUpdateAction(location, newBlock));

                // so that the action doesn't get undone
                previousSelection = new Vector3i(0, 0, 0);
            });
        }

        // wireframe
        if (controller.keyPressed(GLFW_KEY_P)) { wireframe = false; }
        if (controller.keyPressed(GLFW_KEY_O)) { wireframe = true; }
        glPolygonMode(GL_FRONT_AND_BACK, wireframe ? GL_LINE : GL_FILL);

        return updates;
    }

    public ArrayList<Action> update(float dt) {
        ArrayList<Action> updates = new ArrayList<>();

        handleMouseUpdate(dt, controller.mousePosition());
        updates.addAll(handleKeyPresses(dt));

        getSelectedBlock().ifPresent(selection -> {
            if (!selection.equals(previousSelection)) {
//                updates.add(new BlockUpdateAction(selection, new Block(true, BlockType.SAND)));
//                updates.add(new BlockUpdateAction(previousSelection, new Block(true, BlockType.GRASS)));
            }

            previousSelection = selection;
        });

        return updates;
    }
}
