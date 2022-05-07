package engine;

import engine.ecs.*;
import engine.graphics.TextureAtlas;
import engine.main.Block;
import engine.main.Camera;
import engine.main.ChunkManager;
import engine.main.DebugGameState;
import engine.main.GameState;
import engine.main.GameStateI;
import engine.main.MenuGameState;
import engine.main.PlayingGameState;
import engine.main.SkyBox;
import engine.main.SystemDriver;
import engine.systems.*;
import engine.ui.UI;
import engine.view.Window;
import math.*;

import java.util.Optional;

import static engine.main.GameState.MENU;
import static engine.main.GameState.PAUSED;
import static engine.main.GameState.PLAYING;

public class World {
    public static GameStateI gameState;
    public static TextureAtlas atlas;

    public static ChunkManager chunkManager;
    public static Window window;
    public static SkyBox skyBox;
    public static EntityManager entityManager;
    public static UI ui;

    public static SystemDriver systemDriver;

    public static void init() {
        entityManager = EntityManager.instance;
        window = new Window();
        chunkManager = new ChunkManager();
        ui = new UI();
        systemDriver = new SystemDriver();

        skyBox = new SkyBox(new String[] {
                "res/images/skybox/right.png",
                "res/images/skybox/left.png",
                "res/images/skybox/bottom.png",
                "res/images/skybox/top.png",
                "res/images/skybox/front.png",
                "res/images/skybox/back.png",
        });
        atlas = new TextureAtlas("res/images/minecraft_atlas.png", 16, 16);
        gameState = new MenuGameState();
        gameState.initialize();
        initializeSystems();
    }

    public static void onPlayingGame() {
        gameState = new PlayingGameState();
        gameState.initialize();
    }

    public static void onGameMenu() {
        gameState = new MenuGameState();
        gameState.initialize();
    }

    public static void onDebugMode() {
        gameState = new DebugGameState();
        gameState.initialize();
    }

    private static void initializeSystems() {
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

    public static void render() {
        prepareRender();
        gameState.render();
        window.render();
    }

    public static void update(float dt) {
        gameState.update(dt);
    }

    private static void prepareRender() {
        window.prepareWindow();
    }

    public static Camera getPerspective() {
        return gameState.getPerspective();
    }

    public static boolean blockIsActive(int x, int y, int z) {
        return blockIsActive(new Vector3i(x, y, z));
    }

    public static boolean blockIsActive(Vector3i loc) {
        Optional<Block> block = chunkManager.getBlock(loc);
        return block.isPresent() && block.get().isActive();
    }

    public static Optional<Block> getBlock(Vector3i loc) {
        return chunkManager.getBlock(loc);
    }

    public static void reset() {
        gameState.reset();
    }
}
