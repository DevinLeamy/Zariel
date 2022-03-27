public class Leamer {
    final static private int FRAMES_PER_SECOND = 60;
    final static private float SECONDS_PER_FRAME = 1f / FRAMES_PER_SECOND;
    final static private float MILLISECONDS_PER_FRAME = SECONDS_PER_FRAME * 1000f;
    private static boolean running = true;

    public static void main(String[] args) throws Exception {

        Window window = new Window();
        window.run();

//        while (running) {
//            handleInput();
//            update();
//            render();
//
//        }
    }

    private static void update(float dt) {

    }

    private static void render() {

    }

    private void handleInput() {

    }
}