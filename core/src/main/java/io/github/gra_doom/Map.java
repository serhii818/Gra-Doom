package io.github.gra_doom;

import com.badlogic.gdx.Gdx;
import io.github.gra_doom.entity.Entity;
import io.github.gra_doom.entity.Enemy;
import io.github.gra_doom.entity.PickUpItem;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;

public class Map implements Serializable {
    public int[][] arr;
    Player player;
    static final String savePath = "Maps/";
    public LinkedList<Entity> entities;
    private LinkedList<Entity> new_entities;
    private long lastFrameTime;
    private int totalScore = 0;

    private int[][] tileData;


    public Map(int[][] arr, Player player) {
        this.arr = arr;
        this.player = player;

        entities = new LinkedList<>();
        new_entities = new LinkedList<>();



    }




    public int getArr(int x, int y) {
        return arr[x][y];
    }

    public Player getPlayer() {
        return player;
    }

    public void addEntity(Entity e) {
        new_entities.add(e);
    }


    public void update() {
        entities.addAll(new_entities);
        new_entities.clear();


        long currentTime = System.nanoTime();
        // delta time for stable movement for different fps
        float frameTime = (currentTime - lastFrameTime) / 1000000000.0f;
        lastFrameTime = currentTime;

        player.update(this, frameTime);

        for (Entity e : entities) {
            e.update(this, Gdx.graphics.getDeltaTime());
        }

        for (Entity e : entities) {
            if (e instanceof Enemy) {
                Enemy enemy = (Enemy) e;

                if (!enemy.isDeadCounted() && enemy.getHealth() <= 0) {
                    addScore(30);
                    enemy.setDeadCounted(true);
                }
            }
        }




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
            System.out.println("fail!!!");
            System.out.println(ex.getMessage());
        }

        return map;
    }

    public int[][] getTileData() {
        return this.tileData; // lub jak nazywasz swoją tablicę reprezentującą mapę
    }

    // Pobieranie wyniku
    public int getTotalScore() {
        return totalScore;
    }

    // Ewentualnie dodaj prostą metodę na zwiększenie wyniku:
    public void addScore(int points) {
        totalScore += points;
    }


    public int getEnemyCount() {
        int count = 0;
        for (Entity e : entities) {
            // Sprawdzamy, czy obiekt to Enemy i czy jeszcze żyje (Health > 0)
            if (e instanceof Enemy) {
                Enemy enemy = (Enemy) e;
                // Jeżeli w Twoim kodzie "Character" ma logikę health <= 0 => martwy,
                // wtedy sprawdź, czy enemy jest wciąż "żywy"
                if (enemy.getHealth() > 0) {
                    count++;
                }
            }
        }
        return count;
    }
}
