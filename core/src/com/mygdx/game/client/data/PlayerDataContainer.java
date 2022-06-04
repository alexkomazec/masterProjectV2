package com.mygdx.game.client.data;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.config.GameConfig;

/* Player Data ready to send over the network */
public class PlayerDataContainer {

    private boolean[] abInputCommandList = new boolean[GameConfig.LIST_COMMANDS_MAX];
    private Vector2 position       = new Vector2();
    private int playerID;

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
}
