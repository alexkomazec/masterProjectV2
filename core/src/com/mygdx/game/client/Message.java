package com.mygdx.game.client;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.client.data.PlayerDataContainer;

public class Message {

    private Array<PlayerDataContainer> playerDataContainerArray;
    private final int actionType;
    private final boolean actionDependsOnEntity;


    public Message(int actionType, boolean actionDependsOnEntity) {
        this.playerDataContainerArray = new Array<>();
        this.actionType = actionType;
        this.actionDependsOnEntity = actionDependsOnEntity;
    }

    public Array<PlayerDataContainer> getPlayerDataContainerArray() {
        return playerDataContainerArray;
    }

    public PlayerDataContainer getPlayerDataContainer()
    {
        return playerDataContainerArray.get(0);
    }

    public PlayerDataContainer getPlayerDataContainerByIndex(int index)
    {
        return playerDataContainerArray.get(index);
    }

    public PlayerDataContainer getAndRemovePlayerDataContainer()
    {
        return playerDataContainerArray.removeIndex(0);
    }

    public void addPlayerDataContainer(PlayerDataContainer playerDataContainer) {
        this.playerDataContainerArray.add(playerDataContainer);
    }

    public int getActionType() {
        return actionType;
    }

    public boolean doesActionDependsOnEntity() {
        return actionDependsOnEntity;
    }
}
