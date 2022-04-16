import math.Vector3;
import math.Vector3i;

import java.util.*;

public class ChunkManager {
    public static float CHUNK_LOAD_DISTANCE = Config.CHUNK_LOAD_DISTANCE;
    /**
     * @field chunks: Chunks that may be visible
     * @field renderQueue: Queue of chunks we want to render
     * @field updateQueue: Queue of chunk we want to update
     * @field disposeQueue: Queue of chunks we want to dispose
     *
     * All chunks are in the loaded chunks list.
     */
    Map<Vector3i, Chunk> chunkStore;
    Set<Vector3i> loadQueue;

    ChunkManager() {
        this.chunkStore = new HashMap<>();
        this.loadQueue = new HashSet<>();
    }

    private ArrayList<Vector3i> getLoadedChunks() {
        ArrayList<Vector3i> loadedChunks = new ArrayList<>();
        for (Vector3i location : chunkStore.keySet()) {
            if (chunkStore.get(location).loaded) {
                loadedChunks.add(location);
            }
        }

        return loadedChunks;
    }

    public void unloadDistantChunks(Camera perspective) {
        Vector3 origin = Chunk.worldCoordToChunkLocation(perspective.position);
        ArrayList<Vector3i> unload = new ArrayList<>();

        for (Vector3i chunkLocation : getLoadedChunks()) {
            if (!chunkLocationInRange(origin, chunkLocation)) {
                unload.add(chunkLocation);
            }
        }

        // remove irrelevant chunk
        for (Vector3i chunkLocation : unload) {
            unloadChunk(chunkLocation);
        }
    }

    public void createChunk(Vector3i chunkLocation, Chunk chunk) {
        chunk.initialize();
        chunkStore.put(chunkLocation, chunk);
        loadQueue.add(chunkLocation);
    }

    public void unloadChunk(Vector3i chunkLocation) {
        chunkStore.get(chunkLocation).unload();
        chunkStore.remove(chunkLocation);
    }

    public void loadChunk(Vector3i chunkLocation) {
        chunkStore.get(chunkLocation).load();
    }

    public void createRelevantChunks(Camera perspective) {
        Vector3 origin = Chunk.worldCoordToChunkLocation(perspective.position);
        ArrayList<Vector3i> create = new ArrayList<>();

        int lowX = (int) Math.floor(origin.x - CHUNK_LOAD_DISTANCE);
        int lowY = (int) Math.floor(origin.y - CHUNK_LOAD_DISTANCE);
        int lowZ = (int) Math.floor(origin.z - CHUNK_LOAD_DISTANCE);

        int lowHighSpread = (int) Math.ceil(CHUNK_LOAD_DISTANCE * 2);

        for (int x = lowX; x <= lowX + lowHighSpread; ++x) {
            for (int y = lowY; y <= lowY + lowHighSpread; ++y) {
                for (int z = lowZ; z <= lowZ + lowHighSpread; ++z) {
                    Vector3i chunkLocation = new Vector3i(x, y, z);
                    if (chunkLocationInRange(origin, chunkLocation) && !chunkStore.containsKey(chunkLocation)) {
                        create.add(chunkLocation);
                    }
                }
            }
        }

        for (Vector3i chunkLocation : create) {
            createChunk(chunkLocation, new Chunk(chunkLocation));
            addNeighborsToLoadQueue(chunkLocation);
        }
    }

    public void addNeighborsToLoadQueue(Vector3i location) {
        for (Chunk chunk : getNeighboringChunks(location)) {
            loadQueue.add(chunk.location);
        }
    }

    /**
     * @param origin: origin (eg. camera position), in chunk coordinates
     * @param chunkLocation: location of chunk of interest, in chunk coordinates
     * @return whether the chunk should be loaded
     */
    public boolean chunkLocationInRange(Vector3 origin, Vector3i chunkLocation) {
        // TODO: this should user the distance to the nearest point in the chunk
        float chunkDistance = Vector3.sub(origin, chunkLocation.toVector3()).len();
        return chunkDistance <= CHUNK_LOAD_DISTANCE;
    }

    public ArrayList<Chunk> getVisibleChunks(Camera perspective) {
        ArrayList<Chunk> visibleChunks = new ArrayList<>();

        for (Vector3i location : chunkStore.keySet()) {
            Chunk chunk = chunkStore.get(location);
            // check if a chunk has a neighbor on all sides
            if (getNeighboringChunks(chunk.location).size() == 6) {
                continue;
            }

            // TODO: some fancy stuff to determine visibility
            visibleChunks.add(chunk);
        }

        return visibleChunks;
    }

    public Optional<Chunk> getChunk(Vector3i chunkLocation) {
        if (!chunkStore.containsKey(chunkLocation)) {
            return Optional.empty();
        }

        Chunk chunk = chunkStore.get(chunkLocation);
        return chunk.isActive() ? Optional.of(chunk) : Optional.empty();
    }

    public ArrayList<Chunk> getNeighboringChunks(Vector3i chunkLocation) {
       ArrayList<Chunk> neighbors = new ArrayList<>();
       Vector3i[] offsets = {
               new Vector3i(0, 0, 1),
               new Vector3i(0, 0, -1),
               new Vector3i(0, 1, 0),
               new Vector3i(0, -1, 0),
               new Vector3i(1, 0, 0),
               new Vector3i(-1, 0, 0)
       };

       for (Vector3i offset : offsets) {
           Vector3i location = Vector3i.add(chunkLocation, offset);
           Optional<Chunk> maybeChunk = getChunk(location);
           maybeChunk.ifPresent(neighbors::add);
       }

       return neighbors;
    }

    private void checkForChunkUpdates() {
        for (Vector3i location: getLoadedChunks()) {
            if (chunkStore.get(location).updated) {
                // reload chunk
                loadQueue.add(location);

                // TODO: reload neighboring chunks
//                for (Chunk chunk : getNeighboringChunks(location)) {
//                    loadQueue.add(chunk.location);
//                }
            }
        }
    }

    public void render(Camera perspective) {
        for (Chunk chunk : getVisibleChunks(perspective)) {
            chunk.render(perspective);
        }
    }

    public void update(Camera perspective) {
        unloadDistantChunks(perspective);
        createRelevantChunks(perspective);
        checkForChunkUpdates();

        for (Vector3i location : new ArrayList<>(loadQueue)) {
            loadChunk(location);
        }

        loadQueue.clear();
    }

    // TODO: Remove??
    public static Vector3 getChunkCoords(Vector3 v) {
        int innerX = (((int) v.x % Config.CHUNK_SIZE) + Config.CHUNK_SIZE) % Config.CHUNK_SIZE;
        int innerY = (((int) v.y % Config.CHUNK_SIZE) + Config.CHUNK_SIZE) % Config.CHUNK_SIZE;
        int innerZ = (((int) v.z % Config.CHUNK_SIZE) + Config.CHUNK_SIZE) % Config.CHUNK_SIZE;

        int chunkX = ((int) v.x - innerX) / Config.CHUNK_SIZE;
        int chunkY = ((int) v.y - innerY) / Config.CHUNK_SIZE;
        int chunkZ = ((int) v.z - innerZ) / Config.CHUNK_SIZE;

        return new Vector3(chunkX, chunkY, chunkZ);
    }

    public Optional<Block> getBlock(Vector3i loc) {
        Vector3i chunkInnerCoords = Chunk.getChunkLocalCoords(loc);
        Vector3i chunkLocation = Vector3i.scale(Vector3i.sub(loc, chunkInnerCoords), 1.0f / Config.CHUNK_SIZE);

        Optional<Chunk> maybeChunk = getChunk(chunkLocation);

        if (maybeChunk.isEmpty()) {
            return Optional.empty();
        }

        Chunk chunk = maybeChunk.get();
        return Optional.of(chunk.getBlock(chunkInnerCoords));
    }
}
