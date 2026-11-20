package com.davin.frontend.objects.patterns.bulletStrategy;

import com.davin.frontend.objects.GameObject;
import com.davin.frontend.objects.bullets.Bullet;
import com.davin.frontend.objects.enemies.Enemy;
import java.util.List;

public class FantasySealMovement implements BulletMovementPattern {
    private GameObject target;
    private List<GameObject> entities;
    private float homingSpeed;
    private float turnSpeed;
    private float driftDuration;

    public FantasySealMovement(GameObject target, List<GameObject> entities, float homingSpeed, float turnSpeed, float driftDuration) {
        this.target = target;
        this.entities = entities;
        this.homingSpeed = homingSpeed;
        this.turnSpeed = turnSpeed;
        this.driftDuration = driftDuration;
    }

    public FantasySealMovement(GameObject target, List<GameObject> entities) {
        this(target, entities, 400f, 300f, 0.4f);
    }

    public FantasySealMovement(GameObject target) {
        this(target, null, 400f, 300f, 0.4f);
    }

    @Override
    public void move(Bullet bullet, float delta) {
        if (bullet == null) return;

        // Uses each bullet's own stateTime, so one instance is safe to share across all 8 orbs
        if (bullet.getStateTime() < driftDuration) {
            bullet.setX(bullet.getX() + bullet.getVx() * delta);
            bullet.setY(bullet.getY() + bullet.getVy() * delta);
        } else {
            if (target == null || target.isDestroyed()) {
                if (entities != null) {
                    GameObject nearest = null;
                    float minDistanceSq = Float.MAX_VALUE;
                    for (GameObject obj : entities) {
                        if (obj instanceof Enemy && !obj.isDestroyed()) {
                            float dx = obj.getX() - bullet.getX();
                            float dy = obj.getY() - bullet.getY();
                            float distSq = dx * dx + dy * dy;
                            if (distSq < minDistanceSq) {
                                minDistanceSq = distSq;
                                nearest = obj;
                            }
                        }
                    }
                    target = nearest;
                }
            }

            if (target != null && !target.isDestroyed()) {
                float targetAngle = (float) Math.toDegrees(Math.atan2(
                    target.getY() - bullet.getY(),
                    target.getX() - bullet.getX()
                ));
                float diff = (targetAngle - bullet.getAngle()) % 360f;
                if (diff > 180f) diff -= 360f;
                if (diff < -180f) diff += 360f;

                bullet.setAngle(bullet.getAngle() + Math.signum(diff) * Math.min(Math.abs(diff), turnSpeed * delta));
            }

            float rad = (float) Math.toRadians(bullet.getAngle());
            float dx = (float) Math.cos(rad) * homingSpeed * delta;
            float dy = (float) Math.sin(rad) * homingSpeed * delta;
            bullet.setX(bullet.getX() + dx);
            bullet.setY(bullet.getY() + dy);
        }
    }

    public GameObject getTarget() { return target; }
    public void setTarget(GameObject target) { this.target = target; }

    public List<GameObject> getEntities() { return entities; }
    public void setEntities(List<GameObject> entities) { this.entities = entities; }

    public float getHomingSpeed() { return homingSpeed; }
    public void setHomingSpeed(float homingSpeed) { this.homingSpeed = homingSpeed; }

    public float getTurnSpeed() { return turnSpeed; }
    public void setTurnSpeed(float turnSpeed) { this.turnSpeed = turnSpeed; }

    public float getDriftDuration() { return driftDuration; }
    public void setDriftDuration(float driftDuration) { this.driftDuration = driftDuration; }
}
