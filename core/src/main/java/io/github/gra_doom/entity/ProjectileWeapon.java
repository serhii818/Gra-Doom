package io.github.gra_doom.entity;

import com.badlogic.gdx.math.Vector2;
import io.github.gra_doom.Map;

public class ProjectileWeapon extends Weapon{
    Projectile projectileInstance;

    public ProjectileWeapon() {
        super();
        projectileInstance = new Projectile();
    }

    public ProjectileWeapon(int coolDown, AmmoType ammoType, Projectile projectileInstance) {
        super(coolDown, ammoType);
        this.projectileInstance = projectileInstance;
    }

    private Projectile createProjectile(Vector2 pos, Vector2 dir) {
        Projectile projectile = new Projectile(projectileInstance);
        projectile.setVel(dir);
        projectile.setPos(pos);
        return projectile;
    }

    @Override
    public boolean shoot(Vector2 pos, Vector2 dir, Map map) {
        boolean canShoot = super.shoot(pos, dir, map);

        if (canShoot) {
            Projectile p = createProjectile(pos, dir);
            map.addEntity(p);
        }

        return canShoot;
    }
}
