package com.mygdx.game.common;

import com.badlogic.gdx.physics.box2d.Fixture;

public class FixturePair {

    public Fixture fa;
    public Fixture fb;

    FixturePair(Fixture fa,  Fixture fb)
    {
        this.fa = fa;
        this.fb = fb;
    }
}
