package com.mdeiml.ld38;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import java.util.ArrayList;

public class PlayerShip extends Building {

    public static final float START_POS = 440;
    private static final float LEAVE_POS = 600;
    private static final float SPEED = 20;
    private static final float STAY_AWAY_TIME = 60;

    private float position;
    private Texture texture;
    private boolean leave;
    private boolean canBoard;
    private ArrayList<Human> crew;
    private LD38 game;
    private float awayTimer;

    public PlayerShip(Texture texture, float startPosition, LD38 game) {
        super(null, 10);
        position = startPosition;
        this.texture = texture;
        leave = false;
        crew = new ArrayList<Human>();
        this.game = game;
        awayTimer = 0;
    }

    public void render(SpriteBatch batch) {
        if(!leave) {
            if(position < START_POS) {
                position += SPEED * Gdx.graphics.getDeltaTime();
                if(position >= START_POS) {
                    position = START_POS;
                }
            }else if(position > START_POS) {
                position -= SPEED * Gdx.graphics.getDeltaTime();
                if(position <= START_POS) {
                    position = START_POS;
                }
            }else {
                if(!crew.isEmpty()) {
                    game.gold += (int)(20 + Math.random() * 5);
                    for(int i = 0; i < crew.size(); i++) {
                        game.humans.add(crew.get(i));
                    }
                    crew.clear();
                }
            }
            canBoard = position == START_POS;
        }else {
            if(position < LEAVE_POS) {
                position += Gdx.graphics.getDeltaTime() * SPEED;
            }else {
                awayTimer += Gdx.graphics.getDeltaTime();
                if(awayTimer > STAY_AWAY_TIME) {
                    awayTimer = 0;
                    leave = false;
                }
            }
        }
        batch.draw(texture, position, 0);
    }

    public boolean canBoard() {
        return canBoard;
    }

    public boolean needsWorker() {
        return true;
    }

    public boolean use() {
        for(int i = 0; i < game.humans.size(); i++) {
            Human h = game.humans.get(i);
            if(h.getWorkBuilding() == this && h.aim() == h.position()) {
                game.humans.remove(i);
                i--;
                crew.add(h);
                h.workAt(null);
                leave = true;
                if(game.player == h) {
                    game.player = null;
                }
            }
        }
        return false;
    }

}
