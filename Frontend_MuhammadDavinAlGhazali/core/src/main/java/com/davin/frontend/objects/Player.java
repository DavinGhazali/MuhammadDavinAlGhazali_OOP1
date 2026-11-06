package com.davin.frontend.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.davin.frontend.items.Item;
import com.davin.frontend.items.ItemType;
import com.davin.frontend.objects.enemies.Enemy;
import com.davin.frontend.objects.patterns.ShootingPattern;
import com.davin.frontend.systems.AssetManager;
import com.davin.frontend.systems.BulletManager;

import java.awt.*;
import java.util.List;
import com.davin.frontend.observers.GameObserver;
import java.util.ArrayList;

public class Player extends GameObject {
    private String name;
    private int hp;
    private int lives = 3;
    private int power;
    private int spellCards;
    private long score;
    private int currentDir = 0; // -1 = left, 0 = idle, 1 = right
    private ShootingPattern shootingPattern;
    private List<GameObserver> observers = new ArrayList<>();

    private int grazeCount = 0;
    private boolean focused = false;
    private float shootTimer = 0.1f;
    private float shootCooldown = 0.1f;
    private float invulnerableTimer = 0f;

    public Player(String name, int hp, int power, int spellCards) {
        super(280, 40, 32, 48, 0, Color.RED);
        this.name = name;
        this.hp = hp;
        this.power = power;
        this.spellCards = spellCards;
        this.score = 0;
    }

    public Player(float x, float y, String name, int hp, int power, int spellCards) {
        super(x, y, 32, 48, 0, Color.RED);
        this.name = name;
        this.hp = hp;
        this.power = power;
        this.spellCards = spellCards;
        this.score = 0;
    }

    @Override
    public Rectangle getCoreHitbox() {
        return new Rectangle(x + (width / 2f) - 4f, y + (height / 2f) - 4f, 8f, 8f);
    }

    @Override
    public Rectangle getGrazeHitbox() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public void update(float delta) {
        super.update(delta); // advances stateTime for animations
        shootTimer += delta;

        if (invulnerableTimer > 0f) {
            invulnerableTimer -= delta;
            if (invulnerableTimer < 0f) invulnerableTimer = 0f;
        }
    }

    // Movement executed by MoveCommand, clamped to the playfield
    public void move(float dx, float dy) {
        this.x += dx * speed;
        this.y += dy * speed;
        clampToPlayfield();
    }

    private void clampToPlayfield() {
        float minX = 32;
        float maxX = 416 - width;
        float minY = 16;
        float maxY = 560 - height;

        if (x < minX) x = minX;
        if (x > maxX) x = maxX;
        if (y < minY) y = minY;
        if (y > maxY) y = maxY;
    }

    public void updateAnimationState(float dx) {
        AssetManager assets = AssetManager.getInstance();
        if (dx < 0) {
            if (currentDir != -1) {
                currentDir = -1;
                Animation<TextureRegion> anim = assets.getAnimation("player_left");
                if (anim != null) {
                    setAnimation(anim);
                }
            }
        } else if (dx > 0) {
            if (currentDir != 1) {
                currentDir = 1;
                Animation<TextureRegion> anim = assets.getAnimation("player_right");
                if (anim != null) {
                    setAnimation(anim);
                }
            }
        } else {
            if (currentDir != 0) {
                currentDir = 0;
                Animation<TextureRegion> anim = assets.getAnimation("player_idle");
                if (anim != null) {
                    setAnimation(anim);
                }
            }
        }
    }

    @Override
    public void onCollision(Collidable other) {
        if (other instanceof Item) {
            Item item = (Item) other;
            if (!item.isCollected()) {
                System.out.println("Player touches items");
                collectItem(item);
                item.setCollected(true);
            }
        }
    }

    public void shoot(Enemy target) {
        int damage = 10 + getPower();
        System.out.println(getName() + " shoots " + target.getName() + " dealing " + damage + " DMG!");
        target.takeDamage(damage);
    }

    public void shootBullet(BulletManager bulletManager) {
        if (bulletManager == null) return;

        if (shootTimer >= shootCooldown) {
            if (shootingPattern != null) {
                shootingPattern.execute(x + width / 2f - 8f, y + height, bulletManager, true);
            } else {
                bulletManager.spawnPlayerBullet(x + width / 2f - 8f, y + height, 0, 400f, 10 + power);
            }
            shootTimer = 0f;
        }
    }

    public ShootingPattern getShootingPattern() { return shootingPattern; }
    public void setShootingPattern(ShootingPattern shootingPattern) { this.shootingPattern = shootingPattern; }

    // Spell Card (Bomb) executed by BombCommand


    public void useBomb(BulletManager bulletManager) {
        useBomb(bulletManager, null);
    }



    @Override
    public void render(SpriteBatch batch) {
        if (batch != null && active && !isDestroyed()) {
            // Blink while invulnerable
            if (invulnerableTimer > 0f) {
                boolean visible = ((int) (invulnerableTimer * 16)) % 2 == 0;
                if (!visible) return;
            }

            if (focused) {
                batch.setColor(1f, 1f, 1f, 0.5f);
            }
            super.render(batch);
            if (focused) {
                batch.setColor(1f, 1f, 1f, 1.0f);
            }
        }
    }

    // Draws the Core Hitbox dot while Focus Mode is active
    public void renderFocusIndicator(ShapeRenderer shapeRenderer) {
        if (focused && shapeRenderer != null && active) {
            Rectangle core = getCoreHitbox();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.circle(core.x + core.width / 2f, core.y + core.height / 2f, 4f);
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.circle(core.x + core.width / 2f, core.y + core.height / 2f, 2f);
            shapeRenderer.end();
        }
    }

    public boolean isFocused() { return focused; }
    public void setFocused(boolean focused) { this.focused = focused; }
    public int getGrazeCount() { return grazeCount; }

    // ---- Lives & respawn system ----

    public void takeDamage(int damage) {
        takeDamage(damage, null, null);
    }

    public void takeDamage(int damage, BulletManager bulletManager, List<GameObject> entities) {
        if (isInvulnerable() || isDestroyed()) {
            if (isInvulnerable()) {
                System.out.println(name + " is INVULNERABLE! (0 damage taken)");
            }
            return;
        }
        this.hp -= damage;
        if (this.hp < 0) {
            this.hp = 0;
        }
        System.out.println(name + " took " + damage + " damage! Remaining HP: " + this.hp);
        if (this.hp == 0) {
            System.out.println(name + " was defeated (Pichuun~)! ");
            loseLife(bulletManager, entities);
        }
    }

    public void loseLife(BulletManager bulletManager, List<GameObject> entities) {
        this.lives--;

        if (bulletManager != null) {
            bulletManager.clearEnemyBullets();
        }

        if (this.lives > 0) {
            if (bulletManager != null) {
                System.out.println("[Player] " + name + " lost a life! Remaining Lives: " + this.lives + ". Respawning at (200, 50)...");
            }
            respawn();
        } else {
            if (bulletManager != null) {
                System.out.println("[Player] GAME OVER! " + name + " has run out of lives!");
            }
            destroy();
        }
    }

    public void respawn() {
        this.active = true;
        this.x = 200;
        this.y = 50;
        this.hp = 100;
        this.invulnerableTimer = 3.0f;
        this.power = Math.max(10, this.power - 16); // Touhou death penalty
        this.spellCards = 3;
        notifyHpChanged();
        notifyPowerChanged();
        notifySpellCardsChanged();
    }

    public void resetGame() {
        this.lives = 3;
        this.hp = 100;
        this.power = 15;
        this.spellCards = 3;
        this.score = 0;
        this.grazeCount = 0;
        this.active = true;
        this.x = 200;
        this.y = 50;
        this.invulnerableTimer = 3.0f;
        notifyLivesChanged();
        notifyHpChanged();
        notifyPowerChanged();
        notifySpellCardsChanged();
        notifyScoreChanged();
        notifyGrazeChanged();
    }

    public void addScore(long points) {
        if (points > 0) {
            this.score += points;
            notifyScoreChanged();
            System.out.println(getName() + " gained " + points + " pts! Total Score: " + this.score);
        }
    }

    public void collectItem(Item item) {
        if (item.isDestroyed()) return;

        ItemType type = item.getItemTypeEnum();
        if (type != null) {
            switch (type) {
                case POWER:
                    this.power += type.getPowerBonus();
                    notifyPowerChanged();
                    addScore(item.getScoreValue());
                    System.out.println(name + " collected POWER item! Power increased to " + power);
                    break;
                case POINT:
                    addScore(item.getScoreValue());
                    System.out.println(name + " collected POINT item!");
                    break;
                case BOMB:
                    this.spellCards++;
                    notifySpellCardsChanged();
                    addScore(item.getScoreValue());
                    System.out.println(name + " collected BOMB item! SpellCards: " + spellCards);
                    break;
                case LIFE:
                    this.lives++;
                    this.hp = 100;
                    notifyLivesChanged();
                    notifyHpChanged();
                    addScore(item.getScoreValue());
                    System.out.println(name + " collected LIFE item! Remaining Lives: " + lives);
                    break;
            }
        } else {
            addScore(item.getScoreValue());
            System.out.println(name + " collected " + item.getItemType() + "!");
        }

        item.destroy();
    }

    public boolean isAlive() {
        return this.hp > 0 && this.lives > 0 && !isDestroyed();
    }

    public boolean isGameOver() {
        return this.lives <= 0;
    }

    public boolean isInvulnerable() {
        return invulnerableTimer > 0f;
    }

    public float getInvulnerableTimer() {
        return invulnerableTimer;
    }

    public void registerObserver(GameObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
            observer.onScoreChanged(score);
            observer.onHpChanged(hp);
            observer.onLivesChanged(lives);
            observer.onSpellCardsChanged(spellCards);
            observer.onPowerChanged(power);
            observer.onGrazeChanged(grazeCount);
        }
    }

    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    private void notifyScoreChanged() {
        for (GameObserver obs : observers)obs.onScoreChanged(score);
    }

    private void notifyHpChanged() {
        for (GameObserver obs : observers)obs.onHpChanged(hp);
    }

    private void notifyLivesChanged() {
        for (GameObserver obs : observers)obs.onLivesChanged(lives);
    }

    private void notifySpellCardsChanged() {
        for (GameObserver obs : observers)obs.onSpellCardsChanged(spellCards);
    }

    private void notifyPowerChanged() {
        for (GameObserver obs : observers)obs.onPowerChanged(power);
    }

    private void notifyGrazeChanged() {
        for (GameObserver obs : observers)obs.onGrazeChanged(grazeCount);
    }



    public int getLives() { return lives; }
    public void setLives(int lives) { this.lives = lives; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = Math.max(0, hp); }
    public int getPower() { return power; }
    public void setPower(int power) { this.power = power; }
    public int getSpellCards() { return spellCards; }
    public void setSpellCards(int spellCards) { this.spellCards = spellCards; }
    public long getScore() { return score; }
}
