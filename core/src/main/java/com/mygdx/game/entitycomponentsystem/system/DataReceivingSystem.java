package com.mygdx.game.entitycomponentsystem.system;

import static com.mygdx.game.client.ClientHandler.PLAYER_TABLE_UPDATED;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.client.ClientHandler;
import com.mygdx.game.client.Message;
import com.mygdx.game.client.data.PlayerDataContainer;
import com.mygdx.game.common.Direction;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.entitycomponentsystem.components.B2dBodyComponent;
import com.mygdx.game.entitycomponentsystem.components.ControllableComponent;
import com.mygdx.game.entitycomponentsystem.components.ControlledInputComponent;
import com.mygdx.game.entitycomponentsystem.components.DirectionComponent;
import com.mygdx.game.entitycomponentsystem.components.LocalInputComponent;
import com.mygdx.game.entitycomponentsystem.components.PlayerComponent;
import com.mygdx.game.entitycomponentsystem.components.TransformComponent;
import com.mygdx.game.gameworld.GameWorldCreator;

public class DataReceivingSystem extends IteratingSystem {

    protected static final Logger logger = new Logger(DataReceivingSystem.class.getSimpleName(), Logger.INFO);
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
        DirectionComponent directionComponent = entity.getComponent(DirectionComponent.class);

        ControlledInputComponent controlledInputComponent = entity.getComponent(ControlledInputComponent.class);

        switch(actionType)
        {
            case ClientHandler.ASSIGN_ID_TO_PLAYER:

                logger.debug("ASSIGN_ID_TO_PLAYER");
                LocalInputComponent localInputComponent = entity.getComponent(LocalInputComponent.class);
                TransformComponent transformComponent = entity.getComponent(TransformComponent.class);

                if(localInputComponent != null)
                {
                    /* Set received ID to a main player */
                    playerComponent.playerID = playerDataContainer.getPlayerID();
                    logger.info("[ASSIGN_ID_TO_PLAYER]: playerComponent.playerID assigned to " + playerComponent.playerID);

                    this.gameWorldCreator.getGameWorld().getPlayerByReference(entity).
                            getComponent(PlayerComponent.class).playerID = playerComponent.playerID;

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
                if(playerComponent.playerID == playerDataContainer.getPlayerID())
                {
                    controlledInputComponent.abInputCommandList = playerDataContainer.getAbInputCommandList();
                }
            break;*/

            case ClientHandler.UPDATE_PLAYER_POS:

                logger.debug("UPDATE_PLAYER_POS");
                B2dBodyComponent b2dBodyComponent = entity.getComponent(B2dBodyComponent.class);
                //logger.debug("GetLinearVelocity" + b2dBodyComponent.body.getLinearVelocity());

                if(playerComponent.playerID == playerDataContainer.getPlayerID())
                {
                    b2dBodyComponent.body.setTransform(
                            playerDataContainer.getPosition().x * GameConfig.DIVIDE_BY_PPM,
                            playerDataContainer.getPosition().y * GameConfig.DIVIDE_BY_PPM,
                            0);
                }
            break;

            case ClientHandler.PLAYER_FIRED:

                logger.debug("PLAYER_FIRED");

                if(playerComponent.playerID == playerDataContainer.getPlayerID())
                {
                    controlledInputComponent.abInputCommandList = playerDataContainer.getAbInputCommandList();
                }
            break;

            case ClientHandler.PLAYER_CHANGED_DIR:

                logger.debug("PLAYER_CHANGED_DIR");

                if(playerComponent.playerID == playerDataContainer.getPlayerID())
                {
                    if(directionComponent.direction == Direction.LEFT)
                    {
                        directionComponent.direction = Direction.RIGHT;
                    }
                    else
                    {
                        directionComponent.direction = Direction.LEFT;
                    }
                }
                break;

            default:
                logger.error("processData entity: Wrong action type" + actionType);
        }
    }

    private void processData(PlayerDataContainer playerDataContainer, int actionType)
    {
        /* Entities that represent players */
        ImmutableArray<Entity> entityPlayers = this.gameWorldCreator.getPooledEngine().
                getEntitiesFor(Family.all(PlayerComponent.class, ControlledInputComponent.class).
                        get());

        Entity tempEntity = null;

        switch(actionType)
        {
            case ClientHandler.UPDATE_PLAYER_TABLE:

                logger.debug("UPDATE_PLAYER_TABLE");

                boolean isIdFound = false;

                /* Find player searching by Player Id */
                for (Entity entityPlayer: entityPlayers)
                {
                    PlayerComponent playerComponent = entityPlayer.getComponent(PlayerComponent.class);
                    if(playerComponent.playerID == playerDataContainer.getPlayerID())
                    {
                        isIdFound = true;
                        break;
                    }
                }

                if(!isIdFound)
                {
                    Entity entity = this.gameWorldCreator.createPlayer(false, GameConfig.ONLINE_CONNECTION, playerDataContainer.getPosition());
                    entity.getComponent(PlayerComponent.class).playerID = playerDataContainer.getPlayerID();

                    logger.info("Player with ID" + entity.getComponent(PlayerComponent.class).playerID + " has been created ");

                    Message message = new Message(PLAYER_TABLE_UPDATED, true);
                    message.addPlayerDataContainer(new PlayerDataContainer());
                    this.clientHandler.addTransmitingMessage(message);
                }
            break;

            case ClientHandler.PLAYER_DISCONNECTED:

                logger.info("PLAYER_DISCONNECTED");

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
                    logger.error("tempEntity is empty");
                }

            break;

            case ClientHandler.CREATE_ALL_ENEMIES:

                logger.debug("CREATE_ALL_ENEMIES");
                logger.debug("createAllEnemies started, Current time:" + TimeUtils.millis());
                this.gameWorldCreator.createEnemies();
                this.gameWorldCreator.createClouds();
                logger.debug("createAllEnemies finished, Current time:" + TimeUtils.millis());

                for (Entity entityPlayer: entityPlayers)
                {
                    entityPlayer.add(new ControllableComponent());
                }

            break;

            default:
                logger.error("processData no entity: Wrong action type" + actionType);
        }
    }

    public GameWorldCreator getGameWorldCreator() {
        return gameWorldCreator;
    }
}
