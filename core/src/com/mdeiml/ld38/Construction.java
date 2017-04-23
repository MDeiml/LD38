package com.mdeiml.ld38;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;

public class Construction extends Building {

    private LD38 game;
    private Building building;
    private float timer;

    public Construction(int slot, TextureRegion[] sprites, TextureRegion[] toolSprites, LD38 game, Building building, float buildTime) {
        super(sprites[5], toolSprites[3], slot);
        this.game = game;
        this.building = building;
        this.timer = buildTime;
    }

    public boolean needsWorker() {
        return true;
    }

    public void use() {
        timer -= Gdx.graphics.getDeltaTime();
        if(timer < 0) {
            game.buildings[getSlot()] = building;
            for(Human h : game.humans) {
                if(h.getWorkBuilding() == this) {
                    if(building.needsWorker()) {
                        h.workAt(building);
                    }else {
                        h.workAt(null);
                    }
                }
            }
        }
    }

}
