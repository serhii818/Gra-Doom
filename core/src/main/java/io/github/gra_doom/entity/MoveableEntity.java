package io.github.gra_doom.entity;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import io.github.gra_doom.Map;

abstract public class MoveableEntity extends Entity {
    public Vector2 vel;
    public Vector2 oldPos;

    public MoveableEntity( Vector2 pos,  float size, Pixmap sprite) {
        super(pos, size, sprite);
    }

    public void update(Map map) {
        //calc new pos

        // ckeck in new position collides with wall
        // set old position

        // override for player
    }

}
