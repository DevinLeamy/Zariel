import ecs.ComponentRegistry;
import ecs.ComponentStore;
import ecs.Entity;
import ecs.EntityManager;

public class LifeTimeSystem extends InstanceSystem {
    EntityManager entityManager = World.getInstance().entityManager;

    ComponentStore<LifeTime> lifeTimeStore = ComponentStore.of(LifeTime.class);
    public LifeTimeSystem() {
        super(ComponentRegistry.getSignature(LifeTime.class), 0);
    }

    @Override
    protected void update(float dt, Entity entity) {
        LifeTime lifeTime = lifeTimeStore.getComponent(entity).get();

        lifeTime.timeRemaining -= dt;

        if (lifeTime.timeRemaining <= 0) {
            lifeTime.onLifeTimeOver.run();
            entityManager.removeEntity(entity);
        }
    }
}
