package io.github.gra_doom.entity;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.math.Vector2;
import io.github.gra_doom.Player;
import io.github.gra_doom.Map;

import java.io.Serializable;

abstract public class Entity implements Drawable, Serializable {
    public Vector2 pos;
    public float size;
    public Pixmap sprite;

    public Entity() {
        pos = new Vector2(0, 0);
        size = 1;
        sprite = new Pixmap(0, 0, Pixmap.Format.RGBA8888);
    }

    public Entity( Vector2 pos,  float size, Pixmap sprite) {
        this.pos = pos;
        this.size = size;
        this.sprite = sprite;
    }


    public static boolean isColliding(Entity e1, Entity e2) {
        return  e1.pos.x < e2.pos.x + e2.size &&
                e2.pos.x < e1.pos.x + e1.size &&
                e1.pos.y < e2.pos.y + e2.size &&
                e2.pos.y < e1.pos.y + e1.size;

    }

    public boolean isInWall(Map map) {
        return map.arr[(int) (pos.y - size/2)][(int) (pos.x - size/2)] != 0 ||
            map.arr[(int) (pos.y - size/2)][(int) (pos.x + size/2)] != 0 ||
            map.arr[(int) (pos.y + size/2)][(int) (pos.x - size/2)] != 0 ||
            map.arr[(int) (pos.y + size/2)][(int) (pos.x + size/2)] != 0;
    }

    /**
     * describes uniqe interactions with different object
     */
    abstract public void collide();

    @Override
    public float getDistFromCam(Player cam) {
        return 0;
    }

    @Override
    public void draw() {

    }

    abstract void update(Map map);
}
