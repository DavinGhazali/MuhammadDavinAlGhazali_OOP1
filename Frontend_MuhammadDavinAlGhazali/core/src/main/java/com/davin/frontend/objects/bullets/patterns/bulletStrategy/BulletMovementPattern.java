package com.davin.frontend.objects.bullets.patterns.bulletStrategy;

import com.davin.frontend.objects.bullets.Bullet;

public interface BulletMovementPattern {
    void move(Bullet bullet, float delta);
}
