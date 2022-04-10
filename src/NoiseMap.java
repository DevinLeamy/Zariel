import math.Vector2;

public class NoiseMap {
    private static int BLOCK_SIZE = 32;
    Vector2[][] grads;
    int gridH, gridW;
    int blockH, blockW;

    public NoiseMap(int n, int m) {
        this.gridH = n;
        this.gridW = m;
        grads = genGradientVectors(n / BLOCK_SIZE + 1, m / BLOCK_SIZE + 1);
        blockH = (int) Math.floor(n / (float) BLOCK_SIZE);
        blockW = (int) Math.floor(m / (float) BLOCK_SIZE);
    }

    /**
     * Calculate noise at a given point
     * @param n: Row
     * @param m: Col
     * @return: Noise value at point (n, m)
     */
    public float noise(int n, int m) {
        Vector2 point = new Vector2(n / (float) BLOCK_SIZE, m / (float) BLOCK_SIZE);

        int row = (int) Math.floor(n / (float) BLOCK_SIZE);
        int col = (int) Math.floor(m / (float) BLOCK_SIZE);

        if (row + 1 > blockH || col + 1 > blockW) {
            return 0;
        }

        // grads
        Vector2 topLeftGrad = grads[row][col];
        Vector2 bottomLeftGrad = grads[row + 1][col];
        Vector2 topRightGrad = grads[row][col + 1];
        Vector2 bottomRightGrad = grads[row + 1][col + 1];

        // points
        Vector2 topLeftPoint = new Vector2(row, col);
        Vector2 bottomLeftPoint = new Vector2(row + 1, col);
        Vector2 topRightPoint = new Vector2(row, col + 1);
        Vector2 bottomRightPoint = new Vector2(row + 1, col + 1);

        // offsets
        Vector2 topLeftOffset = Vector2.sub(point, topLeftPoint);
        Vector2 bottomLeftOffset = Vector2.sub(point, bottomLeftPoint);
        Vector2 topRightOffset = Vector2.sub(point, topRightPoint);
        Vector2 bottomRightOffset = Vector2.sub(point, bottomRightPoint);

        // dot products
        float topLeftDot = Vector2.dot(topLeftGrad, topLeftOffset);
        float bottomLeftDot = Vector2.dot(bottomLeftGrad, bottomLeftOffset);
        float topRightDot = Vector2.dot(topRightGrad, topRightOffset);
        float bottomRightDot = Vector2.dot(bottomRightGrad, bottomRightOffset);

        // interpolation weights \in (0, 1)
        Vector2 weights = new Vector2(topLeftOffset.x, topLeftOffset.y);

        // interpolate corners
        return interpolate(
                interpolate(topLeftDot, bottomLeftDot, weights.x),
                interpolate(topRightDot, bottomRightDot, weights.x),
                weights.y
        );

    }

    private float interpolate(float a, float b, float weight) {
        return a + (b - a) * smoothstep(weight);
    }

    private float smoothstep(float x) {
        if (x <= 0) return 0f;
        else if (x > 1) return 1.0f;
        else {
            return (float) (3 * Math.pow(x, 2) - 2 * Math.pow(x, 3));
        }
    }

    private float fade(float x){
        return ((6 * x - 15) * x + 10) * x * x * x;
    }

    private Vector2[][] genGradientVectors(int n, int m) {
        Vector2[][] grads = new Vector2[n][m];

        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                grads[i][j] = genGradientVector();
            }
        }

        return grads;
    }

    private Vector2 genGradientVector() {
        // x, y \in (-1, 1)
        return new Vector2(
                (float) Math.random() * 2f - 1f,
                (float) Math.random() * 2f - 1f
        );
    }


}
