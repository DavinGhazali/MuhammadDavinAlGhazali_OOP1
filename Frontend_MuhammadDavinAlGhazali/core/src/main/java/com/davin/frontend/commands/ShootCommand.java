package com.davin.frontend.commands;

import com.davin.frontend.objects.Player;
import com.davin.frontend.systems.BulletManager;

public class ShootCommand implements Command {

    @Override
    public void execute(Player player, BulletManager bulletManager) {
        if (player != null) {
            player.shootBullet(bulletManager);
        }
    }
}
