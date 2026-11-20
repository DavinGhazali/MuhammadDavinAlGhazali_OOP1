package com.davin.frontend.commands;

import com.davin.frontend.objects.Player;
import com.davin.frontend.systems.BulletManager;

public interface Command {
    void execute(Player player, BulletManager bulletManager);
}
