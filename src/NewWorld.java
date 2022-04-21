import ecs.Entity;
import ecs.EntityManager;
import math.Vector3;
import math.Vector3i;

import java.util.ArrayList;

public class NewWorld {
    public static NewWorld world = new NewWorld();
    public static TextureAtlas atlas;

    private ChunkManager chunkManager;
    public Window window;
    public SkyBox skyBox;
    public EntityManager entityManager;

    private MovementSystem movementSystem;

    private NewWorld() {
        this.entityManager = EntityManager.instance;
        this.movementSystem = new MovementSystem();


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
        player.addComponent(new Velocity(0, 0, 0));
        player.addComponent(new Position(0, 0, 0));
        player.addComponent(new Rotation(0, 0, 0));
        player.addComponent(new Scale(1, 1, 1));
        player.addComponent(new VoxelModel(VoxelGeometry.loadFromFile("res/voxels/sword_man.vox").voxels));
        player.addComponent(new PlayerTag());
        player.addComponent(new Prospective(
                (float) Math.PI - (float) Math.PI / 2,
                window.getAspectRatio(),
                0.01f,
                500f
        ));


        entityManager.addEntity(player);
    }

    public void update(float dt) {
        movementSystem.update(dt);
    }

    private void prepareRender() {
        window.prepareWindow();
    }

    private void render() {
        prepareRender();

        window.render();


    }
}
