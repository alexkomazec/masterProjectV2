package com.mygdx.game.gameworld;

import static com.mygdx.game.config.GameConfig.BULLET_BIT;
import static com.mygdx.game.config.GameConfig.CLOUD_BIT;
import static com.mygdx.game.config.GameConfig.COLLECTIBLE_BIT;
import static com.mygdx.game.config.GameConfig.ENEMY_BIT;
import static com.mygdx.game.config.GameConfig.GROUND_BIT;
import static com.mygdx.game.config.GameConfig.HURTABLE_OBJECTS_BIT;
import static com.mygdx.game.config.GameConfig.LIMIT_AREA_BIT;
import static com.mygdx.game.config.GameConfig.MULTIPLY_BY_PPM;
import static com.mygdx.game.config.GameConfig.PLAYER_BIT;
import static com.mygdx.game.config.GameConfig.PORTAL_BIT;
import static com.mygdx.game.config.GameConfig.POTIONS_BIT;
import static com.mygdx.game.config.GameConfig.SPELL_HEIGHT;
import static com.mygdx.game.config.GameConfig.SPELL_WIDTH;
import static com.mygdx.game.config.GameConfig.SENSOR_BIT;
import static com.mygdx.game.config.GameConfig.VIEW_AREA_BIT;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
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
import com.mygdx.game.common.Direction;
import com.mygdx.game.common.SensorType;
import com.mygdx.game.common.assets.AssetDescriptors;
import com.mygdx.game.common.assets.AssetManagmentHandler;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.entitycomponentsystem.components.AIEnemyComponent;
import com.mygdx.game.entitycomponentsystem.components.AnimationComponent;
import com.mygdx.game.entitycomponentsystem.components.B2dBodyComponent;
import com.mygdx.game.entitycomponentsystem.components.BrickComponent;
import com.mygdx.game.entitycomponentsystem.components.BulletComponent;
import com.mygdx.game.entitycomponentsystem.components.CollectibleBasicComponent;
import com.mygdx.game.entitycomponentsystem.components.CollisionComponent;
import com.mygdx.game.entitycomponentsystem.components.CollisionEffectComponent;
import com.mygdx.game.entitycomponentsystem.components.ControllableComponent;
import com.mygdx.game.entitycomponentsystem.components.ControlledInputComponent;
import com.mygdx.game.entitycomponentsystem.components.CoolDownComponent;
import com.mygdx.game.entitycomponentsystem.components.DirectionComponent;
import com.mygdx.game.entitycomponentsystem.components.EnemyComponent;
import com.mygdx.game.entitycomponentsystem.components.HurtableObjectComponent;
import com.mygdx.game.entitycomponentsystem.components.LimitAreaComponent;
import com.mygdx.game.entitycomponentsystem.components.LocalInputComponent;
import com.mygdx.game.entitycomponentsystem.components.PlayerComponent;
import com.mygdx.game.entitycomponentsystem.components.CharacterStatsComponent;
import com.mygdx.game.entitycomponentsystem.components.PortalComponent;
import com.mygdx.game.entitycomponentsystem.components.PotionComponent;
import com.mygdx.game.entitycomponentsystem.components.RemoteInputComponent;
import com.mygdx.game.entitycomponentsystem.components.StateComponent;
import com.mygdx.game.entitycomponentsystem.components.SteeringComponent;
import com.mygdx.game.entitycomponentsystem.components.TextureComponent;
import com.mygdx.game.entitycomponentsystem.components.TransformComponent;
import com.mygdx.game.entitycomponentsystem.components.TypeComponent;
import com.mygdx.game.entitycomponentsystem.components.SensorComponent;
import com.mygdx.game.entitycomponentsystem.system.HealthManagerSystem;
import com.mygdx.game.entitycomponentsystem.system.InputManagerSystem;
import com.mygdx.game.entitycomponentsystem.system.MatchTracker;


public class GameWorldCreator {

    protected static final Logger logger = new Logger(GameWorldCreator.class.getSimpleName(), Logger.DEBUG);
    private BodyCreator bodyCreator;
    public static GameWorldCreator instance;
    public boolean connectionType;

    public static int currentAvailablePlayerID = 0;
    public int bodyIDCounter = 2;

    private TextureAtlas playerAtlas;
    private TextureAtlas magicSpellAtlas;
    private TextureAtlas magicSpellLeftAtlas;
    private TextureAtlas voidSBoltSpellAtlas;
    private TextureAtlas collidedSpellAtlas;

    private Array<TextureRegion> magicLeftFrames;
    private Array<TextureRegion> magicRightFrames;
    private Array<TextureRegion> voidSBoltSpellFrames;
    private Array<TextureRegion> enemyAnimationFrames;
    private Array<TextureRegion> flyingBobAnimationFrames;
    private Array<TextureRegion> heroAnimaitonFrames;
    private Array<TextureRegion> collisionEffectFrames;

    private TextureAtlas uiCharacterStatsAtlas;
    private Skin uiSkin;

    GameWorld gameWorld;
    PooledEngine pooledEngine;
    HealthManagerSystem healthManagerSystem;
    OrthographicCamera orthographicCamera;
    AssetManagmentHandler assetManagmentHandler;
    MatchTracker matchTracker;

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
            this.voidSBoltSpellAtlas = this.assetManagmentHandler.getResources(AssetDescriptors.VOID_MAGIC_ANIMATION);
            this.collidedSpellAtlas = this.assetManagmentHandler.getResources(AssetDescriptors.SPELL_COLLIDED_EFFECT);

            this.collisionEffectFrames = new Array<>();
            this.collisionEffectFrames.add(collidedSpellAtlas.findRegion("boomEffect1"));
            this.collisionEffectFrames.add(collidedSpellAtlas.findRegion("boomEffect2"));
            this.collisionEffectFrames.add(collidedSpellAtlas.findRegion("boomEffect3"));
            this.collisionEffectFrames.add(collidedSpellAtlas.findRegion("boomEffect4"));
            this.collisionEffectFrames.add(collidedSpellAtlas.findRegion("boomEffect5"));
            this.collisionEffectFrames.add(collidedSpellAtlas.findRegion("boomEffect6"));
            this.collisionEffectFrames.add(collidedSpellAtlas.findRegion("boomEffect7"));
            this.collisionEffectFrames.add(collidedSpellAtlas.findRegion("boomEffect8"));
            this.collisionEffectFrames.add(collidedSpellAtlas.findRegion("boomEffect9"));

            this.magicLeftFrames = new Array<>();
            this.magicLeftFrames.add(magicSpellLeftAtlas.findRegion("fireball1"));
            this.magicLeftFrames.add(magicSpellLeftAtlas.findRegion("fireball2"));
            this.magicLeftFrames.add(magicSpellLeftAtlas.findRegion("fireball3"));
            this.magicLeftFrames.add(magicSpellLeftAtlas.findRegion("fireball4"));

            this.magicRightFrames = new Array<>();
            this.magicRightFrames.add(magicSpellAtlas.findRegion("fireball1"));
            this.magicRightFrames.add(magicSpellAtlas.findRegion("fireball2"));
            this.magicRightFrames.add(magicSpellAtlas.findRegion("fireball3"));
            this.magicRightFrames.add(magicSpellAtlas.findRegion("fireball4"));

            this.voidSBoltSpellFrames = new Array<>();
            this.voidSBoltSpellFrames.add(voidSBoltSpellAtlas.findRegion("voidBall01"));

            TextureAtlas enemyAnimationAtlas = this.assetManagmentHandler.getResources(AssetDescriptors.ENEMY_ANIMATION);
            this.enemyAnimationFrames = new Array<>();
            //this.enemyAnimationFrames.add(enemyAnimationAtlas.findRegion("snailyRichard00run"));
            this.enemyAnimationFrames.add(enemyAnimationAtlas.findRegion("snailyRichard01run"));
            this.enemyAnimationFrames.add(enemyAnimationAtlas.findRegion("snailyRichard02run"));
            this.enemyAnimationFrames.add(enemyAnimationAtlas.findRegion("snailyRichard03run"));
            this.enemyAnimationFrames.add(enemyAnimationAtlas.findRegion("snailyRichard04run"));
            this.enemyAnimationFrames.add(enemyAnimationAtlas.findRegion("snailyRichard05run"));
            this.enemyAnimationFrames.add(enemyAnimationAtlas.findRegion("snailyRichard06run"));
            this.enemyAnimationFrames.add(enemyAnimationAtlas.findRegion("snailyRichard07run"));
            this.enemyAnimationFrames.add(enemyAnimationAtlas.findRegion("snailyRichard08run"));

            TextureAtlas flyingBobAnimationAtlas = this.assetManagmentHandler.getResources(AssetDescriptors.FLYING_BOB_ANIMATION);
            this.flyingBobAnimationFrames = new Array<>();
            //this.enemyAnimationFrames.add(enemyAnimationAtlas.findRegion("snailyRichard00run"));
            this.flyingBobAnimationFrames.add(flyingBobAnimationAtlas.findRegion("flyingBob01run"));
            this.flyingBobAnimationFrames.add(flyingBobAnimationAtlas.findRegion("flyingBob02run"));
            this.flyingBobAnimationFrames.add(flyingBobAnimationAtlas.findRegion("flyingBob03run"));
            this.flyingBobAnimationFrames.add(flyingBobAnimationAtlas.findRegion("flyingBob04run"));
            this.flyingBobAnimationFrames.add(flyingBobAnimationAtlas.findRegion("flyingBob05run"));
            this.flyingBobAnimationFrames.add(flyingBobAnimationAtlas.findRegion("flyingBob06run"));
            this.flyingBobAnimationFrames.add(flyingBobAnimationAtlas.findRegion("flyingBob07run"));

            TextureAtlas heroAnimationAtlas = this.assetManagmentHandler.getResources(AssetDescriptors.WIZARD_ANIMATION);
            this.heroAnimaitonFrames = new Array<>();
            this.heroAnimaitonFrames.add(heroAnimationAtlas.findRegion("fireWizard00run"));
            this.heroAnimaitonFrames.add(heroAnimationAtlas.findRegion("fireWizard01run"));
            this.heroAnimaitonFrames.add(heroAnimationAtlas.findRegion("fireWizard02run"));
            this.heroAnimaitonFrames.add(heroAnimationAtlas.findRegion("fireWizard03run"));
            this.heroAnimaitonFrames.add(heroAnimationAtlas.findRegion("fireWizard04run"));
            this.heroAnimaitonFrames.add(heroAnimationAtlas.findRegion("fireWizard05run"));
            this.heroAnimaitonFrames.add(heroAnimationAtlas.findRegion("fireWizard06run"));
            this.heroAnimaitonFrames.add(heroAnimationAtlas.findRegion("fireWizard07run"));
            this.heroAnimaitonFrames.add(heroAnimationAtlas.findRegion("fireWizard08run"));
        }
    }

    public void createClouds()
    {
        TiledMap map = this.gameWorld.getTiledMap();

        MapLayer mapLayer = map.getLayers().get(GameWorld.TM_LAYER_CLOUD_ENEMIES);
        if(mapLayer != null)
        {
            for(EllipseMapObject object : mapLayer.getObjects().getByType(EllipseMapObject.class))
            {
                createCloud(object);
            };
        }
    }

    private void createViewArea(Rectangle rectangle, Entity owner)
    {
        Short categoryFilterBits = VIEW_AREA_BIT;
        Short maskFilterBits = PLAYER_BIT;
        Entity entity = this.pooledEngine.createEntity();
        SensorComponent sensorComponent = this.pooledEngine.createComponent(SensorComponent.class);
        B2dBodyComponent b2dBodyComponent = this.pooledEngine.createComponent(B2dBodyComponent.class);
        TypeComponent typeComponent = this.pooledEngine.createComponent(TypeComponent.class);
        typeComponent.type = TypeComponent.VIEW_AREA_SENSOR;
        /* View area should be wider, than enemy's surface */

        if(owner.getComponent(EnemyComponent.class).enemyType == EnemyComponent.Type.CLOUD)
        {
            rectangle.width *= 5;
            rectangle.height *= 5;
        }
        else
        {
            rectangle.width *= 15;
            rectangle.height *= 15;
        }

        b2dBodyComponent.body = bodyCreator.makeCirclePolyBody(rectangle,
                BodyCreator.STONE,
                BodyDef.BodyType.DynamicBody,
                this.gameWorld.getWorldSingleton().getWorld(),
                true, categoryFilterBits, maskFilterBits);
        bodyCreator.makeAllFixturesSensors(b2dBodyComponent.body);
        b2dBodyComponent.body.setUserData(entity);
        b2dBodyComponent.bodyID = bodyIDCounter;
        b2dBodyComponent.bodyName = "ViewArea";
        logger.debug("Body created: " + b2dBodyComponent.bodyName);
        logger.debug("Body ID: " + b2dBodyComponent.bodyID);
        bodyIDCounter++;

        sensorComponent.owner = owner;
        sensorComponent.sensorType = SensorType.VIEW_AREA;

        entity.add(b2dBodyComponent);
        entity.add(sensorComponent);
        entity.add(typeComponent);

        this.pooledEngine.addEntity(entity);
    }

    private void createEnemySensor(Rectangle rectangle, Entity owner)
    {
        short categoryFilterBits = SENSOR_BIT;
        short maskFilterBits = PLAYER_BIT;
        Entity entity = this.pooledEngine.createEntity();
        SensorComponent sensorComponent = this.pooledEngine.createComponent(SensorComponent.class);
        B2dBodyComponent b2dBodyComponent = this.pooledEngine.createComponent(B2dBodyComponent.class);
        TypeComponent typeComponent = this.pooledEngine.createComponent(TypeComponent.class);
        typeComponent.type = TypeComponent.ENEMY_SENSOR;

        b2dBodyComponent.body = bodyCreator.makeCirclePolyBody(rectangle,
                BodyCreator.STONE,
                BodyDef.BodyType.DynamicBody,
                this.gameWorld.getWorldSingleton().getWorld(),
                true, categoryFilterBits, maskFilterBits);

        bodyCreator.makeAllFixturesSensors(b2dBodyComponent.body);
        b2dBodyComponent.body.setUserData(entity);
        b2dBodyComponent.body.setSleepingAllowed(false);
        b2dBodyComponent.bodyID = bodyIDCounter;
        b2dBodyComponent.bodyName = "EnemySensor";
        logger.debug("Body created: " + b2dBodyComponent.bodyName);
        logger.debug("Body ID: " + b2dBodyComponent.bodyID);
        bodyIDCounter++;

        sensorComponent.owner = owner;
        sensorComponent.sensorType = SensorType.ENEMY_SENSOR;

        entity.add(b2dBodyComponent);
        entity.add(sensorComponent);
        entity.add(typeComponent);

        this.pooledEngine.addEntity(entity);
    }

    private void createCloud(MapObject object)
    {
        Short categoryFilterBits = CLOUD_BIT;
        Short maskFilterBits = BULLET_BIT | GROUND_BIT;

        Entity entity = this.pooledEngine.createEntity();
        B2dBodyComponent b2dBodyComponent = this.pooledEngine.createComponent(B2dBodyComponent.class);
        TransformComponent transformComponent = this.pooledEngine.createComponent(TransformComponent.class);
        CollisionComponent collisionComponent = this.pooledEngine.createComponent(CollisionComponent.class);
        TypeComponent typeComponent = this.pooledEngine.createComponent(TypeComponent.class);
        StateComponent stateComponent = this.pooledEngine.createComponent(StateComponent.class);
        AnimationComponent animationComponent = this.pooledEngine.createComponent(AnimationComponent.class);
        TextureComponent textureComponent = this.pooledEngine.createComponent(TextureComponent.class);
        EnemyComponent enemyComponent = this.pooledEngine.createComponent(EnemyComponent.class);
        DirectionComponent directionComponent = pooledEngine.createComponent(DirectionComponent.class);
        SteeringComponent steeringComponent = this.pooledEngine.createComponent(SteeringComponent.class);
        CharacterStatsComponent characterStatsComponent = this.pooledEngine.createComponent(CharacterStatsComponent.class);
        AIEnemyComponent aiEnemyComponent = this.pooledEngine.createComponent(AIEnemyComponent.class);

        Rectangle rectangle = getRectangle(object);

        b2dBodyComponent.body = bodyCreator.makeCirclePolyBody(rectangle,
                BodyCreator.STONE,
                BodyDef.BodyType.DynamicBody,
                this.gameWorld.getWorldSingleton().getWorld(),
                true, categoryFilterBits, maskFilterBits);

        b2dBodyComponent.body.setGravityScale(0f);  // no gravity for our floating enemy
        b2dBodyComponent.body.setLinearDamping(0.3f); // setting linear dampening so the enemy slows down in our box2d world(or it can float on forever)
        b2dBodyComponent.bodyID = bodyIDCounter;
        b2dBodyComponent.bodyName = "Cloud";
        logger.debug("Body created: " + b2dBodyComponent.bodyName);
        logger.debug("Body ID: " + b2dBodyComponent.bodyID);
        bodyIDCounter++;

        transformComponent.position.set(rectangle.getX(), rectangle.getY());
        typeComponent.type = TypeComponent.ENEMY;
        stateComponent.set(StateComponent.STATE_NORMAL);
        b2dBodyComponent.body.setUserData(entity);

        textureComponent.region = this.flyingBobAnimationFrames.get(0);
        entity.add(textureComponent);

        Array<TextureRegion> enemyFrames = this.flyingBobAnimationFrames;

        Animation anim = new Animation(GameConfig.FRAME_DURATION,enemyFrames);
        anim.setPlayMode(Animation.PlayMode.LOOP);
        animationComponent.animations.put(StateComponent.STATE_NORMAL, anim);
        entity.add(animationComponent);

        // bodyFactory.makeAllFixturesSensors(b2dbody.body); // seeker  should fly about not fall
        steeringComponent.body = b2dBodyComponent.body;
        enemyComponent.enemyType = EnemyComponent.Type.CLOUD;

        // set out steering behaviour
        steeringComponent.currentMode = SteeringComponent.SteeringState.NONE;

        characterStatsComponent.init(this.uiCharacterStatsAtlas, this.uiSkin,
                this.characterHUD
        );

        entity.add(characterStatsComponent);
        entity.add(b2dBodyComponent);
        entity.add(transformComponent);
        entity.add(collisionComponent);
        entity.add(typeComponent);
        entity.add(enemyComponent);
        entity.add(stateComponent);
        entity.add(steeringComponent);
        entity.add(directionComponent);
        entity.add(aiEnemyComponent);

        createEnemySensor(rectangle, entity);
        createViewArea(rectangle, entity);
        this.healthManagerSystem.initializeHealth(entity);
        this.pooledEngine.addEntity(entity);
    }

    public void createPortals()
    {
        TiledMap map = this.gameWorld.getTiledMap();
        //Create Platform
        MapLayer mapLayer = map.getLayers().get(GameWorld.TM_LAYER_PORTALS);
        if(mapLayer != null)
        {
            for(TextureMapObject object : mapLayer.getObjects().getByType(TextureMapObject.class))
            {
                createPortal(object);
            }
        }
    }

    private void createPortal(TextureMapObject object)
    {
        Short categoryFilterBits = PORTAL_BIT;
        Short maskFilterBits = PLAYER_BIT;

        Entity entity = this.pooledEngine.createEntity();
        B2dBodyComponent    b2dBodyComponent = this.pooledEngine.createComponent(B2dBodyComponent.class);
        TypeComponent       typeComponent = this.pooledEngine.createComponent(TypeComponent.class);
        TransformComponent  transformComponent = this.pooledEngine.createComponent(TransformComponent.class);
        CollisionComponent  colComp = this.pooledEngine.createComponent(CollisionComponent.class);
        PortalComponent     portalComponent = this.pooledEngine.createComponent(PortalComponent.class);
        Rectangle rectangle = getRectangle(object);

        //Create a box2d body for each tiled object in the layer
        b2dBodyComponent.body = bodyCreator.makeBoxPolyBody(rectangle,
                BodyCreator.STONE,
                BodyDef.BodyType.StaticBody,
                this.gameWorld.getWorldSingleton().getWorld(),
                true, categoryFilterBits, maskFilterBits);

        /* Set an object that represents an unique ID for the body */
        b2dBodyComponent.body.setUserData(entity);
        b2dBodyComponent.bodyID = bodyIDCounter;
        b2dBodyComponent.bodyName = "Portal";
        logger.debug("Body created: " + b2dBodyComponent.bodyName);
        logger.debug("Body ID: " + b2dBodyComponent.bodyID);
        bodyIDCounter++;
        entity.add(b2dBodyComponent);

        /* Set type component*/
        typeComponent.type = TypeComponent.PORTALS;
        entity.add(typeComponent);

        portalComponent.textureMapObject = object;
        entity.add(portalComponent);

        /* Set transformation component*/
        transformComponent.position.set(rectangle.getX(), rectangle.getY());
        entity.add(transformComponent);
        entity.add(colComp);

        /* Add the index to the brick component*/
        this.pooledEngine.addEntity(entity);
    }

    public void createPlatforms()
    {
        TiledMap map = this.gameWorld.getTiledMap();
        //Create Platform
        MapLayer mapLayer = map.getLayers().get(GameWorld.TM_LAYER_PLATFORM);
        if(mapLayer != null)
        {
            for(TextureMapObject object : mapLayer.getObjects().getByType(TextureMapObject.class))
            {
                createPlatform(object);
            }
        }
    }

    private void createPlatform (TextureMapObject object)
    {
        Short categoryFilterBits = GROUND_BIT;
        Short maskFilterBits = CLOUD_BIT | PLAYER_BIT | ENEMY_BIT | BULLET_BIT;
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
                true, categoryFilterBits, maskFilterBits);

        /* Set an object that represents an unique ID for the body */
        b2dBodyComponent.body.setUserData(entity);
        b2dBodyComponent.bodyID = bodyIDCounter;
        b2dBodyComponent.bodyName = "Platform";
        logger.debug("Body created: " + b2dBodyComponent.bodyName);
        logger.debug("Body ID: " + b2dBodyComponent.bodyID);
        bodyIDCounter++;
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

    public void createHurtableObjects()
    {
        TiledMap map = this.gameWorld.getTiledMap();
        //Create Platform
        MapLayer mapLayer = map.getLayers().get(GameWorld.TM_LAYER_HURTABLE_OBJECTS_LAYER);
        if(mapLayer != null)
        {
            for(TextureMapObject object : mapLayer.getObjects().getByType(TextureMapObject.class))
            {
                createHurtableObject(object);
            }
        }
    }

    private void createHurtableObject (TextureMapObject object)
    {
        Short categoryFilterBits = HURTABLE_OBJECTS_BIT;
        Short maskFilterBits = PLAYER_BIT;
        Entity entity = this.pooledEngine.createEntity();
        B2dBodyComponent    b2dBodyComponent = this.pooledEngine.createComponent(B2dBodyComponent.class);
        TypeComponent       typeComponent = this.pooledEngine.createComponent(TypeComponent.class);
        TransformComponent  transformComponent = this.pooledEngine.createComponent(TransformComponent.class);
        HurtableObjectComponent hurtableObjectComponent = this.pooledEngine.createComponent(HurtableObjectComponent.class);
        Rectangle rectangle = getRectangle(object);

        //Create a box2d body for each tiled object in the layer
        b2dBodyComponent.body = bodyCreator.makeBoxPolyBody(rectangle,
                BodyCreator.STONE,
                BodyDef.BodyType.StaticBody,
                this.gameWorld.getWorldSingleton().getWorld(),
                true, categoryFilterBits, maskFilterBits);

        /* Set an object that represents an unique ID for the body */
        b2dBodyComponent.body.setUserData(entity);
        b2dBodyComponent.bodyID = bodyIDCounter;
        b2dBodyComponent.bodyName = "HurtableObject";
        logger.debug("Body created: " + b2dBodyComponent.bodyName);
        logger.debug("Body ID: " + b2dBodyComponent.bodyID);
        bodyIDCounter++;

        entity.add(b2dBodyComponent);

        /* Set type component*/
        typeComponent.type = TypeComponent.HURTABLE_OBJECT;
        entity.add(typeComponent);

        /* Set transformation component*/
        transformComponent.position.set(rectangle.getX(), rectangle.getY());
        entity.add(transformComponent);

        /* Add the index to the brick component*/
        hurtableObjectComponent.textureMapObject = object;
        entity.add(hurtableObjectComponent);
        this.pooledEngine.addEntity(entity);
    }

    public void createLimitAreaObjects()
    {
        TiledMap map = this.gameWorld.getTiledMap();
        //Create Platform
        MapLayer mapLayer = map.getLayers().get(GameWorld.TM_LAYER_LIMIT_LAYER);
        if(mapLayer != null)
        {
            for(TextureMapObject object : mapLayer.getObjects().getByType(TextureMapObject.class))
            {
                createLimitArea(object);
            }
        }
    }

    private void createLimitArea(TextureMapObject object)
    {
        Short categoryFilterBits = LIMIT_AREA_BIT;
        Short maskFilterBits = CLOUD_BIT | PLAYER_BIT | ENEMY_BIT;

        Entity entity = this.pooledEngine.createEntity();
        B2dBodyComponent    b2dBodyComponent = this.pooledEngine.createComponent(B2dBodyComponent.class);
        TypeComponent       typeComponent = this.pooledEngine.createComponent(TypeComponent.class);
        TransformComponent  transformComponent = this.pooledEngine.createComponent(TransformComponent.class);
        LimitAreaComponent  limitAreaComponent = this.pooledEngine.createComponent(LimitAreaComponent.class);
        Rectangle rectangle = getRectangle(object);

        //Create a box2d body for each tiled object in the layer
        b2dBodyComponent.body = bodyCreator.makeBoxPolyBody(rectangle,
                BodyCreator.STONE,
                BodyDef.BodyType.StaticBody,
                this.gameWorld.getWorldSingleton().getWorld(),
                true, categoryFilterBits, maskFilterBits);

        /* Set an object that represents an unique ID for the body */
        b2dBodyComponent.body.setUserData(entity);
        b2dBodyComponent.bodyID = bodyIDCounter;
        b2dBodyComponent.bodyName = "LimitArea";
        logger.debug("Body created: " + b2dBodyComponent.bodyName);
        logger.debug("Body ID: " + b2dBodyComponent.bodyID);
        bodyIDCounter++;

        entity.add(b2dBodyComponent);

        /* Set type component*/
        typeComponent.type = TypeComponent.SCENERY;
        entity.add(typeComponent);

        /* Set transformation component*/
        transformComponent.position.set(rectangle.getX(), rectangle.getY());
        entity.add(transformComponent);

        /* Add the index to the brick component*/
        limitAreaComponent.textureMapObject = object;
        entity.add(limitAreaComponent);
        this.pooledEngine.addEntity(entity);
    }

    public Entity createPlayer(boolean isLocalPlayer, boolean isOnlineMode, Vector2 position)
    {
        TiledMap map = this.gameWorld.getTiledMap();
        Entity entity = null;
        //Create Players

        MapLayer mapLayer = map.getLayers().get(GameWorld.TM_LAYER_PLAYERS_SPAWN_SPOTS);
        if(mapLayer != null)
        {
            for(EllipseMapObject object :mapLayer.getObjects().getByType(EllipseMapObject.class))
            {
                entity = createPlayer(object, position, isLocalPlayer, isOnlineMode);
            }
        }
        return entity;
    }

    private Entity createPlayer(MapObject object, Vector2 position, boolean isLocalPlayer, boolean isOnlineMode)
    {
        Short categoryFilterBits = PLAYER_BIT;
        Short maskFilterBits = GROUND_BIT | COLLECTIBLE_BIT | POTIONS_BIT |
                               PORTAL_BIT | SENSOR_BIT | BULLET_BIT | VIEW_AREA_BIT |
                               LIMIT_AREA_BIT | HURTABLE_OBJECTS_BIT;

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
        playerComponent.typeOfPlayer = (isLocalPlayer) ? PlayerComponent.PlayerConnectivity.LOCAL:PlayerComponent.PlayerConnectivity.ONLINE;
        this.matchTracker.increaseNoOfAlivePlayers(playerComponent);

        logger.debug("[createPlayer]: playerComponent.playerID assigned to " + playerComponent.playerID);
        entity.add(playerComponent);

        b2dBodyComponent.body = bodyCreator.makeCirclePolyBody(rectangle,
                BodyCreator.STONE,
                BodyDef.BodyType.DynamicBody,
                this.gameWorld.getWorldSingleton().getWorld(),
                true, categoryFilterBits, maskFilterBits);
        /* Set an object that represents an unique ID for the body */
        b2dBodyComponent.body.setUserData(entity);
        // Do not allow unit to sleep or it wil sleep through events if stationary too long
        b2dBodyComponent.body.setSleepingAllowed(false);
        b2dBodyComponent.bodyID = currentAvailablePlayerID;
        currentAvailablePlayerID++;
        b2dBodyComponent.bodyName = "Player";
        logger.debug("Body created: " + b2dBodyComponent.bodyName);
        logger.debug("Body ID: " + b2dBodyComponent.bodyID);
        bodyIDCounter++;

        entity.add(b2dBodyComponent);

        transformComponent.position.set(b2dBodyComponent.body.getPosition().x
                                        * MULTIPLY_BY_PPM,
                                        b2dBodyComponent.body.getPosition().y
                                        * MULTIPLY_BY_PPM);

        entity.add(transformComponent);

        texture.region = this.heroAnimaitonFrames.get(0);
        entity.add(texture);

        Animation anim = new Animation(GameConfig.FRAME_DURATION, this.heroAnimaitonFrames);
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

        characterStatsComponent.init(this.uiCharacterStatsAtlas, this.uiSkin,
                this.characterHUD);

        entity.add(characterStatsComponent);
        //entity.add(controlledInputRemoteComponent);
        entity.add(collisionComponent);
        entity.add(cntrlInComp);

        entity.add(new CoolDownComponent());
        this.healthManagerSystem.initializeHealth(entity);

        InputManagerSystem inputManagerSystem = this.pooledEngine.getSystem(InputManagerSystem.class);
        if(isLocalPlayer)
        {
            if(isOnlineMode == GameConfig.LOCAL_CONNECTION)
            {
                entity.add(new ControllableComponent());
            }
            inputTypeForPlayerComponent = this.pooledEngine.createComponent(LocalInputComponent.class);
            playerComponent.cam = this.orthographicCamera;

            if(inputManagerSystem != null)
                inputManagerSystem.assignPlayerToInputProcessor(playerComponent.playerID, isLocalPlayer);
        }
        else
        {
            inputTypeForPlayerComponent = this.pooledEngine.createComponent(RemoteInputComponent.class);
            b2dBodyComponent.body.setGravityScale(0f);  // no gravity for online players, online players will transmit their position
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
        MapLayer mapLayer = map.getLayers().get(GameWorld.TM_LAYER_BASIC_ENEMIES);
        if(mapLayer != null)
        {
            for(EllipseMapObject object : mapLayer.getObjects().getByType(EllipseMapObject.class))
            {
                createEnemy(object);
            }
        }
    }

    private Entity createEnemy(MapObject object){
        short categoryFilterBits = ENEMY_BIT;
        short maskFilterBits = GROUND_BIT | BULLET_BIT;
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
                true, categoryFilterBits, maskFilterBits);

        stateCom.set(StateComponent.STATE_NORMAL);
        entity.add(stateCom);

        textureComponent.region = this.enemyAnimationFrames.get(0);
        entity.add(textureComponent);

        Array<TextureRegion> enemyFrames = this.enemyAnimationFrames;

        Animation anim = new Animation(GameConfig.FRAME_DURATION,enemyFrames);
        anim.setPlayMode(Animation.PlayMode.LOOP);
        animationComponent.animations.put(StateComponent.STATE_NORMAL, anim);
        entity.add(animationComponent);
        entity.add(directionComponent);

        b2dBodyComponent.body.setUserData(entity);
        b2dBodyComponent.body.setSleepingAllowed(false);
        b2dBodyComponent.bodyID = bodyIDCounter;
        b2dBodyComponent.bodyName = "Enemy";
        logger.debug("Body created: " + b2dBodyComponent.bodyName);
        logger.debug("Body ID: " + b2dBodyComponent.bodyID);
        bodyIDCounter++;
        entity.add(b2dBodyComponent);

        transformComponent.position.set(rectangle.getX(), rectangle.getY());
        entity.add(transformComponent);

        characterStatsComponent.init(this.uiCharacterStatsAtlas, this.uiSkin,
                this.characterHUD
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

        createEnemySensor(rectangle, entity);
        createViewArea(rectangle, entity);
        this.healthManagerSystem.initializeHealth(entity);
        this.pooledEngine.addEntity(entity);

        return entity;
    }

    public void createBasicCollectibles()
    {
        TiledMap map = this.gameWorld.getTiledMap();
        //Create basic collectibles
        MapLayer mapLayer = map.getLayers().get(GameWorld.TM_LAYER_BASIC_COLLECTIBLES);
        if(mapLayer != null)
        {
            for(TextureMapObject object : mapLayer.getObjects().getByType(TextureMapObject.class))
            {
                createBasicCollectible(object);
            }
        }
    }

    private Entity createBasicCollectible(TextureMapObject object){

        Short categoryFilterBits = COLLECTIBLE_BIT;
        Short maskFilterBits = GROUND_BIT | PLAYER_BIT;

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
                true, categoryFilterBits, maskFilterBits);

        b2dBodyComponent.body.setUserData(entity);
        b2dBodyComponent.body.setSleepingAllowed(false);
        bodyCreator.makeAllFixturesSensors(b2dBodyComponent.body);
        b2dBodyComponent.bodyID = bodyIDCounter;
        b2dBodyComponent.bodyName = "BasicCollectible";
        logger.debug("Body created: " + b2dBodyComponent.bodyName);
        logger.debug("Body ID: " + b2dBodyComponent.bodyID);
        bodyIDCounter++;
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
        MapLayer mapLayer = map.getLayers().get(GameWorld.TM_LAYER_POTIONS);
        if(mapLayer != null)
        {
            for(TextureMapObject object :mapLayer.getObjects().getByType(TextureMapObject.class))
            {
                createPotion(object);
            }
        }

    }

    private Entity createPotion(TextureMapObject object){

        Short categoryFilterBits = POTIONS_BIT;
        Short maskFilterBits = GROUND_BIT | PLAYER_BIT;

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
                true, categoryFilterBits, maskFilterBits);

        b2dBodyComponent.body.setUserData(entity);
        b2dBodyComponent.body.setSleepingAllowed(false);
        bodyCreator.makeAllFixturesSensors(b2dBodyComponent.body);
        b2dBodyComponent.bodyID = bodyIDCounter;
        b2dBodyComponent.bodyName = "Potion";
        logger.debug("Body created: " + b2dBodyComponent.bodyName);
        logger.debug("Body ID: " + b2dBodyComponent.bodyID);
        bodyIDCounter++;
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
                               EnemyComponent enemyComponent,
                               BulletComponent.Owner own, B2dBodyComponent ownerRef, PooledEngine pooledEngine, World world)
    {
        Short categoryFilterBits = BULLET_BIT;
        Short maskFilterBits = PLAYER_BIT | ENEMY_BIT | CLOUD_BIT | GROUND_BIT;

        Entity entity = pooledEngine.createEntity();
        B2dBodyComponent b2dBodyComponent = pooledEngine.createComponent(B2dBodyComponent.class);
        TransformComponent position = pooledEngine.createComponent(TransformComponent.class);
        StateComponent stateCom = pooledEngine.createComponent(StateComponent.class);
        TypeComponent type = pooledEngine.createComponent(TypeComponent.class);
        CollisionComponent colComp = pooledEngine.createComponent(CollisionComponent.class);
        BulletComponent bul = pooledEngine.createComponent(BulletComponent.class);
        AnimationComponent animationComponent = this.pooledEngine.createComponent(AnimationComponent.class);
        TextureComponent textureComponent = this.pooledEngine.createComponent(TextureComponent.class);
        DirectionComponent directionComponent = this.pooledEngine.createComponent(DirectionComponent.class);
        bul.owner = own;
        bul.ownerReference = ownerRef;

        Array<TextureRegion> spellFrames = null;

        //x = pleaseOffsetX(direction, x);
        Rectangle rectangle = new Rectangle(x,y, SPELL_WIDTH,SPELL_HEIGHT);
        b2dBodyComponent.body = bodyCreator.makeCirclePolyBody(rectangle,
                BodyCreator.STONE,
                BodyDef.BodyType.DynamicBody,world,
                true, categoryFilterBits, maskFilterBits);
        b2dBodyComponent.body.setBullet(true); // increase physics computation to limit body travelling through other objects
        b2dBodyComponent.bodyID = bodyIDCounter;
        b2dBodyComponent.bodyName = "Bullet";
        logger.debug("Body created: " + b2dBodyComponent.bodyName);
        logger.debug("Body ID: " + b2dBodyComponent.bodyID);
        bodyIDCounter++;
        bodyCreator.makeAllFixturesSensors(b2dBodyComponent.body); // make bullets sensors so they don't move player
        position.position.set(x,y);

        if(enemyComponent!= null)
        {
            if(enemyComponent.enemyType == EnemyComponent.Type.CLOUD)
            {
                spellFrames = this.voidSBoltSpellFrames;
            }
        }
        else
        {
            spellFrames = this.magicRightFrames;
        }

        textureComponent.region = spellFrames.get(0);
        Animation anim = new Animation(GameConfig.FRAME_DURATION,spellFrames);
        anim.setPlayMode(Animation.PlayMode.LOOP);
        animationComponent.animations.put(StateComponent.STATE_NORMAL, anim);

        entity.add(textureComponent);
        entity.add(animationComponent);

        stateCom.set(StateComponent.STATE_NORMAL);
        entity.add(stateCom);

        directionComponent.direction = direction;
        entity.add(directionComponent);

        type.type = TypeComponent.BULLET;
        b2dBodyComponent.body.setUserData(entity);
        bul.xVel = xVel * GameConfig.DIVIDE_BY_PPM;
        bul.yVel = yVel * GameConfig.DIVIDE_BY_PPM;

        entity.add(bul);
        entity.add(colComp);
        entity.add(b2dBodyComponent);
        entity.add(position);
        entity.add(type);

        pooledEngine.addEntity(entity);
        logger.debug("Bullet Created");

        return entity;
    }

    public void createExplosionEffect(float x, float y)
    {
        Entity entity = pooledEngine.createEntity();
        CollisionEffectComponent collisionEffectComponent = pooledEngine.createComponent(CollisionEffectComponent.class);
        AnimationComponent animationComponent = this.pooledEngine.createComponent(AnimationComponent.class);
        TextureComponent textureComponent = this.pooledEngine.createComponent(TextureComponent.class);
        StateComponent stateCom = pooledEngine.createComponent(StateComponent.class);
        TransformComponent position = pooledEngine.createComponent(TransformComponent.class);

        textureComponent.region = collisionEffectFrames.get(0);
        Animation anim = new Animation(GameConfig.FRAME_DURATION, collisionEffectFrames);
        anim.setPlayMode(Animation.PlayMode.NORMAL);
        animationComponent.animations.put(StateComponent.STATE_NORMAL, anim);
        stateCom.set(StateComponent.STATE_NORMAL);
        position.position.set(x,y);

        entity.add(collisionEffectComponent);
        entity.add(animationComponent);
        entity.add(textureComponent);
        entity.add(position);
        entity.add(stateCom);
        pooledEngine.addEntity(entity);
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

    public void setHealthManagerSystem(HealthManagerSystem healthManagerSystem)
    {
        this.healthManagerSystem = healthManagerSystem;
    }

    public void setGameWorld(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    public void setPooledEngine(PooledEngine pooledEngine) {
        this.pooledEngine = pooledEngine;
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

    public Array<TextureRegion> getCollisionEffectFrames() {
        return collisionEffectFrames;
    }

    private float pleaseOffsetX(Direction direction, float x)
    {
        float offsetPos;
        offsetPos = (direction == Direction.LEFT)? (x - 50):(x + 50);
        return offsetPos;
    }

    public void setMatchTracker(MatchTracker matchTracker) {
        this.matchTracker = matchTracker;
    }

    public void resetBodyIDCounter()
    {
        this.bodyIDCounter = 0;
    }

}
