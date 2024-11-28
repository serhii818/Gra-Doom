package io.github.gra_doom;

import com.badlogic.gdx.math.*;

public class GameCamera {

    Vector2 pos;
    Vector2 dir;
    Vector2 plane;
    
    public boolean rotatingRight, rotatingLeft; 
    public boolean movingForward, movingBackward, movingRight, movingLeft;
    
	public static final float movingSpeed = 2f;
	public static final float runSpeed = movingSpeed * 1.5f;
	public static final float rotationSpeed = 60f;
	
    private long lastFrameTime;
    private float frameTime;


    public GameCamera(Vector2 pos, Vector2 dir, Vector2 plane) {
        this.pos = pos;
        this.dir = dir;
        this.plane = plane;
    }
    
    public void update() {
        long currentTime = System.nanoTime();
        frameTime = (currentTime - lastFrameTime) / 1000000000.0f;
        lastFrameTime = currentTime;
    	
    	
    	float movingAmount = movingSpeed * frameTime;
    	float rotateAmount = rotationSpeed * frameTime;
    	
    	if(rotatingRight) {
            this.dir.rotateDeg(rotateAmount);
            this.plane.rotateDeg(rotateAmount);
    	}
    	else if(rotatingLeft) {
    		this.dir.rotateDeg(-rotateAmount);
    		this.plane.rotateDeg(-rotateAmount);
    	}
    	
    	if(movingForward) {
    		pos.add(dir.x * movingAmount, dir.y * movingAmount);
    	}
    	else if(movingBackward) {
    		pos.add(-dir.x * movingAmount, -dir.y * movingAmount);
    	}
    	
    	if (movingRight) {
    	    pos.add(dir.y * movingAmount/2, -dir.x * movingAmount/2);
    	}
    	else if (movingLeft) {
    	    pos.add(-dir.y * movingAmount/2, dir.x * movingAmount/2);
    	}
    }
}