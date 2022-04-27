package engine.components;

import engine.ecs.Component;
import engine.main.Block;
import engine.graphics.MeshGenerator;
import engine.graphics.VoxelGeometry;
import engine.graphics.VoxelMesh;

public class VoxelModel implements Component {
    private boolean updated;
    public Block[][][] voxels;
    private VoxelMesh mesh;

    public VoxelModel(Block[][][] voxels) {
        this.voxels = voxels;
        updated = true;
    }

    public VoxelMesh mesh() {
        if (updated) {
            if (mesh != null) {
                mesh.dispose();
            }
            mesh = MeshGenerator.generateLocalVoxelMesh(new VoxelGeometry(voxels));
        }
        return mesh;
    }
}
