package com.davin.frontend.systems;

import com.davin.frontend.items.Item;
import com.davin.frontend.items.ItemType;
import com.davin.frontend.objects.GameObject;
import com.davin.frontend.objects.Player;
import com.davin.frontend.objects.bullets.Bullet;
import com.davin.frontend.objects.enemies.Enemy;
import java.util.ArrayList;
import java.util.List;

public class CollisionReferee {

    public void resolveCollisions(Player player, List<GameObject> entities, BulletManager bulletManager) {
        if (player == null || entities == null || bulletManager == null) return;

        List<GameObject> newDrops = new ArrayList<>();

        // 1. Player bullets vs enemies
        for (Bullet bullet : bulletManager.getActivePlayerBullets()) {
            if (bullet.isDestroyed()) continue;

            for (GameObject entity : entities) {
                if (entity instanceof Enemy && !entity.isDestroyed()) {
                    Enemy enemy = (Enemy) entity;
                    if (bullet.getCoreHitbox().overlaps(enemy.getCoreHitbox())) {
                        boolean defeated = enemy.takeDamage(bullet.getDamage());
                        if (defeated) {
                            player.addScore(enemy.getScoreValue());
                            Item drop = EntityFactory.createItem(enemy.getX(), enemy.getY(), ItemType.POWER);
                            newDrops.add(drop);
                        }
                        bullet.destroy();
                        break;
                    }
                }
            }
        }

        // 2. Enemy bullets vs player (core hit vs graze)
        for (Bullet bullet : bulletManager.getActiveEnemyBullets()) {
            if (bullet.isDestroyed()) continue;

            if (!player.isDestroyed()) {
                // Priority A: core hitbox (damage / life loss)
                if (bullet.getCoreHitbox().overlaps(player.getCoreHitbox())) {
                    if (!player.isInvulnerable()) {
                        System.out.println("[CollisionReferee] CORE HIT! Enemy Bullet hit " + player.getName() + " for " + bullet.getDamage() + " DMG!");
                        player.takeDamage(100, bulletManager, entities);
                        bullet.destroy();
                        // takeDamage clears the enemy bullet list, so stop looping right away
                        break;
                    }
                }
                // Priority B: graze hitbox
                else if (!bullet.hasBeenGrazed()
                    && bullet.getCoreHitbox().overlaps(player.getGrazeHitbox())) {
                    bullet.setGrazed(true);
                    player.addGraze();
                    System.out.println("[CollisionReferee] GRAZE! " + player.getName() + " grazed an enemy bullet (+50 pts)!");
                }
            }
        }

        // 3. Player vs items and enemy body contact
        for (GameObject entity : entities) {
            if (entity.isDestroyed()) continue;

            if (entity instanceof Item) {
                Item item = (Item) entity;
                if (item.getCoreHitbox().overlaps(player.getCoreHitbox())
                    || item.getCoreHitbox().overlaps(player.getGrazeHitbox())) {
                    player.collectItem(item);
                }
            } else if (entity instanceof Enemy) {
                Enemy enemy = (Enemy) entity;
                if (player.getCoreHitbox().overlaps(enemy.getCoreHitbox())) {
                    if (!player.isInvulnerable()) {
                        System.out.println("[CollisionReferee] CORE COLLISION! Player collided with " + enemy.getName() + "!");
                        player.takeDamage(100, bulletManager, entities);
                    }
                }
            }
        }

        entities.addAll(newDrops);
    }
}
