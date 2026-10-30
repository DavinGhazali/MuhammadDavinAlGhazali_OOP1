package com.davin.frontend.systems;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.davin.frontend.objects.bullets.Bullet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import com.davin.frontend.objects.BulletType;
import java.util.Iterator

public class BulletManager {
    // Pool queues for object recycling (Object Pool Pattern)
    private Queue<Bullet> playerBulletPool;
    private Queue<Bullet> enemyBulletPool;

    // Active bullets on screen
    private List<Bullet> activePlayerBullets;
    private List<Bullet> activeEnemyBullets;

    public BulletManager() {
        playerBulletPool = new LinkedList<>();
        enemyBulletPool = new LinkedList<>();
        activePlayerBullets = new ArrayList<>();
        activeEnemyBullets = new ArrayList<>();
    }

    // Borrowing Player Bullet from Pool (5 Points)
    public Bullet spawnPlayerBullet(float x, float y, float vx, float vy, int damage) {
        Bullet bullet;

        if (!playerBulletPool.isEmpty()) {
            bullet = playerBulletPool.poll();
        } else {
            bullet = EntityFactory.createEnemyBullet(x, y, damage);
        }
        bullet.init(x, y, vx, vy, BulletType.AMULET, damage, true);
        activePlayerBullets.add(bullet);
        return bullet;
    }



    // Borrowing Enemy Bullet from Pool (5 Points)
    public Bullet spawnEnemyBullet(float x, float y, float vx, float vy, int damage) {
        Bullet bullet;

        if (!enemyBulletPool.isEmpty()) {
            bullet = enemyBulletPool.poll();
        } else {
            bullet = EntityFactory.createEnemyBullet(x, y, damage);
        }
        bullet.init(x, y, vx, vy, BulletType.DANMAKU, damage, false);
        activeEnemyBullets.add(bullet);
        return bullet;
}

    public void render(SpriteBatch batch) {
        for (Bullet b : activePlayerBullets) {
            if (!b.isDestroyed()) b.render(batch);
        }
        for (Bullet b : activeEnemyBullets) {
            if (!b.isDestroyed()) b.render(batch);
        }
    }

    public void update(float delta, float screenWidth, float screenHeight) {

        Iterator<Bullet> playerIt = activePlayerBullets.iterator();
        while (playerIt.hasNext()){
            Bullet bullet = playerIt.next();
            bullet.update(delta);
            if(bullet.isOffScreen(screenWidth, screenHeight) || bullet.isDestroyed()) {
                bullet.destroy();
                playerIt.remove();
                playerBulletPool.offer(bullet);
            }

        }

        Iterator<Bullet> enemyIt = activeEnemyBullets.iterator();
        while (enemyIt.hasNext()){
            Bullet bullet = enemyIt.next();
            bullet.update(delta);
            if(bullet.isOffScreen(screenWidth, screenHeight) || bullet.isDestroyed()) {
                bullet.destroy();
                enemyIt.remove();
                enemyBulletPool.offer(bullet);
            }

        }

    }


    public List<Bullet> getActivePlayerBullets() { return activePlayerBullets; }
    public List<Bullet> getActiveEnemyBullets() { return activeEnemyBullets; }
    public int getPlayerPoolSize() { return playerBulletPool.size(); }
    public int getEnemyPoolSize() { return enemyBulletPool.size(); }
}
