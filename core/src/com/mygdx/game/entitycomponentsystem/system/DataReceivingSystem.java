package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.mygdx.game.client.ClientHandler;
import com.mygdx.game.client.Message;
import com.mygdx.game.client.data.PlayerDataContainer;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.entitycomponentsystem.components.ControlledInputComponent;
import com.mygdx.game.entitycomponentsystem.components.LocalInputComponent;
import com.mygdx.game.entitycomponentsystem.components.PlayerComponent;
import com.mygdx.game.entitycomponentsystem.components.TransformComponent;
import com.mygdx.game.gameworld.GameWorldCreator;

public class DataReceivingSystem extends IteratingSystem {

    private ClientHandler clientHandler;
    private GameWorldCreator gameWorldCreator;

    public DataReceivingSystem(ClientHandler clientHandler) {
        super(Family.all(PlayerComponent.class, ControlledInputComponent.class).get());
        this.clientHandler = clientHandler;
        this.gameWorldCreator = GameWorldCreator.getInstance();
    }

    @Override
    public void update (float deltaTime)
    {
        /* Optimization: Go through all entities only if there is some new data */
        if(!this.clientHandler.isRecMessageArrayEmpty())
        {
            if(this.clientHandler.peekAtFirstRecv().doesActionDependsOnEntity())
            {
                super.update(deltaTime);
            }
            else
            {
                Message message = this.clientHandler.getReceivedMessage();
                for (int index = 0; index < message.getPlayerDataContainerArray().size;)
                {
                    processData(message.getPlayerDataContainer(), message.getActionType());
                }
            }
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        Message message = this.clientHandler.getReceivedMessage();

        for (int index = 0; index < message.getPlayerDataContainerArray().size;) {
            processData(message.getPlayerDataContainer(), message.getActionType(), entity);
        }
    }

    private void processData(PlayerDataContainer playerDataContainer, int actionType, Entity entity)
    {
        PlayerComponent playerComponent = entity.getComponent(PlayerComponent.class);

        switch(actionType)
        {
            case ClientHandler.ASSIGN_ID_TO_PLAYER:

                System.out.println("DataReceivingSystem: ASSIGN_ID_TO_PLAYER");
                LocalInputComponent localInputComponent = entity.getComponent(LocalInputComponent.class);
                ControlledInputComponent controlledInputComponent = entity.getComponent(ControlledInputComponent.class);
                TransformComponent transformComponent = entity.getComponent(TransformComponent.class);

                if(localInputComponent != null)
                {
                    /* Set received ID to a main player */
                    playerComponent.playerID = playerDataContainer.getPlayerID();
                    System.out.println("Player ID has been added to a local player");
                    PooledEngine pooledEngine = this.gameWorldCreator.getPooledEngine();
                    InputManagerSystem inputManagerSystem = pooledEngine.getSystem(InputManagerSystem.class);
                    inputManagerSystem.assignPlayerToInputProcessor(playerComponent.playerID, true);


                    /* Fill message */
                    PlayerDataContainer playerDataContainerTmp = new PlayerDataContainer();
                    playerDataContainerTmp.setPlayerID(playerComponent.playerID);
                    playerDataContainerTmp.setAbInputCommandList(controlledInputComponent.abInputCommandList);
                    //transformComponent.position.x = transformComponent.position.x * GameConfig.MULTIPLY_BY_PPM;
                    //transformComponent.position.y = transformComponent.position.y * GameConfig.MULTIPLY_BY_PPM;
                    playerDataContainerTmp.setPosition(transformComponent.position);
                    Message message = new Message(ClientHandler.SEND_PLAYER_TO_SERVER, false);
                    message.addPlayerDataContainer(playerDataContainerTmp);

                    /* Send Relevant data of localy created player */
                    this.clientHandler.addTransmitingMessage(message);
                }
            break;

            default:
                System.out.println("Wrong action type" + actionType);
        }
    }

    private void processData(PlayerDataContainer playerDataContainer, int actionType)
    {
        /* Entities that represent players */
        ImmutableArray<Entity> entityPlayers = this.gameWorldCreator.getPooledEngine().
                getEntitiesFor(Family.all(PlayerComponent.class, ControlledInputComponent.class).
                        get());

        switch(actionType)
        {
            case ClientHandler.UPDATE_PLAYER_TABLE:

                System.out.println("DataReceivingSystem: UPDATE_PLAYER_TABLE");

                for (Entity entityPlayer: entityPlayers)
                {
                    PlayerComponent playerComponent = entityPlayer.getComponent(PlayerComponent.class);
                    if(playerComponent.playerID != playerDataContainer.getPlayerID())
                    {
                        this.gameWorldCreator.createPlayer(false, playerDataContainer.getPosition());
                        break;
                    }
                }

            break;
            default:
                System.out.println("Wrong action type" + actionType);
        }
    }

    public GameWorldCreator getGameWorldCreator() {
        return gameWorldCreator;
    }
}
