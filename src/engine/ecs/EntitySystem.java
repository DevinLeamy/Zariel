package engine.ecs;

import engine.main.World;

import java.util.ArrayList;

abstract public class EntitySystem extends System {
    final static protected World world = World.getInstance();
    final static protected EntityManager entityManager = world.entityManager;

    final private long signature;

    private ArrayList<Entity> entities;

    public EntitySystem(long signature, int priority) {
        super(priority);

        this.signature = signature;
        this.entities = new ArrayList<>();
    }

    private void updateEntities() {
        entities = entityManager.queryEntities(signature);
    }

    @Override
    final public void update(float dt) {
        updateEntities();
        update(dt, entities);
    }

    abstract protected void update(float dt, ArrayList<Entity> entities);
}
