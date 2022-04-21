import ecs.Component;

public class Prospective implements Component {
    final public float aspect;
    final public float ncp;
    final public float fcp;

    public float fov;

    /**
     * @param fov: Field of view
     * @param aspect: Aspect ratio (width/height)
     * @param ncp: Near clip plane
     * @param fcp: Far clip plane
     */
    public Prospective(float fov, float aspect, float ncp, float fcp) {
        this.fov = fov;
        this.aspect = aspect;
        this.ncp = ncp;
        this.fcp = fcp;
    }
}
