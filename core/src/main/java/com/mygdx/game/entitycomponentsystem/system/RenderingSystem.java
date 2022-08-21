package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.common.Direction;
import com.mygdx.game.entitycomponentsystem.components.DirectionComponent;
import com.mygdx.game.entitycomponentsystem.components.TextureComponent;
import com.mygdx.game.entitycomponentsystem.components.TransformComponent;

import java.util.Comparator;

public class RenderingSystem extends SortedIteratingSystem {
	// debug stuff
    private static final Logger logger = new Logger(RenderingSystem.class.getName(), Logger.INFO);
    private boolean shouldRender = true;
    public static final float PPM = 200.0f;
    static final float FRUSTUM_WIDTH = (Gdx.graphics.getWidth()/PPM);
    static final float FRUSTUM_HEIGHT = (Gdx.graphics.getHeight()/PPM);

    public static final float PIXELS_TO_METRES = 1.0f / PPM;

    private static Vector2 meterDimensions = new Vector2();
    private static Vector2 pixelDimensions = new Vector2();
    public static Vector2 getScreenSizeInMeters(){
        meterDimensions.set(Gdx.graphics.getWidth()*PIXELS_TO_METRES,
                            Gdx.graphics.getHeight()*PIXELS_TO_METRES);
        return meterDimensions;
    }

    public static Vector2 getScreenSizeInPixesl(){
        pixelDimensions.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        return pixelDimensions;
    }

    public static float PixelsToMeters(float pixelValue){
        return pixelValue; //* PIXELS_TO_METRES;
    }

    private SpriteBatch batch;
    private Array<Entity> renderQueue;
    private Comparator<Entity> comparator;
    private OrthographicCamera cam;

    private ComponentMapper<TextureComponent> textureM;
    private ComponentMapper<TransformComponent> transformM;

    @SuppressWarnings("unchecked")
	public RenderingSystem(SpriteBatch batch) {
        super(Family.all(TransformComponent.class, TextureComponent.class).get(), new Comparator<Entity>() {
            @Override
            public int compare(Entity entity, Entity t1) {
                return 0;
            }
        });

        textureM = ComponentMapper.getFor(TextureComponent.class);
        transformM = ComponentMapper.getFor(TransformComponent.class);

        renderQueue = new Array<Entity>();

        this.batch = batch;

        comparator = new Comparator<Entity>() {
            @Override
            public int compare(Entity entity, Entity t1) {
                return 0;
            }
        };

        cam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
        cam.position.set(FRUSTUM_WIDTH / 2f, FRUSTUM_HEIGHT / 2f, 0);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        renderQueue.sort(comparator);
        
        cam.update();
        batch.setProjectionMatrix(cam.combined);
        batch.enableBlending();
        if(shouldRender){
	        batch.begin();
	        for (Entity entity : renderQueue) {
	            TextureComponent textureComponent = textureM.get(entity);
	            TransformComponent transformComponent = transformM.get(entity);
	            DirectionComponent directionComponent = entity.getComponent(DirectionComponent.class);

	            if (textureComponent.region == null || transformComponent.isHidden) {
	                continue;
	            }

	            float width = textureComponent.region.getRegionWidth();
	            float height = textureComponent.region.getRegionHeight();
	
	            float originX = width/2f;
	            float originY = height/2f;

	            if(directionComponent!= null)
                    textureComponent = ifNeededLetsflipX(textureComponent,directionComponent);

	            batch.draw(textureComponent.region,
                        transformComponent.position.x - originX + textureComponent.offsetX,
                        transformComponent.position.y - originY + textureComponent.offsetY,
	                    width, height );
	        }
	        batch.end();
        }
        renderQueue.clear();
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        renderQueue.add(entity);
    }

    public OrthographicCamera getCamera() {
        return cam;
    }

    public TextureComponent ifNeededLetsflipX(TextureComponent textureComponent, DirectionComponent directionComponent)
    {
        if(directionComponent.direction == Direction.RIGHT)
        {
            if(textureComponent.region.isFlipX())
            {
                textureComponent.region.flip(true, false);
            }
        }
        else
        {
            if(!textureComponent.region.isFlipX())
            {
                textureComponent.region.flip(true, false);
            }
        }

        return textureComponent;
    }
}