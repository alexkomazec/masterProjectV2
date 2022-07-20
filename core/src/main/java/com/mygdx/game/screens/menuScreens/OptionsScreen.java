package com.mygdx.game.screens.menuScreens;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.common.GameManager;
import com.mygdx.game.common.assets.AssetDescriptors;
import com.mygdx.game.config.GameConfig;

public class OptionsScreen extends MenuScreenBase {

    private CheckBox gameMusic;
    private CheckBox gameSound;

    public OptionsScreen(MyGdxGame game) {
        super(game);
    }

    @Override
    protected Actor createUi() {
        Table table = new Table();
        table.defaults().pad(15);

        //Getting texture atlas from asset manager
        TextureAtlas backGround = assetManager.getResources(AssetDescriptors.BACK_GROUND);

        //Getting skin for all the menus
        Skin uiskin = assetManager.getResources(AssetDescriptors.UI_SKIN);

        gameMusic = new CheckBox(GameConfig.GAME_MUSIC, uiskin);
        gameMusic.setName(GameConfig.GAME_MUSIC);
        setCheckBox(gameMusic);

        gameSound = new CheckBox(GameConfig.GAME_SOUND, uiskin);
        gameSound.setName(GameConfig.GAME_SOUND);
        setCheckBox(gameSound);



        TextureRegion backgroundRegion = backGround.findRegion(GameConfig.BACKGROUND);
        table.setBackground(new TextureRegionDrawable(backgroundRegion));

        TextButton backButton = new TextButton("BACK", uiskin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CheckAndPlayMenuSound();
                back();
            }
        });


        gameMusic.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                muteUnmute(gameMusic);
            }
        });

        gameSound.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                muteUnmute(gameSound);
            }
        });

        // setup table
        Table contentTable = new Table(uiskin);
        contentTable.defaults().pad(10);
        contentTable.add(gameMusic).row();
        contentTable.add(gameSound).row();
        contentTable.add(backButton).row();

        table.add(contentTable);
        table.center();
        table.setFillParent(true);
        table.pack();

        return table;

    }

    void setCheckBox(CheckBox box)
    {
        if(box.getName().equals(GameConfig.GAME_SOUND)){
            box.setChecked(GameManager.INSTANCE.isGameSound());
        }
        else if(box.getName().equals(GameConfig.GAME_MUSIC)){
            box.setChecked(GameManager.INSTANCE.isGameMusic());
        }
    }

    void muteUnmute(CheckBox sound){

        if(!sound.isChecked()){
            sound.setChecked(false);
            GameManager.INSTANCE.updateSoundState(sound);

            if(GameManager.INSTANCE.isItGameMusic(sound)) {
                music.pause();
            }
        }
        else{
            sound.setChecked(true);
            GameManager.INSTANCE.updateSoundState(sound);

            if(GameManager.INSTANCE.isItGameMusic(sound)) {
                music.play();
            }
        }
    }
}
