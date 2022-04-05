import java.nio.FloatBuffer;

public class VBO {
    FloatBuffer buffer;

    public VBO() {

    }

    public void put(float[] data) {
        buffer.put(data);
    }

    public FloatBuffer getBuffer() {
        buffer.flip();

        return buffer;
    }
}
