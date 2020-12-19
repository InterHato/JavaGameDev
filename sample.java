package com.kroy.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.kroy.game.ETFortress.ETFortress;
import com.kroy.game.FireTruck.FireStation;
import com.kroy.game.FireTruck.FireTruck;
import com.kroy.game.FireTruck.ListenerClass;
import com.kroy.game.screen.KroyScreen;
import com.kroy.game.screen.MenuScreen;
import com.kroy.game.screen.worldBox;

import java.util.ArrayList;

public class KroyGame extends Game {

	private boolean end;
	private Box2DDebugRenderer debugRenderer;
	public static World world = new World(new Vector2(0, 0), true);

	private SpriteBatch batch, fontBatch;
	public static String state;

	// FireStation's init variables
	private FireStation fireStation;

	// ETFortress' init variables
	private ArrayList<ETFortress> listETFortress;

	// Font init variable
	private BitmapFont font;

	@Override
	public void create() {
	    //state = "play";

		debugRenderer = new Box2DDebugRenderer();
		batch = new SpriteBatch();
		fontBatch = new SpriteBatch();

		world.setContactListener(new ListenerClass());

		// Creation of worldBoxes
		worldBox worldBox1 = new worldBox(0, 0, 0, 800);
		worldBox worldBox2 = new worldBox(0, 0, 800, 0);
		worldBox worldBox3 = new worldBox(800, 0, 0, 800);
		worldBox worldBox4 = new worldBox(0, 800, 800, 0);

		// Creation of FireStation
		fireStation = new FireStation(Gdx.graphics.getWidth() - 192, 0);

		// Creation of ETFortress' objects
		listETFortress = new ArrayList<ETFortress>();
		listETFortress.add(new ETFortress(50, 50, 100, 280, 1, 900));
		listETFortress.add(new ETFortress(200, 600, 100, 200, 1, 600));
		listETFortress.add(new ETFortress(700, 300, 100, 100, 1, 400));

		// Creation of text in screen
		font = new BitmapFont();
		font.getData();
		font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

		this.setScreen(new MenuScreen(this));
	}

	@Override
	public void dispose() {
		super.dispose();
		world.dispose();
	}

	@Override
	public void render() {
		super.render();
	    if (state == "play") {
	    	end = true;
			world.step(Gdx.graphics.getDeltaTime(), 8, 3);
			debugRenderer.render(world, KroyScreen.camera.combined);
			batch.setProjectionMatrix(KroyScreen.camera.combined);
			batch.begin();
			// FireStation function to render station
			fireStation.renderTextures(batch);

			// ETFortress function to render the fortress sprite
			for (ETFortress fort : listETFortress) { fort.renderFortTexture(batch); }
			FireTruck.getActiveTruck().setTimer(FireTruck.getActiveTruck().getTimer() + Gdx.graphics.getDeltaTime());

			for (ETFortress fort : listETFortress) {
				// ETFortress function to render fortress' gun actor
				fort.renderBulletTexture(batch);
				fort.render(batch);
				fort.removeBullet();

				// Adds delta time to fort's "timer" var. If timer num > than weapon's cooldown, run isAttackable() & reset timer
				fort.setTimer(fort.getTimer() + Gdx.graphics.getDeltaTime());
				//fort.removeBullet();

				if (fort.getTimer()*1000 >= fort.getWeaponCD() && fort.getTruckDistance() <= fort.getWeaponRange() && !(fort.getIsDestroyed())) {
					fort.fortAttack();
					fort.setTimer(0);
				}
				if (!(fort.getIsDestroyed())) {
					end = false;

				}
			}
			batch.end();

			fontBatch.begin();

			// Font text used as temporary logging tool
			font.draw(fontBatch, "Truck's health: " + FireStation.getActiveTruck().getHealth(), 10, 20);

			fontBatch.end();

			if (end) {
				this.setScreen(new MenuScreen(this));
				state = "menu";
			}
        }
	}

	@Override
	public void resize(int width, int height) { super.resize(width, height); }

	@Override
	public void pause() { super.pause(); }

	public void resume() { super.resume(); }


}