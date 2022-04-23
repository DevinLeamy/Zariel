import ecs.Component;
import math.Vector3;

public class Dynamics implements Component {
    public Vector3 acceleration;
    public Vector3 velocity;

    public Dynamics(Vector3 velocity, Vector3 acceleration) {
        this.acceleration = acceleration;
        this.velocity = velocity;
    }
}
