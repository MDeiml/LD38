package com.mdeiml.ld38;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;

public class Pirate extends Human {

    public static final float DRINK_TIME = 3;

    private LD38 game;
    private int thirst;
    private float drinkTimer;
    private float waitTimer;

    public Pirate(Texture texture, LD38 game) {
        super(texture);
        this.game = game;
        this.thirst = (int)(Math.random()*2+1);
        drinkTimer = 0;
        waitTimer = 0;
    }

    public void render(SpriteBatch batch) {
        if(thirst > 0 && game.rum >= 1) {
            for(int i = 0; i < game.buildings.length; i++) {
                if(game.buildings[i] instanceof Bar) {
                    workAt(game.buildings[i]);
                }
            }
            if(aim() == position()) {
                drinkTimer += Gdx.graphics.getDeltaTime();
                System.out.println(drinkTimer);
                if(drinkTimer >= DRINK_TIME) {
                    drinkTimer -= DRINK_TIME;
                    thirst--;
                    game.rum = game.rum - 1;
                }
            }
            waitTimer = 0;
        }else {
            if(waitTimer <= 0) {
                walkTo((float)(Math.random() * 100 - 50 + position())*0.9f + (MAX_POS + MIN_POS) / 2 * 0.1f);
                waitTimer = (float)(Math.random() * 3 + 1);
            }else {
                if(position() == aim()) {
                    waitTimer -= Gdx.graphics.getDeltaTime();
                }
            }
            drinkTimer = 0;
        }
        super.render(batch);
    }

}
