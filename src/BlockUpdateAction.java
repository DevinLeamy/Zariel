import math.Vector3i;

import java.util.Optional;

public class BlockUpdateAction extends Action {
    private Vector3i blockLocation;
    private Block newBlock;

    BlockUpdateAction(Vector3i blockLocation, Block newBlock) {
        super();
        this.blockLocation = blockLocation;
        this.newBlock = newBlock;
    }

    @Override
    public void execute() {
        World world = World.getInstance();
        Optional<Block> maybeBlock = world.getBlock(blockLocation);

        if (maybeBlock.isEmpty()) {
            return;
        }

        Block block = maybeBlock.get();
        block.setBlockType(newBlock.getBlockType());
        block.setActive(newBlock.isActive());
    }
}
