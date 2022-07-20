package com.mygdx.game.common.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;

public class AssetDescriptors {


    /**************************Texture atlasses**************************/
    public static final AssetDescriptor<TextureAtlas> BACK_GROUND =
            new AssetDescriptor<TextureAtlas>(AssetPaths.BACK_GROUND, TextureAtlas.class);

    public static final AssetDescriptor<TextureAtlas> PLAYER_ANIMATION =
            new AssetDescriptor<TextureAtlas>(AssetPaths.PLAYER_ANIMATION, TextureAtlas.class);

    public static final AssetDescriptor<TextureAtlas> FIRE_MAGIC_ANIMATION =
            new AssetDescriptor<TextureAtlas>(AssetPaths.FIRE_MAGIC_ANIMATION, TextureAtlas.class);

    public static final AssetDescriptor<TextureAtlas> FIRE_MAGIC_ANIMATION_LEFT =
            new AssetDescriptor<TextureAtlas>(AssetPaths.FIRE_MAGIC_ANIMATION_LEFT, TextureAtlas.class);

    public static final AssetDescriptor<TextureAtlas> ENEMY_ANIMATION =
            new AssetDescriptor<TextureAtlas>(AssetPaths.ENEMY_ANIMATION, TextureAtlas.class);

    public static final AssetDescriptor<TextureAtlas> WIZARD_ANIMATION =
            new AssetDescriptor<TextureAtlas>(AssetPaths.WIZARD_ANIMATION, TextureAtlas.class);

    public static final AssetDescriptor<TextureAtlas> UI_ATLAS_IN_GAME =
            new AssetDescriptor<>(AssetPaths.UI_ATLAS_IN_GAME, TextureAtlas.class);

    public static final AssetDescriptor<TextureAtlas> UI_CHARACTER_STATS =
            new AssetDescriptor<>(AssetPaths.UI_CHARACTER_STATS, TextureAtlas.class);

    /**************************Skins**************************/
    public static final AssetDescriptor<Skin> UI_SKIN =
            new AssetDescriptor<>(AssetPaths.UI_SKIN, Skin.class);

    public static final AssetDescriptor<Skin> UI_SKIN_IN_GAME =
            new AssetDescriptor<Skin>(AssetPaths.UI_SKIN_IN_GAME, Skin.class);

    public static final AssetDescriptor<TextureAtlas> UI_IN_GAME_BACKGROUNDS =
            new AssetDescriptor<>(AssetPaths.UI_IN_GAME_BACKGROUNDS, TextureAtlas.class);

    /**************************Fonts**************************/
    public static final AssetDescriptor<BitmapFont> FONT =
            new AssetDescriptor<BitmapFont>(AssetPaths.UI_FONT, BitmapFont.class);

    /**************************Music**************************/
    public static final AssetDescriptor<Music> BACKGROUND_MUSIC =
            new AssetDescriptor<Music>(AssetPaths.BACKGROUND_MUSIC, Music.class);


    /**************************Sound**************************/
    public static final AssetDescriptor<Sound> CLICK_SOUND=
            new AssetDescriptor<Sound>(AssetPaths.CLICK_BUTTON, Sound.class);

    private AssetDescriptors(){

    }
}
