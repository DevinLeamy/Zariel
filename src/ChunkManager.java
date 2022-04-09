import math.Vector3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ChunkManager {
    public static float CHUNK_LOAD_DISTANCE = 3.0f;
    /**
     * @field chunks: Chunks that may be visible
     * @field renderQueue: Queue of chunks we want to render
     * @field updateQueue: Queue of chunk we want to update
     * @field disposeQueue: Queue of chunks we want to dispose
     *
     * All chunks are in the loaded chunks list.
     */
    Map<Vector3, Chunk> loadedChunks;

    ChunkManager() {
        this.loadedChunks = new HashMap<>();
    }

    public void unloadDistantChunks(Camera perspective) {
        Vector3 origin = Chunk.worldCoordToChunkLocation(perspective.position);
        ArrayList<Vector3> unload = new ArrayList<>();

        for (Vector3 chunkLocation : loadedChunks.keySet()) {
            if (!chunkLocationInRange(origin, chunkLocation)) {
                unload.add(chunkLocation);
            }
        }

        // remove irrelevant chunk
        for (Vector3 chunkLocation : unload) {
//            System.out.println("UNLOAD CHUNK " + chunkLocation);
            loadedChunks.get(chunkLocation).unload();
            loadedChunks.remove(chunkLocation);
        }
    }

    public void loadRelevantChunks(Camera perspective) {
        Vector3 origin = Chunk.worldCoordToChunkLocation(perspective.position);
        ArrayList<Vector3> load = new ArrayList<>();

        int lowX = (int) Math.floor(origin.x - CHUNK_LOAD_DISTANCE);
        int lowY = (int) Math.floor(origin.y - CHUNK_LOAD_DISTANCE);
        int lowZ = (int) Math.floor(origin.z - CHUNK_LOAD_DISTANCE);

        int lowHighSpread = (int) Math.ceil(CHUNK_LOAD_DISTANCE * 2);

        for (int x = lowX; x <= lowX + lowHighSpread; ++x) {
            for (int y = lowY; y <= lowY + lowHighSpread; ++y) {
                for (int z = lowZ; z <= lowZ + lowHighSpread; ++z) {
                    Vector3 chunkLocation = new Vector3(x, y, z);
                    if (chunkLocationInRange(origin, chunkLocation)) {
                        load.add(chunkLocation);
                    }
                }
            }
        }

        for (Vector3 chunkLocation : load) {
            if (loadedChunks.containsKey(chunkLocation)) {
                continue;
            }

            loadedChunks.put(chunkLocation, new Chunk(chunkLocation));
            loadedChunks.get(chunkLocation).load();
//            System.out.println("CHUNK LOADED " + chunkLocation);
        }
    }

    /**
     * @param origin: origin (eg. camera position), in chunk coordinates
     * @param chunkLocation: location of chunk of interest, in chunk coordinates
     * @return whether the chunk should be loaded
     */
    public boolean chunkLocationInRange(Vector3 origin, Vector3 chunkLocation) {
        float chunkDistance = Vector3.sub(origin, chunkLocation).len();
        return chunkDistance <= CHUNK_LOAD_DISTANCE;
    }

    public ArrayList<Chunk> getVisibleChunks(Camera perspective) {
        ArrayList<Chunk> visibleChunks = new ArrayList<>();

        for (Chunk chunk : loadedChunks.values()) {
            // TODO: some fancy stuff to determine visibility
            visibleChunks.add(chunk);
        }

        return visibleChunks;
    }

    public void render(Camera perspective) {
        for (Chunk chunk : getVisibleChunks(perspective)) {
            chunk.render(perspective);
        }
    }

    public void update(Camera perspective) {
        unloadDistantChunks(perspective);
        loadRelevantChunks(perspective);
    }
}
