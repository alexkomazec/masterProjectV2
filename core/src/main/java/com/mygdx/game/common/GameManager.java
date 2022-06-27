package com.mygdx.game.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.config.GameConfig;

public class GameManager {

    public static final GameManager INSTANCE = new GameManager();

    private static final String GAME_SOUND_KEY = "game_sound_muted";
    private static final String GAME_MUSIC_KEY = "game_music_muted";
    private static final String DIFFICULTY_KEY = "difficulty";

    private Preferences PREFS;

    private boolean gameSound;
    private boolean gameMusic;
    private DifficultyLevel difficultyLevel = DifficultyLevel.MEDIUM;

    private GameManager() {
        PREFS = Gdx.app.getPreferences(MyGdxGame.class.getSimpleName());
        gameSound = PREFS.getBoolean(GAME_SOUND_KEY, false);
        gameMusic = PREFS.getBoolean(GAME_MUSIC_KEY, false);

        String difficultyName = PREFS.getString(DIFFICULTY_KEY, DifficultyLevel.MEDIUM.name());
        difficultyLevel = DifficultyLevel.valueOf(difficultyName);
    }

    // == persistent Music/Sound stuff ==
    public void updateSoundState(CheckBox box){

        if(isItGameSound(box)){
            gameSound = box.isChecked();
            PREFS.putBoolean(GAME_SOUND_KEY, gameSound);
            PREFS.flush();
        }
        else if(isItGameMusic(box)){
            gameMusic = box.isChecked();
            PREFS.putBoolean(GAME_MUSIC_KEY, gameMusic);
            PREFS.flush();
        }
    }

    public boolean isGameSound() {
        return gameSound;
    }

    public boolean isGameMusic() {
        return gameMusic;
    }

    public boolean isItGameMusic(CheckBox sound){
        return sound.getName().equals(GameConfig.GAME_MUSIC);
    }

    public boolean isItGameSound(CheckBox sound){
        return sound.getName().equals(GameConfig.GAME_SOUND);
    }

    // == persistent Game Difficulty stuff ==
    public void updateDifficulty(DifficultyLevel newDifficultyLevel) {
        if(difficultyLevel == newDifficultyLevel) {
            return;
        }

        difficultyLevel = newDifficultyLevel;
        PREFS.putString(DIFFICULTY_KEY, difficultyLevel.name());
        PREFS.flush();
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }
}
