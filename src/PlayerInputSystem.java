import ecs.*;
import math.Vector3;
import math.Vector3i;

import java.util.ArrayList;
import java.util.Optional;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_O;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_FILL;

public class PlayerInputSystem extends InstanceSystem {
    private static Controller controller = Controller.getInstance();
    ComponentStore<Transform> transformStore = ComponentStore.of(Transform.class);

    final private float cameraMovementSpeed = 10; // 1u / second
    final private float cameraRotationSpeed = (float) Math.PI / 4; // 1u / second
    final private float mouseSensitivity = 0.002f;

    private int[] mousePos;
    private boolean wireframe;

    public PlayerInputSystem() {
        super(ComponentRegistry.getSignature(Transform.class, PlayerTag.class, GravityTag.class, CameraTarget.class), 0);

        controller = Controller.getInstance();
        mousePos = new int[] { 0, 0 };
        wireframe = false;
    }


    @Override
    protected void update(float dt, Entity entity) {
        PlayerTag playerTag = entity.getComponent(PlayerTag.class).get();
        Transform playerTransform = transformStore.getComponent(entity).get();
        CameraTarget target = entity.getComponent(CameraTarget.class).get();
        GravityTag gravityTag = entity.getComponent(GravityTag.class).get();

        playerTag.previousTransform = new Transform(
                playerTransform.position.clone(),
                playerTransform.rotation.clone(),
                playerTransform.scale.clone()
        );

        handleMouseUpdate(playerTransform, target, dt, controller.mousePosition());
        handleKeyPresses(dt, playerTransform, gravityTag.falling);
    }


    private void handleMouseUpdate(Transform transform, CameraTarget target, float dt, int[] newMousePos) {
        if (mousePos[0] == 0 && newMousePos[0] != 0) {
            // initialize mouse position
            mousePos = newMousePos;
        }

        int dx = newMousePos[0] - mousePos[0];
        int dy = newMousePos[1] - mousePos[1];

        transform.rotate(new Vector3(0, dx * mouseSensitivity, 0));
        target.targetOffset.y += dy * mouseSensitivity * 3;
        target.targetOffset.y = Utils.clamp(0, 25, target.targetOffset.y);

        mousePos = newMousePos;
    }

    /**
     * Highlight the block under your cursor.
     */
    private Optional<Vector3i> getSelectedBlock(Transform transform) {
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

    private ArrayList<Action> handleKeyPresses(float dt, Transform transform, boolean falling) {
        ArrayList<Action> updates = new ArrayList<>();

        // forwards and backwards
        if (controller.keyPressed(GLFW_KEY_W)) { transform.translate(Vector3.scale(transform.direction(), dt * cameraMovementSpeed)); }
        if (controller.keyPressed(GLFW_KEY_S)) { transform.translate(Vector3.scale(transform.direction(), -dt * cameraMovementSpeed)); }
        // left and right
        if (controller.keyPressed(GLFW_KEY_A)) { transform.translate(Vector3.scale(transform.right(), -dt * cameraMovementSpeed)); }
        if (controller.keyPressed(GLFW_KEY_D)) { transform.translate(Vector3.scale(transform.right(), dt * cameraMovementSpeed)); }

        if (!falling) {
            if (controller.keyPressed(GLFW_KEY_SPACE)) { transform.translate(Vector3.scale(Transform.up, 1.0f)); }
        }

        if (controller.takeMouseButtonState(GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
            Entity bomb = new Entity();
            bomb.addComponent(new Transform(
                Vector3.add(transform.direction(), transform.position).add(Transform.up).add(Transform.up),
                new Vector3(0, transform.rotation.y, 0),
                new Vector3(1/12f, 1/12f, 1/12f)
            ));
            bomb.addComponent(new GravityTag());
            bomb.addComponent(new VoxelModel(VoxelGeometry.loadFromFile("res/voxels/bomb.vox").voxels));
            bomb.addComponent(new Dynamics(
                    transform.direction().normalize().scale(10),
                    Vector3.zeros()
            ));
            bomb.addComponent(new RigidBody(
                    new BoundingBox(1, 1, 1),
                    "BOMB"
            ));

            world.entityManager.addEntity(bomb);
        }

        if (controller.keyDown(GLFW_KEY_M)) {
            Optional<Vector3i> selected = getSelectedBlock(transform);
            Block newBlock = new Block(false, BlockType.RED);
            selected.ifPresent((Vector3i location) -> {
//                updates.add(new BlockUpdateAction(location, newBlock));
            });
        }

        // TODO: move this elsewhere
        // wireframe
        if (controller.keyPressed(GLFW_KEY_P)) { wireframe = false; }
        if (controller.keyPressed(GLFW_KEY_O)) { wireframe = true; }
        glPolygonMode(GL_FRONT_AND_BACK, wireframe ? GL_LINE : GL_FILL);

        return updates;
    }
}
