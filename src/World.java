import ecs.Entity;
import ecs.EntityManager;
import math.Vector3;
import math.Vector3i;

import java.util.ArrayList;
import java.util.Optional;

public class World {
    public static World world;
    public static TextureAtlas atlas;

    public ChunkManager chunkManager;
    public Window window;
//    public SkyBox skyBox;
    public EntityManager entityManager;
    public Camera camera;

    // Systems
    private MovementSystem movementSystem;
    private TerrainSystem terrainSystem;
    private CameraInputSystem cameraInputSystem;
    private PlayerInputSystem playerInputSystem;
    private GORenderingSystem goRenderingSystem;
    private CameraTrackingSystem cameraTrackingSystem;
    private TerrainRenderingSystem terrainRenderingSystem;

    public static World getInstance() {
        if (World.world == null) {
            World.world = new World();
            World.world.init();
        }

        return World.world;
    }


    private World() {
        this.entityManager = EntityManager.instance;
        this.window = new Window();
        this.chunkManager = new ChunkManager();
        this.camera = new Camera(
                (float) Math.PI - (float) Math.PI / 2,
                window.getAspectRatio(),
                Vector3.zeros()
        );


//        this.skyBox = new SkyBox(new String[] {
//                "res/images/skybox/right.png",
//                "res/images/skybox/left.png",
//                "res/images/skybox/top.png",
//                "res/images/skybox/bottom.png",
//                "res/images/skybox/front.png",
//                "res/images/skybox/back.png",
//        });
    }

    private void init() {
        atlas = new TextureAtlas("res/images/minecraft_atlas.png", 16, 16);

        this.movementSystem = new MovementSystem();
        this.terrainSystem = new TerrainSystem();
        this.playerInputSystem = new PlayerInputSystem();
        this.goRenderingSystem = new GORenderingSystem();
        this.cameraInputSystem = new CameraInputSystem();
        this.cameraTrackingSystem = new CameraTrackingSystem();
        this.terrainRenderingSystem = new TerrainRenderingSystem();

        Entity player = new Entity();
        player.addComponent(new Transform(
                new Vector3(Config.WORLD_WIDTH / 2.0f * Config.CHUNK_SIZE, Config.WORLD_HEIGHT * Config.CHUNK_SIZE, Config.WORLD_LENGTH / 2.0f * Config.CHUNK_SIZE),
                new Vector3(0, 0, 0),
                new Vector3(0.1f, 0.1f, 0.1f)
        ));
        player.addComponent(new VoxelModel(VoxelGeometry.loadFromFile("res/voxels/sword_man.vox").voxels));
        player.addComponent(new PlayerTag());
        player.addComponent(new Prospective(
                (float) Math.PI - (float) Math.PI / 2,
                window.getAspectRatio(),
                0.01f,
                500f
        ));
        player.addComponent(new CameraTarget(new Vector3(0, 5, -5)));

        entityManager.addEntity(player);
    }

    public void update(float dt) {
        int NO_DELTA = 0;
        {
            movementSystem.update(dt);
            terrainSystem.update(dt);
            cameraInputSystem.update(dt);
            playerInputSystem.update(dt);
            cameraTrackingSystem.update(dt);
        }
        prepareRender();
        {
            terrainRenderingSystem.update(NO_DELTA);
            goRenderingSystem.update(NO_DELTA);
        }
        window.render();
    }

    private void prepareRender() {
        window.prepareWindow();
    }

    public Camera getPerspective() {
        return camera;
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
