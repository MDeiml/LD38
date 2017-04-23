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
	private static final float SHIP_INTERVAL = 20;
	private static final float SHIP_STAY = 30;

	SpriteBatch batch;
	OrthographicCamera cam;
	OrthographicCamera guiCam;
	Building[] buildings;

	Texture waves;
	float waveTimer;

	Texture background;
	TextureRegion[][] icons;
	TextureRegion[] buildingSprites;
	TextureRegion[] digits;
	Texture guiBar;
	Texture shipTexture;
	Texture playerSprites;
	TextureRegion playerIcon;

	int buildMenu;

	boolean leftLast;
	boolean rightLast;

	public ArrayList<Human> humans;
	private Human player;

	public float wood;
	public float iron;
	public float rum;
	public float weapons;
	public float gold;

	private float shipTimer;
	private Ship ship;

	@Override
	public void create () {
		batch = new SpriteBatch();

		background = new Texture("background.png");
		icons = TextureRegion.split(new Texture("icons.png"), 16, 16);
		buildingSprites = TextureRegion.split(new Texture("buildings.png"), 50, 50)[0];
		digits = TextureRegion.split(new Texture("digits.png"), 3, 5)[0];
		guiBar = new Texture("gui_bar.png");
		shipTexture = new Texture("ship.png");
		playerSprites = new Texture("player.png");
		playerIcon = new TextureRegion(playerSprites, 0, 24*2, 24, 24);

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

		player = new Human(new TextureRegion(playerSprites, 0, 0, 24*4, 24*2));
		humans.add(player);
		humans.add(new Human(new TextureRegion(playerSprites, 0, 0, 24*4, 24*2)));

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

		shipTimer = -SHIP_INTERVAL;
		ship = null;
	}

	@Override
	public void resize(int width, int height) {
		float aspect = (float)width / (float)height;
		float widthF = aspect * CAM_HEIGHT;
		cam.setToOrtho(false, widthF, CAM_HEIGHT);
		guiCam.setToOrtho(false, widthF, CAM_HEIGHT);
		guiCam.position.x = widthF / 2;
		guiCam.position.y = -CAM_HEIGHT / 2;
		guiCam.update();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0xc1/(float)0xff, 0xcc/(float)0xff, 0xdd/(float)0xff, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// input
		Vector3 mousePos = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f));
		boolean leftClicked = Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !leftLast;
		boolean rightClicked = Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && !rightLast;
		if(Gdx.input.isKeyJustPressed(Input.Keys.F)) {
			if(Gdx.graphics.isFullscreen()) {
				Gdx.graphics.setWindowedMode(800, 600);
			}else {
				Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
			}
		}

		// cam movement
		if(mousePos.x - cam.position.x < -cam.viewportWidth/2 + MOUSE_MOVEMENT_BORDER) {
			cam.position.x -= CAMERA_SPEED * Gdx.graphics.getDeltaTime();
		}else if(mousePos.x - cam.position.x > cam.viewportWidth/2 - MOUSE_MOVEMENT_BORDER) {
			cam.position.x += CAMERA_SPEED * Gdx.graphics.getDeltaTime();
		}
		cam.position.x = Math.max(0, Math.min(background.getWidth(), cam.position.x));

		// player movement
		if(leftClicked) {
			for(Human h : humans) {
				if(!(h instanceof Pirate)) {
					if(mousePos.x > h.position()-12 && mousePos.x < h.position() + 12 && mousePos.y > 22 && mousePos.y < 22+24) {
						player = h;
						leftClicked = false;
						break;
					}
				}
			}
		}
		for(int i = 0; i < buildings.length; i++) {
			if(buildings[i] != null) {
				float x = Building.BUILDINGS_OFFSET + i * Building.BUILDINGS_WIDTH;
				if(rightClicked && mousePos.x > x && mousePos.x < x + Building.BUILDINGS_WIDTH && mousePos.y > 22 && mousePos.y < 72) {
					if(buildings[i].needsWorker()) {
						rightClicked = false;
						player.workAt(buildings[i]);
					}
				}
			}
		}
		if(rightClicked) {
			player.walkTo(mousePos.x);
		}

		cam.update();
		batch.setProjectionMatrix(cam.combined);

		batch.begin();

		// ship
		shipTimer += Gdx.graphics.getDeltaTime();
		if(shipTimer >= 0) {
			if(ship == null) {
				ship = new Ship(shipTexture, new TextureRegion(playerSprites, 0, 0, 24*4, 24*2), this);
			}
			if(shipTimer >= SHIP_STAY) {
				ship.leave();
			}
		}
		if(ship != null) {
			ship.render(batch);
			if(ship.dead()) {
				ship = null;
				shipTimer = -SHIP_INTERVAL;
			}
		}

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
									buildings[i] = new WoodChopper(i, buildingSprites, icons[2], this);
									break;
								case 1:
									buildings[i] = new Mine(i, buildingSprites, icons[2], this);
									break;
								case 2:
									buildings[i] = new House(i, buildingSprites);
									break;
								case 3:
									buildings[i] = new Distillery(i, buildingSprites, this);
									break;
								case 4:
									buildings[i] = new Bar(i, buildingSprites, icons[2]);
									break;
							}
							buildings[i] = new Construction(i, buildingSprites, icons[2], this, buildings[i], 2);
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
		for(int i = 0; i < humans.size(); i++) {
			Human h = humans.get(i);
			h.render(batch);
			if(h == player) {
				batch.draw(playerIcon, player.position()-12, 22+24);
			}
			if(h.dead()) {
				humans.remove(i);
				i--;
			}
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
