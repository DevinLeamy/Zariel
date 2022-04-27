package engine.components;

import engine.ecs.Component;

// ref: https://github.com/spacejack/carphysics2d/blob/master/public/js/Car.js

public class CarDynamics implements Component {
    // player inputs
    public float left = 0.0f;
    public float right = 0.0f;
    public float throttle = 0.0f;
    public float brake = 0.0f;
    public float eBrake = 0.0f;

    // car state
    public float yawRate = 0.0f; // angular velocity in radians
    public float steer = 0.0f; // amount of steering input (-1.0 - 1.0)
    public float steerAngle = 0.0f; // actual (local) front wheel steer angle (-maxSteer - maxSteer)
    public boolean smoothSteer = true;
    public boolean safeSteer = true;
    public float linearAcceleration = 0.0f;

    public float inertia;

    // car-specific config
    public float mass;
    public float inertiaScale;
    public float halfWidth; // center to the side of the chassis (meters)
    public float cgToFront; // center of gravity to the front of the chassis (meters)
    public float cgToRear; // center of gravity to the rear of the chassis (meters)
    public float cgToFrontAxle; // c.o.g. to front axle
    public float cgToRearAxle;
    public float cgHeight;
    public float wheelRadius;
    public float wheelWidth;
    public float tireGrip;
    public float lockGrip;
    public float engineForce;
    public float brakeForce;
    public float eBrakeForce;
    public float weightTransfer;
    public float maxSteer;
    public float cornerStiffnessFront;
    public float cornerStiffnessRear;
    public float airResist;
    public float rollResist;


    public CarDynamics() {
        this.mass = 1200.0f;
        this.inertiaScale = 1.0f;
        this.halfWidth = 0.8f;
        this.cgToFront = 2.0f;
        this.cgToRear = 2.0f;
        this.cgToFrontAxle = 1.25f;
        this.cgToRearAxle = 1.25f;
        this.cgHeight = 0.55f;
        this.wheelRadius = 0.3f;
        this.wheelWidth = 0.2f;
        this.tireGrip = 2.0f;
        this.lockGrip = 0.7f;
        this.engineForce = 64000.0f;
        this.brakeForce = 20000.0f;
        this.eBrakeForce = this.brakeForce / 2.5f;
        this.weightTransfer = 0.2f;
        this.maxSteer = 0.6f;
        this.cornerStiffnessFront = 5.0f;
        this.cornerStiffnessRear = 5.2f;
        this.airResist = 2.5f;
        this.rollResist = 8.0f;

        // setup missing state variables based on config
        inertia = mass * inertiaScale;
    }
}
