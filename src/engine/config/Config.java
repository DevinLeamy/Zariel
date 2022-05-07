package engine.config;

public class Config {
    final public static int CHUNK_SIZE = 256;
    // CHUNK_DISTANCES
    final public static int WORLD_LENGTH = 1; // z axis
    final public static int WORLD_WIDTH = 1; // x axis
//    final public static int WORLD_LENGTH = 2; // z axis
//    final public static int WORLD_WIDTH = 2;  // x axis
    final public static int WORLD_HEIGHT = 1;  // y axis
    final public static float CHUNK_LOAD_DISTANCE = 9f;
    final public static int LOAD_LIMIT = 5;

    final public static int BEDROCK_LEVEL = 0;
    final public static int WATER_LEVEL = 5;
    final public static int SAND_LEVEL = 10;
    final public static int GRASS_LEVEL = 13;
    final public static int STONE_LEVEL = 25;
    final public static int SNOW_LEVEL = 31;
    final public static int MAX_LEVEL = CHUNK_SIZE * WORLD_HEIGHT;

    public static float debug1 = 3;
    public static float debug2 = 0;
    public static float debug3 = 1;

    public static boolean orthographic = false;

    public static int MAX_ENTITY_COUNT = 10000;

    // PHYSICS
    final public static float GRAVITY = 9.81f;
}
