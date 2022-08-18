package com.mygdx.game.common;

import com.mygdx.game.MyGdxGame;

public interface Publisher {

    //methods to register and unregister observers
    void registerObserver(Topics topic, Observer obj);
    void unregisterObserver(Topics topic, Observer obj);

    //method to notify observers of change
    void notifyObservers(Topics topic, Object... args);
}
