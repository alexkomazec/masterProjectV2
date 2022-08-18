package com.mygdx.game.client.data;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.client.Room;
import com.mygdx.game.config.GameConfig;

public class GeneralInfoContainer {

    private String userName;
    private Array<Room> coopRooms;
    private Array<Room> pvpRooms;

    public GeneralInfoContainer() {
        this.coopRooms = new Array<>();
        this.pvpRooms = new Array<>();

        for (int i = 0; i < GameConfig.NO_OF_ROOMS; i++)
        {
            coopRooms.add(new Room());
            pvpRooms.add(new Room());
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Array<Room> getCoopRooms() {
        return coopRooms;
    }

    public void setCoopRooms(Array<Room> coopRooms) {
        this.coopRooms = coopRooms;
    }

    public Array<Room> getPvpRooms() {
        return pvpRooms;
    }

    public void setPvpRooms(Array<Room> pvpRooms) {
        this.pvpRooms = pvpRooms;
    }

}
