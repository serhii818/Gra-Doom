package io.github.gra_doom.entity;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import io.github.gra_doom.Map;

abstract public class MoveableEntity extends Entity {

    // must always be normalized (must have lenght 1)
    public Vector2 vel;
    public Vector2 oldPos;

    private long lastFrameTime;

    private float speed;

    public MoveableEntity() {
        super();
        vel = new Vector2(0, 0);
        oldPos = new Vector2(0, 0);
    }

    public MoveableEntity( Vector2 pos,  float size, String spritePath) {
        super(pos, size, spritePath);
        vel = new Vector2(0, 0);
        oldPos = new Vector2(0, 0);
    }

    @Override
    public void update(Map map) {
        long currentTime = System.nanoTime();
        // delta time for stable movement for different fps
        float frameTime = (currentTime - lastFrameTime) / 1000000000.0f;
        lastFrameTime = currentTime;

        // save old position to move back in case of collision
        oldPos.set(pos);

        pos.x += vel.x * speed * frameTime;
        if (isInWall(map)) pos.x = oldPos.x;

        pos.y += vel.y * speed * frameTime;
        if (isInWall(map)) pos.y = oldPos.y;

        for (Entity e : map.entities) {
            if (e != this) {
                if (isColliding(this, e)) {
                    pos.set(oldPos);
                }
            }
        }

        // TODO check for any entity collision
    }

}
