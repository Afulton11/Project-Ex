package com.main.screens;

import static com.main.utils.Constants.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import com.main.Main;
import com.main.entities.Player;


public class PlayScreen implements Screen {
	
	private static boolean DEBUG = true;
	
	private final Main game;
	
	private long lastUpdateTime;
	
	private OrthographicCamera camera;
	private OrthographicCamera hudCam;
	private Box2DDebugRenderer b2dr;
	
	//Box2D world that handles all box2D physics.
	private World world;
	private Player player;
	
	private Texture hudTex;
	
	private TiledMap tiledMap;
	private TiledMapRenderer mapRenderer;
	
	public PlayScreen(final Main game) {
		this.game = game;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Main.V_WIDTH, Main.V_HEIGHT);
		hudCam = new OrthographicCamera();
		hudCam.setToOrtho(false, Main.V_WIDTH, Main.V_HEIGHT);
		
		world = new World(new Vector2(0, 0), false);
		b2dr = new Box2DDebugRenderer();
		
	}
	
	@Override
	public void show() {

		tiledMap = game.assets.get("maps/map1.tmx", TiledMap.class);
		mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
		lastUpdateTime = 0;
		hudTex = game.assets.get("imgs/hud.png", Texture.class);
		player = new Player(createBox(Main.V_WIDTH / 2, Main.V_HEIGHT / 2, 16, 16, BodyDef.BodyType.DynamicBody), game.assets);
		createBox(Main.V_WIDTH / 2, Main.V_HEIGHT / 2 - 64, 128, 32, BodyDef.BodyType.StaticBody);
	}

	@Override
	public void render(float delta) {
		if(TimeUtils.nanoTime() - lastUpdateTime > Main.UPDATE_INTERVALS) {
			lastUpdateTime = TimeUtils.nanoTime();
			update(delta);
		}
		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		mapRenderer.setView(camera);
		mapRenderer.render();
		
		game.batch.setProjectionMatrix(camera.combined);
		player.render(game.batch);
		if(DEBUG) 
			b2dr.render(world, camera.combined.scl(PPM));
		
		
		
		game.batch.setProjectionMatrix(hudCam.combined);
		game.batch.begin();
		game.batch.draw(hudTex, 0, 0);
		game.font16.draw(game.batch, "Screen: Play", 20, 40);
		game.batch.end();
	}
	
	private void update(float delta) {
		world.step(1f / Main.TARGET_UPS, 6, 2);
		player.update(delta);
		cameraUpdate(delta);
	}
	
	private void cameraUpdate(float delta) {
		Vector3 position = camera.position;
		position.x = player.getPosition().x * PPM / 2 + (Main.V_WIDTH / 8 - player.getWidth() / 2);
		position.y = player.getPosition().y * PPM / 2 - (Main.V_HEIGHT / 8 - player.getHeight());
		
//		camera.position.set(position);
//		camera.position.slerp(position, delta);
		camera.translate(position);
		camera.update();
	}

	@Override
	public void resize(int width, int height) {
		camera.setToOrtho(false, Main.V_WIDTH, Main.V_HEIGHT);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		world.dispose();
		b2dr.dispose();
		hudTex.dispose();
		
	}
	
	public Body createBox(int x, int y, int width, int height, BodyDef.BodyType bodyType) {
		Body pBody;
		BodyDef def = new BodyDef();
		def.type = bodyType;
		
		def.position.set(x / PPM, y / PPM);
		
		def.fixedRotation = true;
		pBody = world.createBody(def);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2 / PPM, height / 2 / PPM);
		
		pBody.createFixture(shape, 1.0f);
		shape.dispose();
		
		return pBody;
	}
	
	public Player getPlayer() {
		return player;
	}
	

}