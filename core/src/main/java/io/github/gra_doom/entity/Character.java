package io.github.gra_doom.entity;

import com.badlogic.gdx.math.Vector2;
import io.github.gra_doom.Map;

abstract public class Character extends MoveableEntity implements Killable{
    float health;
    float maxHealth;
    boolean alive;
    protected Weapon weapon;
    public Vector2 dir;
    protected boolean shooting;

    public Character() {
        super();
        health = 100;
        maxHealth = 100;
        alive = true;
        this.dir = new Vector2(0, 0);
        shooting = false;
    }

    public Character(Vector2 pos, float size, String spritePath, float speed, float maxHealth) {
        super(pos, size, spritePath, speed);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.alive = true;
        this.dir = new Vector2(0, 0);
        shooting = false;
    }

    @Override
    public void update(Map map, float frameTime) {
        super.update(map, frameTime);
        if (!alive) selfDestroy();
        if (hasWeapon()) weapon.updateFrameCount();
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
        if (getHealth() <= 0) alive = false;
        else alive = true;
        if (health > maxHealth) health = maxHealth;
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    public void shoot(Map map) {
        Vector2 proj_pos = pos.cpy();
        proj_pos.add(dir);
        if (weapon != null) weapon.shoot(proj_pos, dir.cpy(), map);
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public boolean hasWeapon() {
        return weapon != null;
    }

    public boolean isShooting() {
        return shooting;
    }

    public void setShooting(boolean shooting) {
        this.shooting = shooting;
    }
}
