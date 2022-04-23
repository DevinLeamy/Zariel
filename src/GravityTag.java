import ecs.Component;

public class GravityTag implements Component {
    public boolean falling = false;

    public GravityTag() {}
    public GravityTag(boolean falling) {
        this.falling = falling;
    }
}
