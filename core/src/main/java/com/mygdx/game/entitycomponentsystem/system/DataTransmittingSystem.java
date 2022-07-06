package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.client.ClientHandler;
import com.mygdx.game.client.Message;
import com.mygdx.game.client.data.PlayerDataContainer;
import com.mygdx.game.entitycomponentsystem.components.LocalInputComponent;
import com.mygdx.game.entitycomponentsystem.components.PlayerComponent;
import com.mygdx.game.entitycomponentsystem.components.TransformComponent;

import io.socket.client.Socket;

public class DataTransmittingSystem extends IteratingSystem {

    protected static final Logger logger = new Logger(DataTransmittingSystem.class.getSimpleName(), Logger.INFO);
    private ClientHandler clientHandler;
    private Message message;

    public DataTransmittingSystem(ClientHandler clientHandler) {
        super(Family.all(PlayerComponent.class, LocalInputComponent.class).get());

        this.clientHandler = clientHandler;
    }

    @Override
    public void update (float deltaTime)
    {
        /* Optimization: Go through all entities only if there is some new data */
        if(!this.clientHandler.isTransMessageArrayEmpty())
        {
            this.message = this.clientHandler.getTransmittingMessage();
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
        }
        /* The message has been processed in one of conditions above */
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
                        playerDataContainer.getPlayerID());
            break;

            case ClientHandler.PLAYER_TABLE_UPDATED:

                logger.debug("PLAYER_TABLE_UPDATED");
                playerDataContainer.setPlayerID(playerComponent.playerID);

                logger.debug("playerTableUpdated: Player with ID " + playerComponent.playerID + " updated Player Table");
                socket.emit("playerTableUpdated",
                        playerComponent.playerID);
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
                        playerDataContainer.getPosition().x,
                        playerDataContainer.getPosition().y,
                        playerDataContainer.getPlayerID());
            break;

            default:
                logger.error("processData no entity: Wrong action type" + actionType);
        }
    }

}
