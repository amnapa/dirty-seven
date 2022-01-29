package com.mismattia.dirtyseven.singleton;

import com.mismattia.dirtyseven.model.Game;

public class GameState {
    private static GameState instance = null;

    public long gameId = 0;
    public String gameName = "";
    public int roundNumber = 1;
    public int maxScore = 0;

    protected GameState(){}

    public static synchronized GameState getInstance() {
        if (null == instance) {
            instance = new GameState();
        }

        return instance;
    }

    public void changeState(Game game) {
        gameId = game.getId();
        gameName = game.getName();
        maxScore = game.getMaxScore();
    }
}
