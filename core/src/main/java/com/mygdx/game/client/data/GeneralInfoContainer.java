package com.mygdx.game.client.data;

import com.mygdx.game.config.GameConfig;

public class GeneralInfoContainer {

    private String userName;
    private int[] coopRooms = new int[GameConfig.NO_OF_ROOMS];
    private int[] pvpRooms = new int[GameConfig.NO_OF_ROOMS];
    private boolean isRoomStatusReceived = false;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int[] getCoopRooms() {
        return coopRooms;
    }

    public void setCoopRooms(int[] coopRooms) {
        this.coopRooms = coopRooms;
    }

    public int[] getPvpRooms() {
        return pvpRooms;
    }

    public void setPvpRooms(int[] pvpRooms) {
        this.pvpRooms = pvpRooms;
    }

    public boolean isRoomStatusReceived() {
        return isRoomStatusReceived;
    }

    public void setRoomStatusReceived(boolean roomStatusReceived) {
        isRoomStatusReceived = roomStatusReceived;
    }
}
