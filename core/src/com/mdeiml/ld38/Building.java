package com.mdeiml.ld38;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class Building {

    public static final float BUILDINGS_OFFSET = 70;
    public static final float BUILDINGS_WIDTH = 50;

    private TextureRegion sprite;
    private int slot;

    public Building(TextureRegion sprite, int slot) {
        this.sprite = sprite;
        this.slot = slot;
    }

    public void render(SpriteBatch batch) {
        batch.draw(sprite, BUILDINGS_OFFSET + slot * BUILDINGS_WIDTH, 22);
    }

    public abstract boolean needsWorker();

    public void use() {}

    public int getSlot() {
        return slot;
    }

}
