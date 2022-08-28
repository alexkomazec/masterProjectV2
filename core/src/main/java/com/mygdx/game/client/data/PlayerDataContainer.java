package com.mygdx.game.client.data;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.common.Direction;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.entitycomponentsystem.components.DirectionComponent;

/* Player Data ready to send over the network */
public class PlayerDataContainer {

    private boolean[] abInputCommandList = new boolean[GameConfig.LIST_COMMANDS_MAX];
    private Vector2 position       = new Vector2();
    private float bulletXvelocity  = 0f;
    private float playerWidth      = 0f;
    private int playerID;
    private Direction bulletDirection = Direction.LEFT;

    public PlayerDataContainer() {}

    public PlayerDataContainer(boolean[] abInputCommandList, int playerID)
    {
        this.abInputCommandList = abInputCommandList;
        this.playerID = playerID;
    }

    public PlayerDataContainer(boolean[] abInputCommandList, int playerID, float bulletXvelocity)
    {
        this.abInputCommandList = abInputCommandList;
        this.playerID = playerID;
        this.bulletXvelocity = bulletXvelocity;
    }

    public PlayerDataContainer(Direction bulletDirection, boolean[] abInputCommandList, Vector2 position, int playerID, float bulletXvelocity)
    {
        this.abInputCommandList = abInputCommandList;
        this.position = position;
        this.playerID = playerID;
        this.bulletXvelocity = bulletXvelocity;
        this.bulletDirection = bulletDirection;
    }

    public boolean[] getAbInputCommandList() {
        return abInputCommandList;
    }

    public void setAbInputCommandList(boolean[] abInputCommandList) {
        this.abInputCommandList = abInputCommandList;
    }

    public void setabInputCommandElement(boolean value, int index)
    {
        this.abInputCommandList[index] = value;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public float getPlayerWidth() {
        return playerWidth;
    }

    public void setPlayerWidth(float playerWidth) {
        this.playerWidth = playerWidth;
    }

    public float getBulletXvelocity() {
        return this.bulletXvelocity;
    }

    public Direction getBulletDirection() {
        return bulletDirection;
    }

    public void setBulletXvelocity(float bulletXvelocity) {
        this.bulletXvelocity = bulletXvelocity;
    }

    public void setBulletDirection(Direction bulletDirection) {
        this.bulletDirection = bulletDirection;
    }
}
