package io.github.gra_doom.entity;

import com.badlogic.gdx.math.Vector2;
import io.github.gra_doom.Map;

import java.io.Serializable;

abstract public class Weapon implements Serializable {
    int coolDown;
    int frameCount;
    AmmoType ammoType;

    public Weapon() {
        this(10, AmmoType.PISTOL);
    }

    public Weapon(int coolDown, AmmoType ammoType) {
        this.coolDown = coolDown;
        this.ammoType = ammoType;
    }

    public boolean shoot(Vector2 pos, Vector2 dir, Map map) {
        if (frameCount == 0) {
            frameCount = coolDown;
            return true;
        } else {
            return false;
        }
    }

    public void updateFrameCount() {
        if (frameCount > 0) frameCount--;
    }

    @Override
    public String toString() {
        return "Weapon{" +
            "coolDown=" + coolDown +
            ", frameCount=" + frameCount +
            ", ammoType=" + ammoType +
            '}';
    }
}
