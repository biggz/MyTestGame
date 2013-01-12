package com.bigerstaff.testgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;

public class Drop extends Rectangle {
	{
		this.x = MathUtils.random(0, 800-48);
		this.y = 480;
		this.width = 48;
		this.height = 48;
	}
	public void update(){
		this.y -= 200 * Gdx.graphics.getDeltaTime();
		//Detect Collision with bucket
	}
}