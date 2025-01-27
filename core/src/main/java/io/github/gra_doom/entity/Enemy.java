package io.github.gra_doom.entity;

import com.badlogic.gdx.math.Vector2;
import io.github.gra_doom.Map;
import io.github.gra_doom.Player;

public class Enemy extends Character{
    public enum Type {
        DEMON,
        IMP,
        ZOMBIE
    }

    private float visionDist = 6*6;
    private float shootDist = 4*4;
    private float stopDist = 3*3;



    public Enemy() {
        super();
    }

    public Enemy(Vector2 pos, float size, String spritePath, float speed, float maxHealth) {
        super(pos, size, spritePath, speed, maxHealth);
    }

    public static Enemy makeEnemy(Type type, Vector2 pos) {
        Enemy new_e;
        Projectile p;
        Weapon w;

        switch (type) {
            case IMP:
                new_e = new Enemy(pos, 0.4f, "sprites/Imp.png", 0.6f, 50);
                p = new Projectile(new Vector2(0.0f, 0.0f), 0.5f, "bullet/b1.png", 1.0f, 30, false);
                p.setTransforms(1f, 1f, 100.0f);
                w = new ProjectileWeapon(200, AmmoType.ENERGY_CELL, p);
                new_e.setWeapon(w);
                break;
            case DEMON:
                new_e = new Enemy(pos, 1.4f, "sprites/Demon.png", 1.6f, 150);
                p = new Projectile(new Vector2(0.0f, 0.0f), 0.5f, "bullet/b1.png", 1.0f, 20, false);
                p.setTransforms(1f, 1f, 100.0f);
                w = new ProjectileWeapon(150, AmmoType.ENERGY_CELL, p);
                new_e.setWeapon(w);
                break;
            case ZOMBIE:
                new_e = new Enemy(pos, 0.8f, "sprites/Zombie.png", 1.0f, 80);
                new_e.setTransforms(2f, 2f, 100.0f);
                p = new Projectile(new Vector2(0.0f, 0.0f), 0.5f, "bullet/b1.png", 0.5f, 10, false);
                p.setTransforms(3f, 3f, 100.0f);
                w = new ProjectileWeapon(120, AmmoType.ENERGY_CELL, p);
                new_e.setWeapon(w);
                break;
            default:
                new_e = new Enemy(pos, 1.0f, "pics/barrel.png", 3.0f, 3000);
                p = new Projectile(new Vector2(0.0f, 0.0f), 0.5f, "bullet/b1.png", 5.0f, 100, false);
                w = new ProjectileWeapon(10, AmmoType.ENERGY_CELL, p);
                new_e.setWeapon(w);
                break;
        }

        return new_e;
    }

    @Override
    public void update(Map map, float frameTime) {
        //aiProcess(map);
        super.update(map, frameTime);
        if (shooting) shoot(map);
    }

    private void aiProcess(Map map) {
        Player pl = map.getPlayer();
        float dist = getDistFromCam(pl);
        dir.set((pl.pos.cpy()).sub(pos));
        dir.setLength(1.0f);

        if (dist < visionDist) {
            if (dist > stopDist) setVel(dir);
            else setVel(0, 0);

            if (dist < shootDist) {
                setShooting(true);
            }
        } else {
            setVel(0, 0);
            setShooting(false);
        }

    }

    @Override
    public String toString() {
        return "Enemy:" + super.toString();
    }
}
