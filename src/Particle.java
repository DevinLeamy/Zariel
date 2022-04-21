import math.Vector3;

import java.util.ArrayList;

public class Particle extends VoxelRenderable {
    RigidBody rigidBody;

    public Particle(Transform transform, BlockType particleType) {
        super(transform, new VoxelGeometry(new Block[][][] {{{new Block(true, particleType)}}}));

        rigidBody = new RigidBody(
                Utils.randVector3().scale(3.0f).add(new Vector3(0, 10, 0)),
                Vector3.zeros(),
                new BoundingBox(transform.position, 1, 1, 1)
        );
    }

    @Override
    public ArrayList<Action> update(float dt) {
        ArrayList<Action> actions = new ArrayList<>();

        transform.position = rigidBody.update(dt, transform.position);

        return actions;
    }
}
