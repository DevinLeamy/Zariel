package math;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class Matrix4 {
    private float[][] m;
    public Matrix4(float[][] m) {
        this.m = new float[4][4];
        set(m);
    }

    void set(float[][] m) {
        assert(m.length == 4 && m[0].length == 4);

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                this.m[i][j] = m[i][j];
            }
        }
    }

    static Matrix4 zero() {
        return new Matrix4(new float[][] {
                { 0f, 0f, 0f, 0f },
                { 0f, 0f, 0f, 0f },
                { 0f, 0f, 0f, 0f },
                { 0f, 0f, 0f, 0f }
            });
    }

    public static Matrix4 mult(Matrix4 m1, Matrix4 m2) {
        float[][] res = new float[4][4];

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                float dot = 0;
                for (int k = 0; k < 4; ++k) {
                    dot += m1.m[i][k] * m2.m[k][j];
                }

                res[i][j] = dot;
            }
        }

        return new Matrix4(res);
    }

    public Matrix4 mult(Matrix4 other) {
        set(Matrix4.mult(this, other).m);
        return this;
    }

    public static Matrix4 genRotationMatrix(float x, float y, float z) {
        Matrix4 rotationX = new Matrix4(new float[][]{
                {1.0f, 0.0f,                0.0f,                 0.0f},
                {0.0f, (float) Math.cos(x), (float) -Math.sin(x), 0.0f},
                {0.0f, (float) Math.sin(x), (float) Math.cos(x),  0.0f},
                {0.0f, 0.0f,                0.0f,                 1.0f},
        });

        Matrix4 rotationY = new Matrix4(new float[][]{
                {(float) Math.cos(y),  0.0f, (float) -Math.sin(y), 0.0f},
                {0.0f,                 1.0f, 0.0f,                 0.0f},
                {(float) Math.sin(y),  0.0f, (float) Math.cos(y),  0.0f},
                {0.0f,                 0.0f, 0.0f,                 1.0f}
        });

        Matrix4 rotationZ = new Matrix4(new float[][]{
                {(float) Math.cos(z), (float) -Math.sin(z), 0.0f, 0.0f},
                {(float) Math.sin(z), (float) Math.cos(z),  0.0f, 0.0f},
                {0.0f,                0.0f,                 1.0f, 0.0f},
                {0.0f,                0.0f,                 0.0f, 1.0f}
        });

        return rotationZ.mult(rotationY).mult(rotationX);
    }

    public static Matrix4 genScalingMatrix(float x, float y, float z) {
        return new Matrix4(new float[][]{
                {x,    0.0f, 0.0f, 0.0f},
                {0.0f, y,    0.0f, 0.0f},
                {0.0f, 0.0f, z,    0.0f},
                {0.0f, 0.0f, 0.0f, 1.0f}
        });
    }

    public static Matrix4 genTranslationMatrix(float x, float y, float z) {
        return new Matrix4(new float[][]{
                {1.0f, 0.0f, 0.0f, x   },
                {0.0f, 1.0f, 0.0f, y   },
                {0.0f, 0.0f, 1.0f, z   },
                {0.0f, 0.0f, 0.0f, 1.0f}
        });
    }

    public static Matrix4 identity() {
        return new Matrix4(new float[][] {
                { 1f, 0f, 0f, 0f },
                { 0f, 1f, 0f, 0f },
                { 0f, 0f, 1f, 0f },
                { 0f, 0f, 0f, 1f }
        });
    }

    public FloatBuffer toFloatBuffer() {
        FloatBuffer fb = BufferUtils.createFloatBuffer(4 * 4);
        for (int i = 0; i < 4; ++i) {
            fb.put(m[i]);
        }
        fb.flip();

        return fb;
    }
}
