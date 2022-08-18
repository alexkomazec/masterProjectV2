package com.mygdx.game.screens.menuScreens;


import static com.mygdx.game.MyGdxGame.GAME_SCREEN;
import static com.mygdx.game.config.GameConfig.NO_OF_ROOMS;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.client.Room;
import com.mygdx.game.common.Observer;
import com.mygdx.game.common.Topics;
import com.mygdx.game.common.assets.AssetDescriptors;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.screens.ConnectionTypeScreen;

public class RoomsScreen extends  MenuScreenBase implements Observer {

    private static final Logger logger = new Logger(ConnectionTypeScreen.class.getSimpleName(), Logger.INFO);
    private String modeName;
    private Array<Room> rooms;
    private boolean readyToChangeScreen = false;

    private Label room0StatusLabel;
    private Label room1StatusLabel;
    private Label room2StatusLabel;

    private TextButton room0Button;
    private TextButton room1Button;
    private TextButton room2Button;
    int capacitySlots = GameConfig.PLAYERS_IN_ROOM_CAPACITY;

    public RoomsScreen(MyGdxGame game) {
        super(game);
        this.game.registerObserver(Topics.ONLINE_MATCH_INIT_STARTED, this);
        this.game.registerObserver(Topics.UPDATE_ROOMS_STATE, this);
    }

    @Override
    protected Actor createUi() {
        this.modeName = game.getGameMode();
        Table table = new Table();
        this.rooms = getRooms();

        //Getting texture atlas from asset manager
        TextureAtlas backGround = assetManager.getResources(AssetDescriptors.BACK_GROUND);

        //Getting skin for all the menus
        Skin uiskin = this.game.getUiSkin();

        TextureRegion backgroundRegion = backGround.findRegion(GameConfig.BACKGROUND);
        table.setBackground(new TextureRegionDrawable(backgroundRegion));

        Label profileNameLabel = new Label("Username:" + this.game.getGeneralInfoContainer().getUserName(), uiskin, "roomStatus");
        Label modeLabel        = new Label("Game Mode:" + this.modeName,uiskin, "roomStatus");

        room0StatusLabel = new Label(getGameStatus(GameConfig.ROOM0),uiskin,"roomStatus");
        room1StatusLabel = new Label(getGameStatus(GameConfig.ROOM1),uiskin,"roomStatus");
        room2StatusLabel = new Label(getGameStatus(GameConfig.ROOM2),uiskin,"roomStatus");

        // Room 0
        room0Button = new TextButton("ROOM" + GameConfig.ROOM0 + " " + rooms.get(GameConfig.ROOM0).numOfFullSlots + "/" + capacitySlots, uiskin);
        room0Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                rooms = getRooms();
                if(!rooms.get(GameConfig.ROOM0).isGameInProgress)
                {
                    game.getClientHandler().askServerToJoinRoom(modeName,
                            GameConfig.ROOM0,
                            game.getClientHandler().getSocket().id());
                }
            }
        });

        // Room 1
        room1Button = new TextButton("ROOM" + GameConfig.ROOM1 + " " + rooms.get(GameConfig.ROOM1).numOfFullSlots  + "/" + capacitySlots, uiskin);
        room1Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                rooms = getRooms();
                if(!rooms.get(GameConfig.ROOM1).isGameInProgress)
                {
                    game.getClientHandler().askServerToJoinRoom(modeName,
                            GameConfig.ROOM1,
                            game.getClientHandler().getSocket().id());
                }
            }
        });

        // Room 2
        room2Button = new TextButton("ROOM" + GameConfig.ROOM2 + " " + rooms.get(GameConfig.ROOM2).numOfFullSlots  + "/" + capacitySlots, uiskin);
        room2Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                rooms = getRooms();
                if(!rooms.get(GameConfig.ROOM2).isGameInProgress)
                {
                    game.getClientHandler().askServerToJoinRoom(modeName,
                            GameConfig.ROOM2,
                            game.getClientHandler().getSocket().id());
                }
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
        buttonTable.defaults().pad(1);

        buttonTable.add(modeLabel).row();
        buttonTable.add(profileNameLabel).row();
        buttonTable.add(room0StatusLabel).row();
        buttonTable.add(room0Button).row();
        buttonTable.add(room1StatusLabel).row();
        buttonTable.add(room1Button).row();
        buttonTable.add(room2StatusLabel).row();
        buttonTable.add(room2Button).row();
        buttonTable.add(backButton).pad(20);

        table.add(buttonTable);
        table.center();
        table.setFillParent(true);
        table.pack();

        return table;
    }

    private Array<Room> getRooms()
    {
        Array<Room> rooms;

        if(this.modeName.equals(GameConfig.GAME_MODE_COOP))
        {
            rooms =  this.game.getGeneralInfoContainer().getCoopRooms();
        }
        else if(this.modeName.equals(GameConfig.GAME_MODE_PVP))
        {
            rooms = this.game.getGeneralInfoContainer().getPvpRooms();
        }
        else
        {
            rooms = new Array<>();
            rooms.size = NO_OF_ROOMS;
            logger.error("Wrong mode Name");
        }

        return rooms;
    }

    @Override
    public void update(Object... args) {

        if(args[0] == Topics.ONLINE_MATCH_INIT_STARTED)
        {
            readyToChangeScreen = true;
        }
        else if(args[0] == Topics.UPDATE_ROOMS_STATE)
        {
            Array<Room> rooms;
            room0StatusLabel.setText(getGameStatus(GameConfig.ROOM0));
            room1StatusLabel.setText(getGameStatus(GameConfig.ROOM1));
            room2StatusLabel.setText(getGameStatus(GameConfig.ROOM2));

            rooms = (this.modeName.contains(GameConfig.GAME_MODE_COOP)) ?
                    this.game.getGeneralInfoContainer().getCoopRooms():
                    this.game.getGeneralInfoContainer().getPvpRooms();


            room0Button.setText("ROOM" + GameConfig.ROOM0 + " " + rooms.get(GameConfig.ROOM0).numOfFullSlots + "/" + capacitySlots);
            room1Button.setText("ROOM" + GameConfig.ROOM1 + " " + rooms.get(GameConfig.ROOM1).numOfFullSlots + "/" + capacitySlots);
            room2Button.setText("ROOM" + GameConfig.ROOM2 + " " + rooms.get(GameConfig.ROOM2).numOfFullSlots + "/" + capacitySlots);
        }
        else
        {
            logger.error("Wrong Topics: " + args[0]);
        }

    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if(readyToChangeScreen)
        {
            this.game.changeScreen(GAME_SCREEN);
            readyToChangeScreen = false;
        }
    }

    @Override
    public void hide() {
        super.hide();
    }

    private String getGameStatus(int index)
    {
        String gameStatus = "Free to Enter";

        if(this.rooms.get(index).isGameInProgress)
        {
            gameStatus = "Room is Full";
        }
        return gameStatus;
    }
}
