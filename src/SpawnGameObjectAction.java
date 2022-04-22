public class SpawnGameObjectAction extends Action {
    VoxelRenderable gameObject;
    SpawnGameObjectAction(VoxelRenderable gameObject) {
        super();
        this.gameObject = gameObject;
    }

    @Override
    public void execute() {
        World world = World.getInstance();

//        world.addGameObject(gameObject);
    }
}
