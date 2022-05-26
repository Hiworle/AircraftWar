package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.strategy.ShootStrategy;
import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.basic.AbstractFlyingObject;
import com.example.aircraftwar.prop.AbstractProp;

import java.util.LinkedList;
import java.util.List;

/**
 * 所有种类飞机的抽象父类：
 * 敌机（BOSS, ELITE, MOB），英雄飞机
 *
 * @author hitsz
 */
public abstract class AbstractAircraft extends AbstractFlyingObject {
    /**
     * 生命值
     */
    protected int maxHp;
    protected int hp;
    private ShootStrategy strategy;

    public AbstractAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY);
        this.hp = hp;
        this.maxHp = hp;

        // 默认策略空实现
        strategy = aircraft -> new LinkedList<>();
    }

    public void decreaseHp(int decrease){
        hp -= decrease;
        if(hp <= 0){
            hp=0;
            vanish();
        }
    }

    public int getHp() {
        return hp;
    }

    /**
     * 一定概率掉落道具
     * @return
     *  敌机需实现，按照爆率返回掉落道具或者返回null
     *  英雄机无需实现，返回null
     */
    public abstract AbstractProp dropProp();

    /**
     * 设置具体的射击方法
     * @param strategy 射击策略
     */
    public void setStrategy(ShootStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * 获得当前的射击方法
     * @return 当前的射击策略
     */
    public ShootStrategy getStrategy() {
        return strategy;
    }

    /**
     * 飞机射击方法
     * @return 返回具体策略的射击方法
     *  可射击对象需实现，返回子弹
     *  非可射击对象空实现，返回null
     */
    public List<BaseBullet> shoot() {
        return strategy.shoot(this);
    }
}


