package com.mdeiml.ld38;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.Input;

public class LD38 extends ApplicationAdapter {

	private static final float CAM_HEIGHT = 150;
	private static final float WAVE_SPEED = 3;
	private static final float WAVE_BOB_HEIGHT = 5;
	private static final float WAVE_BOB_OFFSET = 1;
	private static final float WAVE_HEIGHT_OFFSET = 5;
	private static final int MOUSE_MOVEMENT_BORDER = 10;
	private static final float CAMERA_SPEED = 120;

	SpriteBatch batch;
	OrthographicCamera cam;
	Human player;
	Building[] buildings;

	Texture waves;
	float waveTimer;

	Texture background;
	TextureRegion[] icons;

	int buildMenu;

	boolean leftLast;
	boolean rightLast;

	public float wood;

	@Override
	public void create () {
		batch = new SpriteBatch();

		background = new Texture("background.png");
		icons = TextureRegion.split(new Texture("icons.png"), 16, 16)[0];

		float aspect = (float)Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight();
		float width = aspect * CAM_HEIGHT;
		cam = new OrthographicCamera(width, CAM_HEIGHT);
		cam.position.y = CAM_HEIGHT / 2;

		waves = new Texture("waves.png");
		waveTimer = 0;

		player = new Human(new Texture("player.png"));

		buildings = new Building[7];
		for(int i = 0; i < 7; i++) {
			buildings[i] = null;
		}
		buildMenu = -1;

		leftLast = false;
		rightLast = false;
	}

	@Override
	public void render () {
		// c1ccdd
		Gdx.gl.glClearColor(0xc1/(float)0xff, 0xcc/(float)0xff, 0xdd/(float)0xff, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// input
		Vector3 mousePos = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f));
		boolean leftClicked = Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !leftLast;
		boolean rightClicked = Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && !rightLast;

		// cam movement
		if(mousePos.x - cam.position.x < -cam.viewportWidth/2 + MOUSE_MOVEMENT_BORDER) {
			cam.position.x -= CAMERA_SPEED * Gdx.graphics.getDeltaTime();
		}else if(mousePos.x - cam.position.x > cam.viewportWidth/2 - MOUSE_MOVEMENT_BORDER) {
			cam.position.x += CAMERA_SPEED * Gdx.graphics.getDeltaTime();
		}
		cam.position.x = Math.max(0, Math.min(background.getWidth(), cam.position.x));

		// player movement
		boolean playerMove = rightClicked;
		for(int i = 0; i < buildings.length; i++) {
			if(buildings[i] != null) {
				float x = Building.BUILDINGS_OFFSET + i * Building.BUILDINGS_WIDTH;
				if(rightClicked && mousePos.x > x && mousePos.x < x + Building.BUILDINGS_WIDTH && mousePos.y > 22 && mousePos.y < 72) {
					playerMove = false;
					if(buildings[i].needsWorker()) {
						player.workAt(buildings[i]);
					}
				}
			}
		}
		if(playerMove) {
			player.walkTo(mousePos.x);
		}

		cam.update();
		batch.setProjectionMatrix(cam.combined);

		batch.begin();

		// background
		batch.draw(background, 0, 0);

		// buildings
		for(int i = 0; i < buildings.length; i++) {
			if(buildings[i] != null) {
				buildings[i].render(batch);
			}else {
				float x = Building.BUILDINGS_OFFSET + (i+0.5f) * Building.BUILDINGS_WIDTH - 8;
				if(leftClicked && mousePos.x > x && mousePos.x < x+16 && mousePos.y > 30 && mousePos.y < 46) {
					if(buildMenu == i) {
						buildMenu = -1;
					}else {
						buildMenu = i;
					}
				}
				if(buildMenu == i) {
					for(int j = 0; j < 7; j++) {
						float x1 = x + 8 - 18 * 7 / 2f + 1 + j * 18;
						if(leftClicked && mousePos.x > x1 && mousePos.x < x1+16 && mousePos.y > 48 && mousePos.y < 64) {
							buildMenu = -1;
							switch(j) {
								case 0:
									buildings[i] = new WoodChopper(j, this);
									break;
							}
						}
						batch.draw(icons[j+2], x1, 48);
					}
					batch.draw(icons[1], x, 30);
				}else {
					batch.draw(icons[0], x, 30);
				}
			}
		}

		// player
		player.render(batch);

		// waves
		waveTimer += Gdx.graphics.getDeltaTime();
		for(int j = 0; j < 2; j++) {
			float wavePos = (j % 2 == 0 ? 1 : -1) * (waveTimer % waves.getWidth() * WAVE_SPEED);
			float waveStart = cam.position.x - cam.viewportWidth / 2f - wavePos;
			float waveOffset = waveStart % waves.getWidth();
			if(waveOffset < 0) {
				waveOffset += waves.getWidth();
			}
			waveStart -= waveOffset;
			float waveEnd = cam.position.x + cam.viewportWidth / 2f - wavePos;
			int numWaves = (int)((waveEnd - waveStart) / waves.getWidth()) + 1;
			for(int i = 0; i < numWaves; i++) {
				float x = waveStart + i * waves.getWidth() + wavePos;
				float y = (float)(Math.sin(waveTimer + j * WAVE_BOB_OFFSET) * WAVE_BOB_HEIGHT/2 - WAVE_BOB_HEIGHT/2) - j * WAVE_HEIGHT_OFFSET;
				batch.draw(waves, x, y);
			}
		}

		batch.end();

		leftLast = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
		rightLast = Gdx.input.isButtonPressed(Input.Buttons.RIGHT);
	}
}
