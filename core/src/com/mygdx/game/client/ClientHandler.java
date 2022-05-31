package com.mygdx.game.client;

import com.badlogic.gdx.Gdx;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ClientHandler {

    public Socket socket;
    public String socketID;
    public IO.Options options;
    public static String url = "http://138.68.160.152:5000";

    ClientHandler() {
        createSocket();
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

    public void configSocketEvents() {

    }

    //===============Callbacks for event listener ==========================//
    private void reconnectionFailed() {
        Gdx.app.log("SocketIo", "Server is offline");
    }

    private void printSocketID(Object... args) {
        JSONObject data = (JSONObject) args[0];
        try {

            this.socketID = data.getString("id");
            Gdx.app.log("SocketIO", "My ID: " + this.socketID);
        } catch (JSONException e) {
            Gdx.app.log("SocketIO", "Error getting ID");
        }
    }

    private void playerDisconnected(Object... args)
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



}
