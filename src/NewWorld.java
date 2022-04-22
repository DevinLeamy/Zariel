import ecs.Entity;
import ecs.EntityManager;
import math.Vector3;
import math.Vector3i;

import java.util.ArrayList;

public class NewWorld {
    public static NewWorld world = new NewWorld();
    public static TextureAtlas atlas;

    public ChunkManager chunkManager;
    public Window window;
    public SkyBox skyBox;
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

    public static NewWorld getInstance() {
        if (NewWorld.world == null) {
            NewWorld.world = new NewWorld();
            NewWorld.world.init();
        }

        return NewWorld.world;
    }


    private NewWorld() {
        this.entityManager = EntityManager.instance;
        this.movementSystem = new MovementSystem();
        this.terrainSystem = new TerrainSystem();
        this.playerInputSystem = new PlayerInputSystem();
        this.goRenderingSystem = new GORenderingSystem();
        this.cameraInputSystem = new CameraInputSystem();
        this.cameraTrackingSystem = new CameraTrackingSystem();
        this.terrainRenderingSystem = new TerrainRenderingSystem();

        this.camera = new Camera(
                (float) Math.PI - (float) Math.PI / 2,
                window.getAspectRatio(),
                new Vector3(0, Config.CHUNK_SIZE * 4, 0)
        );


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
        Entity player = new Entity();
        player.addComponent(new Transform(
            new Velocity(0, 0, 0),
            new Position(Config.WORLD_WIDTH / 2.0f * Config.CHUNK_SIZE, Config.WORLD_HEIGHT * Config.CHUNK_SIZE, Config.WORLD_LENGTH / 2.0f * Config.CHUNK_SIZE),
            new Rotation(0, 0, 0)
        ));
        player.addComponent(new VoxelModel(VoxelGeometry.loadFromFile("res/voxels/sword_man.vox").voxels));
        player.addComponent(new PlayerTag());
        player.addComponent(new Prospective(
                (float) Math.PI - (float) Math.PI / 2,
                window.getAspectRatio(),
                0.01f,
                500f
        ));
        player.addComponent(new CameraTarget());

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
}
