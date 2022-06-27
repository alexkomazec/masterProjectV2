package com.mygdx.game.client;

import io.socket.client.Socket;

public class EmitterListenerList {

    private Socket socket;

    EmitterListenerList(Socket socket)
    {
        this.socket = socket;
    }
}
