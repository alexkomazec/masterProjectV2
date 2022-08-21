package com.mygdx.game.common;

public class CoolDown {

    public float cooldown = 1f;
    public float elapsedTimeInSeconds = 0f;

    public CoolDown(float cooldown, float elapsedTimeInSeconds )
    {
        this.cooldown = cooldown;
        this.elapsedTimeInSeconds = elapsedTimeInSeconds;
    }
}
