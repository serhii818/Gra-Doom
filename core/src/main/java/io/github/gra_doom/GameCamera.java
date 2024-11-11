package io.github.gra_doom;

import com.badlogic.gdx.math.*;

public class GameCamera {

    Vector2 pos;
    Vector2 dir;
    Vector2 plane;


    public GameCamera(Vector2 pos, Vector2 dir, Vector2 plane) {
        this.pos = pos;
        this.dir = dir;
        this.plane = plane;
    }
}
