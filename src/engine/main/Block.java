package engine.main;

public class Block {
    private boolean active;
    private BlockType blockType;
    private Runnable onUpdateCallback;

    public Block(boolean active, BlockType blockType) {
        this.active = active;
        this.blockType = blockType;
    }

    public Block() {
        this.active = false;
        this.blockType = BlockType.EMPTY;
    }

    public void setUpdateCallback(Runnable onUpdateCallback) {
        this.onUpdateCallback = onUpdateCallback;
    }

    public void setActive(boolean active) {
        this.active = active;
        invokeUpdateCallback();
    }

    private void invokeUpdateCallback() {
        if (onUpdateCallback != null) {
            onUpdateCallback.run();
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setBlockType(BlockType blockType) {
        this.blockType = blockType;
        invokeUpdateCallback();
    }

    public static Block inActive() {
        return new Block(false, BlockType.EMPTY);
    }

    public BlockType getBlockType() {
        return blockType;
    }
}
