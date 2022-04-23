import ecs.ComponentRegistry;
import ecs.ComponentStore;
import ecs.Entity;
import math.Vector3;

public class MovementSystem extends InstanceSystem {
    ComponentStore<Dynamics>  dynamicsStore = ComponentStore.of(Dynamics.class);
    ComponentStore<Transform> transformStore = ComponentStore.of(Transform.class);

    public MovementSystem() {
        super(ComponentRegistry.getSignature(Transform.class, Dynamics.class), 0);
    }

    @Override
    protected void update(float dt, Entity entity) {
        Dynamics dynamics = dynamicsStore.getComponent(entity).get();
        Transform transform = transformStore.getComponent(entity).get();

        Vector3 velocity = dynamics.velocity;
        Vector3 acceleration = dynamics.acceleration;
        Vector3 position = transform.position;

        velocity.add(Vector3.scale(acceleration, dt));
        position.add(Vector3.scale(velocity, dt));
    }
}
