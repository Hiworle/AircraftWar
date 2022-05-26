package com.example.aircraftwar.strategy;

import com.example.aircraftwar.aircraft.AbstractAircraft;
import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.bullet.EnemyBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 单行的向下直射，威力10，方向1
 * 精英敌机的默认射击方式
 * @author yanghaopeng
 */
public class ConcreteShootStrategyE1 implements ShootStrategy{
    /** 子弹一次发射数量*/
    private int shootNum = 1;

    /** 子弹伤害 */
    private int power = 10;

    /** 子弹射击方向 (向上发射：-1，向下发射：1)*/
    private int direction = 1;
    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> res = new LinkedList<>();
        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY() + direction*2;
        int speedX = 0;
        int speedY = direction*8;
        BaseBullet bullet;
        for(int i=0; i<shootNum; i++){
            // 子弹发射位置相对飞机位置向前偏移
            // 多个子弹横向分散
            bullet = new EnemyBullet(x + (i*2 - shootNum + 1)*10, y, speedX, speedY, power);
            res.add(bullet);
        }
        return res;
    }
}
