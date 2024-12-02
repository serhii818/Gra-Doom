package io.github.gra_doom;

import io.github.gra_doom.entity.Entity;

import java.io.Serializable;
import java.util.ArrayList;

public class Map implements Serializable {
    public int[][] arr;
    Player player;
    public ArrayList<Entity> entities;

    public Map(int[][] arr, Player player) {
        this.arr = arr;
        this.player = player;

        entities = new ArrayList<>();

    }

    public int getArr(int x, int y) {
        return arr[x][y];
    }

    public Player getPlayer() {
        return player;
    }

    public void addEntity(Entity e) {
        entities.add(e);
    }

    public void update() {
        player.update(this);


    }
}
