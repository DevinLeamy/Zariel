public class Block {
    private boolean active;
    private BlockType blockType;

    public Block(boolean active, BlockType blockType) {
        this.active = active;
        this.blockType = blockType;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public BlockType getBlockType() {
        return blockType;
    }

}
