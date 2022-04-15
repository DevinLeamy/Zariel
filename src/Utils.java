import math.Vector3;

import java.util.Date;

public class Utils {
    public static float getTimeInSeconds() {
        return new Date().getTime() / 1000f;
    }

    public static Vector3 randVector3() {
        return new Vector3(
                (float) (Math.random() - 0.5f) * 2,
                (float) (Math.random() - 0.5f) * 2,
                (float) (Math.random() - 0.5f) * 2
        );
    }

    public static float clamp(float low, float high, float v) {
        return Math.max(Math.min(high, v), low);
    }

    // TODO: move to Math package
    // Handles mods for negative numbers as well
    public static int mod(int x, int mod) {
        return ((x % mod) + mod) % mod;
    }
}
