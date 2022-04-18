import math.Vector3;
import math.Vector3i;

import java.util.ArrayList;
import java.util.Optional;

public class World {
    private ChunkManager chunkManager;
    public Window window;
    public Player player;
    public Camera camera;
    public SkyBox skyBox;
    private static World world;

    private World() {
        window = new Window();
        chunkManager = new ChunkManager();
        camera = new Camera(
                (float) Math.PI - (float) Math.PI / 2,
                window.getAspectRatio(),
                new Vector3(0, 17 + 6.0f, 0)
        );
        player = new Player(new Vector3(
                Config.WORLD_WIDTH / 2.0f * Config.CHUNK_SIZE,
                17,
                Config.WORLD_LENGTH / 2.0f * Config.CHUNK_SIZE
        ), camera);
        skyBox = new SkyBox(new String[] {
                "res/images/skybox/right.png",
                "res/images/skybox/left.png",
                "res/images/skybox/top.png",
                "res/images/skybox/bottom.png",
                "res/images/skybox/front.png",
                "res/images/skybox/back.png",
        });
    }

    public static World getInstance() {
        if (World.world == null) {
            World.world = new World();
        }

        return World.world;
    }

    public void update(float dt) {
        ArrayList<Action> updates = new ArrayList<>();
        // Game Logic Updates
        updates.addAll(player.update(dt));
        // ai.updates(dt) etc...

        for (Action action : updates) {
            if (action instanceof BlockUpdateAction) {
                action.execute();
            }
        }

        // Chunk updates
        chunkManager.update(camera);
        // ---
        render();
    }

    private void prepareRender() {
        window.prepareWindow();
    }


    private void render() {
        prepareRender();
        skyBox.render(camera);
        chunkManager.render(camera);
        window.render();
    }

    // TODO: REMOVE
    public boolean blockIsActive(int x, int y, int z) {
//        return false;
        return blockIsActive(new Vector3i(x, y, z));
    }

    public boolean blockIsActive(Vector3i loc) {
        Optional<Block> block = chunkManager.getBlock(loc);
        return block.isPresent() && block.get().isActive();
    }

    public Optional<Block> getBlock(Vector3i loc) {
        return chunkManager.getBlock(loc);
    }
}
