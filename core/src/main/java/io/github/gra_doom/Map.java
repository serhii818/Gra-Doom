package io.github.gra_doom;

public class Map {
    public int[][] arr;
    GameCamera cam;

    public Map(int[][] arr, GameCamera cam) {
        this.arr = arr;
        this.cam = cam;
    }
}
