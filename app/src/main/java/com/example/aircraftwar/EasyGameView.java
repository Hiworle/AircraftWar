package com.example.aircraftwar;

import android.content.Context;

import com.example.aircraftwar.factory.AbstractEnemyFactory;
import com.example.aircraftwar.factory.EliteEnemyFactory;
import com.example.aircraftwar.factory.MobEnemyFactory;

public class EasyGameView extends GameView{
    public EasyGameView(Context context) {
        super(context, GameView.EASY);
        enemyMaxNumber = 3;
        heroShootActionCycleDuration = 400;
        enemyShootActionCycleDuration = 720;
        eliteRate = 0.3;
        addEnemyCycleDuration = 600;
        harderWithTime = false;
        dropRate = 0.4;
    }

    /**
     * 简单模式不产生boss机
     */
    @Override
    protected void addEnemy() {
        AbstractEnemyFactory enemyFactory;
        if(enemyAircrafts.size() < enemyMaxNumber){
            if(Math.random() < eliteRate){
                // 召唤精英敌机
                enemyFactory = new EliteEnemyFactory();
                enemyAircrafts.add(enemyFactory.createEnemy());
            } else {
                // 召唤普通敌机
                enemyFactory = new MobEnemyFactory();
                enemyAircrafts.add(enemyFactory.createEnemy());
            }
        }
    }
}
