package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.GameActivity;
import com.example.aircraftwar.prop.AbstractProp;
import com.example.aircraftwar.prop.FireProp;

/**
 * 普通敌机
 * 不可射击
 *
 * @author hitsz
 */
public class MobEnemy extends AbstractAircraft {

    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
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
     * 普通敌机掉落火力道具
     * @return 火力道具FireProp
     */
    @Override
    public AbstractProp dropProp() {
        return new FireProp(
                this.getLocationX(),
                this.getLocationY(),
                0,
                5);
    }

}
