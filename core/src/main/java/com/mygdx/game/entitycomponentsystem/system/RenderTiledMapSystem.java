package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.entitycomponentsystem.components.BrickComponent;
import com.mygdx.game.entitycomponentsystem.components.CollectibleBasicComponent;
import com.mygdx.game.entitycomponentsystem.components.TiledMapComponent;
import com.mygdx.game.gameworld.OrthogonalTiledMapRendererWithSprites;

public class RenderTiledMapSystem extends IteratingSystem {

    private static final Logger logger = new Logger(RenderTiledMapSystem.class.getSimpleName(), Logger.INFO);
    OrthogonalTiledMapRendererWithSprites renderer;
    OrthographicCamera camera;
    TiledMap tiledMap;

    public RenderTiledMapSystem(OrthogonalTiledMapRendererWithSprites renderer,
                                OrthographicCamera camera,
                                TiledMap tiledMap)
    {
        super(Family.one(BrickComponent.class, CollectibleBasicComponent.class).get());
        this.renderer = renderer;
        this.camera = camera;
        this.tiledMap = tiledMap;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        this.renderer.setView(camera);
        this.renderer.render();

    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TiledMapComponent tmc = findActualTiledMapComponent(entity);
        assert tmc != null;

        if(tmc.isDead)
        {
            tiledMap.getLayers().get(tmc.belongsToLayer).
                    getObjects().remove(tmc.textureMapObject);
        }
    }

    private TiledMapComponent findActualTiledMapComponent(Entity entity)
    {
        TiledMapComponent tiledMapComponent;

        tiledMapComponent = entity.getComponent(BrickComponent.class);
        if(tiledMapComponent != null)
            return tiledMapComponent;

        tiledMapComponent = entity.getComponent(CollectibleBasicComponent.class);
        if(tiledMapComponent!= null)
            return tiledMapComponent;

        logger.error("Error: result of findActualTiledMapComponent is null");
        return null;
    }
}
