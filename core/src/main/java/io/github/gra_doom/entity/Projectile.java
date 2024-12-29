package io.github.gra_doom.entity;

import com.badlogic.gdx.math.Vector2;
import io.github.gra_doom.Map;
import io.github.gra_doom.Player;

public class Projectile extends MoveableEntity{
    private float damage;
    boolean belongsToPlayer;


    public Projectile() {
        super();
        damage = 0;
        belongsToPlayer = false;
    }

    public Projectile( Vector2 pos,  float size, String spritePath, float speed, float damage, boolean belongsToPlayer) {
        super(pos, size, spritePath, speed);
        this.damage = damage;
        this.belongsToPlayer = belongsToPlayer;
    }

    public Projectile(Projectile other) {
        this.damage = other.damage;
        this.belongsToPlayer = other.belongsToPlayer;
        this.vel = new Vector2(other.vel);
        this.speed = other.speed;
        this.pos = new Vector2(other.pos);
        this.oldPos = new Vector2(other.oldPos);
        this.size = other.size;
        this.shouldDelete = other.shouldDelete;
        this.spritePath = other.spritePath;
        this.initializeSprite();
    }



    @Override
    public void update(Map map, float frameTime) {
        pos.x += vel.x * speed * frameTime;
        pos.y += vel.y * speed * frameTime;
        if (isInWall(map)) selfDestroy();

        collide(map.getPlayer());
        for (Entity e : map.entities) {
            if (e != this) {
                collide(e);
            }
        }
    }

    @Override
    public void collide(Entity e) {
        if (isColliding(this, e)) {
            if ((e instanceof Character c)) {
                if (belongsToPlayer && e instanceof Enemy) {
                    c.applyDamage(damage);
                    selfDestroy();
                }
                else if (!belongsToPlayer && e instanceof Player) {
                    c.applyDamage(damage);
                    selfDestroy();
                }
            }
        }
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }
}
