package com.davin.frontend.systems;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.davin.frontend.commands.Command;
import com.davin.frontend.commands.InputHandler;
import com.davin.frontend.objects.GameObject;
import com.davin.frontend.objects.Player;
import com.davin.frontend.observers.GameObserver;
import com.davin.frontend.states.StageClearState;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StageFacade {
    private static final float PLAYFIELD_WIDTH = 416f;
    private static final float PLAYFIELD_HEIGHT = 464f;

    private final List<GameObject> entities = new ArrayList<>();
    private final BulletManager bulletManager = new BulletManager();
    private final CollisionReferee collisionReferee = new CollisionReferee();
    private final InputHandler inputHandler = new InputHandler();
    private final Player player;
    private LevelWaveManager waveManager;

    // ====================================================
    // Constructor
    // ====================================================
    public StageFacade(GameObserver hud) {
        player = EntityFactory.createPlayer(200, 50, "Reimu Hakurei", 100, 0, 3);
        player.registerObserver(hud);
        entities.add(player);

        bulletManager.setEntities(entities);
        inputHandler.setEntities(entities);

        waveManager = new LevelWaveManager(entities, bulletManager, player);
    }

    // ====================================================
    // Core Methods
    // ====================================================
    public void update(float delta) {
        // The world freezes once the run ends (Stage Clear or Game Over) so the leaderboard overlay sits on a still frame
        if (isStageClear() || isGameOver()) {
            return;
        }

        List<Command> commands = inputHandler.handleInput(delta);
        for (Command command : commands) {
            command.execute(player, bulletManager);
        }

        waveManager.update(delta);
        updateAndClean(entities, delta, PLAYFIELD_WIDTH, PLAYFIELD_HEIGHT);
        bulletManager.update(delta, PLAYFIELD_WIDTH, PLAYFIELD_HEIGHT);
        collisionReferee.resolveCollisions(player, entities, bulletManager);
    }

    public boolean isStageClear() {
        return waveManager.getCurrentState() instanceof StageClearState;
    }

    public void restart() {
        entities.clear();
        bulletManager.clearEnemyBullets();
        bulletManager.clearPlayerBullets();
        entities.add(player);
        player.resetGame();
        waveManager = new LevelWaveManager(entities, bulletManager, player);
    }

    // ====================================================
    // Render, Query & Helper
    // ====================================================
    public void render(SpriteBatch batch) {
        for (GameObject entity : entities) {
            if (!entity.isDestroyed()) {
                entity.render(batch);
            }
        }
        bulletManager.render(batch);
    }

    public void renderOverlay(ShapeRenderer shapeRenderer) {
        player.renderFocusIndicator(shapeRenderer);
    }

    public boolean isGameOver() {
        return player.isGameOver();
    }

    public long getScore() {
        return player.getScore();
    }

    public <T extends GameObject> void updateAndClean(List<T> list, float delta, float screenWidth, float screenHeight) {
        Iterator<T> iterator = list.iterator();
        while (iterator.hasNext()) {
            T entity = iterator.next();
            entity.update(delta);

            if (entity.isOffScreen(screenWidth, screenHeight) || entity.isDestroyed()) {
                iterator.remove();
            }
        }
    }
}
