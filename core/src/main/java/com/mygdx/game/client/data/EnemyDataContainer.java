package com.mygdx.game.client.data;

import com.badlogic.gdx.math.Vector2;

public class EnemyDataContainer
{
    private Vector2 position       = new Vector2();

    public EnemyDataContainer() {}

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }
}
