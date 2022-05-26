package com.example.aircraftwar.prop;

import com.example.aircraftwar.aircraft.HeroAircraft;
import com.example.aircraftwar.strategy.ConcreteShootStrategyH1;
import com.example.aircraftwar.strategy.ConcreteShootStrategyH3;

/**
 * 火力道具，给英雄机增强子弹效果
 * @author yanghaopeng
 */
public class FireProp extends AbstractProp{

    public FireProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }
    
    @Override
    public int work(HeroAircraft heroAircraft) {
        if(heroAircraft.getStrategy().getClass() == ConcreteShootStrategyH1.class) {
            System.out.println("FireSupply Active!");

            Thread t = new Thread() {
                @Override
                public void run() {
                    // 设置为散射
                    heroAircraft.setStrategy(new ConcreteShootStrategyH3());
                    try {
                        // 8秒持续时间
                        Thread.sleep(8_000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    heroAircraft.setStrategy(new ConcreteShootStrategyH1());
                    System.out.println("FireSupply Over");
                }
            };
            t.start();
        } else {
            System.out.println("FireSupply was Already Active!");
        }

        return 0;
    }
}
