package engine.main;

import engine.config.Config;
import math.Vector2;

import java.util.HashMap;
import java.util.Map;

/**
 * Note: Noise map: Each point is a world coordinate
 *       Noise map grid: Each point is a gradient
 */
public class NoiseMapGenerator {
    private static Map<Integer, NoiseMapGenerator> instances = new HashMap<>();
    private Map<Integer, Vector2> grads;
    private int blockSize;

    private NoiseMapGenerator(int blockSize) {
        this.blockSize = blockSize;

        grads = new HashMap<>();
    }

    public static NoiseMapGenerator getInstance() {
        int blockSize = Config.CHUNK_SIZE;
        if (!instances.containsKey(blockSize)) {
            instances.put(blockSize, new NoiseMapGenerator(blockSize));
        }
        return instances.get(blockSize);
    }

    /**
     * @param row: Row in noise map grid
     * @param col: Col in noise map grid
     * @return Gradient at a grid point
     */
    private Vector2 getGradient(int row, int col) {
        Vector2 location = new Vector2(row, col);
        if (!grads.containsKey(location.hashCode())) {
            grads.put(location.hashCode(), genGradientVector());
        }
        return grads.get(location.hashCode());
    }

    private Vector2 genGradientVector() {
        // x, y \in (-1, 1)
        return new Vector2(
                (float) Math.random() * 2f - 1f,
                (float) Math.random() * 2f - 1f
        );
    }

    /**
     * @param n: Row in noise map
     * @param m: Col in noise map
     * @return Noise at point in noise map
     */
    public float noise(int n, int m) {
        // noise map point in noise map grid coords
        Vector2 point = new Vector2(n / (float) blockSize, m / (float) blockSize);

        int row = (int) Math.floor(point.x);
        int col = (int) Math.floor(point.y);

        // grads
        Vector2 topLeftGrad = getGradient(row, col);
        Vector2 bottomLeftGrad = getGradient(row + 1, col);
        Vector2 topRightGrad = getGradient(row, col + 1);
        Vector2 bottomRightGrad = getGradient(row + 1, col + 1);

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
        float noise = interpolate(
                interpolate(topLeftDot, bottomLeftDot, weights.x),
                interpolate(topRightDot, bottomRightDot, weights.x),
                weights.y
        );
        // normalize (0 - 1)
        return (noise + 1.0f) / 2.0f;
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

    /**
     * Generate a 2D noise map of a given size at a given location.
     * @note All coordinates are noise map coordinates
     */
    public float[][] generateNoiseMap(int minRow, int minCol, int rows, int cols) {
        float[][] noiseMap = new float[rows][cols];

        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                int n = i + minRow;
                int m = j + minCol;
                noiseMap[i][j] = noise(n, m);
            }
        }

        return noiseMap;
    }
}
