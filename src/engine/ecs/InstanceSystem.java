package engine.ecs;

import java.util.ArrayList;

abstract public class InstanceSystem extends EntitySystem {
    public InstanceSystem(long signature, int priority) {
        super(signature, priority);
    }

    final public void update(float dt, ArrayList<Entity> entities) {
        for (Entity entity : entities) {
            update(dt, entity);
        }
    }

    abstract protected void update(float dt, Entity entity);
}
