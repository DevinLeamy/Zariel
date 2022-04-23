import ecs.ComponentRegistry;
import ecs.Entity;
import ecs.EntityManager;

public class DespawnSystem extends InstanceSystem {
    EntityManager entityManager = World.getInstance().entityManager;

    public DespawnSystem() {
        super(ComponentRegistry.getSignature(DespawnTag.class), 0);
    }

    @Override
    protected void update(float dt, Entity entity) {
        entityManager.removeEntity(entity);
    }
}
