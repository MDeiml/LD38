package com.mdeiml.ld38;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;

public class Distillery extends Building {

    private static final float RUM_PER_SECOND = 1f/3f;

    private LD38 game;

    public Distillery(int slot, TextureRegion[] sprites, LD38 game) {
        super(sprites[3], slot);
        this.game = game;
    }

    public boolean needsWorker() {
        return true;
    }

    public void use() {
        game.rum += RUM_PER_SECOND * Gdx.graphics.getDeltaTime();
    }

}
