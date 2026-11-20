package com.davin.frontend.objects.patterns.bulletStrategy;

import com.davin.frontend.objects.GameObject;
import com.davin.frontend.objects.bullets.Bullet;
import com.davin.frontend.objects.enemies.Enemy;
import java.util.List;

/**
 * BulletStrategy dedicated to MOVEMENT: the flight trajectory of homing bullets.
 * 1. Bullets steer toward the target for up to ~0.8 seconds.
 * 2. If the target is eliminated, it re-targets the nearest living enemy.
 * 3. Once homing expires or the target is lost, the direction locks and the bullet
 *    accelerates forward up to 600 px/s.
 */
public class HomingBulletMovement implements BulletMovementPattern {
    private GameObject target;
    private List<GameObject> entities;
    private float turnSpeed;
    private float maxHomingDuration = 0.8f;

    public HomingBulletMovement(GameObject target, float turnSpeed) {
        this(target, null, turnSpeed, 0.8f);
    }

    public HomingBulletMovement(GameObject target, List<GameObject> entities, float turnSpeed) {
        this(target, entities, turnSpeed, 0.8f);
    }

    public HomingBulletMovement(GameObject target, List<GameObject> entities, float turnSpeed, float maxHomingDuration) {
        this.target = target;
        this.entities = entities;
        this.turnSpeed = turnSpeed;
        this.maxHomingDuration = maxHomingDuration;
    }

    public HomingBulletMovement(List<GameObject> entities, float turnSpeed) {
        this(null, entities, turnSpeed, 0.8f);
    }

    public HomingBulletMovement(float turnSpeed) {
        this(null, null, turnSpeed, 0.8f);
    }

    public HomingBulletMovement() {
        this(null, null, 360f, 0.8f);
    }

    @Override
    public void move(Bullet bullet, float delta) {
        if (bullet == null) return;

        // 1. Determine the active target:
        // A: the fixed target if it is still alive/active
        // B: otherwise the nearest living Enemy from the entities list
        GameObject activeTarget = target;
        if (activeTarget == null || activeTarget.isDestroyed() || !activeTarget.isActive()) {
            if (entities != null) {
                GameObject nearest = null;
                float minDistanceSq = Float.MAX_VALUE;
                for (GameObject obj : entities) {
                    if (obj instanceof Enemy) {
                        Enemy enemy = (Enemy) obj;
                        if (!enemy.isDestroyed() && enemy.isActive() && enemy.isAlive()) {
                            float dx = enemy.getX() - bullet.getX();
                            float dy = enemy.getY() - bullet.getY();
                            float distSq = dx * dx + dy * dy;
                            if (distSq < minDistanceSq) {
                                minDistanceSq = distSq;
                                nearest = enemy;
                            }
                        }
                    }
                }
                activeTarget = nearest;
            }
        }

        // 2. Homing window: steer for the first maxHomingDuration seconds
        boolean isHomingActive = (bullet.getStateTime() <= maxHomingDuration);

        if (isHomingActive && activeTarget != null && !activeTarget.isDestroyed() && activeTarget.isActive()) {
            float targetAngle = (float) Math.toDegrees(Math.atan2(
                activeTarget.getY() - bullet.getY(),
                activeTarget.getX() - bullet.getX()
            ));
            float diff = (targetAngle - bullet.getAngle()) % 360f;
            if (diff > 180f) diff -= 360f;
            if (diff < -180f) diff += 360f;

            bullet.setAngle(bullet.getAngle() + Math.signum(diff) * Math.min(Math.abs(diff), turnSpeed * delta));
        } else if (!isHomingActive) {
            // After the homing window, accelerate forward up to 600 px/s
            float newSpeed = Math.min(600f, bullet.getSpeed() + 250f * delta);
            bullet.setSpeed(newSpeed);
        }


        float rad = (float) Math.toRadians(bullet.getAngle());
        float dx = (float) Math.cos(rad) * bullet.getSpeed() * delta;
        float dy = (float) Math.sin(rad) * bullet.getSpeed() * delta;
        bullet.setX(bullet.getX() + dx);
        bullet.setY(bullet.getY() + dy);
    }

    public GameObject getTarget() { return target; }
    public void setTarget(GameObject target) { this.target = target; }

    public List<GameObject> getEntities() { return entities; }
    public void setEntities(List<GameObject> entities) { this.entities = entities; }

    public float getTurnSpeed() { return turnSpeed; }
    public void setTurnSpeed(float turnSpeed) { this.turnSpeed = turnSpeed; }

    public float getMaxHomingDuration() { return maxHomingDuration; }
    public void setMaxHomingDuration(float maxHomingDuration) { this.maxHomingDuration = maxHomingDuration; }
}
