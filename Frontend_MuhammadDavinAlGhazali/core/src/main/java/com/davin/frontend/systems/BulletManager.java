package com.davin.frontend.systems;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.davin.frontend.items.Item;
import com.davin.frontend.items.ItemType;
import com.davin.frontend.objects.BulletType;
import com.davin.frontend.objects.GameObject;
import com.davin.frontend.objects.patterns.bulletStrategy.HomingBulletMovement;
import com.davin.frontend.objects.bullets.Bullet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


private Queue<Bullet> playerBulletPool;
private Queue<Bullet> enemyBulletPool;
private List<GameObject> entities; // [ADDITION] Add field to hold the arena entity list


public class BulletManager {
    private Queue<Bullet> playerBulletPool;
    private Queue<Bullet> enemyBulletPool;

    private List<Bullet> activePlayerBullets;
    private List<Bullet> activeEnemyBullets;

    public BulletManager() {
        playerBulletPool = new LinkedList<>();
        enemyBulletPool = new LinkedList<>();
        activePlayerBullets = new ArrayList<>();
        activeEnemyBullets = new ArrayList<>();
    }

    public Bullet spawnPlayerBullet(float x, float y, float vx, float vy, int damage) {
        return spawnPlayerBullet(x, y, vx, vy, damage, null);
    }

    // [MODIFY] Update the Module 8 spawnPlayerBullet method with the following changes:
    public Bullet spawnPlayerBullet(float x, float y, float vx, float vy, int damage, String spriteKey) { // Add String spriteKey as a parameter
        String key = (spriteKey != null) ? spriteKey : "bullet_amulet"; // Add local String variable key
        Bullet bullet;
        if (!playerBulletPool.isEmpty()) {
            bullet = playerBulletPool.poll();
            bullet.init(x, y, vx, vy, BulletType.AMULET, damage, true);
            bullet.setSprite(AssetManager.getInstance().getTextureRegion(key)); // Add setSprite using key
        } else {
            bullet = EntityFactory.createPlayerBullet(x, y, damage, key); // Add key as 4th argument
            bullet.init(x, y, vx, vy, BulletType.AMULET, damage, true);
        }
        activePlayerBullets.add(bullet);
        return bullet;
    }

    // [ADDITION] Add the following 5-parameter overloaded method (delegating to the 6-parameter method above):
    public Bullet spawnPlayerBullet(float x, float y, float vx, float vy, int damage) {
        return spawnPlayerBullet(x, y, vx, vy, damage, "bullet_amulet");
    }

    // [MODIFY] Update the Module 8 spawnEnemyBullet method with the following changes:
    public Bullet spawnEnemyBullet(float x, float y, float vx, float vy, int damage, String spriteKey) { // Add String spriteKey as a parameter
        String key = (spriteKey != null) ? spriteKey : "bullet_danmaku"; // Add local String variable key
        Bullet bullet;
        if (!enemyBulletPool.isEmpty()) {
            bullet = enemyBulletPool.poll();
            bullet.init(x, y, vx, vy, BulletType.DANMAKU, damage, false);
            bullet.setSprite(AssetManager.getInstance().getTextureRegion(key)); // Add setSprite using key
        } else {
            bullet = EntityFactory.createEnemyBullet(x, y, damage, key); // Add key as 4th argument
            bullet.init(x, y, vx, vy, BulletType.DANMAKU, damage, false);
        }
        activeEnemyBullets.add(bullet);
        return bullet;
    }

    // [ADDITION] Add the following 5-parameter overloaded method (delegating to the 6-parameter method above):
    public Bullet spawnEnemyBullet(float x, float y, float vx, float vy, int damage) {
        return spawnEnemyBullet(x, y, vx, vy, damage, "bullet_danmaku");
    }


    public Bullet spawnEnemyBullet(float x, float y, float vx, float vy, int damage) {
        return spawnEnemyBullet(x, y, vx, vy, damage, null);
    }

    public Bullet spawnEnemyBullet(float x, float y, float vx, float vy, int damage, String spriteKey) {
        Bullet bullet;
        if (!enemyBulletPool.isEmpty()) {
            bullet = enemyBulletPool.poll();
        } else {
            bullet = EntityFactory.createEnemyBullet(x, y, damage);
        }
        bullet.init(x, y, vx, vy, BulletType.DANMAKU, damage, false);
        bullet.setSprite(AssetManager.getInstance().getTextureRegion(spriteKey != null ? spriteKey : "bullet_danmaku"));
        activeEnemyBullets.add(bullet);
        return bullet;
    }

    public void update(float delta, float screenWidth, float screenHeight) {
        Iterator<Bullet> pIter = activePlayerBullets.iterator();
        while (pIter.hasNext()) {
            Bullet bullet = pIter.next();

            // [ADDITION] Insert this check right before bullet.update(delta):
            if (entities != null && bullet.getMovementPattern() instanceof HomingBulletMovement homing && homing.getEntities() == null) {
                homing.setEntities(entities);
            }

            bullet.update(delta);
            ...
        }
        ...
    }

            if (bullet.isOffScreen(screenWidth, screenHeight) || bullet.isDestroyed()) {
                bullet.destroy();
                playerIt.remove();
                playerBulletPool.offer(bullet);
            }
        }

        Iterator<Bullet> enemyIt = activeEnemyBullets.iterator();
        while (enemyIt.hasNext()) {
            Bullet bullet = enemyIt.next();
            bullet.update(delta);
            if (bullet.isOffScreen(screenWidth, screenHeight) || bullet.isDestroyed()) {
                bullet.destroy();
                enemyIt.remove();
                enemyBulletPool.offer(bullet);
            }
        }
    }

    // Spell Card (Bomb): clear all enemy bullets and turn them into POINT item drops
    public void clearEnemyBullets(List<GameObject> entities) {
        Iterator<Bullet> iterator = activeEnemyBullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            if (entities != null) {
                Item drop = EntityFactory.createItem(bullet.getX(), bullet.getY(), ItemType.POINT);
                entities.add(drop);
            }
            bullet.destroy();
            iterator.remove();
            enemyBulletPool.offer(bullet);
        }
    }

    public void clearEnemyBullets() {
        clearEnemyBullets(null);
    }

    public void clearPlayerBullets() {
        Iterator<Bullet> pIter = activePlayerBullets.iterator();
        while (pIter.hasNext()) {
            Bullet bullet = pIter.next();
            bullet.destroy();
            pIter.remove();
            playerBulletPool.offer(bullet);
        }
    }

    public void render(SpriteBatch batch) {
        for (Bullet b : activePlayerBullets) {
            if (!b.isDestroyed()) b.render(batch);
        }
        for (Bullet b : activeEnemyBullets) {
            if (!b.isDestroyed()) b.render(batch);
        }
    }

    public List<Bullet> getActivePlayerBullets() { return activePlayerBullets; }
    public List<Bullet> getActiveEnemyBullets() { return activeEnemyBullets; }
    public int getPlayerPoolSize() { return playerBulletPool.size(); }
    public int getEnemyPoolSize()
    public List<GameObject> getEntities() { return entities; }
    public void setEntities(List<GameObject> entities) { this.entities = entities; }
}
