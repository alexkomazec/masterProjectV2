package com.mygdx.game.screens.loadingScreens;

import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.common.assets.AssetDescriptors;
import com.mygdx.game.common.assets.AssetPaths;

public class LoadingIntroScreen extends LoadingScreenBase {

    // == constructors ==
    public LoadingIntroScreen(MyGdxGame game) {
        super(game);
    }

    // == public methods ==
    @Override
    public void show() {
        super.show();
        game.getAssetManagmentHandler().loadResources(
                AssetDescriptors.BACKGROUND_MUSIC,
                AssetDescriptors.CLICK_SOUND,
                AssetDescriptors.FONT,
                AssetDescriptors.BACK_GROUND,
                AssetDescriptors.UI_SKIN,
                AssetDescriptors.PLAYER_ANIMATION,
                AssetDescriptors.FIRE_MAGIC_ANIMATION,
                AssetDescriptors.FIRE_MAGIC_ANIMATION_LEFT,
                AssetDescriptors.ENEMY_ANIMATION,
                AssetDescriptors.WIZARD_ANIMATION,
                AssetDescriptors.UI_CHARACTER_STATS,
                AssetDescriptors.UI_ATLAS
        );

        game.getAssetManagmentHandler().loadResources(
                AssetDescriptors.UI_IN_GAME_BACKGROUNDS);

        ObjectMap<String, Object> resources = new ObjectMap<>();
        TextureAtlas uiInGameTextureAtlas = assetManager.getResources(AssetDescriptors.UI_ATLAS);
        game.setUIAtlas(uiInGameTextureAtlas);

        for(int i = 0; i <uiInGameTextureAtlas.getRegions().size;i++){
            TextureAtlas.AtlasRegion region = uiInGameTextureAtlas.getRegions().get(i);
            resources.put(region.name,new TextureRegion(region));
        }

        SkinLoader.SkinParameter skinParameter = new SkinLoader.SkinParameter(AssetPaths.UI_IN_GAME_BACKGROUNDS,resources);
        assetManager.loadResource(AssetDescriptors.UI_SKIN.fileName, Skin.class,skinParameter);

        this.game.setUiSkin(assetManager.getAssetManager().get(AssetPaths.UI_SKIN));
        //this.game.setUiInGameSkin(assetManager.getAssetManager().get(AssetPaths.UI_SKIN_IN_GAME));
        this.game.setUiCharacterAtlas();

        //this.game.setUiInGameBackgrounds(assetManager.getResources(AssetDescriptors.UI_IN_GAME_BACKGROUNDS));
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if(changeScreen) {
            game.changeScreen(MyGdxGame.MENU_SCREEN);
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width,height);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

}
