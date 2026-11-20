package com.davin.frontend.states;

import com.davin.frontend.systems.LevelWaveManager;

public interface WaveState {
    void onEnter(LevelWaveManager manager);
    void update(LevelWaveManager manager, float delta);
}
