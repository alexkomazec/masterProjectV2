package com.mygdx.game.entitycomponentsystem.system;

import static com.mygdx.game.client.ClientHandler.PLAYER_TABLE_UPDATED;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.client.ClientHandler;
import com.mygdx.game.client.Message;
import com.mygdx.game.client.data.GeneralInfoContainer;
import com.mygdx.game.client.data.PlayerDataContainer;
import com.mygdx.game.common.Direction;
import com.mygdx.game.common.FixturePair;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.entitycomponentsystem.components.B2dBodyComponent;
import com.mygdx.game.entitycomponentsystem.components.BulletComponent;
import com.mygdx.game.entitycomponentsystem.components.ControllableComponent;
import com.mygdx.game.entitycomponentsystem.components.ControlledInputComponent;
import com.mygdx.game.entitycomponentsystem.components.DirectionComponent;
import com.mygdx.game.entitycomponentsystem.components.LocalInputComponent;
import com.mygdx.game.entitycomponentsystem.components.PlayerComponent;
import com.mygdx.game.entitycomponentsystem.components.TransformComponent;
import com.mygdx.game.gameworld.GameWorldCreator;
import com.mygdx.game.screens.GameScreen;

import java.util.ArrayList;

import javax.swing.text.TabExpander;

public class DataReceivingSystem extends IteratingSystem {

    protected static final Logger logger = new Logger(DataReceivingSystem.class.getSimpleName(), Logger.DEBUG);
    private ClientHandler clientHandler;
    private GameWorldCreator gameWorldCreator;
    private GeneralInfoContainer generalInfoContainer;
    private Message message;

    public DataReceivingSystem(ClientHandler clientHandler) {
        super(Family.all(PlayerComponent.class, ControlledInputComponent.class).get());
        logger.debug("DataReceivingSystem has been created");
        this.clientHandler = clientHandler;

        this.gameWorldCreator = GameWorldCreator.getInstance();
        this.generalInfoContainer = generalInfoContainer;
    }

    @Override
    public void update (float deltaTime)
    {
        /* Optimization: Go through all entities only if there is some new data */
        if(!this.clientHandler.isRecMessageArrayEmpty())
        {

            /* Check if the first element is related to player Data, so just peek in the buffer */
            this.message = this.clientHandler.peekAtFirstRecv();

            if(!this.message.getPlayerDataContainerArray().isEmpty())
            {
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

                /* Message has been processed, so it is okay to remove it from the buffer now */
                this.clientHandler.getReceivedMessageArray().removeIndex(0);
            }
            else
            {
                logger.debug("This message is not related to Player");
            }
        }
        /* Set message to be ready for the next reading */
        this.message = null;
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
        B2dBodyComponent b2dBodyComponent;
        ControlledInputComponent controlledInputComponent = entity.getComponent(ControlledInputComponent.class);

        switch(actionType)
        {
            case ClientHandler.ASSIGN_ID_TO_PLAYER:

                logger.debug("ASSIGN_ID_TO_PLAYER");
                LocalInputComponent localInputComponent = entity.getComponent(LocalInputComponent.class);
                TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
                b2dBodyComponent = entity.getComponent(B2dBodyComponent.class);

                if(localInputComponent != null)
                {
                    /* Set received ID to a player */
                    playerComponent.playerID = playerDataContainer.getPlayerID();
                    this.clientHandler.getGame().setClientIDinGame(playerComponent.playerID);
                    b2dBodyComponent.bodyID = playerDataContainer.getPlayerID();

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

                    if(playerComponent.typeOfPlayer == PlayerComponent.PlayerConnectivity.LOCAL
                        && playerComponent.playerID == 0)
                    {
                        /* This player is a host, so need to initialize B2ContactListener*/
                        this.clientHandler.getGame().setB2ContactListener();
                    }

                    playerDataContainerTmp.setAbInputCommandList(controlledInputComponent.abInputCommandList);
                    playerDataContainerTmp.setPosition(transformComponent.position);
                    playerDataContainerTmp.setPlayerWidth(GameConfig.DEFAULT_PLAYER_WIDTH);
                    Message message = new Message(ClientHandler.SEND_PLAYER_TO_SERVER, false);
                    message.addPlayerDataContainer(playerDataContainerTmp);

                    /* Send Relevant data of localy created player */
                    this.clientHandler.addTransmitingMessage(message);
                }
            break;

            case ClientHandler.REDEFINE_PLAYER_POSITION:
                logger.debug("REDEFINE_PLAYER_POSITION");
                b2dBodyComponent = entity.getComponent(B2dBodyComponent.class);
                b2dBodyComponent.body.setTransform(
                        playerDataContainer.getPosition().x * GameConfig.DIVIDE_BY_PPM ,
                        playerDataContainer.getPosition().y * GameConfig.DIVIDE_BY_PPM,
                        0);

            break;
            /* CLARIFICATION: REMOTE_PLAYER_MOVED is deprecated so far, UPDATE_PLAYER_POS is used instead*/
            /*case ClientHandler.REMOTE_PLAYER_MOVED:
                if(playerComponent.playerID == playerDataContainer.getPlayerID())
                {
                    controlledInputComponent.abInputCommandList = playerDataContainer.getAbInputCommandList();
                }
            break;*/

            case ClientHandler.UPDATE_PLAYER_POS:

                //Alko_uncomment_it: logger.debug("UPDATE_PLAYER_POS");
                b2dBodyComponent = entity.getComponent(B2dBodyComponent.class);
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
                    logger.debug("PLAYER_FIRED: playerComponent.playerID: " + playerComponent.playerID);

                    controlledInputComponent.abInputCommandList[GameConfig.SPACE] = playerDataContainer.getInputCoommandFire();
                    playerComponent.bulletDirectionOnShoot = playerDataContainer.getBulletDirection();
                    playerComponent.bulletXvel = playerDataContainer.getBulletXvelocity();
                    playerComponent.bulletPosition = playerDataContainer.getPosition();
                    playerComponent.needTofire = true;
                }
                else
                {
                    logger.debug("Player with id: " + playerDataContainer.getPlayerID() + " did not fired");
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

                /* If player is found, this player is already in the table, let's go to the next message */
                if(!isIdFound)
                {
                    Entity entity = this.gameWorldCreator.createPlayer(false, GameConfig.ONLINE_CONNECTION, playerDataContainer.getPosition());
                    entity.getComponent(PlayerComponent.class).playerID = playerDataContainer.getPlayerID();
                    entity.getComponent(B2dBodyComponent.class).bodyID = playerDataContainer.getPlayerID();

                    logger.info("Player with ID" + entity.getComponent(PlayerComponent.class).playerID + " has been created ");

                    Message message = new Message(PLAYER_TABLE_UPDATED, true);
                    message.addPlayerDataContainer(new PlayerDataContainer());
                    this.clientHandler.addTransmitingMessage(message);
                }
            break;

            case ClientHandler.PLAYER_DISCONNECTED:

                logger.info("PLAYER_DISCONNECTED");
                /* Do not need return value of markPlayerBodyToBeDeleted */
                markPlayerBodyToBeDeleted(entityPlayers, playerDataContainer);

            break;

            case ClientHandler.GO_OUT_FROM_THE_ROOM_RESP:

                logger.info("GO_OUT_FROM_THE_ROOM_RESP");
                PlayerComponent.PlayerConnectivity playerConnectivity;
                playerConnectivity = markPlayerBodyToBeDeleted(entityPlayers, playerDataContainer);

                if(playerConnectivity == PlayerComponent.PlayerConnectivity.LOCAL)
                {
                    logger.debug(" Local player with id : " + playerDataContainer.getPlayerID() + " will go out of the room");
                    this.clientHandler.notifyToGoOutFromTheRoom();
                }

                break;

            case ClientHandler.CREATE_ALL_ENEMIES:

                logger.debug("CREATE_ALL_ENEMIES");

                if(this.clientHandler.getGame().getGameMode().equals(GameConfig.GAME_MODE_COOP))
                {
                    this.gameWorldCreator.createEnemies();
                    this.gameWorldCreator.createClouds();
                }

                for (Entity entityPlayer: entityPlayers)
                {
                    entityPlayer.add(new ControllableComponent());
                }

            break;

            case ClientHandler.COLLISION_EVENT_RECEIVED:

                logger.debug("COLLISION_EVENT_RECEIVED");

                int bodyID1 = message.getGeneralInfoContainer().getBodyIDPair().integerA;
                int bodyID2 = message.getGeneralInfoContainer().getBodyIDPair().integerB;

                Fixture fa = getFixture(bodyID1);
                if(fa == null)
                    logger.error("Fixture not found for bodyID1: " + bodyID1);

                Fixture fb = getFixture(bodyID2);
                if(fb == null)
                    logger.error("Fixture not found for bodyID1: " + bodyID2);

                logger.debug("Fixture for BodyID1: " + bodyID1);
                logger.debug("Fixture for BodyID1: " + bodyID2);

                if(fa!= null && fb!= null)
                {
                    logger.debug("Has been added to bufferOfFixtures");
                    GameScreen.bufferOfFixtures.add(new FixturePair(fa,fb));
                }
                else
                {
                    logger.debug("Has not been added to bufferOfFixtures");
                }

                break;

            default:
                logger.error("processData no entity: Wrong action type" + actionType);
        }
    }

    private PlayerComponent.PlayerConnectivity markPlayerBodyToBeDeleted(ImmutableArray<Entity> entityPlayers,
                                           PlayerDataContainer playerDataContainer)
    {
        Entity tempEntity = null;
        PlayerComponent.PlayerConnectivity playerConnectivityTemp = PlayerComponent.PlayerConnectivity.NONE;
        boolean localFound = false;
        boolean onlineFound = false;

        for (Entity entityPlayer: entityPlayers)
        {
            PlayerComponent playerComponent = entityPlayer.getComponent(PlayerComponent.class);
            if(playerComponent.playerID == playerDataContainer.getPlayerID())
            {
                tempEntity = entityPlayer;
                playerConnectivityTemp = playerComponent.typeOfPlayer;
                break;
            }
            else
            {
                if(playerComponent.typeOfPlayer == PlayerComponent.PlayerConnectivity.LOCAL)
                {
                    localFound = true;
                }
                else
                {
                    onlineFound = true;
                }
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
            /* Entity is already deleted, reason:  PhysicsSystem already delete the body */
            logger.error("tempEntity is empty");

            if(localFound && !onlineFound)
            {
                playerConnectivityTemp = PlayerComponent.PlayerConnectivity.ONLINE;
            }
            else if(localFound && onlineFound)
            {
                playerConnectivityTemp = PlayerComponent.PlayerConnectivity.ONLINE;
            }
            else if(!localFound)
            {
                playerConnectivityTemp = PlayerComponent.PlayerConnectivity.LOCAL;
            }
            else
            {
                logger.error("Wrong combination");
            }
        }

        return playerConnectivityTemp;
    }

    public GameWorldCreator getGameWorldCreator() {
        return gameWorldCreator;
    }

    private Fixture getFixture(int bodyID)
    {
        ImmutableArray<Entity> entities = this.clientHandler.getGame().getPooledEngine().getEntitiesFor(Family.all(B2dBodyComponent.class).get());
        Fixture tempFixture = null;

        for (Entity entity: entities)
        {
            B2dBodyComponent b2dBodyComponent = entity.getComponent(B2dBodyComponent.class);
            int entityBodyID = b2dBodyComponent.bodyID;

            if(bodyID == entityBodyID)
            {
                Array<Fixture> fixtureList = b2dBodyComponent.body.getFixtureList();
                if(fixtureList.size != 1)
                {
                    logger.error("fixtureList wrong size");
                }
                tempFixture = fixtureList.get(0);
            }
        }

        return tempFixture;
    }
}
