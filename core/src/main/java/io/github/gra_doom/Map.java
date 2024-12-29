package io.github.gra_doom;

import io.github.gra_doom.entity.Entity;
import io.github.gra_doom.entity.PickUpItem;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;

public class Map implements Serializable {
    public int[][] arr;
    Player player;
    static final String savePath = "Maps/";
    public LinkedList<Entity> entities;
    public LinkedList<PickUpItem> pickUpItems;
    private long lastFrameTime;

    public Map(int[][] arr, Player player) {
        this.arr = arr;
        this.player = player;

        entities = new LinkedList<>();

    }

    public int getArr(int x, int y) {
        return arr[x][y];
    }

    public Player getPlayer() {
        return player;
    }

    public void addEntity(Entity e) {
        if (!(e instanceof PickUpItem)) {
            entities.add(e);
        }
    }

    public void addPickUpItem(PickUpItem p) {
        pickUpItems.add(p);
    }

    public void update() {
        long currentTime = System.nanoTime();
        // delta time for stable movement for different fps
        float frameTime = (currentTime - lastFrameTime) / 1000000000.0f;
        lastFrameTime = currentTime;

        player.update(this, frameTime);

        Iterator<Entity> iterator = entities.iterator();
        while(iterator.hasNext()) {
            Entity e = iterator.next();
            e.update(this, frameTime);

            if (e.shouldDelete) {
                iterator.remove();
            }
        }
    }

    public static void saveToFile(Map map ,String fileName) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(savePath + fileName));
            oos.writeObject(map);
            oos.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * load Map from serialized file, all maps are saved and loaded from Maps/ folder
     * in assets folder
     * ! after loading map you should initialized keyboard controller
     */
    public static Map loadFromFile(String fileName) {
        ObjectInputStream ios = null;
        Map map = null;
        try {
            ios = new ObjectInputStream(new FileInputStream(savePath + fileName));
            map = (Map) ios.readObject();
            ios.close();

            for (Entity e : map.entities) {
                e.initializeSprite(); // Let's assume it works :)
            }
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

        return map;
    }
}
