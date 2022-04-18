import math.Vector3;
import controller.Controller;
import math.Vector3i;

import java.util.ArrayList;
import java.util.Optional;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL41.*;

public class Player {
    final private float CAMERA_OFFSET_BACK = 5;
    final private float CAMERA_OFFSET_UP = 5;

    final private float cameraMovementSpeed = 6; // 1u / second
    final private float cameraRotationSpeed = (float) Math.PI / 4; // 1u / second
    final private float mouseSensitivity = 0.002f;
    final private float scrollSensitivity = 0.03f;
    private Vector3i previousSelection;

    private Controller controller;
    public Transform transform;
    public VoxelMesh mesh;
    private Camera camera;
    private int[] mousePos;
    private boolean wireframe;


    public Player(Vector3 position, Camera camera) {
        this.transform = new Transform(position);
        this.camera = camera;

        controller = Controller.getInstance();
        mousePos = new int[] { 0, 0 };
        wireframe = false;
        previousSelection = new Vector3i(0, 0, 0);
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

        transform.updateYaw(-dx * mouseSensitivity);
        transform.updatePitch(-dy * mouseSensitivity);

        mousePos = newMousePos;
    }

    /**
     * Highlight the block under your cursor.
     */
    private Optional<Vector3i> getSelectedBlock() {
        float MAX_SELECT_DISTANCE = 15.0f;
        World world = World.getInstance();

        Vector3 source = camera.transform.position;
        Vector3 direction = camera.transform.getForwardAxis();

        float dist = 0.0f;

        while (dist < MAX_SELECT_DISTANCE) {
            Vector3i pos = Vector3.add(source, Vector3.scale(direction, dist)).toVector3i();

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
        if (controller.keyPressed(GLFW_KEY_W)) { transform.moveForward(dt * cameraMovementSpeed); }
        if (controller.keyPressed(GLFW_KEY_S)) { transform.moveForward(dt * -cameraMovementSpeed); }
        // left and right
        if (controller.keyPressed(GLFW_KEY_A)) { transform.moveRight(-dt * cameraMovementSpeed); }
        if (controller.keyPressed(GLFW_KEY_D)) { transform.moveRight(dt * cameraMovementSpeed); }
        // up and down
        if (controller.keyPressed(GLFW_KEY_SPACE)) { transform.moveUp(dt * cameraMovementSpeed); }
        if (controller.keyPressed(GLFW_KEY_LEFT_SHIFT)) { transform.moveUp(dt * -cameraMovementSpeed); }

        if (controller.keyPressed(GLFW_KEY_M)) {
            Optional<Vector3i> selected = getSelectedBlock();
            Block newBlock = new Block(false, BlockType.SAND);
            selected.ifPresent((Vector3i location) -> {
                updates.add(new BlockUpdateAction(location, newBlock));

//                 so that the action doesn't get undone
                previousSelection = new Vector3i(0, 0, 0);
            });
        }

        // TODO: move this elsewhere
        // wireframe
        if (controller.keyPressed(GLFW_KEY_P)) { wireframe = false; }
        if (controller.keyPressed(GLFW_KEY_O)) { wireframe = true; }
        glPolygonMode(GL_FRONT_AND_BACK, wireframe ? GL_LINE : GL_FILL);

        return updates;
    }

    // TODO: move this into the camera class
    private void handleScrollUpdate(float scrollDelta) {
        camera.fov += scrollDelta * scrollSensitivity;
        camera.fov = Utils.clamp(0.1f * (float) Math.PI, 0.7f * (float) Math.PI, camera.fov);
    }

    public ArrayList<Action> update(float dt) {
        ArrayList<Action> updates = new ArrayList<>();

        // update camera position
        camera.transform.position = transform.position.clone();
        Vector3 offsetBack = Vector3.scale(transform.getForwardAxis(), -CAMERA_OFFSET_BACK);
        Vector3 offsetUp = Vector3.scale(transform.up, CAMERA_OFFSET_UP);

        camera.transform.translate(offsetUp);
        camera.transform.translate(offsetBack);

        handleMouseUpdate(dt, controller.mousePosition());
        handleScrollUpdate(controller.pollScrollDelta());
        updates.addAll(handleKeyPresses(dt));

        getSelectedBlock().ifPresent(selection -> {
//            if (!selection.equals(previousSelection)) {
//                updates.add(new BlockUpdateAction(selection, new Block(true, BlockType.SAND)));
//                updates.add(new BlockUpdateAction(previousSelection, new Block(true, BlockType.GRASS)));
//            }
            previousSelection = selection;
        });



        return updates;
    }
}
