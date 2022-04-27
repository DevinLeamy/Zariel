package engine.main;

import math.Vector3i;

public class Course implements CourseI {
    Block[][][] voxels;
    Vector3i start;
    public Course(Block[][][] voxels, Vector3i start) {
        this.voxels = voxels;
        this.start = start;
    }

    @Override
    public void load() {

    }

    @Override
    public void reset() {

    }

    @Override
    public void unload() {

    }
}
