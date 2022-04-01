package math;

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

    void set(int i, int j, float value) {
        assert(i >= 0 && i < 3 && j >= 0 && j < 3);
        this.m[i][j] = value;
    }

    static Matrix3 zero() {
        return new Matrix3(new float[][] {
                { 0f, 0f, 0f },
                { 0f, 0f, 0f },
                { 0f, 0f, 0f },
        });
    }

    public static Matrix3 mult(Matrix3 m1, Matrix3 m2) {
        float[][] res = new float[3][3];

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                float dot = 0;
                for (int k = 0; k < 3; ++k) {
                    dot += m1.m[i][k] * m2.m[k][j];
                }

                res[i][j] = dot;
            }
        }

        return new Matrix3(res);
    }

    public static Vector3 mult(Matrix3 m, Vector3 v) {
        float[] res = new float[3];

        for (int i = 0; i < 3; ++i) {
            float sum = 0;
            for (int j = 0; j < 3; ++j) {
                sum += m.m[i][j] + v.toArray()[j];
            }
            res[i] = sum;
        }

        return new Vector3(res[0], res[1], res[2]);
    }

    public Matrix3 mult(Matrix3 other) {
        set(Matrix3.mult(this, other).m);
        return this;
    }

    public static Matrix3 genRotationMatrix(float x, float y, float z) {
        Matrix3 rotationX = new Matrix3(new float[][]{
                {1.0f, 0.0f,                0.0f                },
                {0.0f, (float) Math.cos(x), (float) -Math.sin(x)},
                {0.0f, (float) Math.sin(x), (float) Math.cos(x) }
        });

        Matrix3 rotationY = new Matrix3(new float[][]{
                {(float) Math.cos(y),  0.0f, (float) Math.sin(y)},
                {0.0f,                 1.0f, 0.0f               },
                {(float) -Math.sin(y), 0.0f, (float) Math.cos(y)}
        });

        Matrix3 rotationZ = new Matrix3(new float[][]{
                {(float) Math.cos(z), (float) -Math.sin(z), 0.0f},
                {(float) Math.sin(z), (float) Math.cos(z),  0.0f},
                {0.0f,                0.0f,                 1.0f}
        });

        return rotationZ.mult(rotationY).mult(rotationX);
    }

    public static Matrix3 genScalingMatrix(float x, float y, float z) {
        return new Matrix3(new float[][]{
                {x,    0.0f, 0.0f},
                {0.0f, y,    0.0f},
                {0.0f, 0.0f, z   }
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
