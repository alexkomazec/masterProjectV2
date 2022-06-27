package com.mygdx.game.screens.menuScreens;

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

public class ModeSelectionScreen extends  MenuScreenBase {

    private static final String CLASS_NAME  = ModeSelectionScreen.class.getSimpleName();
    private static final Logger log         = new Logger(CLASS_NAME, Logger.DEBUG);

    public ModeSelectionScreen(MyGdxGame game) {
        super(game);
        System.gc();
    }

    @Override
    protected Actor createUi()
    {
        Table table = new Table();

        //Getting texture atlas from asset manager
        TextureAtlas backGround = assetManager.getResource(AssetDescriptors.BACK_GROUND);

        //Getting skin for all the menus
        Skin uiskin = assetManager.getResource(AssetDescriptors.UI_SKIN);

        TextureRegion backgroundRegion = backGround.findRegion(GameConfig.BACKGROUND);
        table.setBackground(new TextureRegionDrawable(backgroundRegion));

        TextButton playButton = new TextButton("Offline - SinglePlayer", uiskin);
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setConnectionType(GameConfig.LOCAL_CONNECTION);
                game.changeScreen(GAME_SCREEN);
            }
        });

        TextButton setDifficulty = new TextButton("Offline - Co-op", uiskin);
        setDifficulty.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            }
        });

        TextButton optionsButton = new TextButton("Online Co-op", uiskin);
        optionsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setConnectionType(GameConfig.ONLINE_CONNECTION);
                game.setClientHandler();
                game.changeScreen(CONNECT_SCREEN);
            }
        });

        TextButton quitButton = new TextButton("Online - PvP", uiskin);
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setConnectionType(GameConfig.ONLINE_CONNECTION);
                game.setClientHandler();
                game.changeScreen(CONNECT_SCREEN);
            }
        });

        TextButton backButton = new TextButton("Back", uiskin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                back();
            }
        });

        // setup table
        Table buttonTable = new Table(uiskin);
        buttonTable.defaults().pad(20);

        buttonTable.add(playButton).row();
        buttonTable.add(setDifficulty).row();
        buttonTable.add(optionsButton).row();
        buttonTable.add(quitButton).row();
        buttonTable.add(backButton);

        buttonTable.center();

        table.add(buttonTable);
        table.center();
        table.setFillParent(true);
        table.pack();

        return table;
    }

}
