package engine;

import engine.components.*;
import engine.ecs.*;
import engine.graphics.TextureAtlas;
import engine.main.Block;
import engine.main.Camera;
import engine.main.ChunkManager;
import engine.main.EntityGenerator;
import engine.main.SkyBox;
import engine.main.SystemDriver;
import engine.systems.*;
import engine.ui.GameMenuUI;
import engine.ui.RacingUI;
import engine.ui.UI;
import engine.view.Window;
import math.*;

import java.util.Optional;

import static engine.GameState.MENU;
import static engine.GameState.PAUSED;
import static engine.GameState.PLAYING;

public class World {
    public GameState gameState;
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

    public static World getInstance() {
        if (World.world == null) {
            World.world = new World();
            World.world.init();
        }

        return World.world;
    }

    private World() {
        this.gameState = MENU;
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

    private void initializeGameState() {
        switch (gameState) {
            case PLAYING -> onPlayingGame();
            case MENU -> onGameMenu();
            case PAUSED -> onPauseGame();
        }
    }

    private void init() {
        atlas = new TextureAtlas("res/images/minecraft_atlas.png", 16, 16);
        initializeGameState();
        initializeSystems();
        initializeEntities();
    }

    public void onPlayingGame() {
        gameState = PLAYING;
        initializeEntities();
        RacingUI.init();
    }

    public void onPauseGame() {
        gameState = PAUSED;
    }

    public void onGameMenu() {
        gameState = MENU;
        GameMenuUI.init();
    }

    private void initializeSystems() {
        // update systems
        systemDriver.registerUpdateSystem(new DebugInputSystem(), PLAYING, GameState.DEBUG, MENU, PAUSED);
        systemDriver.registerUpdateSystem(new TerrainSystem(), PLAYING, GameState.DEBUG);
        systemDriver.registerUpdateSystem(new CameraInputSystem(), PLAYING, GameState.DEBUG);
        systemDriver.registerUpdateSystem(new DebugCameraInputSystem(), GameState.DEBUG);
        systemDriver.registerUpdateSystem(new PlayerInputSystem(), GameState.PLAYING);
        systemDriver.registerUpdateSystem(new FallingSystem(), PLAYING, GameState.DEBUG);
        systemDriver.registerUpdateSystem(new CarPhysicsSystem(), PLAYING, GameState.DEBUG);
        systemDriver.registerUpdateSystem(new MovementSystem(), PLAYING, GameState.DEBUG);
        systemDriver.registerUpdateSystem(new TerrainCollisionDetectionSystem(), PLAYING, GameState.DEBUG);
        systemDriver.registerUpdateSystem(new TerrainCollisionResolutionSystem(), PLAYING, GameState.DEBUG);
        systemDriver.registerUpdateSystem(new LifeTimeSystem(), PLAYING, GameState.DEBUG);
        systemDriver.registerUpdateSystem(new DespawnSystem(), PLAYING, GameState.DEBUG);
        systemDriver.registerUpdateSystem(new CameraTrackingSystem(), PLAYING);
        systemDriver.registerUpdateSystem(new AnimationSystem(), PLAYING, GameState.DEBUG);

        // rendering systems
        systemDriver.registerRenderingSystem(new SkyBoxRenderingSystem(), PLAYING, GameState.DEBUG);
        systemDriver.registerRenderingSystem(new TerrainRenderingSystem(), PLAYING, GameState.DEBUG);
        systemDriver.registerRenderingSystem(new GORenderingSystem(), PLAYING, GameState.DEBUG);
        systemDriver.registerRenderingSystem(new BBoxRenderingSystem(), PLAYING, GameState.DEBUG);
        systemDriver.registerRenderingSystem(new UIRenderingSystem(), MENU, PLAYING, GameState.DEBUG);
    }

    private void initializeEntities() {
        float sizePerCube = 1 / 22.0f;
        Transform playerTransform = new Transform(
                new Vector3(19, 15, 55),
                new Vector3(0, 0, 0),
                new Vector3(sizePerCube, sizePerCube, sizePerCube)
        );
        Entity player = EntityGenerator.generatePlayer(playerTransform);
        Entity metaViewer = EntityGenerator.generateDebugCamera();

        entityManager.addEntity(player);
        entityManager.addEntity(metaViewer);
    }

    public void render() {
        prepareRender();
        systemDriver.renderGameState(gameState);
        window.render();
    }

    public void update(float dt) {
        systemDriver.updateGameState(gameState, dt);
    }

    private void prepareRender() {
        window.prepareWindow();
    }

    public Camera getPerspective() {
        return gameState == GameState.DEBUG ? debugCamera : camera;
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
