package com.example.aircraftwar.factory;

import com.example.aircraftwar.aircraft.AbstractAircraft;

/**
 * @author Hope
 */
public abstract class AbstractEnemyFactory {
    /**
     * 创建敌机并返回
     * @return 创建的敌机
     */
    public abstract AbstractAircraft createEnemy();
}
