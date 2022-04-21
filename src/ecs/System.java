package ecs;

abstract public class System {
    /**
     * signature: eg. Component.VELOCITY | Component.POSITION
     * priority: Systems with higher priority are updated before systems with lower priority
     */
    final private long signature;
    final private int priority;

    public System(long signature, int priority) {
        this.signature = signature;
        this.priority = priority;
    }

    public void updateEntities() {

    }

    abstract public void update(float dt);
}
