package ecs;

public interface Component {
    long VELOCITY   = 1 << 0;
    long POSITION   = 1 << 1;
    long PLAYER_TAG = 1 << 2;
    long RIGID_BODY = 1 << 3;
    long ROTATION   = 1 << 4;
    long SCALE      = 1 << 5;
    long VOXEL_MODEL= 1 << 6;
    long PROSPECTIVE = 1 << 6;
}
