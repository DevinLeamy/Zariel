import math.Vector3;

public class World {
    private ChunkManager chunkManager;
    public Window window;
    private Player player;
    public Camera camera;
    private static World world;

    private World() {
        window = new Window();
        chunkManager = new ChunkManager();
        camera = new Camera(
                (float) Math.PI - (float) Math.PI / 2,
                window.getAspectRatio(),
                new Vector3(0, Config.FLOOR_LEVEL + 6.0f, 0)
        );
        player = new Player(camera);
    }

    public static World getInstance() {
        if (World.world == null) {
            World.world = new World();
        }

        return World.world;
    }

    public void update(float dt) {
        player.update(dt);
        chunkManager.update(camera);
        render();
    }

    private void prepareRender() {
        window.prepareWindow();
    }


    private void render() {
        prepareRender();
        chunkManager.render(camera);
        window.render();
    }

    public boolean blockIsActive(int x, int y, int z) {
        return chunkManager.blockIsActive(x, y, z);
    }
}
