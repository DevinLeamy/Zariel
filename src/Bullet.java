import java.util.ArrayList;

public class Bullet extends VoxelRenderable {
    public Bullet(Transform transform, VoxelGeometry shape, Renderer renderer) {
        super(transform, shape, renderer);
    }

    @Override
    public ArrayList<Action> update(float dt) {
        ArrayList<Action> actions = new ArrayList<>();

        return actions;
    }
}
