package com.mygdx.game.entitycomponentsystem.system;


import static com.mygdx.game.config.GameConfig.GROUND_BIT;
import static com.mygdx.game.config.GameConfig.PLAYER_BIT;

import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.entitycomponentsystem.components.B2dBodyComponent;
import com.mygdx.game.entitycomponentsystem.components.PlayerComponent;
import com.mygdx.game.ui.EndMatchPanel;

import java.util.HashMap;

public class MatchTracker {

    private static final Logger logger = new Logger(MatchTracker.class.getSimpleName(), Logger.INFO);

    public static MatchTracker instance;
    private HashMap<PlayerComponent, Boolean> activePlayers;
    private EndMatchPanel endMatchPanel;

    public static MatchTracker getInstance()
    {
        if (instance == null) {
            instance = new MatchTracker();
        }
        return instance;
    }

    MatchTracker()
    {
        activePlayers = new HashMap<>();
    }

    public void playerFinishedMatch(PlayerComponent playerComponent, B2dBodyComponent b2dBodyComponent)
    {
        this.activePlayers.remove(playerComponent);
        this.activePlayers.put(playerComponent, true);

        /* Make the player invincible because he won the game */
        Filter filter  = new Filter();
        filter.categoryBits = PLAYER_BIT;
        filter.maskBits = GROUND_BIT;
        b2dBodyComponent.body.getFixtureList().get(0).setFilterData(filter);

        //Display Win panel -> Level is finished, Go to next level/ exit
        //Display Message WIN GAME
        if(playerComponent.typeOfPlayer == PlayerComponent.PlayerConnectivity.LOCAL)
        {
            endMatchPanel.setLabelText("WIN GAME");
            logger.debug(" GameConfig.LOCAL_PLAYER) WIN GAME");
            endMatchPanel.show();
        }
    }

    public void increaseNoOfAlivePlayers(PlayerComponent playerComponent)
    {
        this.activePlayers.put(playerComponent, false);
    }

    public void decreaseNoOfAlivePlayers(PlayerComponent playerComponent)
    {
        PlayerComponent.PlayerConnectivity typeOfPlayer = playerComponent.typeOfPlayer;
        this.activePlayers.remove(playerComponent);

        if(typeOfPlayer == PlayerComponent.PlayerConnectivity.LOCAL)
        {
            //Show GameOverScreen => Quit game/ Exit Game panel
            logger.info("typeOfPlayer is LOCAL_PLAYER");
            logger.debug(" //Show GameOverScreen => Quit game/ Exit Game panel");
            endMatchPanel.show();
        }
        else
        {
            /*boolean allPlayersFinished = true;

            //Online player has been died, check the state of all alive players
            for (Object value : this.activePlayers.values())
            {
                if(!(Boolean)value)
                {
                    allPlayersFinished = false;
                    break;
                }
            }

            if(allPlayersFinished && activePlayers.isEmpty())
            {
                //Display Message WIN GAME
                logger.info("allPlayersFinished");
                endMatchPanel.setLabelText("WIN GAME");
                logger.debug(" WIN GAME");
                endMatchPanel.show();
            }*/

        }
    }

    public void reset()
    {
        activePlayers.clear();
        this.endMatchPanel = null;
    }

    public void setGameOverPanel(EndMatchPanel endMatchPanel) {
        this.endMatchPanel = endMatchPanel;
    }
}
