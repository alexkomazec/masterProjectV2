package com.mygdx.game.client;

import com.mygdx.game.client.data.PlayerDataContainer;

public class ReceivedMessage {

    private PlayerDataContainer playerDataContainer;
    int actionType;


    public ReceivedMessage(PlayerDataContainer playerDataContainer,int actionType)
    {
        this.playerDataContainer = playerDataContainer;
        this.actionType = actionType;
    }

    public PlayerDataContainer getPlayerDataContainer() {
        return playerDataContainer;
    }

    public void setPlayerDataContainer(PlayerDataContainer playerDataContainer) {
        this.playerDataContainer = playerDataContainer;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }
}
