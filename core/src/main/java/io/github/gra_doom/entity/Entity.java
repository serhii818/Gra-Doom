package io.github.gra_doom.entity;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.math.Vector2;
import io.github.gra_doom.GameCamera;
import io.github.gra_doom.Map;

abstract public class Entity implements Drawable{
    Vector2 pos;
    Vector2 size;
    Pixmap sprite;

    public Entity(float posx, float posy, float w, float y, Pixmap sprite) {
        this.posx = posx;
        this.posy = posy;
        this.w = w;
        this.h = h;
        this.sprite = sprite;
    }

    public Entity(float posx, float posy, float w, float y, Texture sprite) {
        this.posx = posx;
        this.posy = posy;
        this.w = w;
        this.h = h;

        TextureData t = sprite.getTextureData();
        t.prepare();
        this.sprite = t.consumePixmap();


    }

    public static boolean isColliding(Entity e1, Entity e2) {
        return  e1.posx < e2.posx + e2.w &&
                e2.posx < e1.posx + e1.w &&
                e1.posy < e2.posy + e2.h &&
                e2.posy < e1.posy + e1.h;

    }

    public boolean isInWall(Map map) {
        return map.arr[(int) posx][(int) posy] != 0 ||
            map.arr[(int) posx][(int) (posy + h)] != 0 ||
            map.arr[(int) (posx + w)][(int) posy] != 0 ||
            map.arr[(int) (posx + w)][(int) (posy + h)] != 0;
    }

    /**
     * describes uniqe interactions with different object
     */
    abstract public void collide();

    @Override
    public float getDistFromCam(GameCamera cam) {
        return 0;
    }

    @Override
    public void draw() {

    }
}
