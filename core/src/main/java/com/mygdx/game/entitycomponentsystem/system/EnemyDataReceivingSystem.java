package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.client.ClientHandler;
import com.mygdx.game.client.Message;
import com.mygdx.game.entitycomponentsystem.components.AIEnemyComponent;
import com.mygdx.game.entitycomponentsystem.components.EnemyComponent;
import com.mygdx.game.gameworld.GameWorldCreator;

public class EnemyDataReceivingSystem extends IteratingSystem
{
    protected static final Logger logger = new Logger(EnemyDataReceivingSystem.class.getSimpleName(), Logger.DEBUG);
    private ClientHandler clientHandler;
    private GameWorldCreator gameWorldCreator;
    private Message message;

    public EnemyDataReceivingSystem(ClientHandler clientHandler)
    {
        super(Family.all(EnemyComponent.class, AIEnemyComponent.class).get());

        logger.debug("EnemyDataReceivingSystem has been created");
        this.clientHandler = clientHandler;

        this.gameWorldCreator = GameWorldCreator.getInstance();
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
                        //processData(this.message.getPlayerDataContainerByIndex(index), this.message.getActionType());
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

    }
}
