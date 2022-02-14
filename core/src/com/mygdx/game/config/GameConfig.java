package com.mygdx.game.config;

public class GameConfig {

    /* Size of one tile in the game. Tiles are squared so the dimension of one tile is
       WORLD_BOX_SIZE x WORLD_BOX_SIZE pixels
    */
    public static final float WORLD_BOX_SIZE = 16f;

    // == Box2d Scalling ==

    //INFO: Creating a body with width 16 and height 16, in box2D world it is represented as 16 meters x 16 meters body
    //      which is too heavy, so scalling is important part.
    public static final float PPM = 16; // pixels per meter

    public static final float WIDTH = 800f; //pixels
    public static final float HEIGHT  = 416f; //pixels

    /* Physical dimensions of the screen */
    public static final float PHYSICAL_WIDTH = 640; //pixels
    public static final float PHYSICAL_HEIGHT = 480; //pixels

    //== Gameplay Size ==
    public static final  float GAME_WIDTH = 800f; //pixels
    public static final  float GAME_HEIGHT  = PHYSICAL_HEIGHT /2; //pixels

    // == World Size ==
    public static final float WORLD_WIDTH = PHYSICAL_WIDTH /WORLD_BOX_SIZE; // world units
    public static final float WORLD_HEIGHT = PHYSICAL_HEIGHT /WORLD_BOX_SIZE; // world units

    /*--------------------------------------------------------------------------------------------*/


    // Region atlas names
    public static final String BACKGROUND = "background";

    // == Predefined string ==
    public static final String GAME_SOUND = "GAME SOUND";
    public static final String GAME_MUSIC = "GAME MUSIC";

    // == Enemy Stats ==
    public static final int ENEMY_DPS = 100;
    public static final int ENEMY_HP = 100;
    public static final int ENEMY_SPEED = 100;

    // == Hero Stats ==
    public static final int HERO_DPS = 100;
    public static final int HERO_HP = 100;
    public static final int HERO_ARMOR = 100;
    public static final int HERO_MANA = 100;
    public static final int HERO_ATTACK_SPEED = 100;
    public static final int HERO_MOVEMENT_SPEED = 100;

    public static final int LEVEL_1_EXP_CAPACITY        = 100;
    public static final int LEVEL_2_EXP_CAPACITY        = (int)(1.1*LEVEL_1_EXP_CAPACITY);
    public static final int LEVEL_3_EXP_CAPACITY        = (int)(1.1*LEVEL_2_EXP_CAPACITY);
    public static final int LEVEL_4_EXP_CAPACITY        = (int)(1.1*LEVEL_3_EXP_CAPACITY);
    public static final int LEVEL_5_EXP_CAPACITY        = (int)(1.1*LEVEL_4_EXP_CAPACITY);
    public static final int LEVEL_6_EXP_CAPACITY        = (int)(1.1*LEVEL_5_EXP_CAPACITY);
    public static final int LEVEL_7_EXP_CAPACITY        = (int)(1.1*LEVEL_6_EXP_CAPACITY);
    public static final int LEVEL_8_EXP_CAPACITY        = (int)(1.1*LEVEL_7_EXP_CAPACITY);
    public static final int LEVEL_9_EXP_CAPACITY        = (int)(1.1*LEVEL_8_EXP_CAPACITY);
    public static final int LEVEL_10_EXP_CAPACITY       = (int)(1.1*LEVEL_9_EXP_CAPACITY);

    // == Environmental difficulty
    public static final int SURVIVAL_DIFFICULTY = 100;

    // == Difficulty presets ==

    // == Difficulty multipliers
    private static final float EASY_MULTIPLIER = 0.7f;
    private static final float NORMAL_MULTIPLIER = 1f;
    private static final float HARD_MULTIPLIER = 1.5f;

    // == Easy game
    public static final int ENEMY_DPS_EASY = (int)(ENEMY_DPS*EASY_MULTIPLIER); // 70% of normal
    public static final int ENEMY_HP_EASY = (int)(ENEMY_HP*EASY_MULTIPLIER); // 70% of normal
    public static final int ENEMY_SPEED_EASY = (int)(ENEMY_SPEED*EASY_MULTIPLIER); // 70% of normal
    public static final int SURVIVAL_DIFFICULTY_EASY= (int)(SURVIVAL_DIFFICULTY*EASY_MULTIPLIER); // 70% of normal

    // == Normal game
    public static final int ENEMY_DPS_NORMAL = (int)(ENEMY_DPS*NORMAL_MULTIPLIER); // 100% of normal
    public static final int ENEMY_HP_NORMAL = (int)(ENEMY_HP*NORMAL_MULTIPLIER); // 100% of normal
    public static final int ENEMY_SPEED_NORMAL = (int)(ENEMY_SPEED*NORMAL_MULTIPLIER); // 100% of normal
    public static final int SURVIVAL_DIFFICULTY_NORMAL= (int)(SURVIVAL_DIFFICULTY*NORMAL_MULTIPLIER); // 100% of normal

    // == Hard game
    public static final int ENEMY_DPS_HARD = (int)(ENEMY_DPS*HARD_MULTIPLIER); // 150% of normal
    public static final int ENEMY_HP_HARD = (int)(ENEMY_HP*HARD_MULTIPLIER); // 150% of normal
    public static final int ENEMY_SPEED_HARD = (int)(ENEMY_SPEED*HARD_MULTIPLIER); // 150% of normal
    public static final int SURVIVAL_DIFFICULTY_HARD= (int)(SURVIVAL_DIFFICULTY*HARD_MULTIPLIER); // 150% of normal

    // == Names of Level Maps
    public static final String LEVEL1 = "maps/valleyOfTheHungry_Level1/valleyOfTheHungry.tmx";
    public static final String LEVEL2 = "SnowWave";
    public static final String LEVEL3 = "EternalSummer";
    public static final String LEVEL4 = "TheGreatSea";
    public static final String LEVEL5 = "Kepler51b";

    // == Box2D types of Bodies
    public static final int iSTATIC = 0;
    public static final int iDYNAMIC = 1;
    public static final int iKINEMATIC = 2;
    public static final int iNOT_BODY = 3;

    //Debugging phrases
    public static final String DEVELOPMENT_ERROR = "DEVELOPMENT ERROR:";

    //Parsing .tmx files
    // == Box2D types of Bodies in String
    public static final String strSTATIC = "static";
    public static final String strDYNAMIC = "dynamic";
    public static final String strKINEMATIC = "kinematic";

    public static final String PLAYER_NAME = "player";
    public static final String EXIT_DOOR_NAME = "exit_door";

    public static final short GROUND_BIT = 1;
    public static final short HERO_BIT = 2;
    public static final short EXIT_DOOR_BIT = 4;
    public static final short BONUS_DOOR_BIT = 8;

    public enum HeroType{WARRIOR_SELECTED,MAGE_SELECTED,HUNTER_SELECTED};

    //Debbuging staff
    public static final boolean ENABLE_IT = true;
    public static final boolean DISABLE_IT = false;

    //Camera debugging
    //If cameraDebugging_flag is enabled, the developer can move camera around in order to debug
    // if something is rendered out of the scope
    public static final boolean cameraDebugging_flag = DISABLE_IT;

    //Rendering a map
    //If mapRenderrer_flag is enabled, the .tmx map is rendered.
    // mapRenderrer_flag SHOULD be enabled for users, because they users should enjoy the graphics
    // maprEnderrer_flag COULD be disabled in development phase, when rendering graphics is not important
    public static final boolean mapRenderrer_flag = ENABLE_IT;

    //Rendering box2D bodies
    //If box2dBodyRenderrer_flag is enabled, the developer can see bounds around the bodies
    //Defualt color is green. It can help developers to debug some problems or develop the game
    //without having graphics
    public static final boolean box2dBodyRenderrer_flag = ENABLE_IT;


    //Hardcoded hero frames per movement
    //Developer should count the number of frames per image that represents a movement
    //Pros of this way: Developer can provide a tape of frames (A few frames grouped into one picture)
    //Cons of this way: Developer could not code generic implementation that can automically
    //                  decide number of frames per tape

    //According to warriorMoves folder. Note that all heroes should have the same number of frames
    public static final int NUM_OF_FRAMES_ATTACK = 8;
    public static final int NUM_OF_FRAMES_SHOOT = 10;
    public static final int NUM_OF_FRAMES_CAST = 6;
    public static final int NUM_OF_FRAMES_DEAD = 5;
    public static final int NUM_OF_FRAMES_FALL = 4;
    public static final int NUM_OF_FRAMES_JUMP = 4;
    public static final int NUM_OF_FRAMES_HURT = 3;
    public static final int NUM_OF_FRAMES_RUN = 9;
    public static final int NUM_OF_FRAMES_STAND = 1;

    private GameConfig(){}
}
