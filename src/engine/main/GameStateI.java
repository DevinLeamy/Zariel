package engine.main;

public interface GameStateI {
    void initialize();
    void update(float dt);
    void render();
    void reset();
    Camera getPerspective();
}
