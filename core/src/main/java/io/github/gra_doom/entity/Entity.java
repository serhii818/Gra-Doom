package io.github.gra_doom.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import io.github.gra_doom.Player;
import io.github.gra_doom.Map;

import java.io.Serializable;

abstract public class Entity implements Serializable {
    public Vector2 pos;
    public float size;
    public transient Pixmap sprite;
    public String spritePath; // internal file path (inside assets folder)

    public boolean shouldDelete;

    public Entity() {
        pos = new Vector2(0, 0);
        size = 1;
        spritePath = "";
        initializeSprite();
        shouldDelete = false;
    }

    public Entity( Vector2 pos,  float size, String spritePath) {
        this.pos = pos;
        this.size = size;
        this.spritePath = spritePath;
        initializeSprite();
        this.shouldDelete = false;
    }

    public static boolean isColliding(Entity e1, Entity e2) {
        return  e1.pos.x < e2.pos.x + e2.size &&
                e2.pos.x < e1.pos.x + e1.size &&
                e1.pos.y < e2.pos.y + e2.size &&
                e2.pos.y < e1.pos.y + e1.size;

    }

    /**
     * Tells if the object inside the wall
     * @param map map that object is inside of
     */
    public boolean isInWall(Map map) {
        return map.arr[(int) (pos.y - size/2)][(int) (pos.x - size/2)] != 0 ||
            map.arr[(int) (pos.y - size/2)][(int) (pos.x + size/2)] != 0 ||
            map.arr[(int) (pos.y + size/2)][(int) (pos.x - size/2)] != 0 ||
            map.arr[(int) (pos.y + size/2)][(int) (pos.x + size/2)] != 0;
    }

    /**
     * describes unique interactions with different object
     */
    abstract public void collide();

    public float getDistFromCam(Player cam) {
        return (cam.pos.x - pos.x)*(cam.pos.x - pos.x) + (cam.pos.y - pos.y)*(cam.pos.y - pos.y);
    }

    public void draw() {

    }

    /**
     * Updates object's position
     */
    public abstract void update(Map map);

    /**
     * Sets Pixmap Sprite of object after deserialization and initialization
     */
    public void initializeSprite() {
        if (!spritePath.isEmpty()) this.sprite = new Pixmap(Gdx.files.internal(this.spritePath));
        else this.sprite = new Pixmap(0, 0, Pixmap.Format.RGBA8888);
    }

    @Override
    public String toString() {
        return String.format("pos:%s, size:%f, spritePath:%s", pos.toString(), size, spritePath);
    }
}
