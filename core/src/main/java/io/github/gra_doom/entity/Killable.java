package io.github.gra_doom.entity;

public interface Killable {
    void applyDamage(float damage);
    void heal(float healAmount);
    // set health to zero and set avile to false
    void kill();
    void maxHeal();

    float getHealth();
    float getMaxHealth();

    void setHealth(float health);
    void updateLifeState();
    boolean isAlive();
}
