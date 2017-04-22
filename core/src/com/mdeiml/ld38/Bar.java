package com.mdeiml.ld38;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Bar extends Building {

    public Bar(int slot, TextureRegion[] sprites) {
        super(sprites[4], slot);
    }

    public boolean needsWorker() {
        return false;
    }

}
