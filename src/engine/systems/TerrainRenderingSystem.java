package engine.systems;

import engine.ecs.System;
import engine.main.Camera;
import engine.main.Chunk;
import engine.main.ChunkManager;
import engine.World;

import java.util.ArrayList;
// TODO: TEMP

public class TerrainRenderingSystem extends System {
    World world = World.getInstance();
    ChunkManager chunkManager = World.getInstance().chunkManager;

    public TerrainRenderingSystem() {
        super(0);
    }

    @Override
    public void update(float dt) {
        // TODO: get rid of the chunk manager and chunk classes
        //       and turn them into Entities/Components/Systems
        Camera camera = world.getPerspective();
        ArrayList<Chunk> visibleChunks = chunkManager.getVisibleChunks(camera);
        visibleChunks.forEach(chunk -> chunk.render(camera));
    }
}