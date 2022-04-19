import math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class Cube {
    enum Vertex {
        FRONT_TOP_LEFT(8),
        FRONT_TOP_RIGHT(5),
        FRONT_BOTTOM_LEFT(4),
        FRONT_BOTTOM_RIGHT(1),
        BACK_TOP_LEFT(7),
        BACK_TOP_RIGHT(6),
        BACK_BOTTOM_LEFT(3),
        BACK_BOTTOM_RIGHT(2);

        private final int index;
        Vertex(int index) {
            this.index = index;
        }
    }

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



    Vector3 v1 = new Vector3 (1.0f,  0.0f, 0.0f);
    Vector3 v2 = new Vector3 (1.0f,  0.0f, 1.0f);
    Vector3 v3 = new Vector3 (0.0f, 0.0f, 1.0f);
    Vector3 v4 = new Vector3 (0.0f, 0.0f, 0.0f);
    Vector3 v5 = new Vector3 (1.0f,  1.0f, 0.0f);
    Vector3 v6 = new Vector3 (1.0f,  1.0f, 1.0f);
    Vector3 v7 = new Vector3 (0.0f, 1.0f, 1.0f);
    Vector3 v8 = new Vector3 (0.0f, 1.0f, 0.0f);

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
