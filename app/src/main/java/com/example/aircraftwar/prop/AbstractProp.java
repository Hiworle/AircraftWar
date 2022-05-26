package com.example.aircraftwar.prop;

import com.example.aircraftwar.aircraft.HeroAircraft;
import com.example.aircraftwar.basic.AbstractFlyingObject;

/**
 * 所有道具的抽象父类
 * @author yanghaopeng
 */
public abstract class AbstractProp extends AbstractFlyingObject {
    public AbstractProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }
    
    /**
     *  道具生效
     * @param heroAircraft 道具作用于英雄机
     * @return score 道具作用后添加的得分
     */
    abstract public int work(HeroAircraft heroAircraft);
}
