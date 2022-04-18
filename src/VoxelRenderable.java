import java.util.ArrayList;

abstract public class VoxelRenderable {
    Transform transform;
    VoxelMesh mesh;
    Renderer renderer;

    public VoxelRenderable(Transform transform, VoxelGeometry shape, Renderer renderer) {
        this.transform = transform;
        this.mesh = MeshGenerator.generateVoxelMesh(shape, transform.position.toVector3i());
        this.renderer = renderer;
    }

    abstract public ArrayList<Action> update(float dt);

    abstract public void render();
}
