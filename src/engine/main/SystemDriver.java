package engine.main;

import engine.GameState;
import engine.ecs.System;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static engine.GameState.*;

public class SystemDriver {
    Map<GameState, ArrayList<System>> systems;

    public SystemDriver() {
        this.systems = new HashMap<>();
        systems.put(MENU, new ArrayList<>());
        systems.put(DEBUG, new ArrayList<>());
        systems.put(PLAYING, new ArrayList<>());
        systems.put(PAUSED, new ArrayList<>());
    }

    public void registerSystem(System system, GameState... gameStates) {
        for (GameState state : gameStates) {
            systems.get(state).add(system);
        }
    }

    public void updateGameState(GameState gameState, float dt) {
        for (System system : systems.get(gameState)) {
            system.update(dt);
        }
    }
}
