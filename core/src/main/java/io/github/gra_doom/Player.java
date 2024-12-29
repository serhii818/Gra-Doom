package io.github.gra_doom;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.*;
import io.github.gra_doom.entity.*;
import io.github.gra_doom.entity.Character;

public class Player extends Character {

    Vector2 plane;

    public boolean rotatingRight, rotatingLeft;
    public boolean movingForward, movingBackward, movingRight, movingLeft;

	public static final float movingSpeed = 2f;
	public static final float runSpeed = movingSpeed * 1.5f;
	public static final float rotationSpeed = 60f;

    private long lastFrameTime;
    private float frameTime;


    public Player(Vector2 pos, Vector2 dir, Vector2 plane, float size, String spritePath, float maxHealth) {
        super(pos, size, spritePath, 0, maxHealth);
        this.dir = dir;
        this.plane = plane;

    }

    public static Player makePlayer() {
        return new Player(new Vector2(14.4f, 10.8f), new Vector2(-1, 0),
            new Vector2(0, 0.66f), 0.5f, "", 100);
    }

    @Override
    public void update(Map map, float frameTime) {
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

        oldPos.set(pos);
    	if(movingForward) {
    		pos.x += dir.x * movingAmount;
            if (isInWall(map)) pos.x = oldPos.x;

            pos.y += dir.y * movingAmount;
            if (isInWall(map)) pos.y = oldPos.y;
    	}
    	else if(movingBackward) {
            pos.x -= dir.x * movingAmount;
            if (isInWall(map)) pos.x = oldPos.x;

            pos.y -= dir.y * movingAmount;
            if (isInWall(map)) pos.y = oldPos.y;
    	}

        //oldPos.set(pos);
    	if (movingRight) {
    	    pos.x += dir.y * movingAmount/2;
            if (isInWall(map)) pos.x = oldPos.x;

            pos.y -= dir.x * movingAmount/2;
            if (isInWall(map)) pos.y = oldPos.y;
    	}
    	else if (movingLeft) {
            pos.x -= dir.y * movingAmount/2;
            if (isInWall(map)) pos.x = oldPos.x;

            pos.y += dir.x * movingAmount/2;
            if (isInWall(map)) pos.y = oldPos.y;
    	}

        for (Entity e : map.entities) {
            collide(e);
        }
        if (hasWeapon()) weapon.updateFrameCount();
        if (isShooting()) shoot(map);
    }

    @Override
    public void collide(Entity e) {
        if (!(e instanceof Projectile)) {
            if (e instanceof PickUpItem p) {
                p.pick(this);
                p.selfDestroy();
            }
            else {
                if (isColliding(this, e)) {
                    float x = pos.x;
                    pos.x = oldPos.x;
                    if (isColliding(this, e)) {
                        pos.x = x;
                        pos.y = oldPos.y;
                        if (isColliding(this, e)) {
                            pos.x = oldPos.x;
                        }
                    }

                }
            }
        }

    }

}
