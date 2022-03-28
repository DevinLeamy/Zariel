import org.lwjgl.BufferUtils;

import java.nio.*;

public class Matrix3 {
    private float[][] m;
    Matrix3(float[][] m) {
        this.m = new float[3][3];
        set(m);
    }

    // set the matrix to m
    void set(float[][] m) {
        assert(m.length == 3 && m[0].length == 3);

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                this.m[i][j] = m[i][j];
            }
        }
    }

    static Matrix3 zero() {
        return new Matrix3(new float[][] {
                { 0f, 0f, 0f },
                { 0f, 0f, 0f },
                { 0f, 0f, 0f },
        });
    }

    // rotate and create a new matrix
    static Matrix3 rotate(Matrix3 m, float x, float y, float z) {
        // we'll assume all rotation happen about the z-axis
        x = 0.0f;
        y = 0.0f;

//        // rotation about x
//        return new Matrix3(new float[][] {
//            { 1.0f, 0.0f,                0.0f                 },
//            { 0.0f, (float) Math.cos(x), (float) -Math.sin(x) },
//            { 0.0f, (float) Math.sin(x), (float) Math.cos(x)  }
//        });

        // rotation about z
        return new Matrix3(new float[][] {
            { (float) Math.cos(z), (float) -Math.sin(z), 0.0f },
            { (float) Math.sin(z), (float) Math.cos(z),  0.0f },
            { 0.0f,                0.0f,                 1.0f }
        });
    }


    // rotate an existing matrix
    void rotate(float x, float y, float z) {

    }

    static Matrix3 identity() {
        return new Matrix3(new float[][] {
                { 1f, 0f, 0f },
                { 0f, 1f, 0f },
                { 0f, 0f, 1f },
        });
    }

    public FloatBuffer toFloatBuffer() {
        FloatBuffer fb = BufferUtils.createFloatBuffer(3 * 3);
        for (int i = 0; i < 3; ++i) {
            fb.put(m[i]);
        }
        fb.flip();

        return fb;
    }
}
