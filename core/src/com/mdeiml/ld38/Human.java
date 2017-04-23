package com.mdeiml.ld38;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;

public class Human {

	private static final float SPEED = 20;
	public static final float MIN_POS = 11;
	public static final float MAX_POS = 430;
	private static final float TOOL_USE = 1f;
	private static final float TOOL_IDLE = 1f;

	private TextureRegion spriteSheet;
	private TextureRegion standRight;
	private TextureRegion standLeft;
	private Animation walkRight;
	private Animation walkLeft;
	private float position;
	private float aim;
	private boolean direction; // true = right, false = left
	private float walkTimer;
	private Building workBuilding;
	private boolean dead;
	private float toolTimer;

    public Human(TextureRegion spriteSheet) {
		this.spriteSheet = spriteSheet;
		TextureRegion[][] frames = spriteSheet.split(24, 24);
		standRight = frames[0][0];
		standLeft = frames[1][0];
		walkRight = new Animation(0.2f, frames[0]);
		walkLeft = new Animation(0.2f, frames[1]);
		position = MIN_POS;
		aim = MIN_POS;
		direction = true;
		walkTimer = 0;
		toolTimer = 0;
		workBuilding = null;
		dead = false;
	}

	public int getTextureY() {
		System.out.println(spriteSheet.getRegionY());
		return spriteSheet.getRegionY();
	}

	public boolean dead() {
		return dead;
	}

	public void die() {
		dead = true;
	}

	public void walkTo(float x) {
		aim = Math.max(MIN_POS, Math.min(MAX_POS, x));
		workBuilding = null;
	}

	public void workAt(Building building) {
		if(building == null) {
			workBuilding = null;
			return;
		}
		aim = Building.BUILDINGS_OFFSET + (building.getSlot() + 0.5f) * Building.BUILDINGS_WIDTH;
		this.workBuilding = building;
	}

	public Building getWorkBuilding() {
		return workBuilding;
	}

	public void render(SpriteBatch batch) {
		walkTimer += Gdx.graphics.getDeltaTime();
		if(position < aim) {
			toolTimer = 0;
			position += SPEED * Gdx.graphics.getDeltaTime();
			position = Math.min(position, aim);
			direction = true;
			batch.draw(walkRight.getKeyFrame(walkTimer, true), position-12, 22);
		}else if(position > aim) {
			toolTimer = 0;
			position -= SPEED * Gdx.graphics.getDeltaTime();
			position = Math.max(position, aim);
			direction = false;
			batch.draw(walkLeft.getKeyFrame(walkTimer, true), position-12, 22);
		}else {
			walkTimer = 0;
			if(workBuilding != null) {
				workBuilding.use();
			}
			if(workBuilding != null) {
				batch.draw(standRight, position-12, 22);
				if(workBuilding.getTool() != null) {
					toolTimer += Gdx.graphics.getDeltaTime();
					float angle = 0;
					if(toolTimer > 0) {
						if(toolTimer < TOOL_USE / 2) {
							angle = toolTimer / (TOOL_USE / 2) * 90f;
						}else if(toolTimer < TOOL_USE) {
							angle = 90f - (toolTimer / (TOOL_USE / 2) - 1) * 90f;
						}else {
							toolTimer = -TOOL_IDLE;
						}
					}
					batch.draw(workBuilding.getTool(), position, 21, 3, 8, 16, 16, 1, 1, angle);
				}
			}else {
				if(direction) {
					batch.draw(standRight, position-12, 22);
				}else {
					batch.draw(standLeft, position-12, 22);
				}
			}
		}
	}

	public float position() {
		return position;
	}

	public float aim() {
		return aim;
	}

	public void position(float x) {
		position = x;
	}

}
