package com.mdeiml.ld38;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class House extends Building {

    public House(int slot, TextureRegion[] sprites) {
        super(sprites[0], slot);
    }

    public boolean needsWorker() {
        return false;
    }

}
