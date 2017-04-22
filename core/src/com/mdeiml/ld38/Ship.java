package com.mdeiml.ld38;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;

public class Ship {

    public static final float SPEED = 100;
    public static final float START_POSITION = -300;
    public static final float DOCK_POSITION = -50;
    public static final float CREW_INTERVAL = 2;

    private float position;
    private int numCrew;
    private Texture texture;
    private float crewTimer;
    private LD38 game;
    private Texture crewTexture;
    private boolean leave;
    private boolean dead;

    public Ship(Texture texture, Texture crewTexture, LD38 game) {
        this.texture = texture;
        this.crewTexture = crewTexture;
        this.game = game;
        numCrew = (int)(Math.random() * 3 + 2);
        position = START_POSITION;
        crewTimer = (float)(Math.random() * 1 + CREW_INTERVAL);
        leave = false;
        dead = false;
    }

    public void render(SpriteBatch batch) {
        if(leave) {
            boolean allOnBoard = true;
            for(Human h : game.humans) {
                if(h instanceof Pirate) {
                    allOnBoard = false;
                    break;
                }
            }
            if(allOnBoard) {
                if(position > START_POSITION) {
                    position -= SPEED * Gdx.graphics.getDeltaTime();
                }else {
                    dead = true;
                }
            }
        }else {
            if(position < DOCK_POSITION) {
                position += SPEED * Gdx.graphics.getDeltaTime();
            }else {
                position = DOCK_POSITION;
                if(numCrew > 0) {
                    crewTimer -= Gdx.graphics.getDeltaTime();
                    if(crewTimer <= 0) {
                        crewTimer += (float)(Math.random() * 1 + CREW_INTERVAL);
                        numCrew--;
                        game.humans.add(new Pirate(crewTexture, game));
                    }
                }
            }
        }
        batch.draw(texture, position, 0);
    }

    public void leave() {
        leave = true;
        for(Human h : game.humans) {
            if(h instanceof Pirate) {
                ((Pirate)h).leave();
            }
        }
    }

    public boolean dead() {
        return dead;
    }

}
