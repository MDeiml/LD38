package com.mdeiml.ld38;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;

public class Human {

	private static final float SPEED = 20;
	private static final float MIN_POS = 11;
	private static final float MAX_POS = 430;

	private Texture spriteSheet;
	private TextureRegion standRight;
	private TextureRegion standLeft;
	private Animation walkRight;
	private Animation walkLeft;
	private float position;
	private float aim;
	private boolean direction; // true = right, false = left
	private float walkTimer;
	private Building workBuilding;

    public Human(Texture spriteSheet) {
		this.spriteSheet = spriteSheet;
		TextureRegion[][] frames = TextureRegion.split(spriteSheet, 24, 24);
		standRight = frames[0][0];
		standLeft = frames[1][0];
		walkRight = new Animation(0.2f, frames[0]);
		walkLeft = new Animation(0.2f, frames[1]);
		position = MIN_POS;
		aim = MIN_POS;
		direction = true;
		walkTimer = 0;
		workBuilding = null;
	}

	public void walkTo(float x) {
		aim = Math.max(MIN_POS, Math.min(MAX_POS, x));
		workBuilding = null;
	}

	public void workAt(Building building) {
		aim = Building.BUILDINGS_OFFSET + (building.getSlot() + 0.5f) * Building.BUILDINGS_WIDTH;
		this.workBuilding = building;
	}

	public void render(SpriteBatch batch) {
		walkTimer += Gdx.graphics.getDeltaTime();
		if(position < aim) {
			position += SPEED * Gdx.graphics.getDeltaTime();
			position = Math.min(position, aim);
			direction = true;
			batch.draw(walkRight.getKeyFrame(walkTimer, true), position-12, 22);
		}else if(position > aim) {
			position -= SPEED * Gdx.graphics.getDeltaTime();
			position = Math.max(position, aim);
			direction = false;
			batch.draw(walkLeft.getKeyFrame(walkTimer, true), position-12, 22);
		}else {
			if(workBuilding != null) {
				workBuilding.use();
			}

			walkTimer = 0;
			if(direction) {
				batch.draw(standRight, position-12, 22);
			}else {
				batch.draw(standLeft, position-12, 22);
			}
		}
	}

	public float position() {
		return position;
	}

	public void position(float x) {
		position = x;
	}

}
