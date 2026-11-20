package com.davin.frontend.systems;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.davin.frontend.objects.items.Item;
import com.davin.frontend.objects.items.ItemType;
import com.davin.frontend.objects.BulletType;
import com.davin.frontend.objects.GameObject;
import com.davin.frontend.objects.bullets.Bullet;
import com.davin.frontend.objects.patterns.bulletStrategy.FantasySealMovement;
import com.davin.frontend.objects.patterns.bulletStrategy.HomingBulletMovement;
import com.davin.frontend.objects.patterns.shootingStrategy.RingShot;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BulletManager {
    private Queue<Bullet> playerBulletPool;
    private Queue<Bullet> enemyBulletPool;

    private List<Bullet> activePlayerBullets;
    private List<Bullet> activeEnemyBullets;

    private List<GameObject> entities; // arena entity list (used by homing bullets)

    public BulletManager() {
        playerBulletPool = new LinkedList<>();
        enemyBulletPool = new LinkedList<>();
        activePlayerBullets = new ArrayList<>();
        activeEnemyBullets = new ArrayList<>();
    }

    public Bullet spawnPlayerBullet(float x, float y, float vx, float vy, int damage, String spriteKey) {
        String key = (spriteKey != null) ? spriteKey : "bullet_amulet";
        Bullet bullet;
        if (!playerBulletPool.isEmpty()) {
            bullet = playerBulletPool.poll();
        } else {
            bullet = EntityFactory.createPlayerBullet(x, y, damage, key);
        }
        bullet.init(x, y, vx, vy, BulletType.AMULET, damage, true);
        bullet.setSprite(AssetManager.getInstance().getTextureRegion(key));
        activePlayerBullets.add(bullet);
        return bullet;
    }

    public Bullet spawnPlayerBullet(float x, float y, float vx, float vy, int damage) {
        return spawnPlayerBullet(x, y, vx, vy, damage, "bullet_amulet");
    }

    public Bullet spawnEnemyBullet(float x, float y, float vx, float vy, int damage, String spriteKey) {
        String key = (spriteKey != null) ? spriteKey : "bullet_danmaku";
        Bullet bullet;
        if (!enemyBulletPool.isEmpty()) {
            bullet = enemyBulletPool.poll();
        } else {
            bullet = EntityFactory.createEnemyBullet(x, y, damage, key);
        }
        bullet.init(x, y, vx, vy, BulletType.DANMAKU, damage, false);
        bullet.setSprite(AssetManager.getInstance().getTextureRegion(key));
        activeEnemyBullets.add(bullet);
        return bullet;
    }

    public Bullet spawnEnemyBullet(float x, float y, float vx, float vy, int damage) {
        return spawnEnemyBullet(x, y, vx, vy, damage, "bullet_danmaku");
    }

    // Spell Card: Fantasy Seal orbs (RingShot composed with FantasySealMovement)
    public void spawnBombOrbs(float originX, float originY, GameObject targetEnemy, List<GameObject> entities) {
        int orbCount = 8;
        int orbDamage = 35;

        List<GameObject> targetEntities = (entities != null) ? entities : this.entities;
        RingShot ring = new RingShot(150f, orbCount, orbDamage, new FantasySealMovement(targetEnemy, targetEntities));
        ring.execute(originX, originY, this, true);

        System.out.println("[BulletManager] Spawned 8 Bomb Spirit Orbs via RingShot composed with FantasySealMovement!");
    }

    public void spawnBombOrbs(float originX, float originY, GameObject targetEnemy) {
        spawnBombOrbs(originX, originY, targetEnemy, null);
    }

    public void update(float delta, float screenWidth, float screenHeight) {
        Iterator<Bullet> playerIt = activePlayerBullets.iterator();
        while (playerIt.hasNext()) {
            Bullet bullet = playerIt.next();

            if (entities != null && bullet.getMovementPattern() instanceof HomingBulletMovement) {
                HomingBulletMovement homing = (HomingBulletMovement) bullet.getMovementPattern();
                if (homing.getEntities() == null) {
                    homing.setEntities(entities);
                }
            }

            bullet.update(delta);
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
    public int getEnemyPoolSize() { return enemyBulletPool.size(); }

    public List<GameObject> getEntities() { return entities; }
    public void setEntities(List<GameObject> entities) { this.entities = entities; }
}
