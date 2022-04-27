package util;

import math.Vector3;

import java.util.ArrayList;
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

    public static float radiansToDegrees(float radians) {
        return radians * (float) (180.0f / Math.PI);
    }

    public static Vector3 radiansToDegrees(Vector3 radians) {
        return new Vector3(
                radiansToDegrees(radians.x),
                radiansToDegrees(radians.y),
                radiansToDegrees(radians.z)
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

    public static ArrayList<Integer> range(int low, int high) {
        ArrayList<Integer> range = new ArrayList<>();
        for (int i = low; i < high; ++i) {
            range.add(i);
        }

        return range;
    }
}
