package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.client.ClientHandler;
import com.mygdx.game.client.Message;
import com.mygdx.game.client.data.PlayerDataContainer;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.entitycomponentsystem.components.LocalInputComponent;
import com.mygdx.game.entitycomponentsystem.components.PlayerComponent;
import com.mygdx.game.entitycomponentsystem.components.TransformComponent;

import io.socket.client.Socket;

public class DataTransmittingSystem extends IteratingSystem {

    protected static final Logger logger = new Logger(DataTransmittingSystem.class.getSimpleName(), Logger.DEBUG);
    private ClientHandler clientHandler;
    private Message message;

    public DataTransmittingSystem(ClientHandler clientHandler) {
        super(Family.all(PlayerComponent.class, LocalInputComponent.class).get());
        logger.debug("DataTransmittingSystem has been created");
        this.clientHandler = clientHandler;
    }

    @Override
    public void update (float deltaTime)
    {
        /* Optimization: Go through all entities only if there is some new data */
        if(!this.clientHandler.isTransMessageArrayEmpty())
        {
            /* Check if the first element is related to player Data, so just peek in the buffer */
            this.message = this.clientHandler.peekAtFirstTrans();

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
                this.clientHandler.getTransmitingMessageArray().removeIndex(0);
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

        for (int index = 0; index < this.message.getPlayerDataContainerArray().size; index++)
        {
            processData(this.message.getPlayerDataContainer(), this.message.getActionType(), entity);
        }
    }

    private void processData(PlayerDataContainer playerDataContainer, int actionType, Entity entity)
    {
        Socket socket = this.clientHandler.getSocket();
        PlayerComponent playerComponent = entity.getComponent(PlayerComponent.class);
        TransformComponent transformComponent = entity.getComponent(TransformComponent.class);

        switch(actionType)
        {
            case ClientHandler.UPLOAD_CURRENT_PLAYER_POS_REQ:

                logger.debug("UPLOAD_CURRENT_PLAYER_POS_REQ");

                playerDataContainer.setPlayerID(playerComponent.playerID);
                playerDataContainer.setPosition(transformComponent.position);

                logger.debug("refreshPlayersPosition: Player with ID " + playerDataContainer.getPlayerID() + " sent x,y");
                socket.emit("refreshPlayersPosition",
                        playerDataContainer.getPosition().x,
                        playerDataContainer.getPosition().y,
                        socket.id());
            break;

            case ClientHandler.PLAYER_TABLE_UPDATED:

                logger.debug("PLAYER_TABLE_UPDATED");
                playerDataContainer.setPlayerID(playerComponent.playerID);

                logger.debug("playerTableUpdated: Player with ID " + playerComponent.playerID + " updated Player Table");
                socket.emit("playerTableUpdated",
                        socket.id());
            break;

            case ClientHandler.PLAYER_FIRED_SEND:

                logger.debug("PLAYER_FIRED_SEND");

                this.clientHandler.getSocket().emit("magicFired",
                        this.clientHandler.getSocket().id(),
                        playerDataContainer.getAbInputCommandList()[GameConfig.SPACE],
                        playerDataContainer.getPosition().x,
                        playerDataContainer.getPosition().y,
                        playerDataContainer.getBulletXvelocity(),
                        playerDataContainer.getBulletDirection()
                );
                break;

            default:
                logger.error("processData entity: Wrong action type" + actionType);
        }
    }

    private void processData(PlayerDataContainer playerDataContainer, int actionType)
    {
        Socket socket = this.clientHandler.getSocket();
        switch(actionType)
        {
            /* NOTE: Be careful, this case does not depend on entity*/
            case ClientHandler.SEND_PLAYER_TO_SERVER:

                logger.debug("SEND_PLAYER_TO_SERVER");

                logger.debug("addPlayer: Please add Player with ID " + playerDataContainer.getPlayerID());
                socket.emit("addPlayer",
                        playerDataContainer.getPlayerWidth(),
                        playerDataContainer.getPosition().x,
                        playerDataContainer.getPosition().y,
                        clientHandler.getSocket().id());
            break;

            case ClientHandler.COLLISION_EVENT:

                logger.debug("COLLISION_EVENT");

                int bodyID1 = message.getGeneralInfoContainer().getBodyIDPair().integerA;
                int bodyID2 = message.getGeneralInfoContainer().getBodyIDPair().integerB;
                String bodyName1 = message.getGeneralInfoContainer().getBodyNamesPair().stringA;
                String bodyName2 = message.getGeneralInfoContainer().getBodyNamesPair().stringB;

                logger.debug("Body with id + " + bodyID1 + " and body with id " + bodyID2 + " is being sent ");

                socket.emit("collisionEvent", socket.id(), bodyID1, bodyName1, bodyID2, bodyName2);
            break;

            default:
                logger.error("processData no entity: Wrong action type" + actionType);
        }
    }

}
