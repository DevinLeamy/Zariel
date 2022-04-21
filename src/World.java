import math.Vector3;
import math.Vector3i;

import java.util.ArrayList;
import static org.lwjgl.glfw.GLFW.*;
import java.util.Optional;

public class World {
    public static TextureAtlas atlas;

    private ChunkManager chunkManager;
    public Window window;
    public Player player;
    public DebugCamera debugCamera;
    public SkyBox skyBox;
    public ArrayList<VoxelRenderable> gameObjects;
    private static World world;

    private World() {
        this.gameObjects = new ArrayList<>();
        this.window = new Window();
        this.chunkManager = new ChunkManager();
        this.skyBox = new SkyBox(new String[] {
                "res/images/skybox/right.png",
                "res/images/skybox/left.png",
                "res/images/skybox/top.png",
                "res/images/skybox/bottom.png",
                "res/images/skybox/front.png",
                "res/images/skybox/back.png",
        });
    }

    public void addGameObject(VoxelRenderable gameObject) {
        gameObjects.add(gameObject);
    }

    private void init() {
        atlas = new TextureAtlas("res/images/minecraft_atlas.png", 16, 16);
        this.debugCamera = new DebugCamera();
        this.player = new Player(
                new Transform(
                    new Vector3(
                        Config.WORLD_WIDTH / 2.0f * Config.CHUNK_SIZE,
                        Config.WORLD_HEIGHT * Config.CHUNK_SIZE,
                        Config.WORLD_LENGTH / 2.0f * Config.CHUNK_SIZE
                    ),
                        new Vector3(0, 0, 0),
                        new Vector3(0.5f, 0.5f, 0.5f)
                ),
                new VoxelGeometry(
                    new Block[][][] {
                        {
                            { new Block(true, BlockType.SNOW ) },
                            { new Block(true, BlockType.RED ) },
                            { new Block(true, BlockType.RED ) }
                        }
                    }
                ),
                new Camera(
                        (float) Math.PI - (float) Math.PI / 2,
                        window.getAspectRatio(),
                        new Vector3(0, Config.CHUNK_SIZE * 4, 0)
                )
        );
    }

    public static World getInstance() {
        if (World.world == null) {
            World.world = new World();
            World.world.init();
        }

        return World.world;
    }

    public static void updateDebug() {
        Controller controller = Controller.getInstance();
        if (controller.keyPressed(GLFW_KEY_1)) {
            Config.debug1 += 0.01;
            World.getInstance().chunkManager.clearAll();
        }
        if (controller.keyPressed(GLFW_KEY_2)) {
            Config.debug1 -= 0.01;
            World.getInstance().chunkManager.clearAll();
        }
        if (controller.keyPressed(GLFW_KEY_3)) {
            Config.debug2 += 0.01;
            World.getInstance().chunkManager.clearAll();
        }
        if (controller.keyPressed(GLFW_KEY_4)) {
            Config.debug2 -= 0.01;
            World.getInstance().chunkManager.clearAll();
        }
        if (controller.keyPressed(GLFW_KEY_5)) {
            Config.debug3 += 0.01;
            World.getInstance().chunkManager.clearAll();
        }
        if (controller.keyPressed(GLFW_KEY_6)) {
            Config.debug3 -= 0.01;
            World.getInstance().chunkManager.clearAll();
        }
        if (controller.keyDown(GLFW_KEY_8)) {
            Config.orthographic = true;
        }
        if (controller.keyDown(GLFW_KEY_7)) {
            Config.orthographic = false;
        }

//        System.out.printf("Debug 1: %f Debug 2: %f Debug 3: %f\n", Config.debug1, Config.debug2, Config.debug3);
    }

    public void update(float dt) {
        ArrayList<Action> updates = new ArrayList<>();
        // Game Logic Updates
        if (Debug.DEBUG) {
            debugCamera.update(dt);
        } else {
            updates.addAll(player.update(dt));
        }

        for (VoxelRenderable renderable : gameObjects) {
            updates.addAll(renderable.update(dt));
        }

        updateDebug();

        for (Action action : updates) {
            action.execute();
        }

        chunkManager.update(getPerspective());
        render();
    }

    private void prepareRender() {
        window.prepareWindow();
    }

    public Camera getPerspective() {
        if (Debug.DEBUG) {
            return debugCamera.getPerspective();
        } else {
            return player.getPerspective();
        }
    }


    private void render() {
        prepareRender();
        if (!Config.orthographic && !Debug.DEBUG) {
            skyBox.render(getPerspective());
        }
        chunkManager.render(getPerspective());
        player.render();
        for (VoxelRenderable renderable : gameObjects) {
            renderable.render();
        }
        window.render();


    }

    public boolean blockIsActive(int x, int y, int z) {
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
