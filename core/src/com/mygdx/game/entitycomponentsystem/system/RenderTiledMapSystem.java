package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.mygdx.game.entitycomponentsystem.components.BrickComponent;
import com.mygdx.game.entitycomponentsystem.components.Mapper;
import com.mygdx.game.gameworld.GameWorld;
import com.mygdx.game.gameworld.OrthogonalTiledMapRendererWithSprites;

public class RenderTiledMapSystem extends IteratingSystem {

    OrthogonalTiledMapRendererWithSprites renderer;
    OrthographicCamera camera;
    TiledMap tiledMap;

    public RenderTiledMapSystem(OrthogonalTiledMapRendererWithSprites renderer,
                                OrthographicCamera camera,
                                TiledMap tiledMap)
    {
        super(Family.all(BrickComponent.class).get());
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
        BrickComponent brc = Mapper.brickCom.get(entity);

        if(brc.isDead)
        {
            tiledMap.getLayers().get(GameWorld.TM_LAYER_PLATFORM).
                    getObjects().remove(brc.textureMapObject);
        }
    }
}
