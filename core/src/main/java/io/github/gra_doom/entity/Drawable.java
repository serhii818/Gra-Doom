package io.github.gra_doom.entity;

import io.github.gra_doom.GameCamera;

/**
 * calculates position to object and draws it.
 * */
public interface Drawable {
    float getDistFromCam(GameCamera cam);
    void draw();
}
