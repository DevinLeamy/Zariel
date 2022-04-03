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
    static private Scene world;
    static private ArrayList<Shader> shaders;
    static private Player player;
    static private Camera camera;

    public static void main(String[] args) {
        window = new Window();
        shaders = new ArrayList<>();
        gameObjects = new ArrayList<>();

        // versions
        System.out.println("LWJGL_VERSION: " + Version.getVersion());
        System.out.println("GL_SHADING_LANGUAGE_VERSION: " + glGetString (GL_SHADING_LANGUAGE_VERSION));
        System.out.println("OPEN_GL_VERSION: " + glGetString (GL_VERSION));

        float groundLevel = 0;

        camera = new Camera(
                (float) Math.PI - (float) Math.PI / 4,
                window.getAspectRatio(),
                new Vector3(0, groundLevel + 6.0f, 0)
        );
        player = new Player(camera);
        world = new Scene(camera);

        VertexShader vs = new VertexShader("src/vertex_shader.vert");
        FragmentShader fs = new FragmentShader("src/fragment_shader.frag");
        shaders.add(vs);
        shaders.add(fs);
        ShaderProgram cubeShader = new ShaderProgram(vs, fs);
        Mesh cube = MeshLoader.loadMesh("res/cube.obj");


        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 10; ++j) {
                Transform modelTrans = new Transform(
                        new Vector3(i * 1.1f, groundLevel, j * 1.1f),
                        Vector3.zeros(),
                        new Vector3(0.5f, 0.5f, 0.5f)
                );
                Cube cubeModel = new Cube(cubeShader, modelTrans, cube);
                world.addModel(cubeModel);
                gameObjects.add(cubeModel);
            }
        }

        Transform carTrans = new Transform(
                new Vector3(-3, groundLevel + 5, 0),
                Vector3.zeros(),
                new Vector3(1, 1, 1)
        );
        CarModel carModel = new CarModel(carTrans);
        world.addModel(carModel);
        gameObjects.add(carModel);

        Transform terrainTransform = new Transform(
                new Vector3(0, groundLevel, 0),
                Vector3.zeros(),
                new Vector3(100, 0.1f, 100)
        );



        world.addModel(new Cube(cubeShader, terrainTransform, cube));

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
        Renderer.renderScene(world);
        window.render();
    }

    private static void handleInput() {

    }
}