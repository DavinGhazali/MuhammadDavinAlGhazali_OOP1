package com.davin.frontend.objects.bullets.patterns.entityStrategy;

import com.davin.frontend.objects.GameObject;

public interface EntityMovementPattern {
    void move(GameObject entity, float delta);
}
