package com.mdeiml.ld38;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Mine extends Building {

    private static final float IRON_PER_SECOND = 1f/3f;

    private LD38 game;

    public Mine(int slot, TextureRegion[] sprites, TextureRegion[] toolSprites, LD38 game) {
        super(sprites[2], toolSprites[2], slot);
        this.game = game;
    }

    public boolean needsWorker() {
        return true;
    }

    public boolean use() {
        game.iron += IRON_PER_SECOND * Gdx.graphics.getDeltaTime();
        return true;
    }

}
