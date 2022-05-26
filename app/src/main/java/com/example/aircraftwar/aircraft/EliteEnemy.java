package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.GameActivity;
import com.example.aircraftwar.strategy.ConcreteShootStrategyE1;
import com.example.aircraftwar.prop.AbstractProp;
import com.example.aircraftwar.prop.BombProp;

/**
 * 精英敌机
 * 射击方式：向下发射1枚子弹
 *
 * @author yanghaopeng
 */
public class EliteEnemy extends AbstractAircraft {

    public EliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.setStrategy(new ConcreteShootStrategyE1());
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= GameActivity.screenHeight) {
            vanish();
        }
    }

    /**
     * 精英敌机掉落清屏道具
     * @return 清屏道具BombProp
     */
    @Override
    public AbstractProp dropProp() {
        return new BombProp(
                this.getLocationX(),
                this.getLocationY(),
                0,
                5  );
    }

}
