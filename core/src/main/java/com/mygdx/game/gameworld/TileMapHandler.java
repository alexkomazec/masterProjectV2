package com.mygdx.game.gameworld;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;

public class TileMapHandler implements Disposable {

    public static TileMapHandler instance;

    private final OrthogonalTiledMapRendererWithSprites orthogonalTiledMapRendererWithSprites;
    private TiledMap tiledMap;

    public static TileMapHandler getInstance(String fileName)
    {
        if (instance == null) {
            instance = new TileMapHandler(fileName);
        }
        return instance;
    }

    private TileMapHandler(String fileName)
    {
        TmxMapLoader mapLoader = new TmxMapLoader();
        this.tiledMap = mapLoader.load(fileName);
        this.orthogonalTiledMapRendererWithSprites = new OrthogonalTiledMapRendererWithSprites(
                tiledMap);
    }

    @Override
    public void dispose() {
        this.orthogonalTiledMapRendererWithSprites.dispose();
    }

    public OrthogonalTiledMapRendererWithSprites getOrthogonalTiledMapRenderer() {
        return this.orthogonalTiledMapRendererWithSprites;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }
}
