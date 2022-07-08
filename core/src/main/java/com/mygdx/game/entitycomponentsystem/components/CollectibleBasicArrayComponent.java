package com.mygdx.game.entitycomponentsystem.components;

import com.badlogic.ashley.core.Component;
import com.mygdx.game.config.GameConfig;

public class CollectibleBasicArrayComponent implements Component
{
    public boolean[] collectibleBasicArray = new boolean[GameConfig.COLLECTABLE_BASIC_MAX];
}
