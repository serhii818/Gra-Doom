package io.github.gra_doom.entity;

import com.badlogic.gdx.math.Vector2;
import io.github.gra_doom.Map;
import io.github.gra_doom.Player;

import java.lang.Character;

public class PickUpItem extends Entity{
    public enum Item {
        HEALTH25,
        HEALTH50,
        HEALTH100,
        AMMO_PISTOL,
        AMMO_SHOTGUN_SHELL,
        AMMO_ENERGY_CELL
    }

    private Item item;

    public PickUpItem() {
        super();
        item = Item.HEALTH25;
    }

    public PickUpItem(Vector2 pos, float size, String spritePath, Item item) {
        super(pos, size, spritePath);
        this.item = item;
        System.out.println("Im a pickupItem");
    }

    @Override
    public void selfDestroy() {
        super.selfDestroy();
        System.out.println("IM dead!!");
    }

    @Override
    public void collide(Entity e) {

    }

    @Override
    public void update(Map map, float frameTime) {

    }

    public void pick(Player byEntity) {
        switch (item) {
            case Item.HEALTH25:
                byEntity.heal(25);
                break;
            case Item.HEALTH50:
                byEntity.heal(50);
                break;
            case Item.HEALTH100:
                byEntity.heal(100);
                break;
            case Item.AMMO_PISTOL:

                break;
            case Item.AMMO_ENERGY_CELL:

                break;
            case Item.AMMO_SHOTGUN_SHELL:

                break;
        }
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
