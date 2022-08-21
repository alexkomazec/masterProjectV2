package com.mygdx.game.screens;

import static com.mygdx.game.MyGdxGame.CONNECT_SCREEN;
import static com.mygdx.game.MyGdxGame.GAME_SCREEN;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.common.assets.AssetDescriptors;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.screens.menuScreens.MenuScreenBase;

public class ConnectionTypeScreen extends MenuScreenBase {

    private static final Logger logger = new Logger(ConnectionTypeScreen.class.getSimpleName(), Logger.INFO);

    public ConnectionTypeScreen(MyGdxGame game) {
        super(game);
        this.game.getWorldCreator().getRequiredResources();
        System.gc();
    }

    @Override
    protected Actor createUi() {

        if(game.getClientHandler() != null)
        {
            game.removeClientHandler();
        }

        Table table = new Table();

        //Getting texture atlas from asset manager
        TextureAtlas backGround = assetManager.getResources(AssetDescriptors.BACK_GROUND);

        //Getting skin for all the menus
        Skin uiskin = this.game.getUiSkin();

        TextureRegion backgroundRegion = backGround.findRegion(GameConfig.BACKGROUND);
        table.setBackground(new TextureRegionDrawable(backgroundRegion));

        // Singleplayer
        TextButton singleplayerButton = new TextButton("OFFLINE", uiskin);
        singleplayerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CheckAndPlayMenuSound();
                game.setConnectionType(GameConfig.LOCAL_CONNECTION);
                game.changeScreen(GAME_SCREEN);
            }
        });

        // Multiplayer
        TextButton multiplayerButton = new TextButton("ONLINE", uiskin);
        multiplayerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CheckAndPlayMenuSound();
                game.setConnectionType(GameConfig.ONLINE_CONNECTION);
                game.setClientHandler();
                game.changeScreen(CONNECT_SCREEN);
            }
        });

        TextButton backButton = new TextButton("BACK", uiskin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                back();
            }
        });

        // setup table
        Table buttonTable = new Table(uiskin);
        buttonTable.defaults().pad(20);

        buttonTable.add(singleplayerButton).row();
        buttonTable.add(multiplayerButton).row();
        buttonTable.add(backButton);

        table.add(buttonTable);
        table.center();
        table.setFillParent(true);
        table.pack();

        return table;
    }
}
