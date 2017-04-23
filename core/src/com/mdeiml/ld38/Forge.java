package com.mdeiml.ld38;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;

public class Forge extends Building {

    private static final float WEAPONS_PER_SECOND = 1f/3f;

    private LD38 game;
    private float timer;

    public Forge(int slot, TextureRegion[] sprites, TextureRegion[] toolSprites, LD38 game) {
        super(sprites[6], toolSprites[3], slot);
        this.game = game;
        timer = 0;
    }

    public boolean needsWorker() {
        return true;
    }

    public boolean use() {
        if(game.iron >= 1) {
            timer += Gdx.graphics.getDeltaTime() * WEAPONS_PER_SECOND;
        }else {
            timer = 0;
        }
        if(timer >= 1) {
            game.iron--;
            game.weapons++;
            timer = 0;
        }
        return game.iron >= 1;
    }

}
