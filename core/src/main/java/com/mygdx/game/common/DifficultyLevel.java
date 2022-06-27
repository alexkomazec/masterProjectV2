package com.mygdx.game.common;

import com.mygdx.game.config.GameConfig;

public enum DifficultyLevel {

    EASY(GameConfig.ENEMY_DPS_EASY,GameConfig.ENEMY_HP_EASY,GameConfig.ENEMY_SPEED_EASY,GameConfig.SURVIVAL_DIFFICULTY_EASY),
    MEDIUM(GameConfig.ENEMY_DPS_NORMAL,GameConfig.ENEMY_HP_NORMAL,GameConfig.ENEMY_SPEED_NORMAL,GameConfig.SURVIVAL_DIFFICULTY_NORMAL),
    HARD(GameConfig.ENEMY_DPS_HARD,GameConfig.ENEMY_HP_HARD,GameConfig.ENEMY_SPEED_HARD,GameConfig.SURVIVAL_DIFFICULTY_HARD);

    private final float enemyDPS;
    private final float enemyHP;
    private final float enemySPEED;
    private final float survivalDifficulty;

    DifficultyLevel(float enemyDPS, float enemyHP, float enemySPEED, float survivalDifficulty) {
        this.enemyDPS = enemyDPS;
        this.enemyHP = enemyHP;
        this.enemySPEED = enemySPEED;
        this.survivalDifficulty = survivalDifficulty;
    }

    public boolean isEasy() {
        return this == EASY;
    }

    public boolean isMedium() {
        return this == MEDIUM;
    }

    public boolean isHard() {
        return this == HARD;
    }
}