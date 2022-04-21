public class DespawnGameObjectAction extends Action {
    final private long objectId;

    public DespawnGameObjectAction(long objectId) {
        this.objectId = objectId;
    }

    @Override
    public void execute() {
        World world = World.getInstance();

        world.despawnGameObject(objectId);
    }
}
