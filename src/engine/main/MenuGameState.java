package engine.main;

import engine.World;
import engine.ui.GameMenuUI;

public class MenuGameState implements GameStateI {
    GameState state = GameState.MENU;

    @Override
    public void initialize() {
        Debug.cursorLocked = false;
        GameMenuUI.init();
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
        World.entityManager.removeAllEntities();
    }

    @Override
    public Camera getPerspective() {
        return null;
    }
}
