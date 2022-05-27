package com.example.aircraftwar.factory;

import com.example.aircraftwar.GameActivity;
import com.example.aircraftwar.aircraft.AbstractAircraft;
import com.example.aircraftwar.aircraft.MobEnemy;
import com.example.aircraftwar.application.ImageManager;

/**
 * @author yanghaopeng
 */
public class MobEnemyFactory extends AbstractEnemyFactory {
    @Override
    public AbstractAircraft createEnemy() {
        // 普通敌机只能向前移动
        return new MobEnemy(
                (int) (Math.random() * (GameActivity.screenWidth - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * GameActivity.screenHeight * 0.2),
                0,
                12,
                20);
    }
}
