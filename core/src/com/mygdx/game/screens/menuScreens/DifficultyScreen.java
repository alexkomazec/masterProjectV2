package com.mygdx.game.screens.menuScreens;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.common.DifficultyLevel;
import com.mygdx.game.common.GameManager;
import com.mygdx.game.common.assets.AssetDescriptors;
import com.mygdx.game.config.GameConfig;

public class DifficultyScreen extends MenuScreenBase {

    private ButtonGroup<CheckBox> checkBoxGroup;
    private CheckBox easy;
    private CheckBox medium;
    private CheckBox hard;

    public DifficultyScreen(MyGdxGame game) {
        super(game);
    }

    @Override
    protected Actor createUi() {

        Table table = new Table();
        table.defaults().pad(15);

        TextureAtlas background = assetManager.getResource(AssetDescriptors.BACK_GROUND);
        Skin uiSkin = assetManager.getResource(AssetDescriptors.UI_SKIN);

        TextureRegion backgroundRegion = background.findRegion(GameConfig.BACKGROUND);
        table.setBackground(new TextureRegionDrawable(backgroundRegion));

        // label
        Label label = new Label("DIFFICULTY", uiSkin);

        easy = new CheckBox(DifficultyLevel.EASY.name(), uiSkin);
        medium = new CheckBox(DifficultyLevel.MEDIUM.name(), uiSkin);
        hard = new CheckBox(DifficultyLevel.HARD.name(), uiSkin);

        checkBoxGroup = new ButtonGroup<CheckBox>(easy, medium, hard);

        final DifficultyLevel difficultyLevel = GameManager.INSTANCE.getDifficultyLevel();
        checkBoxGroup.setChecked(difficultyLevel.name());

        TextButton backButton = new TextButton("BACK", uiSkin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                back();
            }
        });

        ChangeListener listener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                difficultyChanged();
                CheckAndPlayMenuSound();
            }
        };

        easy.addListener(listener);
        medium.addListener(listener);
        hard.addListener(listener);

        // setup table
        Table contentTable = new Table(uiSkin);
        contentTable.defaults().pad(10);

        contentTable.add(label).row();
        contentTable.add(easy).row();
        contentTable.add(medium).row();
        contentTable.add(hard).row();
        contentTable.add(backButton);

        table.add(contentTable);
        table.center();
        table.setFillParent(true);
        table.pack();

        return table;
    }

    private void back() {
        CheckAndPlayMenuSound();
        game.changeScreen(MyGdxGame.MENU_SCREEN);
    }

    private void difficultyChanged() {
        CheckBox checked = checkBoxGroup.getChecked();

        if (checked == easy) {
            GameManager.INSTANCE.updateDifficulty(DifficultyLevel.EASY);
        } else if (checked == medium) {
            GameManager.INSTANCE.updateDifficulty(DifficultyLevel.MEDIUM);
        } else if (checked == hard) {
            GameManager.INSTANCE.updateDifficulty(DifficultyLevel.HARD);
        }
    }
}
