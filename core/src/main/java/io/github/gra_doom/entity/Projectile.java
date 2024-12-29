package io.github.gra_doom.entity;

import com.badlogic.gdx.math.Vector2;
import io.github.gra_doom.Map;
import io.github.gra_doom.Player;

public class Projectile extends MoveableEntity{
    private float damage;
    boolean belongsToPlayer;


    public Projectile() {
        super();
        vel = new Vector2(0, 0);
        oldPos = new Vector2(0, 0);
    }

    public Projectile( Vector2 pos,  float size, String spritePath, float speed, float damage, boolean belongsToPlayer) {
        super(pos, size, spritePath, speed);
        this.damage = damage;
        this.belongsToPlayer = belongsToPlayer;
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
}
