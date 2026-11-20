package com.davin.frontend.states;

import com.davin.frontend.objects.enemies.Boss;
import com.davin.frontend.systems.LevelWaveManager;

public class StageClearState implements WaveState {

    @Override
    public void onEnter(LevelWaveManager manager) {
        System.out.println("[StageClearState] STAGE 1 CLEAR!");
        Boss boss = manager.getBoss();
        if (boss != null && !boss.isDestroyed()) {
            boss.destroy();
        }
    }

    @Override
    public void update(LevelWaveManager manager, float delta) {
        // Intentionally empty
    }
}
