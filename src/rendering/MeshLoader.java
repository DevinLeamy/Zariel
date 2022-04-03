package rendering;

import math.Vector2;
import math.Vector3;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


/*
TODO: This can be implemented much better
 */

public class MeshLoader {
    public static Mesh loadMesh(String meshSource) {
        ArrayList<Vector3> rawVertices    = new ArrayList<>();
        ArrayList<Vector2> rawUvs         = new ArrayList<>();
        ArrayList<Vector3> rawNormals     = new ArrayList<>();

        Map<String, Integer> vertexMap    = new HashMap<>();

        ArrayList<Vertex> vertices        = new ArrayList<>();
        ArrayList<Integer> indices        = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(meshSource)));
            String line;

            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("\\s+");
                String type = tokens[0];

                switch (type) {
                    case "v" -> rawVertices.add(new Vector3(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    ));
                    case "vt" -> rawUvs.add(new Vector2(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2])
                    ));
                    case "vn" -> rawNormals.add(new Vector3(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    ));
                    case "f" -> {
                        for (int i = 0; i < 3; ++i) {
                            try {
                                String token = tokens[i + 1];
                                int[] v = parseFaceVertexIndices(token);
                                if (!vertexMap.containsKey(token)) {
                                    vertexMap.put(token, vertexMap.size());
                                    vertices.add(new Vertex(
                                            rawVertices.get(v[0]),
                                            rawUvs     .get(v[1]),
//                                            rawVertices.get(v[0])
                                            rawNormals .get(v[2])
                                    ));
                                }
                                indices.add(vertexMap.get(token));
                            } catch (Exception e) {}

                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Normals: " + rawNormals.size() + " UVs: " + rawUvs.size() + " Vertices: " + rawVertices.size());
        System.out.println("Indices: " + indices.size());

        return new Mesh(vertices, indices);
    }

    private static int[] parseFaceVertexIndices(String vertexAttribIndices) {
        String[] indicesS = vertexAttribIndices.split("/");
        int[] indices = new int[indicesS.length];

        for (int i = 0; i < indicesS.length; ++i) {
            // zero-index
            indices[i] = Integer.parseInt(indicesS[i]) - 1;
        }

        return indices;
    }
}
