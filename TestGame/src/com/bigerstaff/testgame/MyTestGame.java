package com.bigerstaff.testgame;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class MyTestGame implements ApplicationListener {
	Texture dropImage;
	Texture bucketImage;
	Sound dropSound;
	Music rainMusic;
	OrthographicCamera camera;
	SpriteBatch batch;
	Rectangle bucket;
	Array<Drop> raindrops;
	long lastDropTime;
	Vector3 touchPos;
	
	@Override
	public void create() {		
		//Load images for the droplet and bucket, 48x48 pixels each.
		dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));
		
		//Loud Sounds
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
		
		//Create Camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		
		//Create SpriteBatch
		batch = new SpriteBatch();
		
		//Create bucket
		bucket = new Rectangle();
		bucket.x = 800/2 - 48/2;
		bucket.y = 20;
		bucket.width = 48;
		bucket.height = 48;
		
		//Vectors
		touchPos = new Vector3();
		
		//Raindrops
		raindrops = new Array<Drop>();
		spawnRaindrop();
		
		//Start music
		rainMusic.setLooping(true);
		rainMusic.play();
	}

	@Override
	public void dispose() {
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		batch.dispose();
	}

	@Override
	public void render() {	
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
				
		camera.update();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		//Draw bucket
		batch.draw(bucketImage, bucket.x, bucket.y);
		//Draw raindrops
		for(Rectangle raindrop: raindrops){
			batch.draw(dropImage, raindrop.x, raindrop.y);
		}
		batch.end();
		
		//Bucket Movement
		if(Gdx.input.isTouched()){
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - 48 / 2;
			
		}
		
		//Keyboard movement
		if (Gdx.input.isKeyPressed(Keys.LEFT)){
			bucket.x -= 200 * Gdx.graphics.getDeltaTime();
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT)){
			bucket.x += 200 * Gdx.graphics.getDeltaTime();
		}
		//Restrict Movement to screen area
		if(bucket.x < 0 ) bucket.x = 0;
		if(bucket.x > 800-48) bucket.x = 800 - 48;
		
		//Spawn Raindrop every second
		if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();
		//Move Raindrops
		Iterator<Drop> iter = raindrops.iterator();
		//Update Raindrop
		while(iter.hasNext()){
			Drop raindrop = iter.next();
			raindrop.update();
			//Remove Raindrop if off screen
			if (raindrop.y + 48 < 0) iter.remove();
			//Detect Collision with bucket
			if(raindrop.overlaps(bucket)){
				dropSound.play();
				iter.remove();
			}
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
	
	private void spawnRaindrop(){
		Drop raindrop = new Drop();
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}
}
