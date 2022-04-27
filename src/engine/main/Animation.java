package engine.main;

import engine.ecs.Component;

import java.util.ArrayList;

public class Animation implements Component {
    int frame;
    int frameTick;
    int frameDuration;
    ArrayList<Block[][][]> frameVoxels;

    public Animation(ArrayList<String> framePaths, int frameDuration) {
        this.frameDuration = frameDuration;
        this.frame = 0;
        this.frameTick = 0;
        this.frameVoxels = new ArrayList<>();

        for (String path : framePaths) {
            frameVoxels.add(VoxelGeometry.loadFromFile(path).voxels);
        }
    }

    public Block[][][] frame() {
        return frameVoxels.get(frame);
    }

    public void next() {
        frameTick += 1;
        if (frameTick == frameDuration) {
            frame = (frame + 1) % frameVoxels.size();
            frameTick = 0;
        }
    }
}
