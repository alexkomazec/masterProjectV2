package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.mygdx.game.client.ClientHandler;
import com.mygdx.game.client.Message;
import com.mygdx.game.client.data.PlayerDataContainer;
import com.mygdx.game.common.Direction;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.entitycomponentsystem.components.B2dBodyComponent;
import com.mygdx.game.entitycomponentsystem.components.CollisionComponent;
import com.mygdx.game.entitycomponentsystem.components.ControlledInputComponent;
import com.mygdx.game.entitycomponentsystem.components.LocalInputComponent;
import com.mygdx.game.entitycomponentsystem.components.PlayerComponent;
import com.mygdx.game.entitycomponentsystem.components.TransformComponent;
import com.mygdx.game.gameworld.GameWorldCreator;

public class DataReceivingSystem extends IteratingSystem {

    private ClientHandler clientHandler;
    private GameWorldCreator gameWorldCreator;
    private Message message;

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
            this.message = this.clientHandler.getReceivedMessage();
            if(this.message.doesActionDependsOnEntity())
            {
                super.update(deltaTime);
            }
            else
            {
                for (int index = 0; index < this.message.getPlayerDataContainerArray().size; index++)
                {
                    processData(this.message.getPlayerDataContainerByIndex(index), this.message.getActionType());
                }
            }
            /* The message has been processed in one of conditions above */
            this.message = null;
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        /* TODO:          Need Optimization, delete processed data, need mechanism to mark processed
                          data
            OBSERVATION:  getPlayerDataContainer does not make any bugs but causes wasting time
            HOW?       :  processEntity method loops through each entity. Then each entity loops
                          through all elements of this.message.getPlayerDataContainerArray()
                          Let's imagine this.message.getPlayerDataContainerArray().size = 1000
                          Let's imagine number of entities is 1000.
                          Let's image that one of these PlayerDataContainer is supposed to be
                          processed only by one entity.
                          So let's say that now is turn for the last entity to process its
                          PlayerDataContainer. So because of getPlayerDataContainer(This method
                          does not deleted processed data) all previous already processed PlayerData
                          Containers will stay in the array, and the last entity should loop through
                          999 already processed data just to check if these data needed to be processed
                          that leads to RUN TIME DEGRADATION!
            */
        for (int index = 0; index < this.message.getPlayerDataContainerArray().size;index++) {
            processData(this.message.getPlayerDataContainer(), this.message.getActionType(), entity);
        }
    }

    private void processData(PlayerDataContainer playerDataContainer, int actionType, Entity entity)
    {
        PlayerComponent playerComponent = entity.getComponent(PlayerComponent.class);
        ControlledInputComponent controlledInputComponent = entity.getComponent(ControlledInputComponent.class);

        switch(actionType)
        {
            case ClientHandler.ASSIGN_ID_TO_PLAYER:

                System.out.println("DataReceivingSystem: ASSIGN_ID_TO_PLAYER");
                LocalInputComponent localInputComponent = entity.getComponent(LocalInputComponent.class);
                TransformComponent transformComponent = entity.getComponent(TransformComponent.class);

                if(localInputComponent != null)
                {
                    /* Set received ID to a main player */
                    playerComponent.playerID = playerDataContainer.getPlayerID();
                    System.out.println("Player ID has been added to a local player");
                    PooledEngine pooledEngine = this.gameWorldCreator.getPooledEngine();
                    InputManagerSystem inputManagerSystem = pooledEngine.getSystem(InputManagerSystem.class);
                    if(inputManagerSystem!=null)
                        inputManagerSystem.assignPlayerToInputProcessor(playerComponent.playerID, true);


                    /* Fill message */
                    PlayerDataContainer playerDataContainerTmp = new PlayerDataContainer();
                    playerDataContainerTmp.setPlayerID(playerComponent.playerID);
                    playerDataContainerTmp.setAbInputCommandList(controlledInputComponent.abInputCommandList);
                    playerDataContainerTmp.setPosition(transformComponent.position);
                    Message message = new Message(ClientHandler.SEND_PLAYER_TO_SERVER, false);
                    message.addPlayerDataContainer(playerDataContainerTmp);

                    /* Send Relevant data of localy created player */
                    this.clientHandler.addTransmitingMessage(message);
                }
            break;

            /* CLARIFICATION: REMOTE_PLAYER_MOVED is deprecated so far, UPDATE_PLAYER_POS is used instead*/
            /*case ClientHandler.REMOTE_PLAYER_MOVED:
                System.out.println("DataReceivingSystem: REMOTE_PLAYER_MOVED");
                if(playerComponent.playerID == playerDataContainer.getPlayerID())
                {
                    controlledInputComponent.abInputCommandList = playerDataContainer.getAbInputCommandList();
                }
            break;*/

            case ClientHandler.UPDATE_PLAYER_POS:
                System.out.println("DataReceivingSystem: UPDATE_PLAYER_POS");
                B2dBodyComponent b2dBodyComponent = entity.getComponent(B2dBodyComponent.class);

                System.out.println("GetLinearVelocity" + b2dBodyComponent.body.getLinearVelocity());
                if(playerComponent.playerID == playerDataContainer.getPlayerID())
                {
                    b2dBodyComponent.body.setTransform(
                            playerDataContainer.getPosition().x * GameConfig.DIVIDE_BY_PPM,
                            playerDataContainer.getPosition().y * GameConfig.DIVIDE_BY_PPM,
                            0);
                }
            break;

            case ClientHandler.PLAYER_FIRED:
                System.out.println("DataReceivingSystem: PLAYER_FIRED");

                if(playerComponent.playerID == playerDataContainer.getPlayerID())
                {
                    controlledInputComponent.abInputCommandList = playerDataContainer.getAbInputCommandList();
                }
            break;

            case ClientHandler.PLAYER_CHANGED_DIR:
                System.out.println("DataReceivingSystem: PLAYER_CHANGED_DIR");

                if(playerComponent.playerID == playerDataContainer.getPlayerID())
                {
                    if(playerComponent.direction == Direction.LEFT)
                    {
                        playerComponent.direction = Direction.RIGHT;
                    }
                    else
                    {
                        playerComponent.direction = Direction.LEFT;
                    }
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
                        Entity entity = this.gameWorldCreator.createPlayer(false, playerDataContainer.getPosition());
                        entity.getComponent(PlayerComponent.class).playerID = playerDataContainer.getPlayerID();
                        System.out.println("Player " + playerComponent.playerID + " has been created ");
                        break;
                    }
                }
            break;

            case ClientHandler.PLAYER_DISCONNECTED:
                System.out.println("DataReceivingSystem: PLAYER_DISCONNECTED");

                Entity tempEntity = null;
                for (Entity entityPlayer: entityPlayers)
                {
                    PlayerComponent playerComponent = entityPlayer.getComponent(PlayerComponent.class);
                    if(playerComponent.playerID == playerDataContainer.getPlayerID())
                    {
                        tempEntity = entityPlayer;
                        break;
                    }
                }

                /* Delete Entity that contains player component that has been disconected*/
                if(tempEntity!= null)
                {
                    /* Set Box2d Body to be dead, and Physics system will handle it */
                    B2dBodyComponent bodyComponent = tempEntity.getComponent(B2dBodyComponent.class);
                    bodyComponent.isDead = true;
                }
                else
                {
                    System.out.println("Error: tempEntity is empty");
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
