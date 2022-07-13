package com.mygdx.game.entitycomponentsystem.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.common.Direction;

public class DirectionComponent implements Component, Pool.Poolable {
    public Direction direction = Direction.LEFT;

    @Override
    public void reset() {
        direction = Direction.LEFT;
    }
}
