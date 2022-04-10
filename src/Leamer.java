import controller.Controller;
import math.Vector3;
import org.lwjgl.Version;

import static org.lwjgl.opengl.GL41.*;

final public class Leamer {
    final static private int FRAMES_PER_SECOND = 60;
    final static private float SECONDS_PER_FRAME = 1f / FRAMES_PER_SECOND;
    final static private long MILLISECONDS_PER_FRAME = (long) (SECONDS_PER_FRAME * 1000f);
    private static boolean running = true;

    static private Window window;
    static private Player player;
    static private Camera camera;
    static private ChunkManager chunkManager;

    public static void main(String[] args) {
        window = new Window();

        // versions
        System.out.println("LWJGL_VERSION: " + Version.getVersion());
        System.out.println("GL_SHADING_LANGUAGE_VERSION: " + glGetString (GL_SHADING_LANGUAGE_VERSION));
        System.out.println("OPEN_GL_VERSION: " + glGetString (GL_VERSION));

        camera = new Camera(
                (float) Math.PI - (float) Math.PI / 2,
                window.getAspectRatio(),
                new Vector3(0, Config.FLOOR_LEVEL + 6.0f, 0)
        );
        player = new Player(camera);
        chunkManager = new ChunkManager();


        long prevTime = System.currentTimeMillis();
        int frameCount = 0;
        long millisecondsCount = 0;
        while (running) {
            long prevEndTime = prevTime;
            long dtInMillis = System.currentTimeMillis() - prevEndTime;
            float dt = dtInMillis / 1000f;
            prevTime += dtInMillis;

            // updates
            update(dt);
            // rendering
            prepareRender();
            render(camera);

            // waiting
            try {
                long updateTime = System.currentTimeMillis() - prevEndTime;
                long waitTime = Long.max(0L, MILLISECONDS_PER_FRAME - updateTime);
                Thread.sleep(waitTime);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            // update frame count
            long trueDt = System.currentTimeMillis() - prevEndTime;
            millisecondsCount += trueDt;
            ++frameCount;

            if (millisecondsCount >= 1000) {
                window.setTitle(String.format("FPS: %d POS: %s", frameCount, camera.position.toString()));
                millisecondsCount %= 1000;
                frameCount = 0;
            }
        }

        window.cleanUp();
    }

    private static void update(float dt) {
        player.update(dt);
        chunkManager.update(camera);
    }

    private static void prepareRender() {
        window.prepareWindow();
    }


    private static void render(Camera perspective) {
        chunkManager.render(perspective);
        window.render();
    }
}