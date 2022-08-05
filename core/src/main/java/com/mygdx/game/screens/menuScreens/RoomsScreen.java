package com.mygdx.game.screens.menuScreens;

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
import com.mygdx.game.screens.ConnectionTypeScreen;

public class RoomsScreen extends  MenuScreenBase{

    private static final Logger logger = new Logger(ConnectionTypeScreen.class.getSimpleName(), Logger.INFO);
    private String modeName;

    public RoomsScreen(MyGdxGame game) {
        super(game);
        this.modeName = game.getGameMode();
    }

    @Override
    protected Actor createUi() {
        Table table = new Table();
        int[] roomsStatus;

        if(this.modeName.equals(GameConfig.GAME_MODE_COOP))
        {
            roomsStatus =  this.game.getGeneralInfoContainer().getCoopRooms();
        }
        else if(this.modeName.equals(GameConfig.GAME_MODE_PVP))
        {
            roomsStatus = this.game.getGeneralInfoContainer().getPvpRooms();
        }
        else
        {
            roomsStatus = new int[GameConfig.NO_OF_ROOMS];
            logger.error("Wrong mode Name");
        }

        String capacitySlots = "2";

        //Getting texture atlas from asset manager
        TextureAtlas backGround = assetManager.getResources(AssetDescriptors.BACK_GROUND);

        //Getting skin for all the menus
        Skin uiskin = this.game.getUiSkin();

        TextureRegion backgroundRegion = backGround.findRegion(GameConfig.BACKGROUND);
        table.setBackground(new TextureRegionDrawable(backgroundRegion));

        //Profile Name
        TextButton profileNameButton = new TextButton(this.modeName + ":" + this.game.getGeneralInfoContainer().getUserName(), uiskin);

        // Room 1
        TextButton room1Button = new TextButton("ROOM 1 " + roomsStatus[0] + "/" + capacitySlots, uiskin);
        room1Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.changeScreen(GAME_SCREEN);
            }
        });

        // Room 2
        TextButton room2Button = new TextButton("ROOM 2 " + roomsStatus[1] + "/" + capacitySlots, uiskin);
        room2Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.changeScreen(GAME_SCREEN);
            }
        });

        // Room 3
        TextButton room3Button = new TextButton("ROOM 3 " + roomsStatus[2] + "/" + capacitySlots, uiskin);
        room3Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.changeScreen(GAME_SCREEN);
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

        buttonTable.add(profileNameButton).row();
        buttonTable.add(room1Button).row();
        buttonTable.add(room2Button).row();
        buttonTable.add(room3Button).row();
        buttonTable.add(backButton);

        table.add(buttonTable);
        table.center();
        table.setFillParent(true);
        table.pack();

        return table;
    }
}
