import ecs.Entity;
import ecs.EntityManager;
import math.Vector3;
import math.Vector3i;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class World {
    public static World world;
    public static TextureAtlas atlas;

    public ChunkManager chunkManager;
    public Window window;
    public SkyBox skyBox;
    public EntityManager entityManager;
    private Camera camera;
    private Camera debugCamera;

    // Systems
    private MovementSystem movementSystem;
    private TerrainSystem terrainSystem;
    private CameraInputSystem cameraInputSystem;
    private PlayerInputSystem playerInputSystem;
    private GORenderingSystem goRenderingSystem;
    private CameraTrackingSystem cameraTrackingSystem;
    private TerrainRenderingSystem terrainRenderingSystem;
    private FallingSystem fallingSystem;
    private TerrainCollisionDetectionSystem terrainCollisionDetectionSystem;
    private TerrainCollisionResolutionSystem terrainCollisionResolutionSystem;
    private DespawnSystem despawnSystem;
    private LifeTimeSystem lifeTimeSystem;
    private SkyBoxRenderingSystem skyBoxRenderingSystem;
    private DebugCameraInputSystem debugCameraInputSystem;
    private AnimationSystem animationSystem;

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
        this.debugCamera = new Camera(
                (float) Math.PI - (float) Math.PI / 2,
                window.getAspectRatio(),
                new Vector3(Config.WORLD_WIDTH / 2.0f * Config.CHUNK_SIZE, Config.WORLD_HEIGHT * Config.CHUNK_SIZE, Config.WORLD_LENGTH / 2.0f * Config.CHUNK_SIZE)
        );

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
        atlas = new TextureAtlas("res/images/minecraft_atlas.png", 16, 16);

        this.movementSystem = new MovementSystem();
        this.terrainSystem = new TerrainSystem();
        this.playerInputSystem = new PlayerInputSystem();
        this.goRenderingSystem = new GORenderingSystem();
        this.cameraInputSystem = new CameraInputSystem();
        this.cameraTrackingSystem = new CameraTrackingSystem();
        this.terrainRenderingSystem = new TerrainRenderingSystem();
        this.fallingSystem = new FallingSystem();
        this.terrainCollisionResolutionSystem = new TerrainCollisionResolutionSystem();
        this.terrainCollisionDetectionSystem = new TerrainCollisionDetectionSystem();
        this.despawnSystem = new DespawnSystem();
        this.lifeTimeSystem = new LifeTimeSystem();
        this.skyBoxRenderingSystem = new SkyBoxRenderingSystem();
        this.debugCameraInputSystem = new DebugCameraInputSystem();
        this.animationSystem = new AnimationSystem();

        ArrayList<String> playerFrames = new ArrayList<>(Arrays.asList(
                "res/voxels/player_animation/frame1.vox",
                "res/voxels/player_animation/frame2.vox",
                "res/voxels/player_animation/frame3.vox",
                "res/voxels/player_animation/frame4.vox",
                "res/voxels/player_animation/frame5.vox",
                "res/voxels/player_animation/frame6.vox",
                "res/voxels/player_animation/frame7.vox",
                "res/voxels/player_animation/frame8.vox",
                "res/voxels/player_animation/frame9.vox",
                "res/voxels/player_animation/frame10.vox",
                "res/voxels/player_animation/frame11.vox",
                "res/voxels/player_animation/frame12.vox",
                "res/voxels/player_animation/frame13.vox",
                "res/voxels/player_animation/frame14.vox",
                "res/voxels/player_animation/frame13.vox",
                "res/voxels/player_animation/frame12.vox",
                "res/voxels/player_animation/frame11.vox",
                "res/voxels/player_animation/frame10.vox",
                "res/voxels/player_animation/frame9.vox",
                "res/voxels/player_animation/frame8.vox",
                "res/voxels/player_animation/frame7.vox",
                "res/voxels/player_animation/frame6.vox",
                "res/voxels/player_animation/frame5.vox",
                "res/voxels/player_animation/frame4.vox",
                "res/voxels/player_animation/frame3.vox",
                "res/voxels/player_animation/frame2.vox",
                "res/voxels/player_animation/frame1.vox"
        ));

        Entity player = new Entity();
        player.addComponent(new Transform(
                new Vector3(Config.WORLD_WIDTH / 2.0f * Config.CHUNK_SIZE, Config.WORLD_HEIGHT * Config.CHUNK_SIZE, Config.WORLD_LENGTH / 2.0f * Config.CHUNK_SIZE),
                new Vector3(0, 0, 0),
                new Vector3(0.1f, 0.1f, 0.1f)
        ));
        player.addComponent(new Animation(playerFrames, 3));
        player.addComponent(new PlayerTag());
        player.addComponent(new Prospective(
                (float) Math.PI - (float) Math.PI / 2,
                window.getAspectRatio(),
                0.01f,
                500f
        ));
        player.addComponent(new CameraTarget(new Vector3(0, 5, -5)));
        player.addComponent(new Dynamics(
                Vector3.zeros(),
                Vector3.zeros()
        ));
        player.addComponent(new GravityTag());
        player.addComponent(new RigidBody(
                new BoundingBox(1, 2, 1),
                "PLAYER"
        ));

        entityManager.addEntity(player);

        Entity metaViewer = new Entity();
        metaViewer.addComponent(new DebugCameraConfig());

        entityManager.addEntity(metaViewer);
    }

    private void debugInputHandler() {
        Controller controller = Controller.getInstance();

        if (controller.takeKeyPressState(GLFW_KEY_0) == GLFW_PRESS) {
            Debug.DEBUG = true;
        } else if (controller.takeKeyPressState(GLFW_KEY_9) == GLFW_PRESS) {
            Debug.DEBUG = false;
        }
    }

    public void update(float dt) {
        int NO_DELTA = 0;
        {
            debugInputHandler();
            terrainSystem.update(dt);

            cameraInputSystem.update(dt);

            if (Debug.DEBUG) {
                // control the camera
                debugCameraInputSystem.update(dt);
            } else {
                // control the player
                playerInputSystem.update(dt);
            }

            fallingSystem.update(dt);
            movementSystem.update(dt);

            terrainCollisionDetectionSystem.update(dt);
            terrainCollisionResolutionSystem.update(dt);

            lifeTimeSystem.update(dt);
            despawnSystem.update(dt);

            if (!Debug.DEBUG) {
                cameraTrackingSystem.update(dt);
            }

            animationSystem.update(dt);
        }
        prepareRender();
        {
            skyBoxRenderingSystem.update(NO_DELTA);
            terrainRenderingSystem.update(NO_DELTA);
            goRenderingSystem.update(NO_DELTA);
        }
        window.render();
    }

    private void prepareRender() {
        window.prepareWindow();
    }

    public Camera getPerspective() {
        if (Debug.DEBUG) {
            return debugCamera;
        }
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
