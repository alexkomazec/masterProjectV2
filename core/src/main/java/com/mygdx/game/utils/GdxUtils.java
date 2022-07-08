package com.mygdx.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.entitycomponentsystem.components.ControlledInputComponent;

public class GdxUtils {

    public static void clearScreen() {
        clearScreen(Color.BLACK);
    }

    private GdxUtils() {}

    public static float vectorToAngle (Vector2 vector) {
        return (float)Math.atan2(-vector.x, vector.y);
    }

    public static Vector2 angleToVector (Vector2 outVector, float angle) {
        outVector.x = -(float)Math.sin(angle);
        outVector.y = (float)Math.cos(angle);
        return outVector;
    }

    public static void clearScreen(Color color) {
        Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
    public static Vector2 aimTo(Vector2 shooter, Vector2 target){
        Vector2 aim = new Vector2();
        float velx = target.x - shooter.x; // get distance from shooter to target on x plain
        float vely = target.y - shooter.y; // get distance from shooter to target on y plain
        float length = (float) Math.sqrt(velx * velx + vely * vely); // get distance to target direct
        if (length != 0) {
            aim.x = velx / length;  // get required x velocity to aim at target
            aim.y = vely / length;  // get required y velocity to aim at target
        }
        return aim;
    }

    public static boolean isInputCommandTrue(int inputCommandID, ControlledInputComponent cntrlInCom)
    {
        return cntrlInCom.abInputCommandList[inputCommandID];
    }
}
