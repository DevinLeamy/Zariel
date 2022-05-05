package engine.main;

import engine.World;
import engine.ui.RacingUI;
import math.Vector3;

public class DebugGameState implements GameStateI {
    GameState state = GameState.DEBUG;
    Camera perspective = new Camera(
            (float) Math.PI - (float) Math.PI / 2,
            World.window.getAspectRatio(),
            new Vector3(6, 5, 42)
    );
    @Override
    public void initialize() {
        Debug.cursorLocked = true;
        initializeEntities();
        RacingUI.init();
    }

    @Override
    public void update(float dt) {
        World.systemDriver.updateGameState(state, dt);
    }

    @Override
    public void render() {
        World.systemDriver.renderGameState(state);
    }

    @Override
    public void reset() {
        // keep existing entities
    }

    @Override
    public Camera getPerspective() {
        return perspective;
    }

    private void initializeEntities() {
    }
}
