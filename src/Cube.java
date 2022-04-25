import math.Vector3;
import math.Vector3i;

import java.util.ArrayList;
import java.util.List;

public class Cube {
    public enum Vertex {
        NEAR_TOP_LEFT(8, new Vector3 (0.0f, 1.0f, 0.0f)),
        NEAR_TOP_RIGHT(5, new Vector3 (1.0f,  1.0f, 0.0f )),
        NEAR_BOTTOM_LEFT(4, new Vector3(0.0f, 0.0f, 0.0f)),
        NEAR_BOTTOM_RIGHT(1, new Vector3 (1.0f,  0.0f, 0.0f)),
        FAR_TOP_LEFT(7, new Vector3 (0.0f, 1.0f, 1.0f)),
        FAR_TOP_RIGHT(6, new Vector3 (1.0f,  1.0f, 1.0f)),
        FAR_BOTTOM_LEFT(3, new Vector3 (0.0f, 0.0f, 1.0f)),
        FAR_BOTTOM_RIGHT(2, new Vector3 (1.0f,  0.0f, 1.0f));

        public final int index;
        public final Vector3 position;
        Vertex(int index, Vector3 position) {
            this.index = index;
            this.position = position;
        }
    }

    final public static VoxelMesh mesh = MeshGenerator.generateCubeWireMesh();

//    public enum Face {
////        FRONT,
////        BACK,
////        LEFT,
////        RIGHT,
////        TOP,
////        BOTTOM;
//
//        Face(Corner topLeft, Corner topRight, Corner bottomRight, Corner bottomLeft) {
//
//        }
//    };
//
//    public class Corner  {
//
//    }

    ArrayList<Vector3> normals = new ArrayList<>(List.of(
            Vector3.zeros(),
            Direction.DOWN.normal,
            Direction.UP.normal,
            Direction.RIGHT.normal,
            Direction.BACK.normal,
            Direction.LEFT.normal,
            Direction.FRONT.normal
    ));
}
