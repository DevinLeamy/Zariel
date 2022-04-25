import ecs.*;
import math.Vector3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_O;
import static org.lwjgl.opengl.GL41.*;

public class PlayerInputSystem extends InstanceSystem {
    private static Controller controller = Controller.getInstance();
    ComponentStore<Transform> transformStore = ComponentStore.of(Transform.class);

    final private float cameraMovementSpeed = 15; // 1u / second
    final private float mouseSensitivity = 0.002f;
    final private float turnSpeed = 2.0f;

    private int[] mousePos;
    private boolean wireframe;

    public PlayerInputSystem() {
        super(ComponentRegistry.getSignature(CarDynamics.class, Transform.class, PlayerTag.class, GravityTag.class, CameraTarget.class), 0);

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
        CarDynamics carDynamics = entity.getComponent(CarDynamics.class).get();

        playerTag.previousTransform = new Transform(
                playerTransform.position.clone(),
                playerTransform.rotation.clone(),
                playerTransform.scale.clone()
        );

        handleMouseUpdate(target, dt, controller.mousePosition());
        handleKeyPresses(dt, carDynamics, playerTransform, gravityTag.falling);
    }


    private void handleMouseUpdate(CameraTarget target, float dt, int[] newMousePos) {
        if (mousePos[0] == 0 && newMousePos[0] != 0) {
            // initialize mouse position
            mousePos = newMousePos;
        }

        int dy = newMousePos[1] - mousePos[1];

        target.targetOffset.y += dy * mouseSensitivity * 3;
        target.targetOffset.y = Utils.clamp(0, 25, target.targetOffset.y);

        mousePos = newMousePos;
    }

    // TODO: Don't allow braking if the car has a speed of 0
    private void brake(float dt, CarDynamics carDynamics) {
        carDynamics.engineForce = dt * -carDynamics.brakePower;
    }

    private void pushGas(float dt, CarDynamics carDynamics) {
        carDynamics.engineForce = dt * carDynamics.speed;
    }

    private void turn(float mag, Transform transform) {
        transform.rotate(new Vector3(0, mag, 0));
    }

    private void turnLeft(float dt, Transform transform) {
        turn(-dt * turnSpeed, transform);
    }

    private void turnRight(float dt, Transform transform) {
        turn(dt * turnSpeed, transform);
    }

    private void handleKeyPresses(float dt, CarDynamics carDynamics, Transform transform, boolean falling) {
        Map<Integer, Runnable> controls = new HashMap<>();
        controls.put(GLFW_KEY_W, () -> pushGas(dt, carDynamics));
        controls.put(GLFW_KEY_S, () -> brake(dt, carDynamics));
        controls.put(GLFW_KEY_A, () -> turnLeft(dt, transform));
        controls.put(GLFW_KEY_D, () -> turnRight(dt, transform));

        for (int key : controls.keySet()) {
            if (controller.keyPressed(key)) {
                controls.get(key).run();
            }
        }

        if (controller.takeMouseButtonState(GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
            spawnBomb(transform);
        }

        // TODO: move to an InspectorInputSystem
        if (controller.keyPressed(GLFW_KEY_P)) { wireframe = false; }
        if (controller.keyPressed(GLFW_KEY_O)) { wireframe = true; }
        glPolygonMode(GL_FRONT_AND_BACK, wireframe ? GL_LINE : GL_FILL);
    }

    public void spawnBomb(Transform transform) {
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
}
