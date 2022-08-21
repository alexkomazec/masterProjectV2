package com.mygdx.game.entitycomponentsystem.system;


import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.entitycomponentsystem.components.PlayerComponent;
import com.mygdx.game.ui.EndMatchPanel;

import java.util.HashMap;

public class MatchTracker {

    private static final Logger logger = new Logger(MatchTracker.class.getSimpleName(), Logger.DEBUG);

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

    public void playerFinishedMatch(PlayerComponent playerComponent)
    {
        this.activePlayers.remove(playerComponent);
        this.activePlayers.put(playerComponent, true);

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
            boolean allPlayersFinished = true;

            //Online player has been died, check the state of all alive players
            for (Object value : this.activePlayers.values())
            {
                if(!(Boolean)value)
                {
                    allPlayersFinished = false;
                    break;
                }
            }

            if(allPlayersFinished)
            {
                //Display Message WIN GAME
                logger.info("allPlayersFinished");
                endMatchPanel.setLabelText("WIN GAME");
                logger.debug(" WIN GAME");
                endMatchPanel.show();
            }

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
