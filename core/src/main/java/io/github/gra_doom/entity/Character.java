package io.github.gra_doom.entity;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;

abstract public class Character extends MoveableEntity implements Killable{
    float health;
    float maxHealth;
    boolean avile;

    public Character() {
        super();
        health = 100;
        maxHealth = 100;
        avile = true;
    }

    public Character(Vector2 pos, float size, String spritePath, float maxHealth) {
        super(pos, size, spritePath);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.avile = true;
    }

    @Override
    public void applyDamage(float damage) {
        health -= damage;
        updateLifeState();
    }

    @Override
    public void heal(float healAmount) {
        health += healAmount;
        updateLifeState();
    }

    @Override
    public void kill() {
        health = 0;
        updateLifeState();
    }

    @Override
    public void maxHeal() {
        health = maxHealth;
        updateLifeState();
    }

    @Override
    public float getHealth() {
        return health;
    }

    @Override
    public float getMaxHealth() {
        return maxHealth;
    }

    @Override
    public void setHealth(float health) {
        this.health = health;
        updateLifeState();
    }

    @Override
    public void updateLifeState() {
        if (getHealth() <= 0) avile = false;
        else avile = true;
        if (health > maxHealth) health = maxHealth;
    }

    @Override
    public boolean isAlive() {
        return avile;
    }
}
