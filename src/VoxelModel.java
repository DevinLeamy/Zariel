import ecs.Component;
import math.Vector3i;

public class VoxelModel extends VoxelGeometry implements Component {
    public VoxelModel(Block[][][] voxels) {
        super(voxels);
    }
    public VoxelModel(Vector3i dimensions) {
        super(dimensions);
    }
}
