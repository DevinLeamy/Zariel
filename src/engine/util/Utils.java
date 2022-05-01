package engine.util;

import math.Matrix4;
import math.Vector3;
import math.Vector3i;
import math.Vector4;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
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

    public static FloatBuffer createFloatBuffer(Vector3 v) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
        buffer.put(v.toArray());
        buffer.flip();

        return buffer;
    }

    public static FloatBuffer createFloatBuffer(Vector4 v) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
        buffer.put(v.toArray());
        buffer.flip();

        return buffer;
    }

    public static FloatBuffer createFloatBuffer(Vector3i v) {
        return Utils.createFloatBuffer(v.toVector3());
    }

    public static FloatBuffer createFloatBuffer(float v) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(1);
        buffer.put(v);
        buffer.flip();

        return buffer;
    }

    public static FloatBuffer createFloatBuffer(Matrix4 v) {
        return v.toFloatBuffer();
    }

    public static Vector3 createRGB(int red, int green, int blue) {
        return new Vector3(red / 256f, green / 256f, blue / 256f);
    }
}
