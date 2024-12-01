package io.github.gra_doom.entity;

import com.badlogic.gdx.graphics.Pixmap;
import io.github.gra_doom.Map;

abstract public class MoveableEntity {
    float velx;
    float vely;

    float oldPosX;
    float oldPosY;


    public void update(Map map) {
        //calc new pos

        // ckeck in new position collides with wall
        // set old position

        // override for player
    }

}
