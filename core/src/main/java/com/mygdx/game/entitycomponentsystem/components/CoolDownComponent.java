package com.mygdx.game.entitycomponentsystem.components;

import com.badlogic.ashley.core.Component;

/* This represent a cooldown for some abilities*/
public class CoolDownComponent implements Component
{
    /* Set cooldown for shooting*/
    public  final float COOLDOWN = 1f;
    public float elapsedTimeInSeconds = 0f;
}
