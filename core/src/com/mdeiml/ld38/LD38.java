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
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// input
		Vector3 mousePos = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f));

		// cam movement
		if(mousePos.x - cam.position.x < -cam.viewportWidth/2 + MOUSE_MOVEMENT_BORDER) {
			cam.position.x -= CAMERA_SPEED * Gdx.graphics.getDeltaTime();
		}else if(mousePos.x - cam.position.x > cam.viewportWidth/2 - MOUSE_MOVEMENT_BORDER) {
			cam.position.x += CAMERA_SPEED * Gdx.graphics.getDeltaTime();
		}
		cam.position.x = Math.max(0, Math.min(background.getWidth(), cam.position.x));

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
				batch.draw(icons[1], x, 30);
				if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && mousePos.x > x && mousePos.x < x+16 && mousePos.y > 30 && mousePos.y < 46) {
					System.out.println("OK");
				}
			}
		}

		// player
		player.render(batch);
		if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			player.walkTo(mousePos.x);
		}

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
	}
}
