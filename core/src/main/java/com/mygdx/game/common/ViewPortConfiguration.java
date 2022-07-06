package com.mygdx.game.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.entitycomponentsystem.system.DataReceivingSystem;

public class ViewPortConfiguration {

    protected static final Logger logger = new Logger(ViewPortConfiguration.class.getSimpleName(), Logger.INFO);

    //Viewport measures => Visible area
    public static  float viewportWidth;
    public static  float viewportHeight;
    
    //Physical Measures
    public static float physicalWidth;
    public static float physicalHeight;
    
    //Aspect ratio represents division of virtualWidth and virtualHeight
    public static float aspectRatio;

    //Set
    public static void calculateViewport(int worldWidth, int worldHeight){

        float virtualWidth;
        float virtualHeight;

        //Make the viewport a percentage of the total display area
        virtualWidth = worldWidth;
        virtualHeight = worldHeight;

        //Current viewport dimensions
        viewportWidth = virtualWidth;
        viewportHeight = virtualHeight;

        //pixel dimensions of display
        physicalWidth = Gdx.graphics.getWidth();
        physicalHeight = Gdx.graphics.getHeight();

        //aspect ratio for current viewport
        aspectRatio = (virtualWidth / virtualHeight);

        //update viewport if there could be skewing
        if( physicalWidth / physicalHeight >= aspectRatio){
            //Letterbox left and right
            viewportWidth = viewportHeight * (physicalWidth/physicalHeight);
            viewportHeight = virtualHeight;
            logger.debug("Physical Measure ratio is bigger than aspect ratio");
        }else{
            //letterbox above and below
            viewportWidth = virtualWidth;
            viewportHeight = viewportWidth * (physicalHeight/physicalWidth);
            logger.debug("WorldRenderer: Physical Measure ratio is smaller than aspect ratio");
        }

        //Display information
        logger.debug(" Virtual measure: (" + virtualWidth + "," + virtualHeight + ")" );
        logger.debug(" Viewport measure: (" + viewportWidth + "," + viewportHeight + ")" );
        logger.debug(" Physical measure: (" + physicalWidth + "," + physicalHeight + ")" );
    }

    public static void setupPhysicalSize(){

        physicalWidth = Gdx.graphics.getWidth();
        physicalHeight = Gdx.graphics.getHeight();
    }

    public static float getPhysicalWidth(){
        return physicalWidth = Gdx.graphics.getWidth();
    }
    public static float getPhysicalHeight(){
        return physicalHeight = Gdx.graphics.getHeight();
    }

    public static float getViewportWidth() {
        return viewportWidth;
    }

    public static float getViewportHeight() {
        return viewportHeight;
    }
}
