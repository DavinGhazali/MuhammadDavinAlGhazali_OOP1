package com.davin.frontend.objects.enemies;

import com.badlogic.gdx.graphics.Color;
import com.davin.frontend.objects.Collidable;
import com.davin.frontend.objects.Player;

public class Fairy extends Enemy {

    public Fairy(String name, int hp) {
        super(150, 380, 24, 24, Color.PINK, name, hp, 500L);
    }

    public Fairy(float x, float y, String name, int hp) {
        super(x, y, 24, 24, Color.PINK, name, hp, 500L);
    }

    @Override
    public void onCollision(Collidable other) {
        if (other instanceof Player && collisionCooldown <= 0) {
            System.out.println("Player touches fairy");
            collisionCooldown = 1f;
        }
    }
}
