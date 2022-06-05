package com.mygdx.game.client;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.brashmonkey.spriter.Player;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.mygdx.game.client.data.PlayerDataContainer;
import com.mygdx.game.entitycomponentsystem.system.DataReceivingSystem;
import com.mygdx.game.entitycomponentsystem.system.DataTransmittingSystem;
import com.mygdx.game.gameworld.WorldSingleton;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;

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
    public static String url = "http://138.68.160.152:5000";

    private Array<ReceivedMessage> receivedMessage;

    private static final int UPDATE_MOVEMENT_REQ = 0;
    private static final int PLAYER_DISCONNECTED_REQ = 1;
    private static final int PLAYER_DIED_REQ = 2;
    private static final int UPDATE_PLAYER_POS_REQ = 3;
    private static final int UPLOAD_CURRENT_PLAYER_POS_REQ = 4;
    private static final int REMOTE_PLAYER_CONNECTED_REQ = 5;
    private static final int CONNECTION_ESTABLISHED_REQ = 6; /* Connection Established, create player */

    private ClientHandler(PooledEngine pooledEngine) {
        createSocket();
        receivedMessage = new Array<>();
        pooledEngine.addSystem(new DataReceivingSystem(this));
        pooledEngine.addSystem(new DataTransmittingSystem(this));
    }

    public boolean isNewDataReceived()
    {
        return receivedMessage.isEmpty();
    }

    public void sendData(PlayerDataContainer playerDataContainer)
    {
        this.socket.emit("", playerDataContainer);
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
        } catch (URISyntaxException e) {
            Gdx.app.log("SocketIO", "Wrong URL");
            e.printStackTrace();
        }
    }

    public Array<ReceivedMessage> getReceivedMessage() {
        return receivedMessage;
    }

    public void configSocketEvents() {

    }

    //===============Callbacks for event listener ==========================//

    private void reconnectionFailed() {
        Gdx.app.log("SocketIo", "Server is offline");
    }

    private void playerIdReceivedOnCon(Object... args)
    {
        PlayerDataContainer playerDataContainer = (PlayerDataContainer)args[0];
        int actionType = CONNECTION_ESTABLISHED_REQ;
        ReceivedMessage receivedMessage = new ReceivedMessage(playerDataContainer, actionType);
        this.receivedMessage.add(receivedMessage);


        //System.out.println("assignID2Player:" + player.clientID);
        //socket.emit("addPlayer", player.bdef.position.x, player.bdef.position.y, player.clientID);
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

    private void getUpdatedPosition(Object... args)
    {
        //System.out.println("updated position:" + "x: " + player.b2body.getPosition().x + "y: " + player.b2body.getPosition().y);
        //socket.emit("refreshPlayersPosition", player.b2body.getPosition().x, player.b2body.getPosition().y, player.clientID);
    }

    private void updatePlayerTable(Object... args)
    {
        System.out.println("Refresh Player Table");
        JSONArray arrPlayers = (JSONArray)args[0];

        for(int index = 0; index < arrPlayers.length(); index++)
        {
            JSONObject jsonObjectCurPlayer = null;

            try
            {
                jsonObjectCurPlayer = (JSONObject)arrPlayers.get(index);
                System.out.println(jsonObjectCurPlayer);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            try {

                if(jsonObjectCurPlayer != null)
                {
                    *//* Check is it itself*//*
                    if(player.clientID != jsonObjectCurPlayer.getInt("playerID"))
                    {
                        int anotherPlayerID = jsonObjectCurPlayer.getInt("playerID");
                        double x_pos		= jsonObjectCurPlayer.getDouble("x_pos");
                        double y_pos		= jsonObjectCurPlayer.getDouble("y_pos");
                        Player tempPlayer = new Player();
                        creator.createEntity(tempPlayer, (float)x_pos, (float)y_pos);

                        otherPlayers.put(anotherPlayerID, tempPlayer);
                    }
                    else
                    {
                        *//* Do nothing, found itself*//*
                    }
                }
                else
                {
                    System.out.println("jsonObjectCurPlayer = null");
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


        *//*Number of players*//*
        System.out.println("There are " + otherPlayers.size() + " players");
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
    }

    private void connected(Object... args)
    {
        Gdx.app.log("SocketIO", "Connected");

        game.player = new Player();
        game.creator.createEntity(game.player,-1.0f,-1.0f);
        readyToChangeScreen = true;
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
}
