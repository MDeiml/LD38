package com.mdeiml.ld38;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PlayerShip {

    private float position;
    private Texture texture;

    public PlayerShip(Texture texture, float startPosition) {
        position = startPosition;
        this.texture = texture;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position, 0);
    }

}
