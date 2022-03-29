package rendering;
import math.Vector2;
import math.Vector3;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.*;
import java.io.*;

/**
 * A mesh is the 3D geometry
 */
public class Mesh {
    ArrayList<Vector3> vertices;
    ArrayList<Vector2> textureCords;
    ArrayList<Vector3> vertexNormals;
    ArrayList<ArrayList<int[]>> faces;

    public Mesh(String meshSource) {
        loadMesh(meshSource);
    }

    public void loadMesh(String meshSource) {
        vertices = new ArrayList<>();
        textureCords = new ArrayList<>();
        vertexNormals = new ArrayList<>();
        faces = new ArrayList<>();

        try {
            FileInputStream fstream = new FileInputStream(meshSource);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("\\s+");
                String type = tokens[0];

                switch (type) {
                    case "v" -> vertices.add(new Vector3(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    ));
                    case "vt" -> textureCords.add(new Vector2(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2])
                    ));
                    case "vn" -> vertexNormals.add(new Vector3(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    ));
                    case "f" -> {
                        ArrayList<int[]> face = new ArrayList<>();
                        face.add(parseFaceVertexIndices(tokens[1]));
                        face.add(parseFaceVertexIndices(tokens[2]));
                        face.add(parseFaceVertexIndices(tokens[3]));
                        this.faces.add(face);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int[] parseFaceVertexIndices(String vertexAttribIndices) {
        String[] indicesS = vertexAttribIndices.split("/");
        int[] indices = new int[indicesS.length];

        for (int i = 0; i < indicesS.length; ++i) {
            indices[i] = Integer.parseInt(indicesS[i]);
        }

        return indices;
    }

    public FloatBuffer toFloatBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(0);

        return buffer;
    }
}
