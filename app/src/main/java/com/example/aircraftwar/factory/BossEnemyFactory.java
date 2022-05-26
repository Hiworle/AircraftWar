package com.example.aircraftwar.factory;

import com.example.aircraftwar.GameActivity;
import com.example.aircraftwar.aircraft.AbstractAircraft;
import com.example.aircraftwar.aircraft.BossEnemy;
import com.example.aircraftwar.application.ImageManager;

import java.util.Random;

/**
 * @author yanghaopeng
 */
public class BossEnemyFactory extends AbstractEnemyFactory {
    private final int bossHp;

    public BossEnemyFactory(){
        this.bossHp = 400;
    }
    public BossEnemyFactory(int bossHp){
        this.bossHp = bossHp;
    }
    @Override
   public AbstractAircraft createEnemy() {
        // Boss机出现在正中央，能左右移动
        return new BossEnemy(
                        ((GameActivity.screenHeight) / 2),
                        (ImageManager.BOSS_ENEMY_IMAGE.getHeight() / 2),
                        new Random().nextInt(6) - 3,
                        0,
                        bossHp
                );
    }
}
