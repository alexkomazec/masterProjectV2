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
import com.mygdx.game.common.Observer;
import com.mygdx.game.common.Topics;
import com.mygdx.game.common.assets.AssetDescriptors;
import com.mygdx.game.config.GameConfig;

public class ModeSelectionScreen extends  MenuScreenBase implements Observer {

    private static final String CLASS_NAME     = ModeSelectionScreen.class.getSimpleName();
    private static final Logger logger         = new Logger(CLASS_NAME, Logger.INFO);
    private boolean readyToChangeScreen        = false;

    public ModeSelectionScreen(MyGdxGame game) {
        super(game);
        this.game.getWorldCreator().getRequiredResources();
        this.game.registerObserver(Topics.UPDATE_ROOMS_STATE, this);
        System.gc();
    }

    @Override
    protected Actor createUi()
    {
        Table table = new Table();

        //Getting texture atlas from asset manager
        TextureAtlas backGround = assetManager.getResources(AssetDescriptors.BACK_GROUND);

        //Getting skin for all the menus
        Skin uiskin = assetManager.getResources(AssetDescriptors.UI_SKIN);

        TextureRegion backgroundRegion = backGround.findRegion(GameConfig.BACKGROUND);
        table.setBackground(new TextureRegionDrawable(backgroundRegion));

        TextButton optionsButton = new TextButton("COOP", uiskin);
        optionsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setGameMode(GameConfig.GAME_MODE_COOP);
                game.getClientHandler().getRoomsStatus();
            }
        });

        TextButton quitButton = new TextButton("PVP", uiskin);
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setGameMode(GameConfig.GAME_MODE_PVP);
                game.getClientHandler().getRoomsStatus();
            }
        });

        TextButton backButton = new TextButton("BACK", uiskin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.getClientHandler().getSocket().close();
                back();
            }
        });

        // setup table
        Table buttonTable = new Table(uiskin);
        buttonTable.defaults().pad(20);

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

    @Override
    public void render(float delta) {
        super.render(delta);
        if(readyToChangeScreen)
        {
            game.changeScreen(MyGdxGame.ROOMS_SCREEN);
            readyToChangeScreen = false;
        }
    }

    @Override
    public void update(Object... args)
    {
        readyToChangeScreen = true;
    }
}
