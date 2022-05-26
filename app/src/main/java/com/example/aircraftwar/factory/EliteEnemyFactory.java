package com.example.aircraftwar.factory;

import com.example.aircraftwar.GameActivity;
import com.example.aircraftwar.aircraft.AbstractAircraft;
import com.example.aircraftwar.aircraft.EliteEnemy;
import com.example.aircraftwar.application.ImageManager;

import java.util.Random;

/**
 * @author Hope
 */
public class EliteEnemyFactory extends AbstractEnemyFactory {
    @Override
    public AbstractAircraft createEnemy() {
        // 精英敌机能左右移动
        return new EliteEnemy(
                (int) (Math.random() * (GameActivity.screenWidth - ImageManager.ELITE_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * GameActivity.screenHeight * 0.2),
                new Random().nextInt(20) - 10,
                5,
                30);
    }
}
