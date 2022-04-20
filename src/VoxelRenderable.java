import math.Matrix4;
import math.Vector3;

import java.util.ArrayList;

abstract public class VoxelRenderable {
    Transform transform;
    VoxelGeometry shape;
    VoxelMesh mesh;
    Renderer renderer;

    public VoxelRenderable(Transform transform, VoxelGeometry shape, Renderer renderer) {
        this.transform = transform;
        this.shape = shape;
        this.mesh = MeshGenerator.generateVoxelMesh(shape, transform.position.toVector3i());
        this.renderer = renderer;
    }

    abstract public ArrayList<Action> update(float dt);

    public void render() {
        World world = World.getInstance();
        Camera camera = world.getPerspective();

        Vector3 cameraPos = camera.transform.position;
        Vector3 playerPos = world.player.transform.position;

        Matrix4 viewMatrix = Camera.lookAt(cameraPos, playerPos, new Vector3(0, 1, 0));
        Matrix4 projectionMatrix = camera.projectionMatrix();

        renderer.shader.setUniform("viewM", viewMatrix);
        renderer.shader.setUniform("modelM", transform.modelMatrix());
        renderer.shader.setUniform("projectionM", projectionMatrix);

        renderer.renderMesh(mesh);
    }
}
