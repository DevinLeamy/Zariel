import ecs.Component;
import ecs.ComponentStore;
import ecs.Entity;
import math.Vector3;

public class PlayerMovementSystem extends InstanceSystem {
    ComponentStore<Velocity> velocityStore = ComponentStore.of(Velocity.class);
    ComponentStore<Position> positionStore = ComponentStore.of(Position.class);
    ComponentStore<Rotation> rotationStore = ComponentStore.of(Rotation.class);
    ComponentStore<Prospective> prospectiveStore = ComponentStore.of(Prospective.class);

    public PlayerMovementSystem() {
        super(Component.PLAYER_TAG | Component.POSITION | Component.ROTATION | Component.PROSPECTIVE, 0);
    }

    @Override
    protected void update(float dt, Entity entity) {
        Velocity velocity = velocityStore.getComponent(entity).get();
        Position position = positionStore.getComponent(entity).get();
        Rotation rotation = rotationStore.getComponent(entity).get();
        Prospective prospective = prospectiveStore.getComponent(entity).get();
    }
}