package engine.main;

import engine.ecs.ComponentRegistry;
import engine.ecs.Entity;
import engine.ecs.EntityManager;
import engine.ecs.InstanceSystem;

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
