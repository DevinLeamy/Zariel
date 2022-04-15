import math.Vector3;

import java.util.*;

public class ChunkManager {
    public static float CHUNK_LOAD_DISTANCE = 2.0f;
    /**
     * @field chunks: Chunks that may be visible
     * @field renderQueue: Queue of chunks we want to render
     * @field updateQueue: Queue of chunk we want to update
     * @field disposeQueue: Queue of chunks we want to dispose
     *
     * All chunks are in the loaded chunks list.
     */
    Map<Vector3, Chunk> chunkStore;
    Set<Vector3> loadQueue;

    ChunkManager() {
        this.chunkStore = new HashMap<>();
        this.loadQueue = new HashSet<>();
    }

    private ArrayList<Vector3> getLoadedChunks() {
        ArrayList<Vector3> loadedChunks = new ArrayList<>();
        for (Vector3 location : chunkStore.keySet()) {
            if (chunkStore.get(location).loaded) {
                loadedChunks.add(location);
            }
        }

        return loadedChunks;
    }

    public void unloadDistantChunks(Camera perspective) {
        Vector3 origin = Chunk.worldCoordToChunkLocation(perspective.position);
        ArrayList<Vector3> unload = new ArrayList<>();

        for (Vector3 chunkLocation : getLoadedChunks()) {
            if (!chunkLocationInRange(origin, chunkLocation)) {
                unload.add(chunkLocation);
            }
        }

        // remove irrelevant chunk
        for (Vector3 chunkLocation : unload) {
            unloadChunk(chunkLocation);
        }
    }

    public void createChunk(Vector3 chunkLocation, Chunk chunk) {
        chunk.initialize();
        chunkStore.put(chunkLocation, chunk);
        loadQueue.add(chunkLocation);
    }

    public void unloadChunk(Vector3 chunkLocation) {
        chunkStore.get(chunkLocation).unload();
        chunkStore.remove(chunkLocation);
    }

    public void loadChunk(Vector3 chunkLocation) {
        chunkStore.get(chunkLocation).load();
    }

    public void createRelevantChunks(Camera perspective) {
        Vector3 origin = Chunk.worldCoordToChunkLocation(perspective.position);
        ArrayList<Vector3> create = new ArrayList<>();

        int lowX = (int) Math.floor(origin.x - CHUNK_LOAD_DISTANCE);
        int lowY = (int) Math.floor(origin.y - CHUNK_LOAD_DISTANCE);
        int lowZ = (int) Math.floor(origin.z - CHUNK_LOAD_DISTANCE);

        int lowHighSpread = (int) Math.ceil(CHUNK_LOAD_DISTANCE * 2);

        for (int x = lowX; x <= lowX + lowHighSpread; ++x) {
            for (int y = lowY; y <= lowY + lowHighSpread; ++y) {
                for (int z = lowZ; z <= lowZ + lowHighSpread; ++z) {
                    Vector3 chunkLocation = new Vector3(x, y, z);
                    if (chunkLocationInRange(origin, chunkLocation) && !chunkStore.containsKey(chunkLocation)) {
                        create.add(chunkLocation);
                    }
                }
            }
        }

        for (Vector3 chunkLocation : create) {
            createChunk(chunkLocation, new Chunk(chunkLocation));
            addNeighborsToLoadQueue(chunkLocation);
        }
    }

    public void addNeighborsToLoadQueue(Vector3 location) {
        for (Chunk chunk : getNeighboringChunks(location)) {
            loadQueue.add(chunk.location);
        }
    }

    /**
     * @param origin: origin (eg. camera position), in chunk coordinates
     * @param chunkLocation: location of chunk of interest, in chunk coordinates
     * @return whether the chunk should be loaded
     */
    public boolean chunkLocationInRange(Vector3 origin, Vector3 chunkLocation) {
        // TODO: this should user the distance to the nearest point in the chunk
        float chunkDistance = Vector3.sub(origin, chunkLocation).len();
        return chunkDistance <= CHUNK_LOAD_DISTANCE;
    }

    public ArrayList<Chunk> getVisibleChunks(Camera perspective) {
        ArrayList<Chunk> visibleChunks = new ArrayList<>();

        for (Vector3 location : chunkStore.keySet()) {
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

    public Optional<Chunk> getChunk(Vector3 chunkLocation) {
        if (!chunkStore.containsKey(chunkLocation)) {
            return Optional.empty();
        }

        Chunk chunk = chunkStore.get(chunkLocation);
        return chunk.isActive() ? Optional.of(chunk) : Optional.empty();
    }

    public ArrayList<Chunk> getNeighboringChunks(Vector3 chunkLocation) {
       ArrayList<Chunk> neighbors = new ArrayList<>();
       int[][] offsets = {
               {0, 0, 1},
               {0, 0, -1},
               {0, 1, 0},
               {0, -1, 0},
               {1, 0, 0},
               {-1, 0, 0},
       };

       for (int[] offset : offsets) {
           Vector3 location = new Vector3(
                   chunkLocation.x + offset[0],
                   chunkLocation.y + offset[1],
                   chunkLocation.z + offset[2]
           );
           Optional<Chunk> maybeChunk = getChunk(location);
           maybeChunk.ifPresent(neighbors::add);
       }

       return neighbors;
    }

    public void render(Camera perspective) {
        for (Chunk chunk : getVisibleChunks(perspective)) {
            chunk.render(perspective);
        }
    }

    public void update(Camera perspective) {
        unloadDistantChunks(perspective);
        createRelevantChunks(perspective);

        for (Vector3 location : new ArrayList<>(loadQueue)) {
            loadChunk(location);
        }

        loadQueue.clear();
    }


    // TODO: don't know where to put this
    public boolean blockIsActive(int x, int y, int z) {
        int innerX = ((x % Config.CHUNK_SIZE) + Config.CHUNK_SIZE) % Config.CHUNK_SIZE;
        int innerY = ((y % Config.CHUNK_SIZE) + Config.CHUNK_SIZE) % Config.CHUNK_SIZE;
        int innerZ = ((z % Config.CHUNK_SIZE) + Config.CHUNK_SIZE) % Config.CHUNK_SIZE;

        int chunkX = (x - innerX) / Config.CHUNK_SIZE;
        int chunkY = (y - innerY) / Config.CHUNK_SIZE;
        int chunkZ = (z - innerZ) / Config.CHUNK_SIZE;


        Optional<Chunk> maybeChunk = getChunk(new Vector3(chunkX, chunkY, chunkZ));

        if (maybeChunk.isEmpty()) {
            return false;
        }

        Chunk chunk = maybeChunk.get();
        return chunk.blockIsActive(innerX, innerY, innerZ);
    }

    public static Vector3 getChunkCoords(Vector3 v) {
        int innerX = (((int) v.x % Config.CHUNK_SIZE) + Config.CHUNK_SIZE) % Config.CHUNK_SIZE;
        int innerY = (((int) v.y % Config.CHUNK_SIZE) + Config.CHUNK_SIZE) % Config.CHUNK_SIZE;
        int innerZ = (((int) v.z % Config.CHUNK_SIZE) + Config.CHUNK_SIZE) % Config.CHUNK_SIZE;

        int chunkX = ((int) v.x - innerX) / Config.CHUNK_SIZE;
        int chunkY = ((int) v.y - innerY) / Config.CHUNK_SIZE;
        int chunkZ = ((int) v.z - innerZ) / Config.CHUNK_SIZE;

        return new Vector3(chunkX, chunkY, chunkZ);
    }
}
