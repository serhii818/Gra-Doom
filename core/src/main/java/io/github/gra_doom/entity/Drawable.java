package io.github.gra_doom.entity;

import io.github.gra_doom.Player;

/**
 * calculates position to object and draws it.
 * */
public interface Drawable {
    float getDistFromCam(Player cam);
    void draw();
}
