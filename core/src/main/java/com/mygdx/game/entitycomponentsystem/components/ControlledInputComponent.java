package com.mygdx.game.entitycomponentsystem.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.config.GameConfig;

public class ControlledInputComponent implements Component, Pool.Poolable {
    public boolean[] abInputCommandList = new boolean[GameConfig.LIST_COMMANDS_MAX];
    public boolean newInputHappend;

    @Override
    public void reset()
    {
        abInputCommandList = new boolean[GameConfig.LIST_COMMANDS_MAX];
        newInputHappend = false;
    }
}
