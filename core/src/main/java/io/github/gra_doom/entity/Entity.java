package io.github.gra_doom.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import io.github.gra_doom.Player;
import io.github.gra_doom.Map;

import java.io.Serializable;

/**
 * Base class for all entities, contains information about: position, size, sprite data and flat for deletion from
 * entity array in Map object
 */
abstract public class Entity implements Serializable {
    public Vector2 pos;
    public float size;

    public transient Pixmap sprite;
    public String spritePath; // internal file path (inside assets' folder)

    public boolean shouldDelete; // is set to true on next iteration in update method of Map object will be deleted from array

    // transforms for rendering
    public float uDiv = 1.0f;
    public float vDiv = 1.0f;
    public float vMove = 0.0f;

    /**
     * Default constructor, created entity at (0, 0) of size 1, with no sprite
     */
    public Entity() {
        pos = new Vector2(0, 0);
        size = 1;
        spritePath = "";
        initializeSprite();
        shouldDelete = false;
    }

    /**
     * Entity constructor
     * @param pos position on map where single unit is one square on map, should have only positive values
     * @param size size for collisions and interpreted as width and height of box
     * @param spritePath path for sprite, use initializeSprite to update sprite Pixmap
     */
    public Entity( Vector2 pos,  float size, String spritePath) {
        this.pos = pos.cpy();
        this.size = size;
        this.spritePath = spritePath;
        initializeSprite();
        this.shouldDelete = false;
    }

    /**
     * Check if two entities are colliding
     */
    public static boolean isColliding(Entity e1, Entity e2) {
        return  e1.pos.x < e2.pos.x + e2.size &&
                e2.pos.x < e1.pos.x + e1.size &&
                e1.pos.y < e2.pos.y + e2.size &&
                e2.pos.y < e1.pos.y + e1.size;

    }

    public boolean isInBound(Map map, Entity e) {

        return 0 <= (int) (pos.y - size / 2) && (int) (pos.y - size / 2) <= map.arr.length &&
            0 <= (int) (pos.y + size / 2) && (int) (pos.y + size / 2) <= map.arr.length &&
            0 <= (int) (pos.x - size / 2) && (int) (pos.x - size / 2) <= map.arr.length &&
            0 <= (int) (pos.x + size / 2) && (int) (pos.x + size / 2) <= map.arr.length;
    }

    /**
     * Tells if the Entity inside the wall
     * @param map map that object is inside of
     */
    public boolean isInWall(Map map) {

        boolean inBound = isInBound(map, this);

        if (inBound) {
            return map.arr[(int) (pos.y - size / 2)][(int) (pos.x - size / 2)] != 0 ||
                map.arr[(int) (pos.y - size / 2)][(int) (pos.x + size / 2)] != 0 ||
                map.arr[(int) (pos.y + size / 2)][(int) (pos.x - size / 2)] != 0 ||
                map.arr[(int) (pos.y + size / 2)][(int) (pos.x + size / 2)] != 0;
        } else {
            return true;
        }
    }

    /**
     * describes unique interactions with different Entities
     */
    abstract public void collide(Entity e);

    /**
     * returns squared distance of Entity from Player (player and camera are the same object)
     * @param cam the player
     * @return squared distance
     */
    public float getDistFromCam(Player cam) {
        return (cam.pos.x - pos.x)*(cam.pos.x - pos.x) + (cam.pos.y - pos.y)*(cam.pos.y - pos.y);
    }

    /**
     * Updates object's position, state, etc
     */
    public abstract void update(Map map, float frameTime);

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

    public void setShouldDelete(boolean shouldDelete) {
        this.shouldDelete = shouldDelete;
    }

    /**
     * sets shouldDelete to true, so on next iteration of update method in Map, Map will remove that Entity from array
     */
    public void selfDestroy() {
        setShouldDelete(true);
    }

    public void setPos(Vector2 pos) {
        this.pos = pos;
    }

    public void setPos(float x, float y) {
        this.pos.x = x;
        this.pos.y = y;
    }

    public void setTransforms(float uDiv, float vDiv, float vMove) {
        this.uDiv = uDiv;
        this.vDiv = vDiv;
        this.vMove = vMove;
    }
}
