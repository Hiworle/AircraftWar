package com.example.aircraftwar.strategy;


import com.example.aircraftwar.aircraft.AbstractAircraft;
import com.example.aircraftwar.bullet.BaseBullet;

import java.util.List;

/**
 * 射击策略接口
 * @author yanghaopeng
 */
public interface ShootStrategy {
    /**
     * 重写不同的射击方式
     * @param aircraft 操作的飞机
     * @return 射出的子弹
     */
    abstract List<BaseBullet> shoot(AbstractAircraft aircraft);
}
