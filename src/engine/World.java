package engine;

import engine.components.*;
import engine.ecs.*;
import engine.graphics.TextureAtlas;
import engine.graphics.VoxelGeometry;
//import engine.main.*;
import engine.main.Block;
import engine.main.BoundingBox;
import engine.main.Camera;
import engine.main.ChunkManager;
import engine.main.Debug;
import engine.main.SkyBox;
import engine.systems.*;
import engine.view.Window;
import math.*;

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
    private BBoxRenderingSystem bboxRenderingSystem;
    private CarPhysicsSystem carPhysicsSystem;
    private DebugInputSystem debugInputSystem;

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
        this.bboxRenderingSystem = new BBoxRenderingSystem();
        this.carPhysicsSystem = new CarPhysicsSystem();
        this.debugInputSystem = new DebugInputSystem();

//        ArrayList<String> playerFrames = new ArrayList<>(Arrays.asList(
//                "res/voxels/player_animation/frame1.vox",
//                "res/voxels/player_animation/frame2.vox",
//                "res/voxels/player_animation/frame3.vox",
//                "res/voxels/player_animation/frame4.vox",
//                "res/voxels/player_animation/frame5.vox",
//                "res/voxels/player_animation/frame6.vox",
//                "res/voxels/player_animation/frame7.vox",
//                "res/voxels/player_animation/frame8.vox",
//                "res/voxels/player_animation/frame9.vox",
//                "res/voxels/player_animation/frame10.vox",
//                "res/voxels/player_animation/frame11.vox",
//                "res/voxels/player_animation/frame12.vox",
//                "res/voxels/player_animation/frame13.vox",
//                "res/voxels/player_animation/frame14.vox",
//                "res/voxels/player_animation/frame13.vox",
//                "res/voxels/player_animation/frame12.vox",
//                "res/voxels/player_animation/frame11.vox",
//                "res/voxels/player_animation/frame10.vox",
//                "res/voxels/player_animation/frame9.vox",
//                "res/voxels/player_animation/frame8.vox",
//                "res/voxels/player_animation/frame7.vox",
//                "res/voxels/player_animation/frame6.vox",
//                "res/voxels/player_animation/frame5.vox",
//                "res/voxels/player_animation/frame4.vox",
//                "res/voxels/player_animation/frame3.vox",
//                "res/voxels/player_animation/frame2.vox",
//                "res/voxels/player_animation/frame1.vox"
//        ));

        Entity player = new Entity();
        float sizePerCube = 1 / 11.0f;
        player.addComponent(new Transform(
                new Vector3(19, 5, 78),
                new Vector3(0, 0, 0),
                new Vector3(sizePerCube, sizePerCube, sizePerCube)
        ));
        player.addComponent(new VoxelModel(VoxelGeometry.loadFromFile("res/voxels/car.vox").voxels));
        player.addComponent(new PlayerTag());
        player.addComponent(new CameraTarget(new Vector3(0f, 5, -5)));
        player.addComponent(new Dynamics(
                Vector3.zeros(),
                Vector3.zeros()
        ));
        player.addComponent(new CarDynamics());
        player.addComponent(new GravityTag());
        player.addComponent(new RigidBody(
                new BoundingBox(2, 2, 2),
                "PLAYER"
        ));

        entityManager.addEntity(player);

        Entity metaViewer = new Entity();
        metaViewer.addComponent(new DebugCameraConfig());

        entityManager.addEntity(metaViewer);
    }

    public void update(float dt) {
        int NO_DELTA = 0;
        {
            debugInputSystem.update(dt);
            terrainSystem.update(dt);

            cameraInputSystem.update(dt);

            if (Debug.DEBUG) {
                // control the camera
                debugCameraInputSystem.update(dt);
            } else {
                // control the player
                playerInputSystem.update(dt);
            }

            // update accelerations
            // TODO: (make this update forces and then have the physics system update
            //       based on the forces)
            fallingSystem.update(dt);
            carPhysicsSystem.update(dt);

            // update velocity and position based on acceleration
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
            bboxRenderingSystem.update(NO_DELTA);
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

    public void reset() {
        entityManager.removeAllEntities();

        Entity player = new Entity();
        float sizePerCube = 1 / 11.0f;
        player.addComponent(new Transform(
                new Vector3(19, 5, 78),
                new Vector3(0, 0, 0),
                new Vector3(sizePerCube, sizePerCube, sizePerCube)
        ));
        player.addComponent(new VoxelModel(VoxelGeometry.loadFromFile("res/voxels/car.vox").voxels));
        player.addComponent(new PlayerTag());
        player.addComponent(new CameraTarget(new Vector3(0f, 5, -5)));
        player.addComponent(new Dynamics(
                Vector3.zeros(),
                Vector3.zeros()
        ));
        player.addComponent(new CarDynamics());
        player.addComponent(new GravityTag());
        player.addComponent(new RigidBody(
                new BoundingBox(2, 2, 2),
                "PLAYER"
        ));

        entityManager.addEntity(player);

        Entity metaViewer = new Entity();
        metaViewer.addComponent(new DebugCameraConfig());

        entityManager.addEntity(metaViewer);
    }
}