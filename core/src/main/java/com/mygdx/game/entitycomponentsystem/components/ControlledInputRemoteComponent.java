package com.mygdx.game.entitycomponentsystem.components;

import com.badlogic.ashley.core.Component;
import com.mygdx.game.config.GameConfig;

/* Note: This component is not used, discontinued, but it could be reused in the future*/
public class ControlledInputRemoteComponent implements Component
{
    public boolean[] abInputCommandList = new boolean[GameConfig.LIST_COMMANDS_MAX];
    public boolean newInputHappend;
}
