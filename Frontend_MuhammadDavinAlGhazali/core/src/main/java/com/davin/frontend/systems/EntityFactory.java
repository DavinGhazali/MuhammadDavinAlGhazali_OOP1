package com.davin.frontend.systems;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.davin.frontend.items.Item;
import com.davin.frontend.items.ItemType;
import com.davin.frontend.objects.BulletType;
import com.davin.frontend.objects.Player;
import com.davin.frontend.objects.bullets.Bullet;
import com.davin.frontend.objects.enemies.Boss;
import com.davin.frontend.objects.enemies.Fairy;

public class EntityFactory {

    public static Player createPlayer(float x, float y, String name, int hp, int power, int spellCards) {
        Player player = new Player(x, y, name, hp, power, spellCards);
        Animation<TextureRegion> anim = AssetManager.getInstance().getAnimation("player_idle");
        player.setAnimation(anim);
        return player;
    }

    public static Item createItem(float x, float y, ItemType itemType) {
        Item item = new Item(x, y, itemType);
        String key;
        switch (itemType) {
            case POWER: key = "item_power"; break;
            case POINT: key = "item_point"; break;
            case BOMB:  key = "item_bomb";  break;
            default:    key = "item_life";  break;
        }
        item.setSprite(AssetManager.getInstance().getTextureRegion(key));
        return item;
    }

    public static Bullet createEnemyBullet(float x, float y, int damage) {
        Bullet bullet = new Bullet(x, y, 0f, BulletType.DANMAKU, damage);
        bullet.setSprite(AssetManager.getInstance().getTextureRegion("bullet_danmaku"));
        return bullet;
    }

    // Answer to the doc's hint: Fairy and Boss had no factory methods
    public static Fairy createFairy(float x, float y, String name, int hp) {
        Fairy fairy = new Fairy(x, y, name, hp);
        fairy.setAnimation(AssetManager.getInstance().getAnimation("fairy_idle"));
        return fairy;
    }

    public static Boss createBoss(float x, float y, String name, int hp) {
        Boss boss = new Boss(x, y, name, hp);
        boss.setAnimation(AssetManager.getInstance().getAnimation("boss_idle"));
        return boss;
    }
}
