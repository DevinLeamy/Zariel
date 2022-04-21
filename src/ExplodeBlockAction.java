import math.Vector3;
import math.Vector3i;

import java.util.ArrayList;
import java.util.Optional;

public class ExplodeBlockAction extends Action {
    Vector3i blockPosition;

    public ExplodeBlockAction(Vector3i blockPosition) {
        this.blockPosition = blockPosition;
    }

    @Override
    public void execute() {
        ArrayList<Action> actions = new ArrayList<>();

        Optional<Block> maybeBlock = World.getInstance().getBlock(blockPosition);

        if (maybeBlock.isEmpty()) {
            return;
        }

        actions.add(new BlockUpdateAction(blockPosition, Block.inActive()));

        BlockType explodedBlockType = maybeBlock.get().getBlockType();

        for (int i = 0; i < 10; ++i) {
            actions.add(new SpawnGameObjectAction(generateParticle(explodedBlockType)));
        }

        // TODO: nah man
        for (Action action : actions) {
            action.execute();
        }
    }

    private Particle generateParticle(BlockType explodedBlockType) {
        return new Particle(
                new Transform(
                    this.blockPosition.toVector3(),
                    Utils.randVector3(),
                    new Vector3(0.5f, 0.5f, 0.5f)
                ),
                explodedBlockType
        );
    }
}