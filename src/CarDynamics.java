import ecs.Component;

public class CarDynamics implements Component {
    float wheelBase = 1.0f; // meters
    float mass = 100.0f; // kg
    float speed = 1000; // not actual speed, more like a rating on a cart-kart sliding bar
    float brakePower = 2 * speed;
    float engineForce;

    public CarDynamics() {
        this.engineForce = 0;
    }
}
