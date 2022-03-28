public class Matrix4 {
    private float[][] m;
    Matrix4(float[][] m) {
        m = new float[4][4];
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

    // rotate and create a new matrix
    static Matrix4 rotate(Matrix4 m, float x, float y, float z) {
        return Matrix4.zero();
    }

    // rotate an existing matrix
    void rotate(float x, float y, float z) {

    }

    static Matrix4 identity() {
        return new Matrix4(new float[][] {
                { 1f, 0f, 0f, 0f },
                { 0f, 1f, 0f, 0f },
                { 0f, 0f, 1f, 0f },
                { 0f, 0f, 0f, 1f }
        });
    }
}
