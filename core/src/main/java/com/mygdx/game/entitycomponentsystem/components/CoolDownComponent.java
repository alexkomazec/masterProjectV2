package com.mygdx.game.entitycomponentsystem.components;

import com.badlogic.ashley.core.Component;
import com.mygdx.game.common.CoolDown;

/* This represent a cooldown for some abilities*/
public class CoolDownComponent implements Component
{
    /* Set cooldown for shooting*/
    public CoolDown coolDown = new CoolDown(1f, 0f);
}
