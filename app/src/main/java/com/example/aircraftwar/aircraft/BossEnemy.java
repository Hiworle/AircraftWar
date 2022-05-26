package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.GameActivity;
import com.example.aircraftwar.prop.AbstractProp;
import com.example.aircraftwar.prop.BloodProp;
import com.example.aircraftwar.strategy.ConcreteShootStrategyE2;

/**
 * Boss机
 * 射击方式：三行散射
 *
 * @author yanghaopeng
 */
public class BossEnemy extends AbstractAircraft {

    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.setStrategy(new ConcreteShootStrategyE2());
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= GameActivity.screenHeight ) {
            vanish();
        }
    }

    /**
     * Boss敌机掉落加血道具
     * @return 加血道具BloodProp
     */
    @Override
    public AbstractProp dropProp() {
        return new BloodProp(
                this.getLocationX(),
                this.getLocationY(),
                0,
                5  );
    }


}
