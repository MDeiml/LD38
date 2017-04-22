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
import java.util.ArrayList;

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
	OrthographicCamera guiCam;
	Human player;
	ArrayList<Human> humans;
	Building[] buildings;

	Texture waves;
	float waveTimer;

	Texture background;
	TextureRegion[][] icons;
	TextureRegion[] buildingSprites;
	TextureRegion[] digits;
	Texture guiBar;

	int buildMenu;

	boolean leftLast;
	boolean rightLast;

	public float wood;
	public float iron;
	public float rum;
	public float weapons;
	public float gold;

	@Override
	public void create () {
		batch = new SpriteBatch();

		background = new Texture("background.png");
		icons = TextureRegion.split(new Texture("icons.png"), 16, 16);
		buildingSprites = TextureRegion.split(new Texture("buildings.png"), 50, 50)[0];
		digits = TextureRegion.split(new Texture("digits.png"), 3, 5)[0];
		guiBar = new Texture("gui_bar.png");

		float aspect = (float)Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight();
		float width = aspect * CAM_HEIGHT;
		cam = new OrthographicCamera(width, CAM_HEIGHT);
		cam.position.y = CAM_HEIGHT / 2;
		guiCam = new OrthographicCamera(width, CAM_HEIGHT);
		guiCam.position.y = -CAM_HEIGHT / 2;
		guiCam.position.x = width / 2;
		guiCam.update();

		waves = new Texture("waves.png");
		waveTimer = 0;

		humans = new ArrayList<Human>();

		player = new Human(new Texture("player.png"));
		humans.add(player);

		humans.add(new Pirate(new Texture("player.png"), this));

		buildings = new Building[7];
		for(int i = 0; i < 7; i++) {
			buildings[i] = null;
		}
		buildMenu = -1;

		leftLast = false;
		rightLast = false;

		wood = 0;
		iron = 0;
		rum = 0;
		weapons = 0;
		gold = 0;
	}

	@Override
	public void render () {
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
					if(buildings[i].needsWorker()) {
						playerMove = false;
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
									buildings[i] = new WoodChopper(i, buildingSprites, this);
									break;
								case 1:
									buildings[i] = new Mine(i, buildingSprites, this);
									break;
								case 2:
									buildings[i] = new House(i, buildingSprites);
									break;
								case 3:
									buildings[i] = new Distillery(i, buildingSprites, this);
									break;
								case 4:
									buildings[i] = new Bar(i, buildingSprites);
									break;
							}
						}
						batch.draw(icons[0][j+2], x1, 48);
					}
					batch.draw(icons[0][1], x, 30);
				}else {
					batch.draw(icons[0][0], x, 30);
				}
			}
		}

		// humans
		for(Human h : humans) {
			h.render(batch);
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

		// gui
		batch.setProjectionMatrix(guiCam.combined);
		batch.begin();

		batch.draw(guiBar, 0, -11, guiCam.viewportWidth, 11);

		// wood
		batch.draw(icons[1][1], 1, -9, 16, 16);
		Utils.drawNumber((int)wood, 11, -8, batch, digits);

		// iron
		batch.draw(icons[1][2], 25, -9, 16, 16);
		Utils.drawNumber((int)iron, 34, -8, batch, digits);

		// rum
		batch.draw(icons[1][3], 47, -9, 16, 16);
		Utils.drawNumber((int)rum, 57, -8, batch, digits);

		// weapons
		batch.draw(icons[1][0], 70, -9, 16, 16);
		Utils.drawNumber((int)weapons, 79, -8, batch, digits);

		// gold
		batch.draw(icons[1][4], 92, -9, 16, 16);
		Utils.drawNumber((int)gold, 101, -8, batch, digits);

		batch.end();

		leftLast = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
		rightLast = Gdx.input.isButtonPressed(Input.Buttons.RIGHT);
	}

}
