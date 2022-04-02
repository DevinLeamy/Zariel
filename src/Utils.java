import math.Vector3;

import java.util.Date;

public class Utils {
    public static float getTimeInSeconds() {
        return new Date().getTime() / 1000f;
    }

    public static Vector3 randVector3() {
        return new Vector3(
                (float) Math.random(),
                (float) Math.random(),
                (float) Math.random()
        );
    }
}
