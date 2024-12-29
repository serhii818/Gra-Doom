package io.github.gra_doom.entity;

import io.github.gra_doom.Map;
import io.github.gra_doom.Player;

import java.lang.Character;

public class PickUpItem extends Entity{

    @Override
    public void collide(Entity e) {

    }

    @Override
    public void update(Map map, float frameTime) {

    }

    public void pick(Player byEntity) {
        // TODO put item into players inventory
    }
}
