package com.mygdx.game.entitycomponentsystem.components;

import com.badlogic.ashley.core.Component;
import com.mygdx.game.config.GameConfig;

public class ControlledInputComponent implements Component {
    public boolean[] abInputCommandList = new boolean[GameConfig.LIST_COMMANDS_MAX];
    public boolean newInputHappend;
}
