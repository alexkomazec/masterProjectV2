package com.mygdx.game.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ViewPortConfiguration {

    private static final String CLASS_NAME = Viewport.class.getSimpleName();

    //Viewport measures => Visible area
    public static  float viewportWidth;
    public static  float viewportHeight;
    
    //Virtul Measures => Measure
    public static float virtualWidth;
    public static float virtualHeight;
    
    //Physical Measures
    public static float physicalWidth;
    public static float physicalHeight;
    
    //Aspect ratio represents division of virtualWidth and virtualHeight
    public static float aspectRatio;

    //Set
    public static void calculateViewport(int worldWidth, int worldHeight){

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
            Gdx.app.debug(CLASS_NAME, "WorldRenderer: Physical Measure ratio is bigger than aspect ratio" );
        }else{
            //letterbox above and below
            viewportWidth = virtualWidth;
            viewportHeight = viewportWidth * (physicalHeight/physicalWidth);
            Gdx.app.debug(CLASS_NAME, "WorldRenderer: Physical Measure ratio is smaller than aspect ratio" );
        }

        //Display information
        Gdx.app.debug(CLASS_NAME, " Virtual measure: (" + virtualWidth + "," + virtualHeight + ")" );
        Gdx.app.debug(CLASS_NAME, " Viewport measure: (" + viewportWidth + "," + viewportHeight + ")" );
        Gdx.app.debug(CLASS_NAME, " Physical measure: (" + physicalWidth + "," + physicalHeight + ")" );
    }

    /*public static float[] checkBoundariesCollision(MainCharacter mainCharacter){

        float x_position = mainCharacter.getB2Body_positionX();
        float y_position = mainCharacter.getB2Body_positionY();

        float[] camera_position_offset = new float[2];

        //camera_position[0] => x
        //camera_position[1] => y
        camera_position_offset[0] = 0;
        camera_position_offset[1] = 0;

        Map map = MapManager.getCurrentMap();

        float rightVisibleArea  =   x_position + viewportWidth/2;
        float leftVisibleArea   =   x_position - viewportWidth/2;
        float aboveVisibleArea  =   y_position + viewportHeight/2;
        float belowVisibleArea  =   y_position - viewportHeight/2;
        final float mapWidth    =   (float) ((int)(map.getTiledMap().getProperties().get("width")));
        final float mapHeight   =   (float) ((int)(map.getTiledMap().getProperties().get("height")));

        float rightCameraOffset     =   mapWidth - rightVisibleArea;
        float leftCameraOffset      =   0 - leftVisibleArea;
        float aboveCameraOffset     =   mapHeight - aboveVisibleArea;
        float belowCameraOffset     =   0 - belowVisibleArea;

        //Check if the camera area is collided to the right map bound
        if(rightCameraOffset <= 0) {
            camera_position_offset[0] = rightCameraOffset;
            Gdx.app.error(CLASS_NAME, "rightCameraOffset" );
        }

        if(leftCameraOffset>=0){
            camera_position_offset[0] = leftCameraOffset;
            Gdx.app.error(CLASS_NAME, "leftCameraOffset" );
        }

        if(aboveCameraOffset<=0){
            camera_position_offset[1] = aboveCameraOffset;
            Gdx.app.error(CLASS_NAME, "aboveCameraOffset" );
        }

        if(belowCameraOffset>=0){
            camera_position_offset[1] = belowCameraOffset;
            Gdx.app.error(CLASS_NAME, "belowCameraOffset" );
        }

        return camera_position_offset;
    }
    */

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
}
