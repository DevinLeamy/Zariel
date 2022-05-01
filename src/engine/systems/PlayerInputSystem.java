package engine.systems;

import engine.components.*;
import engine.controller.Controller;
import engine.ecs.ComponentRegistry;
import engine.ecs.ComponentStore;
import engine.ecs.Entity;
import engine.ecs.InstanceSystem;
import engine.main.EntityGenerator;
import math.Vector3;
import engine.util.Utils;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerInputSystem extends InstanceSystem {
    ComponentStore<Transform> transformStore = ComponentStore.of(Transform.class);

    final private float mouseSensitivity = 0.002f;
    final private float turnSpeed = 2.0f;

    private int[] mousePos;

    public PlayerInputSystem() {
        super(ComponentRegistry.getSignature(CarDynamics.class, Transform.class, PlayerTag.class, GravityTag.class, CameraTarget.class), 0);

        mousePos = new int[] { 0, 0 };
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

        Vector3 rightParticlePos = transform.position.clone().sub(Vector3.scale(transform.direction(), 1))
                .add(Vector3.scale(transform.right(), 1));
        Vector3 leftParticlePos = transform.position.clone().sub(Vector3.scale(transform.direction(), 1));

        entityManager.addEntity(EntityGenerator.generateParticle(leftParticlePos, transform));
        entityManager.addEntity(EntityGenerator.generateParticle(rightParticlePos, transform));

        handleMouseUpdate(target, dt, Controller.mousePosition());
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

    private void brake(float dt, CarDynamics carDynamics) {
        carDynamics.brake = 0.5f;
    }

    private void pushGas(float dt, CarDynamics carDynamics) {
        carDynamics.throttle = 0.5f;
    }

    private void turn(float mag, CarDynamics carDynamics) {
        carDynamics.steerAngle = Utils.clamp(-(float) Math.PI / 4, (float) Math.PI / 4, carDynamics.steerAngle + mag * 0.4f);
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
            if (Controller.keyPressed(key)) {
                controls.get(key).run();
            }
        }

        if (Controller.takeMouseButtonState(GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
            world.entityManager.addEntity(EntityGenerator.generateBomb(transform));
        }
    }
}
