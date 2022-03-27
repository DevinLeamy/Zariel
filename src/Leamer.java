import java.util.ArrayList;

public class Leamer {
    final static private int FRAMES_PER_SECOND = 60;
    final static private float SECONDS_PER_FRAME = 1f / FRAMES_PER_SECOND;
    final static private float MILLISECONDS_PER_FRAME = SECONDS_PER_FRAME * 1000f;
    private static boolean running = true;

    static private Window window;
    static private ArrayList<Model> gameObjects;

    public static void main(String[] args) {
        window = new Window();
        gameObjects = new ArrayList<>();

        gameObjects.add(new Triangle());

        while (running) {
            runGameLoop(SECONDS_PER_FRAME);
        }


        for (Model gameObject : gameObjects) {
            gameObject.cleanUp();
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