package engine.main;

import engine.GameState;
import engine.ecs.System;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static engine.GameState.*;

public class SystemDriver {
    Map<GameState, ArrayList<System>> updateSystems;
    Map<GameState, ArrayList<System>> renderingSystems;

    public SystemDriver() {
        this.updateSystems = new HashMap<>();
        updateSystems.put(MENU, new ArrayList<>());
        updateSystems.put(DEBUG, new ArrayList<>());
        updateSystems.put(PLAYING, new ArrayList<>());
        updateSystems.put(PAUSED, new ArrayList<>());

        this.renderingSystems = new HashMap<>();
        renderingSystems.put(MENU, new ArrayList<>());
        renderingSystems.put(DEBUG, new ArrayList<>());
        renderingSystems.put(PLAYING, new ArrayList<>());
        renderingSystems.put(PAUSED, new ArrayList<>());
    }

    public void registerUpdateSystem(System system, GameState... gameStates) {
        for (GameState state : gameStates) {
            updateSystems.get(state).add(system);
        }
    }

    public void registerRenderingSystem(System system, GameState... gameStates) {
        for (GameState state : gameStates) {
            renderingSystems.get(state).add(system);
        }
    }

    public void updateGameState(GameState gameState, float dt) {
        for (System system : updateSystems.get(gameState)) {
            system.update(dt);
        }
    }

    public void renderGameState(GameState gameState) {
        int NO_DELTA = 0;

        for (System system : renderingSystems.get(gameState)) {
            system.update(NO_DELTA);
        }
    }
}
