package com.mygdx.game.screens.menuScreens;

import static com.mygdx.game.MyGdxGame.MODE_SELECTION_SCREEN;

import com.badlogic.gdx.Gdx;
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

public class MenuScreen extends  MenuScreenBase {

    private static final String CLASS_NAME  = MenuScreen.class.getSimpleName();
    private static final Logger logger      = new Logger(CLASS_NAME, Logger.INFO);

    public MenuScreen(MyGdxGame game) {
        super(game);
        //Fresh start, force garbage collector to clean all the garbage
        //When the menu screen has been created(For the first time or when
        // losing a game and come to the menu screen again)
        System.gc();
    }

    @Override
    protected Actor createUi() {
        Table table = new Table();

        //Getting texture atlas from asset manager
        TextureAtlas backGround = assetManager.getResources(AssetDescriptors.BACK_GROUND);

        //Getting skin for all the menus
        Skin uiskin = this.game.getUiSkin();

        TextureRegion backgroundRegion = backGround.findRegion(GameConfig.BACKGROUND);
        table.setBackground(new TextureRegionDrawable(backgroundRegion));

        // start game
        TextButton playButton = new TextButton("START GAME", uiskin);
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CheckAndPlayMenuSound();
                game.changeScreen(MODE_SELECTION_SCREEN);
            }
        });

        // set difficulty button
        TextButton setDifficulty = new TextButton("SET DIFFICULTY", uiskin);
        setDifficulty.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CheckAndPlayMenuSound();
                game.changeScreen(MyGdxGame.DIFFICULTY_SCREEN);
            }
        });

        // options button
        TextButton optionsButton = new TextButton("OPTIONS", uiskin);
        optionsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CheckAndPlayMenuSound();
                game.changeScreen(MyGdxGame.OPTIONS_SCREEN);
            }
        });

        // quit button
        TextButton quitButton = new TextButton("QUIT", uiskin);
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CheckAndPlayMenuSound();
                quit();
            }
        });

        // setup table
        Table buttonTable = new Table(uiskin);
        buttonTable.defaults().pad(20);

        buttonTable.add(playButton).row();
        buttonTable.add(setDifficulty).row();
        buttonTable.add(optionsButton).row();
        buttonTable.add(quitButton);

        //buttonTable.center();

        table.add(buttonTable);
        table.center();
        table.setFillParent(true);
        table.pack();

        return table;
    }

    private void quit()
    {
        CheckAndPlayMenuSound();
        Gdx.app.exit();
    }
}
