package com.davin.frontend.items;

import com.badlogic.gdx.graphics.Color;
import com.davin.frontend.objects.Collidable;
import com.davin.frontend.objects.GameObject;
import com.davin.frontend.objects.Player;

public class Item extends GameObject {
    private String itemType;
    private long scoreValue;
    private ItemType itemTypeEnum;
    private boolean collected = false;

    public Item(float x, float y, String itemType) {
        super(x, y, 16, 16, 100f, Color.WHITE);
        this.itemType = itemType;
        this.scoreValue = 1000L;
    }

    public Item(float x, float y, float width, float height, float speed, String itemType) {
        super(x, y, width, height, speed, Color.WHITE);
        this.itemType = itemType;
        this.scoreValue = 1000L;
    }

    public Item(float x, float y, float width, float height, float speed, String itemType, long scoreValue) {
        super(x, y, width, height, speed, Color.WHITE);
        this.itemType = itemType;
        this.scoreValue = scoreValue;
    }

    public Item(float x, float y, ItemType itemTypeEnum) {
        super(x, y, 16, 16, 100f, Color.WHITE);
        this.itemTypeEnum = itemTypeEnum;
        this.itemType = itemTypeEnum.name();
        this.scoreValue = itemTypeEnum.getScoreValue();
    }

    public Item(float x, float y, float width, float height, float speed, ItemType itemTypeEnum, long scoreValue) {
        super(x, y, width, height, speed, Color.WHITE);
        this.itemTypeEnum = itemTypeEnum;
        this.itemType = itemTypeEnum.name();
        this.scoreValue = scoreValue;
    }

    @Override
    public void update(float delta) {
        this.y -= speed * delta;
    }

    @Override
    public void onCollision(Collidable other) {
        if (other instanceof Player) {
            // Item pickup is handled on the Player side via collectItem()
        }
    }

    public String getItemType() { return itemType; }
    public long getScoreValue() { return scoreValue; }
    public ItemType getItemTypeEnum() { return itemTypeEnum; }
    public boolean isCollected() { return collected; }
    public void setCollected(boolean collected) { this.collected = collected; }
}
