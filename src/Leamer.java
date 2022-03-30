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
    static private ArrayList<Model> gameObjects;
    static private ArrayList<Shader> shaders;

    public static void main(String[] args) {
        window = new Window();
        gameObjects = new ArrayList<>();
        shaders = new ArrayList<>();

        // versions
        System.out.println("LWJGL_VERSION: " + Version.getVersion());
        System.out.println("GL_SHADING_LANGUAGE_VERSION: " + glGetString (GL_SHADING_LANGUAGE_VERSION));
        System.out.println("OPEN_GL_VERSION: " + glGetString (GL_VERSION));

        VertexShader vs = new VertexShader("src/vertex_shader.vert");
        FragmentShader fs = new FragmentShader("src/fragment_shader.frag");
        Mesh cube = MeshLoader.loadMesh("res/square.obj");

        gameObjects.add(new Cube(vs, fs, new Transform(0, 0), cube));
        shaders.add(vs);
        shaders.add(fs);

        while (running) {
            runGameLoop(SECONDS_PER_FRAME);
        }

        for (Model gameObject : gameObjects) {
            gameObject.cleanUp();
        }

        for (Shader shader : shaders) {
            shader.cleanUp();
        }

        // clean up
        window.cleanUp();
    }

    // delta time in seconds
    private static void runGameLoop(float dt) {
        handleInput();
        update(dt);

        prepareRender();
        render();
    }

    private static void update(float dt) {
        for (Model gameObject : gameObjects) {
            gameObject.update(dt);
        }
    }

    private static void prepareRender() {
        window.prepareWindow();
    }


    private static void render() {
        for (Model gameObject : gameObjects) {
            gameObject.render();
        }
        window.render();
    }

    private static void handleInput() {

    }
}