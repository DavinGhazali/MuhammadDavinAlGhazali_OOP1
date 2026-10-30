package com.davin.frontend.objects.enemies;

import com.badlogic.gdx.graphics.Color;
import com.davin.frontend.objects.Collidable;
import com.davin.frontend.objects.Player;
import com.davin.frontend.objects.patterns.ShootingPattern;
import com.davin.frontend.systems.BulletManager;

public class Boss extends Enemy {
    private ShootingPattern shootingPattern = new SpreadShot(200f, 3, 30f);

    public Boss(String name, int hp) {
        super(380, 400, 64, 64, Color.BLUE, name, hp, 5000L);
    }

    public Boss(float x, float y, String name, int hp) {
        super(x, y, 64, 64, Color.BLUE, name, hp, 5000L);
    }

    public void shootBullet(BulletManager bulletManager) {
        if (shootingPattern != null && !isDestroyed()) {
            shootingPattern.execute(x + width / 2 - 8, y, bulletManager, false);
        }
    }

    @Override
    public void onCollision(Collidable other) {
        if (other instanceof Player && collisionCooldown <= 0) {
            System.out.println("Player touches boss");
            collisionCooldown = 1f;
        }
    }

    public ShootingPattern getShootingPattern() { return shootingPattern; }
    public void setShootingPattern(ShootingPattern shootingPattern) { this.shootingPattern = shootingPattern; }
}

// GameObject -> Enemy -> Fairy/Boss forms MULTILEVEL inheritance,
// combined with HIERARCHICAL inheritance at the Fairy/Boss level
// since both extend the same parent class Enemy.
