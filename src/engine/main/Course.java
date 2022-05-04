package engine.main;

import engine.components.VoxelModel;
import math.Vector3i;

public class Course implements CourseI {
    VoxelModel model;
    Vector3i start;
    public Course(VoxelModel model, Vector3i start) {
        this.model = model;
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
