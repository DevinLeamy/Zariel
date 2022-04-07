import controller.Controller;
import math.Vector3;
import org.lwjgl.Version;

import java.util.ArrayList;
import rendering.*;

import static org.lwjgl.opengl.GL41.*;

final public class Leamer {
    final static private int FRAMES_PER_SECOND = 60;
    final static private float SECONDS_PER_FRAME = 1f / FRAMES_PER_SECOND;
    final static private float MILLISECONDS_PER_FRAME = SECONDS_PER_FRAME * 1000f;
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

        float groundLevel = 0;

        camera = new Camera(
                (float) Math.PI - (float) Math.PI / 4,
                window.getAspectRatio(),
                new Vector3(9, groundLevel + 20.0f, 22)
        );
        player = new Player(camera);
        chunkManager = new ChunkManager();

        while (running) {
            float dt = SECONDS_PER_FRAME;
            handleInput();
            update(dt);

            prepareRender();
            render(camera);
        }

        window.cleanUp();
    }

    private static void update(float dt) {
        player.update(dt);
        chunkManager.update(camera);
        window.setTitle(camera.position.toString());
    }

    private static void prepareRender() {
        window.prepareWindow();
    }


    private static void render(Camera perspective) {
        chunkManager.render(perspective);
        window.render();
    }

    private static void handleInput() {

    }
}