package io.github.gra_doom.entity;

import com.badlogic.gdx.math.Vector2;

public class Enemy extends Character{


    public Enemy() {
        super();
    }

    public Enemy(Vector2 pos, float size, String spritePath, float speed, float maxHealth) {
        super(pos, size, spritePath, speed, maxHealth);
    }

    @Override
    public String toString() {
        return "Enemy:" + super.toString();
    }
}
