package engine.ecs;

abstract public class System {
    /**
     * signature: eg. Component.VELOCITY | Component.POSITION
     * priority: Systems with higher priority are updated before systems with lower priority
     */
    final private int priority;

    public System(int priority) {
        this.priority = priority;
    }



    abstract public void update(float dt);
}
