package com.mdeiml.ld38;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class WoodChopper extends Building {

    private static final float WOOD_PER_SECOND = 2f/3f;

    private LD38 game;

    public WoodChopper(int slot, LD38 game) {
        super(new TextureRegion(new Texture("badlogic.jpg"), 0, 0, 50, 50), slot);
        this.game = game;
    }

    public boolean needsWorker() {
        return true;
    }

    public void use() {
        game.wood += WOOD_PER_SECOND * Gdx.graphics.getDeltaTime();
    }

}
