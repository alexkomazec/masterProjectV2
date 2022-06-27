package com.mygdx.game.gameworld;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class OrthogonalTiledMapRendererWithSprites extends OrthogonalTiledMapRenderer {

    public OrthogonalTiledMapRendererWithSprites(TiledMap map) {
        super(map);
    }

    public OrthogonalTiledMapRendererWithSprites(TiledMap map, Batch batch) {
        super(map, batch);
    }

    public OrthogonalTiledMapRendererWithSprites(TiledMap map, float unitScale) {
        super(map, unitScale);
    }

    public OrthogonalTiledMapRendererWithSprites(TiledMap map, float unitScale, Batch batch) {
        super(map, unitScale, batch);
    }

    @Override
    public void renderObject(MapObject obj) {

        if (obj instanceof TextureMapObject) {

            TextureMapObject object = (TextureMapObject) obj;
            TextureRegion textureRegion = object.getTextureRegion();
            batch.draw(textureRegion, object.getX(), object.getY());
        }
    }
}
