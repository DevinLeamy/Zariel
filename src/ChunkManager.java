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
    Chunk[][][] chunks;
    Set<Vector3i> loadQueue;

    ChunkManager() {
        this.chunks = new Chunk[Config.WORLD_LENGTH][Config.WORLD_HEIGHT][Config.WORLD_WIDTH];
        this.loadQueue = new HashSet<>();
    }

    private ArrayList<Vector3i> getLoadedChunks() {
        ArrayList<Vector3i> loadedChunks = new ArrayList<>();
        for (int i = 0; i < chunks.length; ++i) {
            for (int j = 0; j < chunks[0].length; ++j) {
                for (int k = 0; k < chunks[0][0].length; ++k) {
                    Optional<Chunk> chunk = getChunk(i, j, k);
                    if (chunk.isPresent() && chunk.get().loaded) {
                        loadedChunks.add(chunk.get().location);
                    }
                }
            }
        }


        return loadedChunks;
    }

    public void unloadDistantChunks(Camera perspective) {
        Vector3 origin = Chunk.worldCoordToChunkLocation(perspective.transform.position);
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

    private boolean validCoord(Vector3i location) {
        return validCoord(location.x, location.y, location.z);
    }

    private boolean validCoord(int x, int y, int z) {
        if (0 > x || x >= chunks.length || 0 > y || y >= chunks[0].length || 0 > z || z >= chunks[0][0].length) {
            return false;
        }
        return true;
    }

    private void setChunk(Vector3i location, Chunk chunk) {
        if (validCoord(location)) {
            chunks[location.x][location.y][location.z] = chunk;
        }
    }

    public void createChunk(Vector3i chunkLocation, Chunk chunk) {
        chunk.initializeGeometry();
        setChunk(chunkLocation, chunk);
        loadQueue.add(chunkLocation);
    }

    public void unloadChunk(Vector3i chunkLocation) {
        getChunk(chunkLocation).get().unload();
        setChunk(chunkLocation, null);
    }

    public void loadChunk(Vector3i chunkLocation) {
        getChunk(chunkLocation).get().load();
    }

    public void createRelevantChunks(Camera perspective) {
        Vector3 origin = Chunk.worldCoordToChunkLocation(perspective.transform.position);
        ArrayList<Vector3i> create = new ArrayList<>();

        for (int x = 0; x < chunks.length; ++x) {
            for (int y = 0; y < chunks[0].length; ++y) {
                for (int z = 0; z < chunks[0][0].length; ++z) {
                    Vector3i chunkLocation = new Vector3i(x, y, z);
                    if (chunkLocationInRange(origin, chunkLocation) && getChunk(chunkLocation).isEmpty()) {
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
        Frustum viewFrustum = perspective.getViewFrustum();

        for (Vector3i location : getLoadedChunks()) {
            Chunk chunk = getChunk(location).get();
            BoundingBox chunkBBox = chunk.getBoundingBox();

            // check if a chunk has a neighbor on all sides
//            if (getNeighboringChunks(chunk.location).size() == 6) { continue; }
            // TODO: fix
//            if (!viewFrustum.boxInOrIntersectsFrustum(chunkBBox)) { continue; }
            if (!chunk.isActive()) { continue; }

            visibleChunks.add(chunk);
        }

        return visibleChunks;
    }

    public Optional<Chunk> getChunk(int x, int y, int z) {
        if (!validCoord(x, y, z)) {
            return Optional.empty();
        }
        Chunk chunk = chunks[x][y][z];

        if (chunk == null) {
            return Optional.empty();
        }

        return Optional.of(chunk);
    }

    public Optional<Chunk> getChunk(Vector3i location) {
        return getChunk(location.x, location.y, location.z);
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
           if (maybeChunk.isPresent() && maybeChunk.get().isActive()) {
               neighbors.add(maybeChunk.get());
           }
       }

       return neighbors;
    }

    private void checkForChunkUpdates() {
        for (Vector3i location: getLoadedChunks()) {
            if (getChunk(location).get().updated) {
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
        ArrayList<Chunk> visibleChunks = getVisibleChunks(perspective);
        visibleChunks.forEach(chunk -> chunk.render(perspective));

//        System.out.printf("Rendered chunks: %d%n", visibleChunks.size());
    }

    public void update(Camera perspective) {
        unloadDistantChunks(perspective);
        createRelevantChunks(perspective);
        checkForChunkUpdates();

        ArrayList<Vector3i> temp = new ArrayList<>(loadQueue);
        loadQueue.clear();

        for (int i = 0; i < temp.size(); ++i) {
            if (i < Config.LOAD_LIMIT) {
                loadChunk(temp.get(i));
            } else {
                loadQueue.add(temp.get(i));
            }
        }
    }

    public void clearAll() {
        this.chunks = new Chunk[Config.WORLD_LENGTH][Config.WORLD_HEIGHT][Config.WORLD_WIDTH];
        this.loadQueue = new HashSet<>();
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
