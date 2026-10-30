package com.davin.frontend.systems;

import com.davin.frontend.objects.Player;
import com.davin.frontend.items.Item;
import com.davin.frontend.items.ItemType;
import com.davin.frontend.objects.GameObject;
import com.davin.frontend.objects.bullets.Bullet;
import com.davin.frontend.objects.enemies.Enemy;
import java.util.ArrayList;
import java.util.List;

public class CollisionReferee {

    public void resolveCollisions(Player player, List<GameObject> entities, BulletManager bulletManager) {
        if (player == null || entities == null || bulletManager == null) return;

        List<GameObject> newDrops = new ArrayList<>();

        // Collision logic is divided into 3 stages
        //
        // 1. Evaluate Player Bullets vs Enemies
// TODO Code for Question 3:

        for(Bullet bullet : BulletManager.getActivePlayerBullet()){
            if
        }
// - Loop through each bullet in the ActivePlayerBullets list.
//   - If the bullet is already destroyed, skip it (continue)
//
//   - Loop through each entity in entities: (HINT: the `entities` variable is in the parameters above)
//     - Check whether entity is an instance of Enemy enemy and is not destroyed yet.
//     - If so, check whether the bullet's Core Hitbox collides with the enemy's Core Hitbox.
//     - If they collide:
//       1. Deal damage to the enemy: boolean defeated = enemy.takeDamage(bullet.getDamage());
//       2. If the enemy is killed (defeated):
//          - Add the enemy's score (scoreValue) to the player.
//          - Create a POWER item drop via the factory at the x, y coordinates where the enemy died,
//            then add that item to the newDrops list
//       3. Mark the bullet as destroyed so it will be recycled later by BulletManager



        //
        // TODO: Code for Question 4
        //
        // TODO: Code for Question 5
        //
        // ...

        entities.addAll(newDrops);
    }

}
