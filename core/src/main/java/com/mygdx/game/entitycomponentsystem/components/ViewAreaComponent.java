package com.mygdx.game.entitycomponentsystem.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;

public class ViewAreaComponent implements Component, Pool.Poolable {

    public Body viewAreaBody;
    public Entity owner;
    public Entity bodyThatColidedViewArea;
    public boolean collisionHappened;

    @Override
    public void reset() {
        viewAreaBody = null;
    }
}
