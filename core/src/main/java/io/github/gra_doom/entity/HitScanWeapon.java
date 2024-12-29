package io.github.gra_doom.entity;

import com.badlogic.gdx.math.Vector2;
import io.github.gra_doom.Map;
import io.github.gra_doom.Player;

public class HitScanWeapon extends Weapon{
    private float damage;

    public HitScanWeapon() {
        super();
        damage = 1;
    }

    public HitScanWeapon(int coolDown, AmmoType ammoType, float damage) {
        super(coolDown, ammoType);
        this.damage = damage;
    }

    private float getDistToWall(Player cam, Map map, float cameraX) {
        if (cameraX > 1.0f) cameraX = 1.0f;
        if (cameraX < -1.0f) cameraX = -1.0f;

        float rayDirX = cam.dir.x + cam.plane.x* cameraX;
        float rayDirY = cam.dir.y + cam.plane.y* cameraX;

        float deltaDistX = (rayDirX == 0) ? 1e30f : Math.abs(1/ rayDirX); // distance to travel along y for next integer x value
        float deltaDistY = (rayDirY == 0) ? 1e30f : Math.abs(1/ rayDirY);

        // current map position
        int mapX = (int)(cam.pos.x);
        int mapY = (int)(cam.pos.y);

        // accumulative distance travelled along x and y
        float sideDistX;
        float sideDistY;

        // distance to the wall perpendicular to the camera plane
        float perpWallDist = -1f;

        // map position increments
        int stepX;
        int stepY;

        // info about wall intercestion
        int hit = 0;
        int side = 0;

        if (rayDirX < 0)
        {
            stepX = -1;
            sideDistX = (cam.pos.x - mapX) * deltaDistX;
        }
        else
        {
            stepX = 1;
            sideDistX = (mapX + 1.0f - cam.pos.x) * deltaDistX;
        }
        if (rayDirY < 0)
        {
            stepY = -1;
            sideDistY = (cam.pos.y - mapY) * deltaDistY;
        }
        else
        {
            stepY = 1;
            sideDistY = (mapY + 1.0f - cam.pos.y) * deltaDistY;
        }

        // trawel along ray direction until wall hit
        int c = 0;
        while (hit == 0 && c < 100) {
            c++;
            if (sideDistX < sideDistY) {
                sideDistX += deltaDistX;
                mapX += stepX;
                side = 0;
            } else {
                sideDistY += deltaDistY;
                mapY += stepY;
                side = 1;
            }

            if (map.arr.length > mapY && mapY >= 0 && map.arr[0].length > mapX && mapX >= 0) {
                if (map.arr[mapY][mapX] > 0) hit = 1;
            } else {
                break;
            }
        }

        if (hit == 1) {
            if (side == 0) perpWallDist = (sideDistX - deltaDistX);
            else perpWallDist = (sideDistY - deltaDistY);
        }

        return perpWallDist;
    }

    /**
     *
     * @param cam
     * @param map
     * @param directionOffset persentage offset from center of the screen, -0.2 20% offset to the left, 0.5 50% offset to the right
     *                        it can only be between -1 and 1
     * @return Character that got hit
     */
    private Character shootRay(Player cam, Map map, float directionOffset) {
        float perpWallDist = getDistToWall(cam, map, directionOffset);
        Character ret = null;


        for (Entity e : map.entities) {
            if (e instanceof Character) {
                double spriteX = e.pos.x - cam.pos.x;
                double spriteY = e.pos.y - cam.pos.y;
                double invDet = 1.0 / (cam.plane.x * cam.dir.y - cam.dir.x * cam.plane.y);
                double transformX = invDet * (cam.dir.y * spriteX - cam.dir.x * spriteY);
                double transformY = invDet * (-cam.plane.y * spriteX + cam.plane.x * spriteY);
                double eCenter = transformX / transformY;
                double eWidth = Math.abs(1/transformY);

                if ((eCenter - eWidth < directionOffset ) && (eCenter + eWidth > directionOffset ) ) {
                    ret = (Character) e;
                }
            }
        }

        return ret;
    }


    @Override
    public boolean shoot(Vector2 pos, Vector2 dir, Map map) {
        boolean canShoot = super.shoot(pos, dir, map);

        if (canShoot) {
            Character character = shootRay(map.getPlayer(), map, 0f);
            character.applyDamage(damage);
        }

        return canShoot;
    }
}
