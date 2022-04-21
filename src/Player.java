import math.Matrix4;
import math.Vector3;
import math.Vector3i;

import java.util.ArrayList;
import java.util.Optional;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL41.*;

public class Player extends VoxelRenderable {
    final private float CAMERA_OFFSET_BACK = 5;
    private float CAMERA_OFFSET_UP = 5;

    final private float cameraMovementSpeed = 20; // 1u / second
    final private float cameraRotationSpeed = (float) Math.PI / 4; // 1u / second
    final private float mouseSensitivity = 0.002f;
    final private float scrollSensitivity = 0.03f;
    private Vector3i previousSelection;

    private Controller controller;
    private Camera camera;
    private int[] mousePos;
    private boolean wireframe;

    private RigidBody rigidBody;

    public Player(Transform transform, VoxelGeometry shape, Camera camera) {
        super(transform, shape);

        this.camera = camera;
        this.rigidBody = new RigidBody(Vector3.zeros(), Vector3.zeros(), new BoundingBox(transform.position, 1, 3, 1));
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

        transform.rotate(new Vector3(0, dx * mouseSensitivity, 0));
        CAMERA_OFFSET_UP += dy * mouseSensitivity * 3;
        CAMERA_OFFSET_UP = Utils.clamp(0, 25, CAMERA_OFFSET_UP);

        mousePos = newMousePos;
    }

    /**
     * Highlight the block under your cursor.
     */
    private Optional<Vector3i> getSelectedBlock() {
        float MAX_SELECT_DISTANCE = 15.0f;
        World world = World.getInstance();
        Vector3 source = transform.position;
        Vector3 direction = transform.direction();

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
        if (controller.keyPressed(GLFW_KEY_W)) { transform.translate(Vector3.scale(transform.direction(), dt * cameraMovementSpeed)); }
        if (controller.keyPressed(GLFW_KEY_S)) { transform.translate(Vector3.scale(transform.direction(), -dt * cameraMovementSpeed)); }
        // left and right
        if (controller.keyPressed(GLFW_KEY_A)) { transform.translate(Vector3.scale(transform.right(), -dt * cameraMovementSpeed)); }
        if (controller.keyPressed(GLFW_KEY_D)) { transform.translate(Vector3.scale(transform.right(), dt * cameraMovementSpeed)); }

        if (controller.keyPressed(GLFW_KEY_SPACE)) { transform.translate(Vector3.scale(Transform.up, 1.0f)); }

        if (controller.takeMouseButtonState(GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
            Vector3 bulletPosition = Vector3.add(transform.direction(), transform.position).add(Transform.up).add(Transform.up);
            Transform bulletTransform = new Transform(
                    bulletPosition,
                    new Vector3(0, transform.rotation.y, 0),
                    new Vector3(1/12f, 1/12f, 1/12f)
            );
            VoxelRenderable bullet = new Bullet(bulletTransform);
            SpawnGameObjectAction spawnBullet = new SpawnGameObjectAction(bullet);
            updates.add(spawnBullet);
        }

        if (controller.keyDown(GLFW_KEY_M)) {
            Optional<Vector3i> selected = getSelectedBlock();
            Block newBlock = new Block(false, BlockType.RED);
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

    @Override
    public ArrayList<Action> update(float dt) {
        ArrayList<Action> updates = new ArrayList<>();

        handleMouseUpdate(dt, controller.mousePosition());
        handleScrollUpdate(controller.pollScrollDelta());
        updates.addAll(handleKeyPresses(dt));

        transform.position = rigidBody.update(dt, transform.position);

        // update camera position
        camera.transform.position = transform.position.clone();
        Vector3 offsetBack = Vector3.scale(transform.direction(), -CAMERA_OFFSET_BACK);
        offsetBack.y = 0;
        Vector3 offsetUp = Vector3.scale(Transform.up, CAMERA_OFFSET_UP);

        camera.transform.translate(offsetUp);
        camera.transform.translate(offsetBack);

        return updates;
    }
}
