package com.mdeiml.ld38;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class WoodChopper extends Building {

    private static final float WOOD_PER_SECOND = 2f/3f;

    private LD38 game;

    public WoodChopper(int slot, TextureRegion[] sprites, TextureRegion[] toolSprites, LD38 game) {
        super(sprites[1], toolSprites[1], slot);
        this.game = game;
    }

    public boolean needsWorker() {
        return true;
    }

    public void use() {
        game.wood += WOOD_PER_SECOND * Gdx.graphics.getDeltaTime();
    }

}
