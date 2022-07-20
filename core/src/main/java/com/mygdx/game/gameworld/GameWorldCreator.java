package com.mygdx.game.gameworld;

import static com.mygdx.game.config.GameConfig.MULTIPLY_BY_PPM;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.ai.SteeringPresets;
import com.mygdx.game.common.Direction;
import com.mygdx.game.common.assets.AssetDescriptors;
import com.mygdx.game.common.assets.AssetManagmentHandler;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.entitycomponentsystem.components.AnimationComponent;
import com.mygdx.game.entitycomponentsystem.components.B2dBodyComponent;
import com.mygdx.game.entitycomponentsystem.components.BrickComponent;
import com.mygdx.game.entitycomponentsystem.components.BulletComponent;
import com.mygdx.game.entitycomponentsystem.components.CollectibleBasicComponent;
import com.mygdx.game.entitycomponentsystem.components.CollisionComponent;
import com.mygdx.game.entitycomponentsystem.components.ControllableComponent;
import com.mygdx.game.entitycomponentsystem.components.ControlledInputComponent;
import com.mygdx.game.entitycomponentsystem.components.CoolDownComponent;
import com.mygdx.game.entitycomponentsystem.components.DirectionComponent;
import com.mygdx.game.entitycomponentsystem.components.EnemyComponent;
import com.mygdx.game.entitycomponentsystem.components.LocalInputComponent;
import com.mygdx.game.entitycomponentsystem.components.PlayerComponent;
import com.mygdx.game.entitycomponentsystem.components.CharacterStatsComponent;
import com.mygdx.game.entitycomponentsystem.components.PotionComponent;
import com.mygdx.game.entitycomponentsystem.components.RemoteInputComponent;
import com.mygdx.game.entitycomponentsystem.components.StateComponent;
import com.mygdx.game.entitycomponentsystem.components.SteeringComponent;
import com.mygdx.game.entitycomponentsystem.components.TextureComponent;
import com.mygdx.game.entitycomponentsystem.components.TransformComponent;
import com.mygdx.game.entitycomponentsystem.components.TypeComponent;
import com.mygdx.game.entitycomponentsystem.system.HealthManagerSystem;
import com.mygdx.game.entitycomponentsystem.system.InputManagerSystem;


public class GameWorldCreator {

    protected static final Logger logger = new Logger(GameWorldCreator.class.getSimpleName(), Logger.DEBUG);
    private BodyCreator bodyCreator;
    private static int currentAvailablePlayerID = 0;
    public static GameWorldCreator instance;
    public boolean connectionType;

    private TextureAtlas playerAtlas;
    private TextureAtlas magicSpellAtlas;
    private TextureAtlas magicSpellLeftAtlas;

    private Array<TextureRegion> magicLeftFrames;
    private Array<TextureRegion> magicRightFrames;
    private Array<TextureRegion> enemyAnimationFrames;
    private Array<TextureRegion> heroAnimaitonFrames;
    private TextureAtlas uiCharacterStatsAtlas;
    private Skin uiSkin;

    GameWorld gameWorld;
    PooledEngine pooledEngine;
    HealthManagerSystem healthManagerSystem;
    OrthographicCamera orthographicCamera;
    AssetManagmentHandler assetManagmentHandler;

    Stage characterHUD;


    public static GameWorldCreator getInstance() {
        if (instance == null) {
            instance = new GameWorldCreator();
        }

        return instance;
    }

    public GameWorldCreator()
    {
        this.bodyCreator = BodyCreator.getInstance();
    }

    public void setAssetManagementHandler(AssetManagmentHandler assetManagmentHandler) {
        this.assetManagmentHandler = assetManagmentHandler;
    }

    public void getRequiredResources()
    {
        if(this.assetManagmentHandler != null)
        {
            this.playerAtlas = this.assetManagmentHandler.getResources(AssetDescriptors.PLAYER_ANIMATION);
            this.magicSpellAtlas = this.assetManagmentHandler.getResources(AssetDescriptors.FIRE_MAGIC_ANIMATION);
            this.magicSpellLeftAtlas = this.assetManagmentHandler.getResources(AssetDescriptors.FIRE_MAGIC_ANIMATION_LEFT);

            this.magicLeftFrames = new Array<>();
            this.magicLeftFrames.add(magicSpellLeftAtlas.findRegion("FB001"));
            this.magicLeftFrames.add(magicSpellLeftAtlas.findRegion("FB002"));
            this.magicLeftFrames.add(magicSpellLeftAtlas.findRegion("FB003"));
            this.magicLeftFrames.add(magicSpellLeftAtlas.findRegion("FB004"));
            this.magicLeftFrames.add(magicSpellLeftAtlas.findRegion("FB005"));

            this.magicRightFrames = new Array<>();
            this.magicRightFrames.add(magicSpellAtlas.findRegion("FB001"));
            this.magicRightFrames.add(magicSpellAtlas.findRegion("FB002"));
            this.magicRightFrames.add(magicSpellAtlas.findRegion("FB003"));
            this.magicRightFrames.add(magicSpellAtlas.findRegion("FB004"));
            this.magicRightFrames.add(magicSpellAtlas.findRegion("FB005"));

            TextureAtlas enemyAnimationAtlas = this.assetManagmentHandler.getResources(AssetDescriptors.ENEMY_ANIMATION);
            this.enemyAnimationFrames = new Array<>();
            this.enemyAnimationFrames.add(enemyAnimationAtlas.findRegion("skeleWalk1"));
            this.enemyAnimationFrames.add(enemyAnimationAtlas.findRegion("skeleWalk2"));
            this.enemyAnimationFrames.add(enemyAnimationAtlas.findRegion("skeleWalk3"));

            TextureAtlas heroAnimationAtlas = this.assetManagmentHandler.getResources(AssetDescriptors.WIZARD_ANIMATION);
            this.heroAnimaitonFrames = new Array<>();
            this.heroAnimaitonFrames.add(heroAnimationAtlas.findRegion("wizardRunning1"));
            this.heroAnimaitonFrames.add(heroAnimationAtlas.findRegion("wizardRunning2"));
            this.heroAnimaitonFrames.add(heroAnimationAtlas.findRegion("wizardRunning3"));
            this.heroAnimaitonFrames.add(heroAnimationAtlas.findRegion("wizardRunning4"));
            this.heroAnimaitonFrames.add(heroAnimationAtlas.findRegion("wizardRunning5"));
            this.heroAnimaitonFrames.add(heroAnimationAtlas.findRegion("wizardRunning6"));
        }
    }

    public void createClouds()
    {
        TiledMap map = this.gameWorld.getTiledMap();
        for(EllipseMapObject object : map.getLayers().
                get(GameWorld.TM_LAYER_CLOUD_ENEMIES).
                getObjects().
                getByType(EllipseMapObject.class))
        {
            createCloud(object);
        };
    }

    private void createCloud(MapObject object)
    {
        Entity entity = this.pooledEngine.createEntity();
        B2dBodyComponent b2dBodyComponent = this.pooledEngine.createComponent(B2dBodyComponent.class);
        TransformComponent transformComponent = this.pooledEngine.createComponent(TransformComponent.class);
        CollisionComponent collisionComponent = this.pooledEngine.createComponent(CollisionComponent.class);
        TypeComponent typeComponent = this.pooledEngine.createComponent(TypeComponent.class);
        StateComponent stateComponent = this.pooledEngine.createComponent(StateComponent.class);
        EnemyComponent enemyComponent = this.pooledEngine.createComponent(EnemyComponent.class);
        SteeringComponent steeringComponent = this.pooledEngine.createComponent(SteeringComponent.class);
        Rectangle rectangle = getRectangle(object);

        b2dBodyComponent.body = bodyCreator.makeCirclePolyBody(rectangle,
                BodyCreator.STONE,
                BodyDef.BodyType.DynamicBody,
                this.gameWorld.getWorldSingleton().getWorld(),
                true);

        b2dBodyComponent.body.setGravityScale(0f);  // no gravity for our floating enemy
        b2dBodyComponent.body.setLinearDamping(0.3f); // setting linear dampening so the enemy slows down in our box2d world(or it can float on forever)

        transformComponent.position.set(rectangle.getX(), rectangle.getY());
        typeComponent.type = TypeComponent.ENEMY;
        stateComponent.set(StateComponent.STATE_NORMAL);
        b2dBodyComponent.body.setUserData(entity);
        // bodyFactory.makeAllFixturesSensors(b2dbody.body); // seeker  should fly about not fall
        steeringComponent.body = b2dBodyComponent.body;
        enemyComponent.enemyType = EnemyComponent.Type.CLOUD;

        // set out steering behaviour
        steeringComponent.steeringBehavior  = SteeringPresets.getWander(steeringComponent);
        //scom.setIndependentFacing(true); // stop clouds rotating
        steeringComponent.currentMode = SteeringComponent.SteeringState.WANDER;

        this.healthManagerSystem.initializeHealth(entity);
        entity.add(b2dBodyComponent);
        entity.add(transformComponent);
        entity.add(collisionComponent);
        entity.add(typeComponent);
        entity.add(enemyComponent);
        entity.add(stateComponent);
        entity.add(steeringComponent);

        this.pooledEngine.addEntity(entity);
    }

    public void createPlatforms()
    {
        TiledMap map = this.gameWorld.getTiledMap();
        //Create Platform
        for(TextureMapObject object : map.getLayers().
                get(GameWorld.TM_LAYER_PLATFORM).
                getObjects().
                getByType(TextureMapObject.class))
        {
            createPlatform(object);
        };
    }

    private void createPlatform (TextureMapObject object)
    {
        Entity entity = this.pooledEngine.createEntity();
        B2dBodyComponent    b2dBodyComponent = this.pooledEngine.createComponent(B2dBodyComponent.class);
        TypeComponent       typeComponent = this.pooledEngine.createComponent(TypeComponent.class);
        TransformComponent  transformComponent = this.pooledEngine.createComponent(TransformComponent.class);
        BrickComponent      brickComponent = this.pooledEngine.createComponent(BrickComponent.class);
        Rectangle rectangle = getRectangle(object);

        //Create a box2d body for each tiled object in the layer
        b2dBodyComponent.body = bodyCreator.makeBoxPolyBody(rectangle,
                BodyCreator.STONE,
                BodyDef.BodyType.StaticBody,
                this.gameWorld.getWorldSingleton().getWorld(),
                true);

        /* Set an object that represents an unique ID for the body */
        b2dBodyComponent.body.setUserData(entity);
        entity.add(b2dBodyComponent);

        /* Set type component*/
        typeComponent.type = TypeComponent.SCENERY;
        entity.add(typeComponent);

        /* Set transformation component*/
        transformComponent.position.set(rectangle.getX(), rectangle.getY());
        entity.add(transformComponent);

        /* Add the index to the brick component*/
        brickComponent.textureMapObject = object;
        entity.add(brickComponent);

        this.pooledEngine.addEntity(entity);
    }

    public Entity createPlayer(boolean isLocalPlayer, Vector2 position)
    {
        TiledMap map = this.gameWorld.getTiledMap();
        Entity entity = null;
        //Create Players
        for(EllipseMapObject object : map.getLayers().
                get(GameWorld.TM_LAYER_PLAYERS_SPAWN_SPOTS).
                getObjects().
                getByType(EllipseMapObject.class))
        {
            entity = createPlayer(object, position, isLocalPlayer);
        }
        return entity;
    }

    private Entity createPlayer(MapObject object, Vector2 position, boolean isLocalPlayer)
    {
        Entity entity = this.pooledEngine.createEntity();
        B2dBodyComponent b2dBodyComponent = this.pooledEngine.createComponent(B2dBodyComponent.class);
        TransformComponent transformComponent = this.pooledEngine.createComponent(TransformComponent.class);
        PlayerComponent playerComponent = this.pooledEngine.createComponent(PlayerComponent.class);
        DirectionComponent directionComponent = this.pooledEngine.createComponent(DirectionComponent.class);
        ControlledInputComponent cntrlInComp = this.pooledEngine.createComponent(ControlledInputComponent.class);
        CollisionComponent collisionComponent = this.pooledEngine.createComponent(CollisionComponent.class);
        TypeComponent typeComponent = this.pooledEngine.createComponent(TypeComponent.class);
        StateComponent stateComponent = this.pooledEngine.createComponent(StateComponent.class);
        SteeringComponent steeringComponent = this.pooledEngine.createComponent(SteeringComponent.class);
        AnimationComponent animationComponent = this.pooledEngine.createComponent(AnimationComponent.class);
        TextureComponent texture = this.pooledEngine.createComponent(TextureComponent.class);
        CharacterStatsComponent characterStatsComponent = this.pooledEngine.createComponent(CharacterStatsComponent.class);
        Component inputTypeForPlayerComponent;
        Rectangle rectangle;
        //ControlledInputRemoteComponent controlledInputRemoteComponent = this.pooledEngine.createComponent(ControlledInputRemoteComponent.class);

        rectangle = (isLocalPlayer) ? getRectangle(object) : getRectangle(position);
        playerComponent.playerID = currentAvailablePlayerID;
        currentAvailablePlayerID++;
        entity.add(playerComponent);

        b2dBodyComponent.body = bodyCreator.makeCirclePolyBody(rectangle,
                BodyCreator.STONE,
                BodyDef.BodyType.DynamicBody,
                this.gameWorld.getWorldSingleton().getWorld(),
                true);
        /* Set an object that represents an unique ID for the body */
        b2dBodyComponent.body.setUserData(entity);
        // Do not allow unit to sleep or it wil sleep through events if stationary too long
        b2dBodyComponent.body.setSleepingAllowed(false);
        entity.add(b2dBodyComponent);

        transformComponent.position.set(b2dBodyComponent.body.getPosition().x
                                        * MULTIPLY_BY_PPM,
                                        b2dBodyComponent.body.getPosition().y
                                        * MULTIPLY_BY_PPM);

        entity.add(transformComponent);

        texture.region = this.heroAnimaitonFrames.get(0);
        entity.add(texture);

        Animation anim = new Animation(0.1f, this.heroAnimaitonFrames);
        anim.setPlayMode(Animation.PlayMode.LOOP);
        animationComponent.animations.put(StateComponent.STATE_NORMAL, anim);
        animationComponent.animations.put(StateComponent.STATE_MOVING, anim);
        animationComponent.animations.put(StateComponent.STATE_JUMPING, anim);
        animationComponent.animations.put(StateComponent.STATE_FALLING, anim);
        animationComponent.animations.put(StateComponent.STATE_HIT, anim);
        entity.add(animationComponent);

        entity.add(directionComponent);

        typeComponent.type = TypeComponent.PLAYER;
        entity.add(typeComponent);

        stateComponent.set(StateComponent.STATE_NORMAL);
        entity.add(stateComponent);

        steeringComponent.body = b2dBodyComponent.body;
        entity.add(steeringComponent);

        characterStatsComponent.init(this.uiCharacterStatsAtlas, this.uiSkin
                ,rectangle.getX()
                ,rectangle.getY()
                ,this.characterHUD
                );

        entity.add(characterStatsComponent);
        //entity.add(controlledInputRemoteComponent);
        entity.add(collisionComponent);
        entity.add(cntrlInComp);
        entity.add(new ControllableComponent());
        entity.add(new CoolDownComponent());
        this.healthManagerSystem.initializeHealth(entity);

        InputManagerSystem inputManagerSystem = this.pooledEngine.getSystem(InputManagerSystem.class);
        if(isLocalPlayer)
        {
            inputTypeForPlayerComponent = this.pooledEngine.createComponent(LocalInputComponent.class);
            playerComponent.cam = this.orthographicCamera;

            if(inputManagerSystem != null)
                inputManagerSystem.assignPlayerToInputProcessor(playerComponent.playerID, isLocalPlayer);
        }
        else
        {
            inputTypeForPlayerComponent = this.pooledEngine.createComponent(RemoteInputComponent.class);
        }
        entity.add(inputTypeForPlayerComponent);

        this.gameWorld.setPlayer(entity);
        this.pooledEngine.addEntity(entity);

        return entity;
    }

    public void createEnemies()
    {
        TiledMap map = this.gameWorld.getTiledMap();
        //Create Enemies
        for(EllipseMapObject object : map.getLayers().
                get(GameWorld.TM_LAYER_BASIC_ENEMIES).
                getObjects().
                getByType(EllipseMapObject.class))
        {
            createEnemy(object);
        }
    }

    private Entity createEnemy(MapObject object){
        Entity entity = this.pooledEngine.createEntity();
        B2dBodyComponent b2dBodyComponent = this.pooledEngine.createComponent(B2dBodyComponent.class);
        TransformComponent transformComponent = this.pooledEngine.createComponent(TransformComponent.class);
        EnemyComponent enemyComponent = this.pooledEngine.createComponent(EnemyComponent.class);
        TypeComponent typeComponent = this.pooledEngine.createComponent(TypeComponent.class);
        CollisionComponent colComp = this.pooledEngine.createComponent(CollisionComponent.class);
        Rectangle rectangle = getRectangle(object);
        AnimationComponent animationComponent = this.pooledEngine.createComponent(AnimationComponent.class);
        TextureComponent textureComponent = this.pooledEngine.createComponent(TextureComponent.class);
        StateComponent stateCom = pooledEngine.createComponent(StateComponent.class);
        DirectionComponent directionComponent = pooledEngine.createComponent(DirectionComponent.class);
        CharacterStatsComponent characterStatsComponent = this.pooledEngine.createComponent(CharacterStatsComponent.class);

        b2dBodyComponent.body = bodyCreator.makeCirclePolyBody(rectangle,
                BodyCreator.STONE,
                BodyDef.BodyType.DynamicBody,
                this.gameWorld.getWorldSingleton().getWorld(),
                true);

        stateCom.set(StateComponent.STATE_NORMAL);
        entity.add(stateCom);

        textureComponent.region = this.enemyAnimationFrames.get(0);
        entity.add(textureComponent);

        Array<TextureRegion> enemyFrames = this.enemyAnimationFrames;

        Animation anim = new Animation(0.1f,enemyFrames);
        anim.setPlayMode(Animation.PlayMode.LOOP);
        animationComponent.animations.put(StateComponent.STATE_NORMAL, anim);
        entity.add(animationComponent);
        entity.add(directionComponent);

        b2dBodyComponent.body.setUserData(entity);
        b2dBodyComponent.body.setSleepingAllowed(false);
        entity.add(b2dBodyComponent);

        transformComponent.position.set(rectangle.getX(), rectangle.getY());
        entity.add(transformComponent);

        characterStatsComponent.init(this.uiCharacterStatsAtlas, this.uiSkin
                ,rectangle.getX()
                ,rectangle.getY()
                ,this.characterHUD
        );

        entity.add(characterStatsComponent);

        enemyComponent.xPosCenter = rectangle.getX();
        boolean isOrientedLeft = (boolean)object.getProperties().get("StartingDirectionLeft");
        enemyComponent.velocity = (isOrientedLeft)?EnemyComponent.LEFT_SPEED:EnemyComponent.RIGHT_SPEED;
        enemyComponent.noOfSteps = (int)object.getProperties().get("NoOfSteps") *
                (int) MULTIPLY_BY_PPM;

        entity.add(enemyComponent);

        typeComponent.type = TypeComponent.ENEMY;
        entity.add(typeComponent);

        entity.add(colComp);
        entity.add(b2dBodyComponent);
        entity.add(transformComponent);
        entity.add(enemyComponent);
        entity.add(typeComponent);
        this.healthManagerSystem.initializeHealth(entity);
        this.pooledEngine.addEntity(entity);


        return entity;
    }

    public void createBasicCollectibles()
    {
        TiledMap map = this.gameWorld.getTiledMap();
        //Create basic collectibles
        for(TextureMapObject object : map.getLayers().
                get(GameWorld.TM_LAYER_BASIC_COLLECTIBLES).
                getObjects().
                getByType(TextureMapObject.class))
        {
            createBasicCollectible(object);
        }
    }

    private Entity createBasicCollectible(TextureMapObject object){
        Entity entity = this.pooledEngine.createEntity();
        B2dBodyComponent b2dBodyComponent = this.pooledEngine.createComponent(B2dBodyComponent.class);
        TransformComponent transformComponent = this.pooledEngine.createComponent(TransformComponent.class);
        TypeComponent typeComponent = this.pooledEngine.createComponent(TypeComponent.class);
        CollisionComponent colComp = this.pooledEngine.createComponent(CollisionComponent.class);
        CollectibleBasicComponent collectibleBasicComponent = this.pooledEngine.createComponent(CollectibleBasicComponent.class);
        Rectangle rectangle = getRectangle(object);
        int collectibleBasicType;

        b2dBodyComponent.body = bodyCreator.makeCirclePolyBody(rectangle,
                BodyCreator.STONE,
                BodyDef.BodyType.StaticBody,
                this.gameWorld.getWorldSingleton().getWorld(),
                true);

        b2dBodyComponent.body.setUserData(entity);
        b2dBodyComponent.body.setSleepingAllowed(false);
        bodyCreator.makeAllFixturesSensors(b2dBodyComponent.body);
        entity.add(b2dBodyComponent);

        transformComponent.position.set(rectangle.getX(), rectangle.getY());
        entity.add(transformComponent);

        collectibleBasicType = (int)object.getProperties().get("CollectibleType");
        if(collectibleBasicType < GameConfig.DOUBLE_JUMP || collectibleBasicType > GameConfig.STOMP)
        {
            logger.error("Error: Wrong Collectible basic type!");
        }
        collectibleBasicComponent.type = collectibleBasicType;
        collectibleBasicComponent.textureMapObject = object;
        entity.add(collectibleBasicComponent);

        typeComponent.type = TypeComponent.BASIC_COLLECTIBLE;
        entity.add(typeComponent);

        entity.add(colComp);
        entity.add(b2dBodyComponent);
        entity.add(transformComponent);
        entity.add(typeComponent);
        this.pooledEngine.addEntity(entity);

        return entity;
    }

    public void createPotions()
    {
        TiledMap map = this.gameWorld.getTiledMap();
        //Create basic collectibles
        for(TextureMapObject object : map.getLayers().
                get(GameWorld.TM_LAYER_POTIONS).
                getObjects().
                getByType(TextureMapObject.class))
        {
            createPotion(object);
        }
    }

    private Entity createPotion(TextureMapObject object){
        Entity entity = this.pooledEngine.createEntity();
        B2dBodyComponent b2dBodyComponent = this.pooledEngine.createComponent(B2dBodyComponent.class);
        TransformComponent transformComponent = this.pooledEngine.createComponent(TransformComponent.class);
        TypeComponent typeComponent = this.pooledEngine.createComponent(TypeComponent.class);
        CollisionComponent colComp = this.pooledEngine.createComponent(CollisionComponent.class);
        PotionComponent potionComponent = this.pooledEngine.createComponent(PotionComponent.class);
        Rectangle rectangle = getRectangle(object);

        b2dBodyComponent.body = bodyCreator.makeCirclePolyBody(rectangle,
                BodyCreator.STONE,
                BodyDef.BodyType.StaticBody,
                this.gameWorld.getWorldSingleton().getWorld(),
                true);

        b2dBodyComponent.body.setUserData(entity);
        b2dBodyComponent.body.setSleepingAllowed(false);
        bodyCreator.makeAllFixturesSensors(b2dBodyComponent.body);
        entity.add(b2dBodyComponent);

        transformComponent.position.set(rectangle.getX(), rectangle.getY());
        entity.add(transformComponent);

        potionComponent.textureMapObject = object;
        entity.add(potionComponent);

        typeComponent.type = TypeComponent.POTIONS;
        entity.add(typeComponent);

        entity.add(colComp);
        entity.add(b2dBodyComponent);
        entity.add(transformComponent);
        entity.add(typeComponent);
        this.pooledEngine.addEntity(entity);

        return entity;
    }

    public Entity createBullet(float x, float y,
                               float xVel, float yVel,
                               Direction direction,
                               BulletComponent.Owner own, PooledEngine pooledEngine, World world)
    {
        Entity entity = pooledEngine.createEntity();
        B2dBodyComponent b2dbody = pooledEngine.createComponent(B2dBodyComponent.class);
        TransformComponent position = pooledEngine.createComponent(TransformComponent.class);
        StateComponent stateCom = pooledEngine.createComponent(StateComponent.class);
        TypeComponent type = pooledEngine.createComponent(TypeComponent.class);
        CollisionComponent colComp = pooledEngine.createComponent(CollisionComponent.class);
        BulletComponent bul = pooledEngine.createComponent(BulletComponent.class);
        AnimationComponent animationComponent = this.pooledEngine.createComponent(AnimationComponent.class);
        TextureComponent textureComponent = this.pooledEngine.createComponent(TextureComponent.class);
        DirectionComponent directionComponent = this.pooledEngine.createComponent(DirectionComponent.class);
        bul.owner = own;

        Rectangle rectangle = new Rectangle(x,y, 32,32);
        b2dbody.body = bodyCreator.makeCirclePolyBody(rectangle,
                BodyCreator.STONE,
                BodyDef.BodyType.DynamicBody,world,
                true);
        b2dbody.body.setBullet(true); // increase physics computation to limit body travelling through other objects
        bodyCreator.makeAllFixturesSensors(b2dbody.body); // make bullets sensors so they don't move player
        position.position.set(x,y);

        textureComponent.region = this.magicRightFrames.get(0);
        entity.add(textureComponent);

        stateCom.set(StateComponent.STATE_NORMAL);
        entity.add(stateCom);

        directionComponent.direction = direction;
        entity.add(directionComponent);

        Animation anim = new Animation(0.1f,this.magicRightFrames);
        anim.setPlayMode(Animation.PlayMode.LOOP);
        animationComponent.animations.put(StateComponent.STATE_NORMAL, anim);
        entity.add(animationComponent);

        type.type = TypeComponent.BULLET;
        b2dbody.body.setUserData(entity);
        bul.xVel = xVel * GameConfig.DIVIDE_BY_PPM;
        bul.yVel = yVel * GameConfig.DIVIDE_BY_PPM;

        entity.add(bul);
        entity.add(colComp);
        entity.add(b2dbody);
        entity.add(position);
        entity.add(type);

        pooledEngine.addEntity(entity);
        logger.debug("Bullet Created");

        return entity;
    }

    private Rectangle getRectangle(MapObject object)
    {
        /* Parse parameters from the tiled map object */
        String tileMapWidth = object.getProperties().get("width").toString();
        String tileMapHeight = object.getProperties().get("height").toString();

        float width = Float.parseFloat(tileMapWidth);
        float height = Float.parseFloat(tileMapHeight);

        float xPos = (float) object.getProperties().get("x");
        float yPos = (float) object.getProperties().get("y");

        return new Rectangle(xPos, yPos, width, height);
    }

    private Rectangle getRectangle(Vector2 position)
    {
        return new Rectangle(
                position.x - GameConfig.DEFAULT_PLAYER_WIDTH/2,
                position.y - GameConfig.DEFAULT_PLAYER_HEIGHT/2,
                GameConfig.DEFAULT_PLAYER_WIDTH,
                GameConfig.DEFAULT_PLAYER_HEIGHT);
    }

    private void setHealthManagerSystem(HealthManagerSystem healthManagerSystem)
    {
        this.healthManagerSystem = healthManagerSystem;
    }

    public void setGameWorld(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    public void setPooledEngine(PooledEngine pooledEngine) {
        this.pooledEngine = pooledEngine;
        setHealthManagerSystem(this.pooledEngine.getSystem(HealthManagerSystem.class));
    }

    public void setOrthographicCamera(OrthographicCamera orthographicCamera) {
        this.orthographicCamera = orthographicCamera;
    }

    public PooledEngine getPooledEngine() {
        return pooledEngine;
    }

    public void setConnectionType(boolean online) {
        this.connectionType = online;
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public TextureAtlas getUiCharacterStatsAtlas() {
        return uiCharacterStatsAtlas;
    }

    public void setUiCharacterStatsAtlas(TextureAtlas uiCharacterStatsAtlas) {
        this.uiCharacterStatsAtlas = uiCharacterStatsAtlas;
    }

    public Skin getUiSkin() {
        return uiSkin;
    }

    public void setUiSkin(Skin uiSkin) {
        this.uiSkin = uiSkin;
    }

    public Stage getCharacterHUD() {
        return characterHUD;
    }

    public void setCharacterHUD(Stage characterHUD) {
        this.characterHUD = characterHUD;
    }
}
