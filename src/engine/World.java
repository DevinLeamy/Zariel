package engine;

import engine.components.*;
import engine.ecs.*;
import engine.graphics.TextureAtlas;
import engine.graphics.VoxelGeometry;
import engine.main.Block;
import engine.main.BoundingBox;
import engine.main.Camera;
import engine.main.ChunkManager;
import engine.main.Debug;
import engine.main.EntityGenerator;
import engine.main.SkyBox;
import engine.main.SystemDriver;
import engine.systems.*;
import engine.ui.GameMenu;
import engine.ui.UI;
import engine.view.Window;
import math.*;

import java.util.Optional;

import static engine.GameState.MENU;
import static engine.GameState.PAUSED;
import static engine.GameState.PLAYING;

public class World {
    private GameState gameState;
    public static World world;
    public static TextureAtlas atlas;

    public ChunkManager chunkManager;
    public Window window;
    public SkyBox skyBox;
    public EntityManager entityManager;
    public UI ui;
    private Camera camera;
    private Camera debugCamera;

    private SystemDriver systemDriver;

    // Systems
    private GORenderingSystem goRenderingSystem;
    private TerrainRenderingSystem terrainRenderingSystem;
    private SkyBoxRenderingSystem skyBoxRenderingSystem;
    private BBoxRenderingSystem bboxRenderingSystem;
    private UIRenderingSystem uiRenderingSystem;

    public static World getInstance() {
        if (World.world == null) {
            World.world = new World();
            World.world.init();
        }

        return World.world;
    }

    private World() {
        this.gameState = PLAYING;
        this.entityManager = EntityManager.instance;
        this.window = new Window();
        this.chunkManager = new ChunkManager();
        this.ui = new UI();
        this.systemDriver = new SystemDriver();
        this.camera = new Camera(
                (float) Math.PI - (float) Math.PI / 3,
                window.getAspectRatio(),
                Vector3.zeros()
        );
        this.debugCamera = new Camera(
                (float) Math.PI - (float) Math.PI / 2,
                window.getAspectRatio(),
                new Vector3(6, 5, 42)
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
        initializeUI();
        initializeSystems();
        initializeEntities();
    }

    private void initializeUI() {
        GameMenu.init();
    }

    private void initializeSystems() {
        systemDriver.registerSystem(new DebugInputSystem(), PLAYING, GameState.DEBUG, MENU, PAUSED);
        systemDriver.registerSystem(new TerrainSystem(), PLAYING, GameState.DEBUG);
        systemDriver.registerSystem(new CameraInputSystem(), PLAYING, GameState.DEBUG);
        systemDriver.registerSystem(new DebugCameraInputSystem(), GameState.DEBUG);
        systemDriver.registerSystem(new PlayerInputSystem(), GameState.PLAYING);
        systemDriver.registerSystem(new FallingSystem(), PLAYING, GameState.DEBUG);
        systemDriver.registerSystem(new CarPhysicsSystem(), PLAYING, GameState.DEBUG);
        systemDriver.registerSystem(new MovementSystem(), PLAYING, GameState.DEBUG);

        systemDriver.registerSystem(new TerrainCollisionDetectionSystem(), PLAYING, GameState.DEBUG);
        systemDriver.registerSystem(new TerrainCollisionResolutionSystem(), PLAYING, GameState.DEBUG);
        systemDriver.registerSystem(new LifeTimeSystem(), PLAYING, GameState.DEBUG);
        systemDriver.registerSystem(new DespawnSystem(), PLAYING, GameState.DEBUG);
        systemDriver.registerSystem(new CameraTrackingSystem(), PLAYING);
        systemDriver.registerSystem(new AnimationSystem(), PLAYING, GameState.DEBUG);

        this.goRenderingSystem = new GORenderingSystem();
        this.terrainRenderingSystem = new TerrainRenderingSystem();
        this.skyBoxRenderingSystem = new SkyBoxRenderingSystem();
        this.bboxRenderingSystem = new BBoxRenderingSystem();
        this.uiRenderingSystem = new UIRenderingSystem();
    }

    private void initializeEntities() {
        float sizePerCube = 1 / 22.0f;
        Entity player = EntityGenerator.createEntity(
                new Transform(
                        new Vector3(19, 5, 78),
                        new Vector3(0, 0, 0),
                        new Vector3(sizePerCube, sizePerCube, sizePerCube)
                ),
                new VoxelModel(VoxelGeometry.loadFromFile("res/voxels/car.vox").voxels),
                new PlayerTag(),
                new GravityTag(),
                new CameraTarget(new Vector3(0f, 5, -5)),
                new Dynamics(Vector3.zeros(), Vector3.zeros()),
                new CarDynamics(),
                new RigidBody(new BoundingBox(1, 1, 1), "PLAYER")
        );
        Entity metaViewer = EntityGenerator.createEntity(
                new DebugCameraConfig()
        );

        entityManager.addEntity(player);
        entityManager.addEntity(metaViewer);
    }

    public void render() {
        int NO_DELTA = 0;

        prepareRender();
        skyBoxRenderingSystem.update(NO_DELTA);
        terrainRenderingSystem.update(NO_DELTA);
        goRenderingSystem.update(NO_DELTA);
        bboxRenderingSystem.update(NO_DELTA);
        uiRenderingSystem.update(NO_DELTA);
        window.render();
    }

    public void update(float dt) {
        systemDriver.updateGameState(gameState, dt);
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

    public void reset() {
        entityManager.removeAllEntities();
        initializeEntities();
    }
}
