package com.mygdx.game.client;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.client.data.PlayerDataContainer;
import com.mygdx.game.entitycomponentsystem.system.DataReceivingSystem;
import com.mygdx.game.entitycomponentsystem.system.DataTransmittingSystem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ClientHandler {

    public static ClientHandler instance;

    public static ClientHandler getInstance(PooledEngine pooledEngine) {
        if (instance == null) {
            instance = new ClientHandler(pooledEngine);
        }
        return instance;
    }

    private Socket socket;
    private IO.Options options;
    //public static String url = "http://138.68.160.152:5000";
    public static String url = "http://localhost:8080";

    private Array<Message> receivedMessageArray;
    private Array<Message> transmitingMessageArray;

    /* Receiving Actions */
    public static final int UPDATE_MOVEMENT_REQ = 0;
    public static final int PLAYER_DISCONNECTED_REQ = 1;
    public static final int PLAYER_DIED_REQ = 2;
    public static final int UPDATE_PLAYER_POS_REQ = 3;

    public static final int REMOTE_PLAYER_CONNECTED_REQ = 5;
    public static final int CONNECTION_ESTABLISHED_REQ = 6;     /* Connection Established, create player */
    public static final int ASSIGN_ID_TO_PLAYER = 7;            /* Assign ID to a newly created player */
    public static final int UPDATE_PLAYER_TABLE = 9;            /* Get info of all players already connected */

    /* Transmitting Actions */
    public static final int SEND_PLAYER_TO_SERVER = 8;          /* Send Player data of a newly created player */
    public static final int UPLOAD_CURRENT_PLAYER_POS_REQ = 4;  /* Upload Current Player Position upon a request */

    private ClientHandler(PooledEngine pooledEngine) {
        createSocket();
        this.receivedMessageArray = new Array<>();
        this.transmitingMessageArray = new Array<>();
        pooledEngine.addSystem(new DataReceivingSystem(this));
        pooledEngine.addSystem(new DataTransmittingSystem(this));
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
                .setReconnectionAttempts(3)
                .setReconnectionDelay(10)
                .setTimeout(50)
                .setAuth(new HashMap<String, String>())
                .setTimeout(10000)
                .build();

        try {
            this.socket = IO.socket(url, options);
            Gdx.app.log("SocketIO", "Socket set");
            configSocketEvents();
        } catch (URISyntaxException e) {
            Gdx.app.log("SocketIO", "Wrong URL");
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
    }

    //===============Callbacks for event listener ==========================//

    private void updatePlayerTable(Object... args) throws JSONException {
        System.out.println("updatePlayerTable");
        JSONArray arrPlayers = (JSONArray)args[0];
        Message message = new Message(UPDATE_PLAYER_TABLE, false);

        /* Parse one by one player data, and put it into playerDataContainerArray */
        for(int index = 0; index < arrPlayers.length(); index++)
        {
            JSONObject jsonObjectCurPlayer = null;
            PlayerDataContainer playerDataContainer = new PlayerDataContainer();

            try
            {
                jsonObjectCurPlayer = (JSONObject)arrPlayers.get(index);
                System.out.println(jsonObjectCurPlayer);
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

    private void playerIdReceivedOnCon(Object... args)
    {
        System.out.println("playerIdReceivedOnCon has been called");
        PlayerDataContainer playerDataContainer = new PlayerDataContainer();
        playerDataContainer.setPlayerID((int)args[0]);
        Message message = new Message(ASSIGN_ID_TO_PLAYER, true);
        message.addPlayerDataContainer(playerDataContainer);
        receivedMessageArray.add(message);
    }

    private void getUpdatedPosition(Object... args)
    {
        System.out.println("getUpdatedPosition");
        Message message = new Message(UPLOAD_CURRENT_PLAYER_POS_REQ, true);
        message.addPlayerDataContainer(new PlayerDataContainer());
        transmitingMessageArray.add(message);
    }

    /*private void playerDisconnected(Object... args)
    {
        JSONObject data = (JSONObject) args[0];

        try {
            this.socketID = data.getString("id");
            Gdx.app.log("SocketIO", "Player with id" + this.socketID + " has been disconnected");
            //otherPlayers.remove(id);
        }catch(JSONException e){
            Gdx.app.log("SocketIO", "Error getting disconnected PlayerID");
        }
    }

    private void playerMoved(Object... args)
    {
        JSONArray objects = (JSONArray) args[0];
        Integer playerId = 0;
        String moveType  = "";
        playerId = objects.optInt(0);
        moveType = objects.optString(1);

        System.out.println("playerId " + playerId);
        System.out.println("moveType " + moveType);


        //System.out.println("Player " + playerId);
        //otherPlayers.get(playerId).b2body.setTransform(convertToFloat(pos_x), convertToFloat(pos_y),convertToFloat(0));
        //otherPlayers.get(playerId).b2body.setTransform(convertToFloat(pos_x), convertToFloat(pos_y),convertToFloat(0));

        if(moveType.equals("LEFT"))
        {
            //otherPlayers.get(playerId).turnLeft();
            //otherPlayers.get(playerId).direction = Direction.LEFT;
        }
        else if(moveType.equals("RIGHT"))
        {
            //otherPlayers.get(playerId).turnRight();
            //otherPlayers.get(playerId).direction = Direction.RIGHT;
        }
        else if(moveType.equals("JUMP"))
        {
            //otherPlayers.get(playerId).jump();
        }
        else
        {
            System.out.println("Wrong move type");
        }
    }

    private void playerFiredMagic(Object... args)
    {
        int clientID = (int)args[0];
        otherPlayers.get(clientID).fireMagicBall();
    }

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
