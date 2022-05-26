package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.GameActivity;
import com.example.aircraftwar.application.ImageManager;
import com.example.aircraftwar.strategy.ConcreteShootStrategyH1;
import com.example.aircraftwar.prop.AbstractProp;

/**
 * 英雄机，游戏玩家操控，使用单例模式创建
 * @author hitsz、yanghaopeng
 */
public class HeroAircraft extends AbstractAircraft {

    /** DCL单例模式 */
    private volatile static HeroAircraft instance;

    /**
     * @param locationX 英雄机位置x坐标
     * @param locationY 英雄机位置y坐标
     * @param speedX 英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param speedY 英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param hp    初始生命值
     */
    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.setStrategy(new ConcreteShootStrategyH1());
    }

    /**
     * 获得单例，使用DCL方式
     * @return 唯一HeroAircraft实例
     */
    public static HeroAircraft getInstance() {
        if(instance == null) {
            synchronized (HeroAircraft.class) {
                if(instance == null) {

                    // 英雄机初始位置在正中间，生命值为1000
                    instance = new HeroAircraft(
                            GameActivity.screenWidth / 2,
                            GameActivity.screenHeight - ImageManager.HERO_IMAGE.getHeight() ,
                            0, 0, 500);
                }
            }
        }
        return instance;
    }

    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }

    /** 英雄机不掉落物品 */
    @Override
    public AbstractProp dropProp() {
        return null;
    }

    /**
     * 加血，上限是maxHp
     * @param blood 增加的血量
     */
    public void addBlood(int blood) {
        hp = Math.min(maxHp, hp + blood);
    }
}
