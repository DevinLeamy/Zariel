import math.Vector3;
import math.Vector3i;

import java.util.ArrayList;
import java.util.Optional;

public class RigidBody {
    abstract public class Collision {
        public Collision () {}
        abstract public ArrayList<Action> collide();
    }

    public class BlockCollision extends Collision {
        Vector3i blockPosition;

        public BlockCollision(Vector3i blockPosition) {
            this.blockPosition = blockPosition;
        }

        @Override
        public ArrayList<Action> collide() {
            ArrayList<Action> actions = new ArrayList<>();

            actions.add(new ExplodeBlockAction(blockPosition));
            return actions;
        }
    }
    final private static float GRAVITY = 9.81f;
//    private float mass;
    public Vector3 velocity;
    private Vector3 acceleration;
    private BoundingBox boundingBox;

    public RigidBody(Vector3 velocity, Vector3 acceleration, BoundingBox boundingBox) {
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.boundingBox = boundingBox;
    }
    public RigidBody(BoundingBox boundingBox) {
        this(Vector3.zeros(), Vector3.zeros(), boundingBox);
    }

    public Vector3 update(float dt, Vector3 position) {
        boundingBox.setOrigin(position.clone());

        if (!onGround(boundingBox)) {
            velocity.y -= GRAVITY * dt;
        } else {
            velocity.y = 0;
        }

        Vector3 deltaPosition = Vector3.scale(velocity, dt);
        Vector3 deltaVelocity = Vector3.scale(acceleration, dt);

        velocity.add(deltaVelocity);

        return Vector3.add(position, deltaPosition);
    }

    public boolean onGround(BoundingBox boundingBox) {
        World world = World.getInstance();
        Vector3 origin = boundingBox.getOrigin();

        for (int i = 0; i < boundingBox.getWidth(); ++i) {
            for (int j = 0; j < boundingBox.getDepth(); ++j) {
                /**
                 * Add x and z offset from the origin, reduce height
                 * level by 1, and round to the nearest block.
                 */
                Vector3i position = origin.clone().add(new Vector3(i, -1, j)).toVector3i();
                if (world.blockIsActive(position)) {
                    return true;
                }
            }
        }

        return false;
    }

    // TODO: not sure how to implement this yet
//    public boolean colliding(BoundingBox other) {
//        return false;
//    }
//
    public Optional<Collision> collide() {
        World world = World.getInstance();

        for (Vector3 vertex : boundingBox.vertices()) {
            Vector3i blockPosition = vertex.toVector3i();

            if (world.blockIsActive(blockPosition)) {
                return Optional.of(new BlockCollision(blockPosition));
            }
        }

        return Optional.empty();
    }
}
