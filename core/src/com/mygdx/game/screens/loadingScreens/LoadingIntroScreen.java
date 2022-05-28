package com.mygdx.game.screens.loadingScreens;

import com.mygdx.game.MyGdxGame;
import com.mygdx.game.common.assets.AssetDescriptors;

public class LoadingIntroScreen extends LoadingScreenBase {

    // == constructors ==
    public LoadingIntroScreen(MyGdxGame game) {
        super(game);
    }

    // == public methods ==
    @Override
    public void show() {
        super.show();
        game.getAssetManagmentHandler().loadResource(
                AssetDescriptors.BACKGROUND_MUSIC,
                AssetDescriptors.CLICK_SOUND,
                AssetDescriptors.FONT,
                AssetDescriptors.BACK_GROUND,
                AssetDescriptors.UI_SKIN
        );
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
