import ecs.Component;
import ecs.ComponentStore;
import ecs.Entity;
import math.Vector3;

public class MovementSystem extends InstanceSystem {
    ComponentStore<Velocity> velocityStore = ComponentStore.of(Velocity.class);
    ComponentStore<Position> positionStore = ComponentStore.of(Position.class);

    public MovementSystem() {
        super(Component.VELOCITY | Component.POSITION, 0);
    }

    /**
     * Updates the list of matching entities.
     * Must be called before update()
     */
    public void updateEntities() {

    }

    @Override
    protected void update(float dt, Entity entity) {
        Velocity velocity = velocityStore.getComponent(entity).get();
        Position position = positionStore.getComponent(entity).get();

        Vector3 deltaPosition = Vector3.scale(velocity, dt);

        position.add(deltaPosition);
    }
}
