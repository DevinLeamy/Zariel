import ecs.Entity;
import ecs.System;

import java.util.ArrayList;

abstract public class InstanceSystem extends System {
    ArrayList<Entity> entities;

    public InstanceSystem(long signature, int priority) {
        super(signature, priority);
    }

    @Override
    final public void update(float dt) {
        for (Entity entity : entities) {
            update(dt, entity);
        }
    }

    abstract protected void update(float dt, Entity entity);
}
