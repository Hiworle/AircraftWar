package com.example.aircraftwar.prop;

import com.example.aircraftwar.aircraft.HeroAircraft;

/**
 * 加血道具
 * @author yanghaopeng
 */
public class BloodProp extends AbstractProp{

    public BloodProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    /**
     * 回复100点血量
     */
    @Override
    public int work(HeroAircraft heroAircraft) {
        heroAircraft.addBlood(100);
        return 0;
    }
    
}
