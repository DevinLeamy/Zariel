package lib.voxlib;

import lib.voxlib.chunk.VoxRootChunk;
import lib.voxlib.mat.VoxMaterial;
import lib.voxlib.mat.VoxOldMaterial;

import java.util.HashMap;
import java.util.List;

public final class VoxFile {
    private final int version;
    private final VoxRootChunk root;

    public VoxFile(int version, VoxRootChunk root) {
        this.version = version;
        this.root = root;
    }

    VoxRootChunk getRoot() {
        return root;
    }

    public List<VoxModelInstance> getModelInstances() {
        return root.getModelInstances();
    }

    public int[] getPalette() {
        return root.getPalette();
    }

    public HashMap<Integer, VoxMaterial> getMaterials() {
        return root.getMaterials();
    }

    @Deprecated
    public HashMap<Integer, VoxOldMaterial> getOldMaterials() {
        return root.getOldMaterials();
    }

    public int getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "VoxFile{version=" + version + "}";
    }
}
