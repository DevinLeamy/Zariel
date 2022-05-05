package engine.main;

import engine.World;
import engine.components.Transform;
import engine.ecs.Entity;
import engine.ui.RacingUI;
import math.Vector3;

public class PlayingGameState implements GameStateI {
    GameState state = GameState.PLAYING;

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
        World.entityManager.removeAllEntities();
        initializeEntities();
    }

    private void initializeEntities() {
        float sizePerCube = 1 / 22.0f;
        Transform playerTransform = new Transform(
                new Vector3(19, 20, 55),
                new Vector3(0, 0, 0),
                new Vector3(sizePerCube, sizePerCube, sizePerCube)
        );
        Entity player = EntityGenerator.generatePlayer(playerTransform);
        Entity metaViewer = EntityGenerator.generateDebugCamera();

        World.entityManager.addEntity(player);
        World.entityManager.addEntity(metaViewer);
    }
}
