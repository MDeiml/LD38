package com.mdeiml.ld38;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Shipyard extends Building {

    private static final float BUILD_TIME = 30;

    private LD38 game;
    private float timer;

    public Shipyard(int slot, TextureRegion[] sprites, TextureRegion[] toolSprites, LD38 game) {
        super(sprites[7], toolSprites[3], slot);
        this.game = game;
    }

    public boolean needsWorker() {
        return true;
    }

    public void use() {
        if(game.wood >= 40 && game.weapons >= 20) {
            timer += Gdx.graphics.getDeltaTime();
            if(timer > BUILD_TIME) {
                timer = 0;
                game.wood -= 40;
                game.weapons -= 20;
            }
        }else {
            timer = 0;
        }
    }

}
