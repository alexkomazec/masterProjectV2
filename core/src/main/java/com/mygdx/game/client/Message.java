package com.mygdx.game.client;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.client.data.EnemyDataContainer;
import com.mygdx.game.client.data.GeneralInfoContainer;
import com.mygdx.game.client.data.PlayerDataContainer;

public class Message {

    private Array<PlayerDataContainer> playerDataContainerArray;
    private Array<EnemyDataContainer> enemyDataContainerArray;
    private GeneralInfoContainer generalInfoContainer;

    private final int actionType;
    private final boolean actionDependsOnEntity;


    public Message(int actionType, boolean actionDependsOnEntity) {
        this.playerDataContainerArray = new Array<>();
        this.enemyDataContainerArray = new Array<>();
        this.generalInfoContainer = new GeneralInfoContainer();

        this.actionType = actionType;
        this.actionDependsOnEntity = actionDependsOnEntity;
    }

    public int getActionType() {
        return actionType;
    }
    public boolean doesActionDependsOnEntity() { return actionDependsOnEntity; }

    /* Methods for GeneralInfoContainer */
    public GeneralInfoContainer getGeneralInfoContainer() {
        return this.generalInfoContainer;
    }

    /* Methods for PlayerDataContainer */
    public Array<PlayerDataContainer> getPlayerDataContainerArray() { return playerDataContainerArray; }
    public PlayerDataContainer getPlayerDataContainer()
    {
        return playerDataContainerArray.get(0);
    }
    public PlayerDataContainer getPlayerDataContainerByIndex(int index) { return playerDataContainerArray.get(index); }
    public void addPlayerDataContainer(PlayerDataContainer playerDataContainer) { this.playerDataContainerArray.add(playerDataContainer); }

    /* Methods for PlayerDataContainer */
    public Array<EnemyDataContainer> getEnemyDataContainerArray() { return enemyDataContainerArray; }
}
