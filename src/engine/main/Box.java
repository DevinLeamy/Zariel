package engine.main;

import math.Vector3;

import java.util.Arrays;
import java.util.stream.Stream;

public class Box extends Cube {
    Face[] faces;
    Vector3 dimensions;

    public Box(Vector3 dimensions) {
        this.dimensions = dimensions;
        faces = new Face[Cube.faces.length];
        for (int i = 0; i < Cube.faces.length; ++i) {
            faces[i] = Cube.faces[i].clone().scale(dimensions);
        }
    }

    public Face leftFace() {
        return faces[2];
    }

    public Face rightFace() {
        return faces[3];
    }

    public Face topFace() {
       return faces[4];
    }

    public Face bottomFace() {
       return faces[5];
    }

    public Face frontFace() {
       return faces[0];
    }

    public Face backFace() {
       return faces[1];
    }

    public Vector3[] vertices() {
        return Stream.concat(Arrays.stream(frontFace().vertices()), Arrays.stream(backFace().vertices())).toArray(Vector3[]::new);
    }
}
