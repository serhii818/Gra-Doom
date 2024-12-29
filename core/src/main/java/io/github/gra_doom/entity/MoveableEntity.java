package io.github.gra_doom.entity;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import io.github.gra_doom.Map;

abstract public class MoveableEntity extends Entity {



    // must always be normalized (must have lenght 1)
    public Vector2 vel;
    public Vector2 oldPos;

    private long lastFrameTime;

    protected float speed;

    public MoveableEntity() {
        super();
        vel = new Vector2(0, 0);
        oldPos = new Vector2(0, 0);
        this.speed = 1;
    }

    public MoveableEntity( Vector2 pos,  float size, String spritePath, float speed) {
        super(pos, size, spritePath);
        vel = new Vector2(0, 0);
        oldPos = new Vector2(0, 0);
        this.speed = speed;
    }

    @Override
    public void update(Map map, float frameTime) {

        // save old position to move back in case of collision
        oldPos.set(pos);

        pos.x += vel.x * speed * frameTime;
        if (isInWall(map)) pos.x = oldPos.x;

        pos.y += vel.y * speed * frameTime;
        if (isInWall(map)) pos.y = oldPos.y;

        for (Entity e : map.entities) {
            collide(e);
        }
    }

    @Override
    public void collide(Entity e) {
        if (e != this && !((e instanceof PickUpItem) || e instanceof Projectile)) {
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

    public void setVel(Vector2 vel) {
        this.vel = vel;
    }

    public void setVel(float x, float y) {
        this.vel.x = x;
        this.vel.y = y;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    // TODO add methods for controling movement like: setVelosity, stop, etc
}
