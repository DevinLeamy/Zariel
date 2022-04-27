package engine.main;

import engine.graphics.Mesh;
import engine.graphics.MeshGenerator;
import math.Vector3;

public class Cube {
    final public static Vector3 NEAR_TOP_LEFT = new Vector3 (0.0f, 1.0f, 0.0f);
    final public static Vector3 NEAR_TOP_RIGHT = new Vector3 (1.0f,  1.0f, 0.0f );
    final public static Vector3 NEAR_BOTTOM_LEFT = new Vector3(0.0f, 0.0f, 0.0f);
    final public static Vector3 NEAR_BOTTOM_RIGHT = new Vector3 (1.0f,  0.0f, 0.0f);
    final public static Vector3 FAR_TOP_LEFT = new Vector3 (0.0f, 1.0f, 1.0f);
    final public static Vector3 FAR_TOP_RIGHT = new Vector3 (1.0f,  1.0f, 1.0f);
    final public static Vector3 FAR_BOTTOM_LEFT = new Vector3 (0.0f, 0.0f, 1.0f);
    final public static Vector3 FAR_BOTTOM_RIGHT = new Vector3 (1.0f,  0.0f, 1.0f);

    final public static Face FRONT = new Face(NEAR_TOP_LEFT, NEAR_TOP_RIGHT, NEAR_BOTTOM_LEFT, NEAR_BOTTOM_RIGHT, Direction.FRONT.normal, Face.FRONT);
    final public static Face BACK = new Face(FAR_TOP_LEFT, FAR_TOP_RIGHT, FAR_BOTTOM_LEFT, FAR_BOTTOM_RIGHT, Direction.BACK.normal, Face.BACK);
    final public static Face LEFT = new Face(FAR_TOP_LEFT, NEAR_TOP_LEFT, FAR_BOTTOM_LEFT, NEAR_BOTTOM_LEFT, Direction.LEFT.normal, Face.LEFT);
    final public static Face RIGHT = new Face(NEAR_TOP_RIGHT, FAR_TOP_RIGHT, NEAR_BOTTOM_RIGHT, FAR_BOTTOM_RIGHT, Direction.RIGHT.normal, Face.RIGHT);
    final public static Face TOP = new Face(FAR_TOP_LEFT, FAR_TOP_RIGHT, NEAR_TOP_LEFT, NEAR_TOP_RIGHT, Direction.UP.normal, Face.TOP);
    final public static Face BOTTOM = new Face(NEAR_BOTTOM_LEFT, NEAR_BOTTOM_RIGHT, FAR_BOTTOM_LEFT, FAR_BOTTOM_RIGHT, Direction.DOWN.normal, Face.BOTTOM);

    final public static Vector3[] vertices = new Vector3[] {
            NEAR_TOP_LEFT,
            NEAR_TOP_RIGHT,
            NEAR_BOTTOM_LEFT,
            NEAR_BOTTOM_RIGHT,
            FAR_TOP_LEFT,
            FAR_TOP_RIGHT,
            FAR_BOTTOM_LEFT,
            FAR_BOTTOM_RIGHT
    };
    final public static Face[] faces = new Face[] { FRONT, BACK, LEFT, RIGHT, TOP, BOTTOM };
    final public static Mesh mesh = MeshGenerator.generateCubeWireMesh();
}
