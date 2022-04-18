import math.Vector3;
import math.Vector3i;

import java.util.ArrayList;
import java.util.Optional;

public class World {
    private ChunkManager chunkManager;
    public Window window;
    public Player player;
    public DebugCamera debugCamera;
    public SkyBox skyBox;
    private static World world;

    private World() {
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

    private void init() {
//        NoiseMapGenerator noiseMapGenerator = NoiseMapGenerator.getInstance();
        Camera playerPerspective = new Camera(
                (float) Math.PI - (float) Math.PI / 2,
                window.getAspectRatio(),
                new Vector3(0, 17 + 6.0f, 0)
        );
        this.debugCamera = new DebugCamera();
        this.player = new Player(
                new Transform(
                    new Vector3(
                        Config.WORLD_WIDTH / 2.0f * Config.CHUNK_SIZE,
                        60,
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
                playerPerspective
        );
    }

    public static World getInstance() {
        if (World.world == null) {
            World.world = new World();
            World.world.init();
        }

        return World.world;
    }

    public void update(float dt) {
        ArrayList<Action> updates = new ArrayList<>();
        // Game Logic Updates
        if (Debug.DEBUG) {
            debugCamera.update(dt);
        } else {
            updates.addAll(player.update(dt));
        }
        // ai.updates(dt) etc...

        for (Action action : updates) {
            if (action instanceof BlockUpdateAction) {
                action.execute();
            }
        }

        chunkManager.update(getPerspective());
        render();
    }

    private void prepareRender() {
        window.prepareWindow();
    }

    private Camera getPerspective() {
        if (Debug.DEBUG) {
            return debugCamera.getPerspective();
        } else {
            return player.getPerspective();
        }
    }


    private void render() {
        prepareRender();
        skyBox.render(getPerspective());
        chunkManager.render(getPerspective());
        player.render();
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
