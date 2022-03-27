import java.util.Date;

public class Utils {
    public static float getTimeInSeconds() {
        return new Date().getTime() / 1000f;
    }
}
