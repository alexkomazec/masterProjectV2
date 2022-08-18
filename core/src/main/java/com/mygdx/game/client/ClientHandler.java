package com.mygdx.game.client;

import static com.mygdx.game.MyGdxGame.GAME_SCREEN;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.client.data.GeneralInfoContainer;
import com.mygdx.game.client.data.PlayerDataContainer;
import com.mygdx.game.common.Direction;
import com.mygdx.game.common.Topics;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.entitycomponentsystem.system.DataReceivingSystem;
import com.mygdx.game.entitycomponentsystem.system.DataTransmittingSystem;
import com.mygdx.game.entitycomponentsystem.system.InputManagerTransmittingSystem;
import com.mygdx.game.screens.loadingScreens.LoadingIntroScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ClientHandler {

    protected static final Logger logger = new Logger(ClientHandler.class.getSimpleName(), Logger.DEBUG);
    public static ClientHandler instance;

    public static ClientHandler getInstance(PooledEngine pooledEngine, MyGdxGame game) {
        if (instance == null) {
            instance = new ClientHandler(pooledEngine, game);
        }
        return instance;
    }

    private Socket socket;
    private IO.Options options;
    //public static String url = "http://138.68.160.152:8080";
    //public static String url = "http://localhost:8080";
    public static String url = "http://192.168.0.12:8080";

    private MyGdxGame game;
    private Array<Message> receivedMessageArray;
    private Array<Message> transmitingMessageArray;

    /* Receiving Actions */
    public static final int REMOTE_PLAYER_MOVED = 0;
    public static final int ASSIGN_ID_TO_PLAYER = 1;            /* Assign ID to a newly created player */
    public static final int UPDATE_PLAYER_TABLE = 2;            /* Get info of all players already connected */
    public static final int UPDATE_PLAYER_POS = 3;              /* Update position of a player that has been moved */
    public static final int PLAYER_FIRED = 4;                   /* Some Player fired a magic spell */
    public static final int PLAYER_CHANGED_DIR = 5;             /* Some Player changed direction*/
    public static final int PLAYER_DISCONNECTED = 6;            /* Some Player Disconnected */
    public static final int CREATE_ALL_ENEMIES = 7;             /* Create All enemies */
    public static final int SET_USERNAME = 8;                   /* Set username to the client */
    public static final int REDEFINE_PLAYER_POSITION = 9;       /* Server redefined player's position */
    public static final int GO_OUT_FROM_THE_ROOM_RESP = 10;     /* Response from the server for go out form the room req */

    /* Transmitting Actions */
    public static final int SEND_PLAYER_TO_SERVER = 11;          /* Send Player data of a newly created player */
    public static final int UPLOAD_CURRENT_PLAYER_POS_REQ = 12; /* Upload Current Player Position upon a request */
    public static final int PLAYER_TABLE_UPDATED = 13;          /* Inform server that player table has been updated */
    public static final int CLIENT_JOIN_ROOM = 14;              /* Inform the server that a client wants to join the certain room */
    public static final int GO_OUT_FROM_THE_ROOM = 15;          /* Inform server that the player leaves from the room */

    private ClientHandler(PooledEngine pooledEngine, MyGdxGame game) {
        createSocket();
        this.receivedMessageArray = new Array<>();
        this.transmitingMessageArray = new Array<>();
        this.game = game;
        pooledEngine.addSystem(new DataReceivingSystem(this));
        pooledEngine.addSystem(new DataTransmittingSystem(this));
        pooledEngine.addSystem(new InputManagerTransmittingSystem(this));
    }

    public boolean isRecMessageArrayEmpty() {
        return receivedMessageArray.isEmpty();
    }

    public boolean isTransMessageArrayEmpty()
    {
        return transmitingMessageArray.isEmpty();
    }

    private void createSocket() {
        this.options = IO.Options.builder()
                .setReconnectionAttempts(10)
                .setReconnectionDelay(10)
                .setTimeout(50)
                .setAuth(new HashMap<String, String>())
                .setTimeout(10000)
                .build();

        try {
            this.socket = IO.socket(url, options);
            logger.debug("Socket set");
            configSocketEvents();
        } catch (URISyntaxException e) {
            logger.error("Wrong URL");
            e.printStackTrace();
        }
    }

    /* Remove, and get the first element */
    public Message getReceivedMessage() {
        return receivedMessageArray.removeIndex(0);
    }

    /* Get the first element in the array*/
    public Message peekAtFirstRecv()
    {
        return receivedMessageArray.get(0);
    }

    /* Remove, and get the first element */
    public Message getTransmittingMessage() {
        return transmitingMessageArray.removeIndex(0);
    }

    /* Get the first element in the array*/
    public Message peekAtFirstTrans()
    {
        return transmitingMessageArray.get(0);
    }

    public void configSocketEvents() {

        /* Newly connected player got a proper ID from the server*/
        this.socket.on("assignID2Player", new Emitter.Listener() {

            @Override
            public void call(Object... args) {

                playerIdReceivedOnCon(args);
            }
        });

        /* Local Player (All players) should upload current position upon server broadcast request */
        this.socket.on("getUpdatedPosition", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                getUpdatedPosition(args);
            }
        });

        /* Update player list (Add all players that are already in the game (on the server)) */
        this.socket.on("updatePlayerTable", new Emitter.Listener() {

            @Override
            public void call(Object... args)
            {
                try {
                    updatePlayerTable(args);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        this.socket.on("updatePlayerInputPositionResp", new Emitter.Listener() {

            @Override
            public void call(Object... args)
            {
                try {
                    updatePlayerPos(args);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        this.socket.on("playerFiredMagic", new Emitter.Listener()
        {
            @Override
            public void call(Object... args)
            {
                try {
                    playerFiredMagic(args);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        this.socket.on("createAllEnemies", new Emitter.Listener()
        {
            @Override
            public void call(Object... args)
            {
                try {
                    createAllEnemies(args);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        this.socket.on("playerChangedDirResp", new Emitter.Listener()
        {
            @Override
            public void call(Object... args)
            {
                try {
                    playerChangedDir(args);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        this.socket.on("playerDisconnected", new Emitter.Listener()
        {
            @Override
            public void call(Object... args)
            {
                try {
                    playerDisconnected(args);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        this.socket.on("setUsername", new Emitter.Listener()
        {
            @Override
            public void call(Object... args)
            {
                try {
                    setUsername(args);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        this.socket.on("getRoomsStatusResp", new Emitter.Listener()
        {
            @Override
            public void call(Object... args)
            {
                try {
                    parseRoomsStatus(args);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        this.socket.on("redefinedPlayerPosition", new Emitter.Listener()
        {
            @Override
            public void call(Object... args)
            {
                try {
                    redefinePlayerPosition(args);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        this.socket.on("goOutFromRoomResp", new Emitter.Listener()
        {
            @Override
            public void call(Object... args)
            {
                try {
                    goOutFromTheRoomResp(args);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    //===============Callbacks for event listener ==========================//

    public void goOutFromTheRoomResp(Object... args) throws JSONException {

        logger.debug("GO_OUT_FROM_THE_ROOM_RESP " + GO_OUT_FROM_THE_ROOM_RESP);
        this.game.notifyObservers(Topics.PLAYER_LEAVE_ROOM);
    }

    public void redefinePlayerPosition(Object... args) throws JSONException
    {
        logger.info("REDEFINE_PLAYER_POSITION");
        JSONArray jsonArray = (JSONArray) args[0];
        PlayerDataContainer playerDataContainer = new PlayerDataContainer();
        float xPosition = (float)((double)jsonArray.get(1));
        float yPosition = (float)((double)jsonArray.get(2));

        Vector2 position = new Vector2(xPosition, yPosition);

        playerDataContainer.setPlayerID((int)jsonArray.get(0));
        playerDataContainer.setPosition(position);
        Message message = new Message(REDEFINE_PLAYER_POSITION, true);
        message.addPlayerDataContainer(playerDataContainer);
        receivedMessageArray.add(message);
    }

    public void parseRoomsStatus(Object... args) throws JSONException {
        logger.debug("PARSE_ROOMS_STATUS");
        JSONArray jsonArray = (JSONArray) args[0];

        /* Parse coop room status*/
        JSONArray jsonArrayRoom  = (JSONArray) jsonArray.get(0);
        JSONArray jsonArrayRoomMatchStatus = (JSONArray) jsonArray.get(1);

        for (int i = 0; i < this.game.getGeneralInfoContainer().getCoopRooms().size; i++)
        {
            this.game.getGeneralInfoContainer().getCoopRooms().get(i).numOfFullSlots = (Integer) jsonArrayRoom.get(i);
            this.game.getGeneralInfoContainer().getCoopRooms().get(i).isGameInProgress = (Boolean) jsonArrayRoomMatchStatus.get(i);
        }

        /* Parse pvp room*/
        jsonArrayRoom = (JSONArray) jsonArray.get(2);
        jsonArrayRoomMatchStatus = (JSONArray) jsonArray.get(3);

        for (int i = 0; i < this.game.getGeneralInfoContainer().getCoopRooms().size; i++)
        {
            this.game.getGeneralInfoContainer().getPvpRooms().get(i).numOfFullSlots = (Integer) jsonArrayRoom.get(i);
            this.game.getGeneralInfoContainer().getPvpRooms().get(i).isGameInProgress = (Boolean) jsonArrayRoomMatchStatus.get(i);
        }

        this.game.notifyObservers(Topics.UPDATE_ROOMS_STATE);
    }

    public void getRoomsStatus()
    {
        logger.debug("GET_ROOMS_STATUS");
        this.socket.emit("getRoomsStatusReq",socket.id());
    }

    public void setUsername(Object... args) throws JSONException
    {
        logger.debug("SET_USERNAME " + SET_USERNAME);
        this.game.getGeneralInfoContainer().setUserName((String)args[0]);
    }

    public void createAllEnemies(Object... args) throws JSONException
    {
        logger.debug("CREATE_ALL_ENEMIES");
        logger.debug("createAllEnemies received, Current time:" + TimeUtils.millis());
        Message message = new Message(CREATE_ALL_ENEMIES, false);
        message.addPlayerDataContainer(new PlayerDataContainer());
        receivedMessageArray.add(message);

    }
    public void playerDisconnected(Object... args) throws JSONException {

        logger.debug("PLAYER_DISCONNECTED");
        int playerId = (int) args[0];

        Message message = new Message(PLAYER_DISCONNECTED, false);
        message.addPlayerDataContainer(new PlayerDataContainer(new boolean[GameConfig.LIST_COMMANDS_MAX], playerId));
        receivedMessageArray.add(message);
    }

    private void playerChangedDir(Object... args) throws JSONException
    {
        logger.debug("PLAYER_CHANGED_DIR");
        JSONArray jsonArray = (JSONArray) args[0];
        int playerId = (int)jsonArray.get(0);
        logger.debug("Player with ID: " + playerId + " has changed direction");

        Message message = new Message(PLAYER_CHANGED_DIR, true);
        message.addPlayerDataContainer(new PlayerDataContainer(new boolean[GameConfig.LIST_COMMANDS_MAX], playerId));
        receivedMessageArray.add(message);
    }
    private void updatePlayerTable(Object... args) throws JSONException
    {
        logger.debug("UPDATE_PLAYER_TABLE");
        JSONArray arrPlayers = (JSONArray)args[0];

        Message message = new Message(UPDATE_PLAYER_TABLE, false);
        fillMessage(arrPlayers, message);
    }

    private void updatePlayerPos(Object... args) throws JSONException {

        logger.debug("UPDATE_PLAYER_POS");
        JSONArray arrPlayers = new JSONArray ();
        arrPlayers.put(args[0]);
        Message message = new Message(UPDATE_PLAYER_POS, true);
        fillMessage(arrPlayers, message);
    }

    private void playerIdReceivedOnCon(Object... args)
    {
        logger.debug("ASSIGN_ID_TO_PLAYER");
        this.game.notifyObservers(Topics.ONLINE_MATCH_INIT_STARTED);
        PlayerDataContainer playerDataContainer = new PlayerDataContainer();
        playerDataContainer.setPlayerID((int)args[0]);
        Message message = new Message(ASSIGN_ID_TO_PLAYER, true);
        message.addPlayerDataContainer(playerDataContainer);
        receivedMessageArray.add(message);
    }

    private void getUpdatedPosition(Object... args)
    {
        logger.debug("UPLOAD_CURRENT_PLAYER_POS_REQ");
        Message message = new Message(UPLOAD_CURRENT_PLAYER_POS_REQ, true);
        message.addPlayerDataContainer(new PlayerDataContainer());
        transmitingMessageArray.add(message);
    }

    /* CLARIFICATION: REMOTE_PLAYER_MOVED is deprecated so far, UPDATE_PLAYER_POS is used instead*/
    /*private void playerMoved(Object... args) throws JSONException {

        JSONArray jsonArray = (JSONArray) args[0];
        int playerId = (int)jsonArray.get(0);
        JSONArray inputCommandList = (JSONArray) jsonArray.get(1);
        boolean[] abInputCommandList = new boolean[GameConfig.LIST_COMMANDS_MAX];

        for (int index = 0; index < inputCommandList.length(); index++)
        {
            abInputCommandList[index] = inputCommandList.getBoolean(index);
        }

        Message message = new Message(REMOTE_PLAYER_MOVED, true);
        message.addPlayerDataContainer(new PlayerDataContainer(abInputCommandList, playerId));
        receivedMessageArray.add(message);
    }*/

    public void askServerToJoinRoom(String roomType, int roomNumber, String socketID)
    {
        logger.debug("CLIENT_JOIN_ROOM " + CLIENT_JOIN_ROOM);
        this.socket.emit("askToJoinRoom", roomType, roomNumber, socketID);
    }

    private void playerFiredMagic(Object... args) throws JSONException
    {

        logger.debug("PLAYER_FIRED");
        JSONArray jsonArray = (JSONArray) args[0];
        int playerId = (int)jsonArray.get(0);
        JSONArray inputCommandList = (JSONArray) jsonArray.get(1);
        boolean[] abInputCommandList = new boolean[GameConfig.LIST_COMMANDS_MAX];

        for (int index = 0; index < inputCommandList.length(); index++)
        {
            abInputCommandList[index] = inputCommandList.getBoolean(index);
        }

        Message message = new Message(PLAYER_FIRED, true);
        message.addPlayerDataContainer(new PlayerDataContainer(abInputCommandList, playerId));
        receivedMessageArray.add(message);

    }


    public void fillMessage(JSONArray arrPlayers, Message message) throws JSONException {

        /* Parse one by one player data, and put it into playerDataContainerArray */
        for(int index = 0; index < arrPlayers.length(); index++)
        {
            JSONObject jsonObjectCurPlayer = null;
            PlayerDataContainer playerDataContainer = new PlayerDataContainer();

            try
            {
                jsonObjectCurPlayer = (JSONObject)arrPlayers.get(index);
                logger.debug(jsonObjectCurPlayer.toString());
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            if(jsonObjectCurPlayer != null)
            {
                /* Fill Player Data Container*/
                playerDataContainer.setPlayerID(jsonObjectCurPlayer.getInt("playerID"));

                Vector2 playerPos = new Vector2((float)jsonObjectCurPlayer.getDouble("x_pos"),
                        (float)jsonObjectCurPlayer.getDouble("y_pos"));
                playerDataContainer.setPosition(playerPos);
                message.addPlayerDataContainer(playerDataContainer);
            }
        }

        receivedMessageArray.add(message);
    }

    /*
    private void connectionError(Object... args)
    {
        JSONObject message = (JSONObject) args[0];
        if(stage == null)
        {
            stage = getStage();
        }

        try {
            Dialogs.showErrorDialog(stage,message.getString("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    public IO.Options getOptions()
    {
        return options;
    }

    public void connectSocket()
    {
        this.socket.connect();
    }

    public Socket getSocket() {
        return socket;
    }

    public Array<Message> getTransmitingMessageArray() {
        return transmitingMessageArray;
    }

    public void addTransmitingMessage(Message message)
    {
        this.transmitingMessageArray.add(message);
    }

    public void goOutFromTheRoom()
    {
        logger.debug("GO_OUT_FROM_THE_ROOM" + GO_OUT_FROM_THE_ROOM);
        this.socket.emit("goOutFromRoom", this.socket.id());
    }

    public MyGdxGame getGame() {
        return game;
    }
}
