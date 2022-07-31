package com.mygdx.game.entitycomponentsystem.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class CollisionEffectComponent implements Component, Pool.Poolable
{

    public float aliveTime = 0;

    @Override
    public void reset() {
        aliveTime = 0;
    }
}
