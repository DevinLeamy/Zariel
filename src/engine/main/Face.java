package engine.main;

import math.Vector2;
import math.Vector3;

import java.util.Arrays;

public class Face {
    final public static String LEFT = "LEFT";
    final public static String RIGHT = "RIGHT";
    final public static String TOP = "TOP";
    final public static String BOTTOM = "BOTTOM";
    final public static String FRONT = "FRONT";
    final public static String BACK = "BACK";

    private Vector3[] vertices;
    final public Vector3 normal;
    final public String name;

    Face(Vector3 topLeft, Vector3 topRight, Vector3 bottomLeft, Vector3 bottomRight, Vector3 normal, String name) {
        vertices = new Vector3[] { topLeft, topRight, bottomLeft, bottomRight };
        this.normal = normal;
        this.name = name;
    }

    public Face scale(Vector3 dimensions) {
        for (Vector3 vertex : vertices) {
            vertex.scale(dimensions);
        }

        return this;
    }

    public Vector3 normal() {
        return normal.clone();
    }

    public Vector3[] vertices() {
        return Arrays.stream(vertices).map(Vector3::clone).toArray(Vector3[]::new);
    }

    public Vector3 topLeft() {
        return vertices[0];
    }

    public Vector3 bottomRight() {
        return vertices[3];
    }

    public Vector3 bottomLeft() {
        return vertices[2];
    }

    public Vector3 topRight() {
        return vertices[1];
    }

    public Vector3 center() {
        return Vector3.add(bottomLeft(), Vector3.sub(topRight(), bottomLeft()).scale(0.5f));
    }

    public Face clone() {
        return new Face(topLeft().clone(), topRight().clone(), bottomLeft().clone(), bottomRight().clone(), normal.clone(), name);
    }
}
