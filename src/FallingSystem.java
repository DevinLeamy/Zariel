import ecs.ComponentRegistry;
import ecs.Entity;
import math.Vector3;
import math.Vector3i;

/**
 * Updates the players velocity based on the effects of gravity and
 * whether they are in the air.
 */
public class FallingSystem extends InstanceSystem {
    final private static float GRAVITY = 9.81f;

    World world = World.getInstance();

    public FallingSystem() {
        super(ComponentRegistry.getSignature(GravityTag.class, Dynamics.class, Transform.class), 0);
    }

    @Override
    protected void update(float dt, Entity entity) {
        GravityTag gravityTag = entity.getComponent(GravityTag.class).get();
        Dynamics dynamics = entity.getComponent(Dynamics.class).get();
        Transform transform = entity.getComponent(Transform.class).get();

        Vector3 acceleration = dynamics.acceleration;
        Vector3 velocity = dynamics.velocity;

        boolean previouslyFalling = gravityTag.falling;
        boolean falling = !onGround(transform.position);

        /**
         * Add negative acceleration due to gravity if the entity
         * has just started falling.
         *
         * If they just landed, remove all negative acceleration
         * and velocity.
         */
        if (!previouslyFalling && falling) {
            dynamics.acceleration.y -= GRAVITY;
        } else if (previouslyFalling && !falling) {
            acceleration.y = Float.max(0, acceleration.y);
            velocity.y = Float.max(0, velocity.y);
        }

        gravityTag.falling = falling;
    }

    private boolean onGround(Vector3 pos) {
        Vector3i feet = pos.toVector3i();
        Vector3i ground = Vector3i.sub(feet, new Vector3i(0, 0, 0));

        return world.blockIsActive(ground);
    }
}
