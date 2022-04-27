package engine.main;

import engine.controller.Controller;
import engine.ecs.ComponentRegistry;
import engine.ecs.ComponentStore;
import engine.ecs.Entity;
import engine.ecs.InstanceSystem;
import ecs.*;
import math.Vector3;
import math.Vector3i;
import util.Utils;

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
        Transform transform = transformStore.getComponent(entity).get();
        CameraTarget target = entity.getComponent(CameraTarget.class).get();
        GravityTag gravityTag = entity.getComponent(GravityTag.class).get();
        CarDynamics carDynamics = entity.getComponent(CarDynamics.class).get();

        playerTag.previousTransform = new Transform(
                transform.position.clone(),
                transform.rotation.clone(),
                transform.scale.clone()
        );

        Vector3 rightParticlePos = transform.position.clone().sub(Vector3.scale(transform.direction(), 2))
                .add(Vector3.scale(transform.right(), 2));
        Vector3 leftParticlePos = transform.position.clone().sub(Vector3.scale(transform.direction(), 2));

        entityManager.addEntity(generateParticle(leftParticlePos, transform));
        entityManager.addEntity(generateParticle(rightParticlePos, transform));

        handleMouseUpdate(target, dt, controller.mousePosition());
        handleKeyPresses(dt, carDynamics, transform, gravityTag.falling);
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
        carDynamics.brake = 0.5f;
    }

    private void pushGas(float dt, CarDynamics carDynamics) {
        carDynamics.throttle = 0.5f;
    }

    private void turn(float mag, CarDynamics carDynamics) {
        carDynamics.steerAngle = Utils.clamp(-(float) Math.PI / 4, (float) Math.PI / 4, carDynamics.steerAngle + mag * 0.3f);
    }

    private void turnLeft(float dt, CarDynamics carDynamics) {
        turn(-dt * turnSpeed, carDynamics);
    }

    private void turnRight(float dt, CarDynamics carDynamics) {
        turn(dt * turnSpeed, carDynamics);
    }

    private void handleKeyPresses(float dt, CarDynamics carDynamics, Transform transform, boolean falling) {
        Map<Integer, Runnable> controls = new HashMap<>();
        controls.put(GLFW_KEY_W, () -> pushGas(dt, carDynamics));
        controls.put(GLFW_KEY_S, () -> brake(dt, carDynamics));
        controls.put(GLFW_KEY_A, () -> turnLeft(dt, carDynamics));
        controls.put(GLFW_KEY_D, () -> turnRight(dt, carDynamics));

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

    private Entity generateParticle(Vector3 position, Transform transform) {
        Entity particle = new Entity();
        float size = 0.2f;
        particle.addComponent(new Transform(
                position,

                transform.direction(),
                new Vector3(size, size, size)
        ));
        particle.addComponent(new Dynamics(
                transform.direction().clone().scale(-0.3f),
                Vector3.zeros()
        ));
        particle.addComponent(new RigidBody(
                new BoundingBox(0.2f, 0.2f, 0.2f),
                "PARTICLE"
        ));
        particle.addComponent(new GravityTag());
        particle.addComponent(new LifeTime(2));
        particle.addComponent(new VoxelModel(new Block[][][] {{{new Block(true, new BlockType(new Vector3i(10, 10, 10)))}}}));
        return particle;
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
