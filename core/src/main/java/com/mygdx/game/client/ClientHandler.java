package com.mygdx.game.client;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.client.data.PlayerDataContainer;
import com.mygdx.game.common.Direction;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.entitycomponentsystem.system.DataReceivingSystem;
import com.mygdx.game.entitycomponentsystem.system.DataTransmittingSystem;
import com.mygdx.game.entitycomponentsystem.system.InputManagerTransmittingSystem;
import com.mygdx.game.screens.loadingScreens.LoadingIntroScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ClientHandler {

    protected static final Logger logger = new Logger(ClientHandler.class.getSimpleName(), Logger.INFO);
    public static ClientHandler instance;

    public static ClientHandler getInstance(PooledEngine pooledEngine) {
        if (instance == null) {
            instance = new ClientHandler(pooledEngine);
        }
        return instance;
    }

    private Socket socket;
    private IO.Options options;
    //public static String url = "http://138.68.160.152:8080";
    //public static String url = "http://localhost:8080";
    public static String url = "http://192.168.0.12:8080";

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

    /* Transmitting Actions */
    public static final int SEND_PLAYER_TO_SERVER = 8;          /* Send Player data of a newly created player */
    public static final int UPLOAD_CURRENT_PLAYER_POS_REQ = 9;  /* Upload Current Player Position upon a request */
    public static final int PLAYER_TABLE_UPDATED = 10;          /* Inform server that player table has been updated */

    private ClientHandler(PooledEngine pooledEngine) {
        createSocket();
        this.receivedMessageArray = new Array<>();
        this.transmitingMessageArray = new Array<>();
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


    }

    //===============Callbacks for event listener ==========================//

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


}
