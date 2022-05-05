package engine.main;

import engine.World;
import engine.ui.RacingUI;

public class DebugGameState implements GameStateI {
    GameState state = GameState.DEBUG;

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

    private void initializeEntities() {
    }
}
