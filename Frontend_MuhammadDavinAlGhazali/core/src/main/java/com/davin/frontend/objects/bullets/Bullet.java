package com.davin.frontend.objects.bullets;

import com.davin.frontend.objects.Collidable;
import com.davin.frontend.objects.enemies.Enemy;
import com.badlogic.gdx.graphics.Color;
import com.davin.frontend.objects.BulletType;
import com.davin.frontend.objects.GameObject;

public class Bullet extends GameObject {
    private BulletType bulletType;
    private int damage;

    public Bullet(float x, float y, BulletType bulletType, int damage) {
        super(x, y, 8, 16, 400f, Color.YELLOW);
        this.bulletType = bulletType;
        this.damage = damage;
    }

    public Bullet(float x, float y, float speed, BulletType bulletType, int damage) {
        super(x, y, 8, 16, speed, Color.YELLOW);
        this.bulletType = bulletType;
        this.damage = damage;
    }

    @Override
    public void update(float delta) {
        this.y += speed * delta;
    }

    public BulletType getBulletType() { return bulletType; }
    public int getDamage() { return damage; }

    @Override
    public void onCollision(Collidable other) {
        if (other instanceof Enemy ) {
            Enemy enemy = (Enemy) other;
            System.out.println("Bullet hit" + enemy.getName() + "for" + damage + "DMG");
            enemy.takeDamage(damage);
            destroy();
        }
    }
}
