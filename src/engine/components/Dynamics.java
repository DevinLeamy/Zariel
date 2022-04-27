package engine.components;

import engine.ecs.Component;
import math.Vector3;

public class Dynamics implements Component {
    public Vector3 acceleration;
    public Vector3 velocity;
    public Vector3 force;
    public float angularVelocity;

    public Dynamics(Vector3 velocity, Vector3 acceleration) {
        this.acceleration = acceleration;
        this.velocity = velocity;
        this.angularVelocity = 0;
        this.force = Vector3.zeros();
    }
}
