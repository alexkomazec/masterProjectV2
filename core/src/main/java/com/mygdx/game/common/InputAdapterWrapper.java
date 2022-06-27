package com.mygdx.game.common;

import com.badlogic.gdx.InputAdapter;

public class InputAdapterWrapper extends InputAdapter {
    /* This wrapper is here just to add some command list
    *  This will be used in some Input manager to get current
    *  input data from the input processor
    * */
    protected boolean[] abInputCommandList;
    protected boolean inputProcesorSet = false;

    public boolean getInputCommand(int inputCommandID) {
        return abInputCommandList[inputCommandID];
    }

    public void setInputCommand(int inputCommandID, boolean value)
    {
        abInputCommandList[inputCommandID] = value;
    }

    public boolean isInputProcesorSet() {
        return inputProcesorSet;
    }

    public void setInputProcesorSet(boolean inputProcesorSet) {
        this.inputProcesorSet = inputProcesorSet;
    }
}
