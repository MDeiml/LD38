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
        timer = 0;
    }

    public boolean needsWorker() {
        return true;
    }

    public void use() {
        if(timer == 0) {
            if(game.wood >= 40 && game.weapons >= 20) {
                timer = BUILD_TIME;
                game.wood -= 40;
                game.weapons -= 20;
            }else {
                for(Human h : game.humans) {
                    if(h.getWorkBuilding() == this) {
                        h.workAt(null);
                    }
                }
            }
        }else if (timer > 0){
            timer -= Gdx.graphics.getDeltaTime();
            if(timer < 0) {
                timer = 0;
                for(Human h : game.humans) {
                    if(h.getWorkBuilding() == this) {
                        h.workAt(null);
                    }
                }
            }
        }
    }

}
