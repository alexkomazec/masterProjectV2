package com.mygdx.game.config;

public class GameConfig {

    public static final float DEFAULT_PLAYER_WIDTH = 180f;
    public static final float DEFAULT_PLAYER_HEIGHT = 200f;

    // == Box2d Scalling ==

    //INFO: Creating a body with width X and height X, in box2D world it is represented as X meters x X meters body
    //      which is too heavy, so scalling is important part.
    //private static final float PPM = 32; // pixels per meter
    private static final float PPM = 200; // pixels per meter
    public static final float MULTIPLY_BY_PPM = PPM;
    public static final float DIVIDE_BY_PPM = 1/PPM;


    public static final int   NUMBER_OF_TILES = 10; // One tile is sized PPMxPPM
    public static final float WORLD_WIDTH = PPM*NUMBER_OF_TILES;
    public static final float WORLD_HEIGHT = PPM*NUMBER_OF_TILES;

    /* Physical dimensions of the screen */
    public static final float PHYSICAL_WIDTH = 640; //pixels
    public static final float PHYSICAL_HEIGHT = 640; //pixels

    //== Gameplay Size ==
    public static final  float GAME_WIDTH = 800f; //pixels
    public static final  float GAME_HEIGHT  = PHYSICAL_HEIGHT /2; //pixels


    /*--------------------------------------------------------------------------------------------*/


    // Region atlas names
    public static final String BACKGROUND = "background1";

    // == Predefined string ==
    public static final String GAME_SOUND = "SFX";
    public static final String GAME_MUSIC = "MUSIC";

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
    //public static final String LEVEL1 = "tiledmaps/dummy.tmx";
    public static final String LEVEL1 = "tiledmaps/Level1.tmx";
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

    //Box2D Collision Bits
    public static final short GROUND_BIT = 1;
    public static final short PLAYER_BIT = 2;
    public static final short MAGIC_BIT = 4;
    public static final short COLLECTIBLE_BIT = 8;
    public static final short ENEMY_BIT = 16;

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

    /* Input commands stuff*/

    //No of currently implemented input commands
    public static final int LIST_COMMANDS_MAX = 5;

    public static final int LEFT        = 0;
    public static final int RIGHT       = 1;
    public static final int UP          = 2;
    public static final int DOWN        = 3;
    public static final int SPACE       = 4;

    /* Types of game connectivity */
    public static final boolean LOCAL_CONNECTION = false;
    public static final boolean ONLINE_CONNECTION = true;

    /* Max Init Health*/
    public static final int MAX_PLAYER_LIVES = 3;
    public static final int MAX_BASIC_ENEMY_LIVES = 2;
    public static final int MAX_CLOUD = 4;
    public static final int ERROR = -1;
    public static final int MAX_SIZE = 2;
    public static final int INCREASE_HP = 0;
    public static final int DECREASE_HP = 1;


    /* Collectibles basic */
    public static final int DEFAULT_TYPE = -1;
    public static final int DOUBLE_JUMP = 0;
    public static final int DOUBLE_SHOOT = 1;
    public static final int STOMP = 2;
    public static final int COLLECTABLE_BASIC_MAX = 3;

    public static final float SPELL_WIDTH = 200f;
    public static final float SPELL_HEIGHT = 200f;

    public static final int EMPTY_ROOM = 0;
    public static final int HALF_ROOM = 1;
    public static final int FULL_ROOM = 2;
    public static final int NO_OF_ROOMS = 3;

    public static final String GAME_MODE_COOP = "COOP";
    public static final String GAME_MODE_PVP = "PVP";

    public static final float FRAME_DURATION = 0.1f; // Frame duration for one frame in seconds
    private GameConfig(){}
}
