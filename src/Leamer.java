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
    static private ArrayList<Model> gameObjects;
    static private ArrayList<Shader> shaders;
    static private Player player;
    static private Camera camera;

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
        shaders.add(vs);
        shaders.add(fs);

        Mesh cube = MeshLoader.loadMesh("res/cube.obj");
        for (int i = 0; i < 10; ++i) {
            Transform cubeTransform = new Transform(
                    Utils.randVector3().scale(-3),
                    Utils.randVector3().scale((float) Math.PI),
                    new Vector3(0.5f, 0.5f, 0.5f)
            );
            gameObjects.add(new Cube(vs, fs, cubeTransform, cube));
        }
//        Transform cubeTransform = new Transform(
//                new Vector3(4, 0, -2),
//                Utils.randVector3().scale((float) Math.PI),
//                new Vector3(0.5f, 0.5f, 0.5f)
//        );
//        gameObjects.add(new Cube(vs, fs, cubeTransform, cube));

        Transform terrainTransform = new Transform(
                new Vector3(0, -5, 0),
                Vector3.zeros(),
                new Vector3(100, 0.1f, 100)
        );
        gameObjects.add(new Cube(vs, fs, terrainTransform, cube));

        camera = new Camera(
                (float) Math.PI - (float) Math.PI / 4,
                window.getAspectRatio(),
                new Vector3(0, 0, 6.0f)
        );
        player = new Player(camera);


        while (running) {
            float dt = SECONDS_PER_FRAME;
            handleInput();
            update(dt);

            prepareRender();
            render();
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

    private static void update(float dt) {
        player.update(dt);
        for (Model gameObject : gameObjects) {
            gameObject.update(dt);
        }

        window.setTitle(camera.position.toString());
    }

    private static void prepareRender() {
        window.prepareWindow();
    }


    private static void render() {
        for (Model gameObject : gameObjects) {
            gameObject.render(camera);
        }
        window.render();
    }

    private static void handleInput() {

    }
}