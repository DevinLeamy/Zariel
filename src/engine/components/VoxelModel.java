package engine.components;

import engine.ecs.Component;
import engine.graphics.Mesh;
import engine.main.Block;
import engine.graphics.MeshGenerator;
import engine.graphics.VoxelGeometry;

public class VoxelModel implements Component {
    private boolean updated;
    public Block[][][] voxels;
    private Mesh mesh;

    public VoxelModel(Block[][][] voxels) {
        this.voxels = voxels;
        updated = true;
    }

    public Mesh mesh() {
        if (updated) {
            if (mesh != null) {
                mesh.dispose();
            }
            mesh = MeshGenerator.generateLocalMesh(new VoxelGeometry(voxels));
        }
        return mesh;
    }
}
